package explorer.rest;

import java.io.*;
import java.net.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David Grossman
 */
public class RestClient
{
   public static String read(String urlString) throws IOException
   {
      return lines(urlString).collect(Collectors.joining("\n"));
   }

   public static Stream<String> lines(String urlString) throws IOException
   {
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection();
      return new BufferedReader(
              new InputStreamReader(
                      connection.getInputStream()))
              .lines();
   }
}
