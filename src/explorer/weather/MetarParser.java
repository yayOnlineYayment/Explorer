package explorer.weather;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
   REFERENCE MATERIALS
   http://meteocentre.com/doc/metar.html
   https://www.wunderground.com/metarFAQ.asp
   https://library.wmo.int/doc_num.php?explnum_id=3425
 */

/**
 * @author Michael Peng
 */
public class MetarParser
{
   static class Validator
   {
      static final String
              IDENT_GROUP = "^[A-Z]{4}\\s\\d{6}Z\\s",
              SURFACE_WIND = "([0-3]\\d\\d|VRB)(((\\d{2,}(G\\d{2,})?)(KT|MPS|KPH))|P99KT|P49MPS)(\\s\\d{3}V\\d{3})?\\s",
              OPTIONAL_REPORT_MODIFIER = "(AUTO\\s)?",
              VISIBILITY = "(\\d{4}|CAVOK|M?(\\d|\\s|/)+?SM)\\s",
              RUNWAY_VISUAL_RANGE = "R\\d\\d[LCR]?/[MP]?\\d{4}(V[MP]?\\d{4})?(FT)?[UDN]? ",
              RUNWAY_VISUAL_RANGES = "(" + RUNWAY_VISUAL_RANGE + "){0,}",
              WEATHER_PHENOMENON_GROUP = "(-|\\+|VC)?(MI|BC|PR|DR|BL|SH|TS|FZ){0,4}(DZ|RA|SN|SG|PL|GR|GS|UP){0,4}(BR|FG|FU|VA|DU|SA|HZ){0,4}(PO|SQ|FC|SS|DS){0,4}",
              PRESENT_WEATHER = "(" + WEATHER_PHENOMENON_GROUP + "\\s){0,3}",
              SKY_LAYER = "(((FEW|SCT|BKN|OVC|VV)(\\d{3}|///)(CB|TCU)?)|NSC|NCD)",
              SKY_CONDITION = "((" + SKY_LAYER + "\\s)*|CLR\\s|SKC\\s|CAVOK\\s)",
              TEMPERATURE = "M?\\d\\d/M?\\d\\d\\s",
              ALTIMETER = "(Q[01]|A[23])\\d{3}",
              TRENDS = ".*",
              REMARKS = "(RMK.*)?";
      // Trends not supported

      private static final String METAR_REGEX =
              IDENT_GROUP + OPTIONAL_REPORT_MODIFIER + SURFACE_WIND + VISIBILITY + RUNWAY_VISUAL_RANGES + PRESENT_WEATHER + SKY_CONDITION +
                      TEMPERATURE + ALTIMETER + TRENDS + REMARKS;

      public static boolean isValid(String metar)
      {
         return metar.toUpperCase().matches(METAR_REGEX);
      }
   }


   public class MissingValueException extends RuntimeException
   {
      private MissingValueException(String missingValue)
      {
         super("Missing " + missingValue);
      }
   }

   public class InvalidFormatException extends IllegalArgumentException
   {
      private InvalidFormatException(String badMetar)
      {
         super(badMetar);
      }
   }

   private int windHeading = 0, windKnots = 0;
   private Integer windGust = null;
   private String icaoCode, skyCondition = "Unspecified";
   private int altimeterValue;

