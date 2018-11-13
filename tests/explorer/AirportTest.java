package explorer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirportTest
{
   @Test
   void testToString()
   {
      Airport bos = new Airport(
              "Boston Logan Intl",
              "bos",
              "kbos   ",
              4.25,
              3.15);
      assertEquals("Boston Logan Intl (BOS)", bos.toString());
   }
}