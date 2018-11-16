package explorer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Comparator;

/**
 * @author David Grossman
 */
public class Location
{
   private static final String API_KEY = "AIzaSyAUcrM52N8388mbzHXBbkUrXddWEocXJr4";

   protected double latitude, longitude;
   protected String name;

   public Location(double latitude, double longitude, String name)
   {
      this.latitude = latitude;
      this.longitude = longitude;
      this.name = name;
   }

   public static Location fromText(String input) throws IOException
   {
      JSONObject json = new JSONObject(HttpClient.read("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key=" + API_KEY + "&input=" + input.replace(" ", "+") + "&inputtype=textquery&locationbias=ipbias&fields=name,geometry"));
      JSONArray candidates = json.getJSONArray("candidates");

      if (candidates.length() == 0)
         throw new IllegalArgumentException("Invalid location");

      JSONObject candidate = candidates.getJSONObject(0);
      JSONObject location = candidate.getJSONObject("geometry").getJSONObject("location");

      return new Location(
              location.getDouble("lat"),
              location.getDouble("lng"),
              candidate.getString("name"));
   }

   public double distanceTo(Location loc)
   {
      return Math.hypot(loc.latitude - latitude, loc.longitude - longitude);
   }

   public Airport[] nearestAirports(int count) throws IOException
   {
      return Airport.Database.allAirports()
              .sorted(Comparator.comparingDouble(this::distanceTo))
              .limit(count)
              .toArray(Airport[]::new);
   }

   public String getName()
   {
      return name;
   }
}
