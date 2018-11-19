package explorer.weather;

import explorer.HttpClient;

import java.io.IOException;

/**
 * @author Michael Peng
 */
public class Weather
{
   public static class Retriever
   {
      private static final String ENDPOINT = "http://tgftp.nws.noaa.gov/data/observations/metar/stations/%s.TXT";

      public static Weather readWeather(String icao) throws IOException
      {
         return new MetarParser().parse(readMetar(icao));
      }

      public static String readMetar(String icao) throws IOException
      {
         String response = HttpClient.read(String.format(ENDPOINT, icao.toUpperCase()));
         return response.substring(response.indexOf('\n') + 1);
      }
   }

   private final String icaoCode, skyCondition, presentWeather;
   /**
    * In the US, altimeter is expressed in inHg (29.92).
    * For example, this is encoded in the form "1013", since it is represented in hPa.
    */
   private final int altimeter;

   private final int windDirection, windSpeed, celsius;
   private final Integer windGust;

   public Weather(String icaoCode,
                  int altimeter,
                  int windDirection,
                  int windSpeed,
                  int celsius,
                  String skyCondition, String presentWeather)
   {
      this(icaoCode, altimeter, windDirection, windSpeed, celsius, skyCondition, presentWeather, null);
   }

   public Weather(String icaoCode,
                  int altimeter,
                  int windDirection,
                  int windSpeed,
                  int celsius,
                  String skyCondition,
                  String presentWeather, Integer windGust)
   {
      this.icaoCode = icaoCode;
      this.presentWeather = presentWeather;
      this.altimeter = altimeter;
      this.windDirection = windDirection;
      this.windSpeed = windSpeed;
      this.celsius = celsius;
      this.skyCondition = skyCondition;
      this.windGust = windGust;
   }

   public String getIcaoCode()
   {
      return icaoCode;
   }

   public int getAltimeter()
   {
      return altimeter;
   }

   public int getWindDirection()
   {
      return windDirection;
   }

   public int getWindSpeed()
   {
      return windSpeed;
   }

   public int getCelsius()
   {
      return celsius;
   }

   public Integer getWindGust()
   {
      return windGust;
   }

   public String getSkyCondition()
   {
      return skyCondition;
   }

   public String getPresentWeather()
   {
      return presentWeather;
   }

   @Override
   public String toString()
   {
      double fahrenheit = celsius * 9.0 / 5.0 + 32;
      return String.format("Weather for %s:\nAltimeter: %d hPa\nSky Condition: %s\nWind: %s at %d knots%s\nTemperature: %d °C (%.2f °F)\n%s",
              icaoCode.toUpperCase(),
              altimeter,
              skyCondition.trim().isEmpty() ? "Unknown" : skyCondition,
              windDirection < 0 ? "variable": windDirection + "°",
              windSpeed,
              windGust == null ? "" : " gusting " + windGust + " knots",
              celsius, fahrenheit,
              presentWeather.trim().isEmpty() ? "" : presentWeather + '\n');
   }
}
