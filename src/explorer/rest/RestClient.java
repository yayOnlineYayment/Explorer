package explorer.rest;

import java.io.*;
import java.net.*;
import java.util.stream.Collectors;

public class RestClient
{
   public static String read(String urlString) throws IOException
   {
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection();
      return new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining());
   }
}
