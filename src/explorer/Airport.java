package explorer;

/**
 * @author Michael Peng
 */
public class Airport
{
   private final String name, iataCode, icaoCode;
   private final double latitude, longitude;

   public Airport(String name, String iataCode, String icaoCode, double latitude, double longitude)
   {
      this.name = name;
      this.iataCode = iataCode.toUpperCase().trim();
      this.icaoCode = icaoCode.toUpperCase().trim();
      this.latitude = latitude;
      this.longitude = longitude;
   }

   @Override
   public String toString()
   {
      return String.format("%s (%s)", name.trim(), iataCode.toUpperCase());
   }

   public String getName()
   {
      return name;
   }

   public String getIataCode()
   {
      return iataCode;
   }

   public String getIcaoCode()
   {
      return icaoCode;
   }

   public double getLatitude()
   {
      return latitude;
   }

   public double getLongitude()
   {
      return longitude;
   }
}
