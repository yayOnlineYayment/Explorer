package explorer.weather;

import explorer.Airport;

import java.util.Optional;

public class Weather
{
   private final String icaoCode;
   /**
    * In the US, altimeter is expressed in inHg (29.92).
    * For example, this is encoded in the form "2992".
    */
   private final int altimeter;

   private final int windDirection, windSpeed, fahrenheit;
   private final Integer windGust;

   public Weather(String icaoCode, int altimeter, int windDirection, int windSpeed, int fahrenheit)
   {
      this(icaoCode, altimeter, windDirection, windSpeed, fahrenheit, null);
   }

   public Weather(String icaoCode,
                   int altimeter,
                   int windDirection,
                   int windSpeed,
                   int fahrenheit, Integer windGust)
   {
      this.icaoCode = icaoCode;
      this.altimeter = altimeter;
      this.windDirection = windDirection;
      this.windSpeed = windSpeed;
      this.fahrenheit = fahrenheit;
      this.windGust = windGust;
   }
}
