package explorer.weather;

public class Weather
{
   private final String icaoCode;
   /**
    * In the US, altimeter is expressed in inHg (29.92).
    * For example, this is encoded in the form "1013", since it is represented in hPa.
    */
   private final int altimeter;

   private final int windDirection, windSpeed, celsius;
   private final Integer windGust;

   public Weather(String icaoCode, int altimeter, int windDirection, int windSpeed, int celsius)
   {
      this(icaoCode, altimeter, windDirection, windSpeed, celsius, null);
   }

   public Weather(String icaoCode,
                  int altimeter,
                  int windDirection,
                  int windSpeed,
                  int celsius, Integer windGust)
   {
      this.icaoCode = icaoCode;
      this.altimeter = altimeter;
      this.windDirection = windDirection;
      this.windSpeed = windSpeed;
      this.celsius = celsius;
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

   @Override
   public String toString()
   {
      double fahrenheit = celsius * 9.0 / 5.0 + 32;
      return String.format("Weather for %s:\nAltimeter: %d hPa\nWind: %s at %d knots%s\nTemperature: %d C (%.2f F)\n",
              icaoCode.toUpperCase(),
              altimeter,
              windDirection < 0 ? "variable": windDirection + "°",
              windSpeed,
              windGust == null ? "" : " gusting " + windGust + " knots",
              celsius, fahrenheit);
   }
}
