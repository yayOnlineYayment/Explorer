package explorer;

import explorer.rest.Location;
import explorer.weather.MetarParser;
import explorer.weather.Weather;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main
{
   private static Scanner stdin = new Scanner(System.in);

   public static void main(String[] args)
   {
      String target = "";

      while (true)
      {
         try
         {
            target = readLine("Please enter target location: ");
            Location location = Location.fromText(target);
            System.out.println("Processed as " + location.getName());
            int airportCount = readInt("How many airports to show? (up to 50): ", 1, 51);
            Airport[] nearestAirports = location.nearestAirports(airportCount);

            System.out.println("Nearest Airports:");
            for (int i = 0; i < nearestAirports.length; ++i)
               System.out.printf("%d. %s\n", i + 1, nearestAirports[i]);

            System.out.println();
            int selection = readInt("Select an airport (0 to exit): ", 0, airportCount + 1);

            if (selection == 0)
               break;

            Airport currentAirport = nearestAirports[selection - 1];
            System.out.println("You have selected: " + currentAirport);
            System.out.println(Weather.Retriever.readWeather(currentAirport.getIcaoCode()));

         } catch (FileNotFoundException metarNotFound)
         {
            System.err.println("The METAR for this aerodrome is unavailable");
         } catch (IOException genericIO)
         {
            if (genericIO.getMessage() == null)
            {
               System.err.println("An unspecified I/O error occurred. Stack trace follows");
               genericIO.printStackTrace();
            } else
            {
               System.err.printf("An I/O error occurred: %s\n", genericIO.getMessage());
            }
         } catch (MetarParser.MissingValueException metarMissingValue)
         {
            System.err.println("Received METAR is missing value: " + metarMissingValue.getMessage());
         } catch (MetarParser.InvalidFormatException badMetarFormat)
         {
            System.err.println("Received METAR is either invalid or unsupported: " + badMetarFormat.getMessage());
         } catch (IllegalArgumentException badLocation)
         {
            System.err.printf("'%s' is not a valid location\n", target);
         }
      }
   }

   private static String readLine(String prompt)
   {
      stdin = new Scanner(System.in);
      String input;

      do
      {
         System.out.print(prompt);
         input = stdin.nextLine();

      } while (input == null || input.trim().length() == 0);

      return input;
   }

   private static int readInt(String prompt, int lowerInclusive, int upperExclusive)
   {
      int input = lowerInclusive - 1;

      do
      {
         System.out.print(prompt);
         try
         {
            input = stdin.nextInt();
         } catch (NumberFormatException e)
         {
            System.err.printf("I see what you're trying to do. I require an integer in the range [%d, %d). Try again.",
                    lowerInclusive, upperExclusive);
         }
      } while (input < lowerInclusive || input >= upperExclusive);

      return input;
   }
}
