package explorer;

import explorer.rest.Location;

import java.util.Scanner;
import java.util.Optional;

public class Main
{
   public static void main(String[] args)
   {
      System.out.println("Please enter current location: ");

      Scanner scanner = new Scanner(System.in);
      String input = scanner.next();

      try {
         Location location = new Location(input);
         String[] nearestAirports = location.nearestAirports();
      }
      catch (Exception e) {
         System.out.println("The location you entered was not recognized.");
      }

      System.out.println("Nearest Airports:");

      for (int i=1; i<=5; i++) {
         System.out.println(i + ": Airport");
      }

      System.out.println("Select an airport (0 to exit): ");

      int airport;

      do
      {
         input = new Scanner(System.in);
         airport = input.nextInt();
      } while (airport < 0 || airport > 5);

      System.out.println("Name: ");
      System.out.println("IATA Code: ");
      System.out.println("ICAO Code: ");
      System.out.println("Latitude: ");
      System.out.println("Distance from current location: ");
      System.out.println("Weather: ");
   }
}
