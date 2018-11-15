package explorer.weather;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   REFERENCE MATERIALS
   http://meteocentre.com/doc/metar.html
   https://www.wunderground.com/metarFAQ.asp
   https://library.wmo.int/doc_num.php?explnum_id=3425
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
              IDENT_GROUP + OPTIONAL_REPORT_MODIFIER + SURFACE_WIND + VISIBILITY + PRESENT_WEATHER + SKY_CONDITION +
                      TEMPERATURE + ALTIMETER + TRENDS + REMARKS;

      public static boolean isValid(String metar)
      {
         return metar.toUpperCase().matches(METAR_REGEX);
      }
   }


   class MissingValueException extends RuntimeException
   {
      private MissingValueException(String missingValue)
      {
         super("Missing " + missingValue);
      }
   }

   class InvalidFormatException extends IllegalArgumentException
   {
      private InvalidFormatException()
      {
         super();
      }
   }

   private int windHeading = 0, windKnots = 0;
   private Integer windGust = null;
   private String icaoCode;
   private int altimeterValue;

   public Weather parse(String metar)
   {
      if (Objects.requireNonNull(metar).trim().length() == 0)
         throw new MissingValueException("METAR string");
      metar = metar.toUpperCase().trim();

      if (!Validator.isValid(metar))
         throw new InvalidFormatException();

      try
      {
         icaoCode = metar.substring(0, 4);
         altimeterValue = getAltimeterValue(metar);

         parseWind(metar);
         int temp = parseTemperature(metar);

         return new Weather(icaoCode, altimeterValue, windHeading, windKnots, temp, windGust);
      } catch (Exception e)
      {
         e.printStackTrace();
         throw e;
      }
   }

   private int parseTemperature(String metar)
   {
      return Integer.parseInt(
              matchedValue("(M?\\d\\d)(?=/M?\\d\\d)", metar, "temperature")
              .replace('M',  '-'));
   }

   private void parseWind(String metar)
   {
      String windCode = metar.split(before(Validator.SURFACE_WIND))[1].split("\\s")[0];
      Optional<String> stableWindMatch = tryMatchedValue("(\\d{5})(G\\d{2,})?(KT|MPS)", windCode);
      Optional<String> variableWindMatch = tryMatchedValue("VRB(\\d{2,}(G\\d{2,})?)(KT|MPS)", windCode);

      if (stableWindMatch.isPresent())
      {
         windHeading = Integer.valueOf(windCode.substring(0, 3));
         String[] tokens = windCode.substring(3).split("(G|KT|MPS)");
         windKnots = Integer.valueOf(tokens[0]);

         boolean isGusting = tokens.length > 1,
                 isMps = windCode.endsWith("MPS");

         if (isGusting)
            windGust = Integer.valueOf(tokens[1]);
         if (isMps)
            windKnots *= 1.944;
         if (isMps && isGusting)
            windGust = (int) (windGust * 1.944);
      } else if (variableWindMatch.isPresent())
      {
         String[] tokens = variableWindMatch.get().substring(3).split("(G|KT|MPS)");
         double multiplier = windCode.endsWith("MPS") ? 1.944 : 1;

         windHeading = -1;
         windKnots = (int) (Integer.parseInt(tokens[0]) * multiplier);
         if (tokens.length > 1)
            windGust = (int) (Integer.parseInt(tokens[1]) * multiplier);
      } else throw new MissingValueException("wind");
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


}