   public Weather parse(String metar)
   {
      if (Objects.requireNonNull(metar).trim().length() == 0)
         throw new MissingValueException("METAR string");
      metar = metar.toUpperCase().trim();

      if (!Validator.isValid(metar))
         throw new InvalidFormatException(metar);

      try
      {
         return parseValidMetar(metar);
      } catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }
   }

   private Weather parseValidMetar(String metar)
   {
      icaoCode = metar.substring(0, 4);
      altimeterValue = getAltimeterValue(metar);

      parseWind(metar);
      parseSkyCondition(metar);
      int temp = parseTemperature(metar);

      return new Weather(icaoCode, altimeterValue, windHeading, windKnots, temp, skyCondition, windGust);
   }

   private void parseSkyCondition(String metar)
   {
      if (isClearSkies(metar))
         skyCondition = "Clear skies";
      else if (metar.indexOf(" NCD ") != -1)
         skyCondition = "No cloud type determined";
      else if (!metar.matches(".*(FEW|SCT|BKN|OVC|VV).*"))
         skyCondition = "No cloud layers included";
      else
         parseSkyLayers(metar);
   }

   private void parseSkyLayers(String metar)
   {
      String[] tokens = metar.split("(?=(FEW|SCT|BKN|OVC|VV))|(?=(" + Validator.TEMPERATURE + "))");
      skyCondition = Stream.of(tokens)
              .skip(1)
              .map(String::trim)
              .filter(it -> it.matches(Validator.SKY_LAYER))
              .map(this::formatCloudLayer)
              .collect(Collectors.joining(", "));
   }

   private boolean isClearSkies(String metar)
   {
      return metar.indexOf(" CLR ") != -1 ||
              metar.indexOf(" CAVOK ") != -1 ||
              metar.indexOf(" SKC ") != -1 ||
              metar.indexOf(" NSC ") != -1;
   }

   private int parseTemperature(String metar)
   {
      return Integer.parseInt(
              matchedValue("(M?\\d\\d)(?=/M?\\d\\d)", metar, "temperature")
                      .replace('M', '-'));
   }

   private void parseWind(String metar)
   {
      String windCode = metar.split(before(Validator.SURFACE_WIND))[1].split("\\s")[0];
      Optional<String> stableWindMatch = tryMatchedValue("(\\d{5})(G\\d{2,})?(KT|MPS)", windCode);
      Optional<String> variableWindMatch = tryMatchedValue("VRB(\\d{2,}(G\\d{2,})?)(KT|MPS)", windCode);

      parseWindSpeed(windCode);

      if (stableWindMatch.isPresent())
         windHeading = Integer.valueOf(windCode.substring(0, 3));
      else if (variableWindMatch.isPresent())
         windHeading = -1;
      else throw new MissingValueException("wind");
   }

   private void parseWindSpeed(String windCode)
   {
      double multiplier = windCode.endsWith("MPS") ? 1.944 : 1;
      String[] tokens = windCode.substring(3).split("(G|KT|MPS)");
      windKnots = (int) (Integer.parseInt(tokens[0]) * multiplier);
      if (tokens.length > 1)
         windGust = (int) (Integer.parseInt(tokens[1]) * multiplier);
   }

   private int getAltimeterValue(String metar)
   {
      String altimeterCode = matchedValue(Validator.ALTIMETER, metar, "altimeter").substring(1);
      boolean isAmerican = metar.matches(".*A\\d{4}.*");
      int altimeterValue = Integer.valueOf(altimeterCode);

      if (isAmerican)
         altimeterValue *= 33.864 / 100;
      return altimeterValue;
   }

   private String before(String regex)
   {
      return "(?=" + regex + ")";
   }

   private Optional<String> tryMatchedValue(String regex, String value)
   {
      Matcher matcher = Pattern.compile(regex).matcher(value);

      if (matcher.find())
         return Optional.ofNullable(matcher.group());
      else
         return Optional.empty();
   }

   // https://stackoverflow.com/questions/3926451/how-to-match-but-not-capture-part-of-a-regex
   private String matchedValue(String regex, String value, String valueName)
   {
      Optional<String> match = tryMatchedValue(regex, value);
      return match.orElseThrow(() -> new MissingValueException(valueName));
   }

   private String formatCloudLayer(String cloudLayer)
   {
      String[] tokens = cloudLayer.split("((?<=[A-Z])(?=[\\d/])|(?<=[\\d/])(?=[A-Z]))");
      String output = "";

      // I really could've used a Map for denseness and less noise, so this is
      // only meant as a demonstration of knowledge.
      switch (tokens[0])
      {
         case "FEW":
            output += "Few at ";
            break;
         case "SCT":
            output += "Scattered at ";
            break;
         case "BKN":
            output += "Broken at ";
            break;
         case "OVC":
            output += "Overcast at ";
            break;
         case "VV":
            output += "Vertical Visibility ";
            break;
         default:
            output += tokens[0] + " at ";
      }

      output += tokens[1].equals("///") ?
              "below weather station level" :
              Integer.parseInt(tokens[1]) * 100 + " ft";

      if (tokens.length == 3)
      {
         switch (tokens[2])
         {
            case "CB":
               output += " (Cumulonimbus)";
               break;
            case "TCU":
               output += " (Towering cumulus)";
               break;
         }
      }

      return output;
   }
}
