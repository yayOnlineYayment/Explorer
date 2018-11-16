package explorer;

/**
 * @author Michael Peng
 */
public class Airport extends Location
{
   private final String iataCode, icaoCode;

   public Airport(String name, String iataCode, String icaoCode, double latitude, double longitude)
   {
      super(latitude, longitude, name);
      this.iataCode = iataCode.toUpperCase().trim();
      this.icaoCode = icaoCode.toUpperCase().trim();
   }

   @Override
   public String toString()
   {
      return String.format("%s (%s)", name.trim(), iataCode.toUpperCase());
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
