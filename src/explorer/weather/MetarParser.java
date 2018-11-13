package explorer.weather;

import explorer.Airport;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

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
      private static final String
              IDENT_GROUP = "^[A-Z]{4}\\s\\d{6}Z\\s",
              SURFACE_WIND = "([0-3]\\d\\d|VRB)\\d{2}(G\\d\\d)?(KT|MPS|KPH)(\\s\\d{3}V\\d{3})?\\s",
              OPTIONAL_REPORT_MODIFIER = "(AUTO\\s)?",
              VISIBILITY = "(\\d{4}|CAVOK|M?(\\d|\\s|/)+?SM)\\s",
              WEATHER_PHENOMENON_GROUP = "(-|\\+|VC)?(MI|BC|PR|DR|BL|SH|TS|FZ){0,4}(DZ|RA|SN|SG|PL|GR|GS|UP){0,4}(BR|FG|FU|VA|DU|SA|HZ){0,4}(PO|SQ|FC|SS|DS){0,4}",
              PRESENT_WEATHER = "(" + WEATHER_PHENOMENON_GROUP + "\\s){0,3}",
              SKY_LAYER = "(FEW|SCT|BKN|OVC|VV)(\\d{3}|///)(CB|TCU)?",
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

   public Weather parse(String metar)
   {
      if (Objects.requireNonNull(metar).trim().length() == 0)
         throw new MissingValueException("METAR string");
      metar = metar.toUpperCase().trim();

      if (!Validator.isValid(metar))
         throw new InvalidFormatException();

      return null;
   }


}
