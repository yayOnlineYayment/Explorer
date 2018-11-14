package explorer.rest;

import org.json.*;

import java.io.IOException;

public class Location
{
   private double latitude, longitude;
   private String name;
   private static final String APIKey = "AIzaSyAUcrM52N8388mbzHXBbkUrXddWEocXJr4";

   public Location(String input) throws IOException
   {
      JSONObject json = new JSONObject(RestClient.read("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key=" + APIKey + "&input=" + input.replace(" ", "+") + "&inputtype=textquery&locationbias=ipbias&fields=name,geometry"));
      JSONArray candidates = json.getJSONArray("candidates");

      if (candidates.length() == 0) {
         throw new IllegalArgumentException();
      }

      JSONObject candidate = candidates.getJSONObject(0);
      JSONObject location = candidate.getJSONObject("geometry").getJSONObject("location");

      this.latitude = location.getDouble("lat");
      this.longitude = location.getDouble("lng");
      this.name = candidate.getString("name");
   }

   public String[] nearestAirports() throws IOException
   {
      JSONObject json = new JSONObject(RestClient.read("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + APIKey + "&location=" + latitude + "," + longitude + "&radius=50000&type=airport"));
      JSONArray results = json.getJSONArray("results");

      if (results.length() == 0) {
         throw new IllegalArgumentException();
      }

      String airportsString = "";

      for (int i=0; i<results.length(); i++) {
         JSONObject result = results.getJSONObject(i);

         if (result.getString("name").toLowerCase().indexOf("heli") == -1) {
            //result.getString("name") + " " + result.getString("vicinity");
         }
      }

      //return airports;
   }

   public static void main(String[] args) {
      try {
         Location l = new Location("washington dc");
         System.out.println(l.latitude + "\n" + l.longitude + "\n" + l.name + "\n");

         for (int i=0; i<l.nearestAirports().length; i++)
         {
            System.out.println(l.nearestAirports()[i]);
         }
      }
      catch (Exception e) {

      }
   }
}
