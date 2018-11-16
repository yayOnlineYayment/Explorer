package explorer;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * @author Michael Peng
 */
public class AirportDatabase
{
   private static final String CSV_ENDPOINT = "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat";

   private static String[] DB_CACHE = null;

   public static Stream<Airport> allAirports() throws IOException
   {
      return csvLines()
              .map(AirportDatabase::parseCsvLine);
   }

   private static Stream<String> csvLines() throws IOException
   {
      if (DB_CACHE == null)
         DB_CACHE = HttpClient.lines(CSV_ENDPOINT).toArray(String[]::new);

      return Stream.of(DB_CACHE);
   }

   private static Airport parseCsvLine(String line)
   {
      String[] tokens = line.split(",(?=\"|\\d|-|\\\\)");
      return new Airport(
              stripQuotes(tokens[1]),
              stripQuotes(tokens[4]).toUpperCase(),
              stripQuotes(tokens[5]).toUpperCase(),
              Double.parseDouble(tokens[6]),
              Double.parseDouble(tokens[7])
      );
   }

   private static String stripQuotes(String token)
   {
      return token.substring(1, token.length() - 1);
   }
}