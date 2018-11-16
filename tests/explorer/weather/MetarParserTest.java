package explorer.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MetarParserTest
{
   private static final String[] METARS = {
           "KBOS 101854Z 27016G25KT 10SM FEW035 SCT080 SCT250 09/M04 A2978 RMK AO2 PK WND 30029/1809 SLP084 T00891039",
           "KJFK 101851Z 27022G31KT 10SM BKN060 BKN250 07/M06 A3000 RMK AO2 PK WND 27032/1815 SLP157 T00721061",
           "KATL 101852Z 34011G18KT 10SM CLR 09/M02 A3023 RMK AO2 SLP240 T00941017",
           "KMIA 101853Z 10007KT 10SM SCT036 SCT070 28/22 A3000 RMK AO2 SLP159 T02830222",
           "KLAX 101853Z 13005KT 10SM FEW030 SCT050 24/M16 A2998 RMK AO2 SLP149 FU FEW030 FU SCT050 T02391161 $",
           "KSJC 101853Z 34004KT 1 3/4SM HZ FEW001 BKN026 OVC040 14/M04 A3005 RMK AO2 SLP176 FU FEW001 FU BKN026 FU OVC040 T01391039",
           "KPDX 101853Z 00000KT 10SM OVC015 07/03 A3038 RMK AO2 SLP287 T00720033",
           "ksea 101853Z 35009kt 8SM bkn008 OVC020 08/04 A3041 rmk AO2 SLP306 T00780044",
           "kmsp 101853Z 17008KT 10SM SCT015 OVC095 M07/M12 A3027 RMK AO2 SLP266 T10671117",
           "KIAD 101852Z 29016G25KT 10SM FEW060 06/M08 A3020 RMK AO2 PK WND 31033/1812 SLP227 T00561078",
           "KDCA 101852Z 31026G34KT 10SM FEW070 07/M08 A3020 RMK AO2 PK WND 32034/1848 SLP226 T00721083",
           "KLWM 101854Z 28015G36KT 10SM BKN060 OVC075 08/M06 A2978 RMK AO2 PK WND 28036/1851 SLP085 T00781061",
           "EGKK 101850Z 17006KT 140V210 9000 RA FEW026 SCT040 10/09 Q0995",
           "EDDF 101850Z 17008KT 9999 -RA BKN042 14/09 Q1007 NOSIG",
           "EGSS 141520Z AUTO 17008KT 9999 NCD 12/08 Q1022",
           "EDDM 101850Z 07004KT 030V100 6000 OVC004 08/07 Q1011 BECMG 4000 BR",
           "LFPG 101830Z 20015KT 9999 SCT013 BKN026 12/10 Q1002 NOSIG",
           "OMDB 101900Z 10005KT CAVOK 28/18 Q1014 NOSIG",
           "EGKK 141650Z 15004KT CAVOK 11/09 Q1021",
           "wSsS 141530Z VRB02KT 9999 FEW016 BKN300 27/25 Q1011 NOSIG",
           "ENSB 141520Z 19016KT 9999 -SHSN FEW007 SCT018 BKN040 M02/M07 Q1009 RMK WIND 1400FT 20022KT",
           "lfPG 141530Z 15006KT 110V170 CAVOK 14/09 Q1024 NOSIG",
           "EDDM 141520Z 08004KT 050V110 8000 OVC004 05/05 Q1029 NOSIG",
           "VHHH 101900Z 08008KT 9999 FEW018 23/20 Q1017 NOSIG",
           "ZBAA 101830Z 14001MPS CAVOK 07/M01 Q1025 NOSIG",
           "RJAA 101830Z VRB02KT 9999 FEW020 11/09 Q1019 NOSIG RMK 1CU020 A3011",
           "ZHHH 101800Z VRB01MPS 6000 OVC030 12/11 Q1021 NOSIG",
           "RCTP 101830Z 21003KT 9999 FEW020 20/19 Q1016 NOSIG RMK A3003",
           "EDDM 160020Z VRB01KT 0500 R08L/0550N R08R/0550V0800D FZFG NSC M02/M02 Q1028 NOSIG"
   };

   private static final String[] BAD_METARS = {
           "BOS 101830Z 20015KT 9999 SCT013 BKN026 12/10 Q1002 NOSIG",
           "EDDF 10189Z VRB02KT 9999 FEW020 11/09 Q1019 NOSIG RMK 1CU020 A3011",
           "KLWM 101854Z 2412G13KT 10SM FEW060 06/M08 A3020 RMK AO2 PK WND 31033/1812 SLP227 T00561078",
           "EGKK 102314Z 17006KTS 140V210 9000 RA FEW026 SCT040 10/09 Q0995",
           "OMDB 101900Z 10005KT CAV0K 28/18 Q1014 NOSIG",
           "RCTP 101830Z 21003KT 9999 FEW20 20/19 Q1016 NOSIG RMK A3003",
           "KDCA 101852Z 31026G34KT 10SM FEW070 07/M A3020 RMK AO2 PK WND 32034/1848 SLP226 T00721083",
           "KMIA 101853Z 10007KT 10SM SCT036 SCT070 28/22 A1000 RMK AO2 SLP159 T02830222"
   };
   private static MetarParser parser = new MetarParser();

   private static Stream<String> metarProvider()
   {
      return Stream.of(METARS);
   }

   private static Stream<String> badMetarProvider()
   {
      return Stream.of(BAD_METARS);
   }

   @BeforeEach
   void setUp()
   {
      parser = new MetarParser();
   }

   @ParameterizedTest
   @ValueSource(strings = {"", "  ", "\t  \n", "\n\t\n\r\t\n  "})
   void emptyInput(String whitespace)
   {
      assertThrows(MetarParser.MissingValueException.class,
              () -> parser.parse(whitespace));
   }

   @ParameterizedTest
   @MethodSource("metarProvider")
   void validateValidMetars(String metar)
   {
      assertTrue(MetarParser.Validator.isValid(metar));
   }

   @ParameterizedTest
   @MethodSource("badMetarProvider")
   void validateInvalidMetars(String badMetar)
   {
      assertThrows(MetarParser.InvalidFormatException.class,
              () -> parser.parse(badMetar));
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "KBOS 131454Z 15013KT 4SM -RA BR BKN010 OVC070 12/11 A2994 RMK AO2 SLP139 P0007 60026 T01220106 58036",
           "kbos 101854Z 27016G25KT 10SM FEW035 SCT080 SCT250 09/M04 A2978 RMK AO2 PK WND 30029/1809 SLP084 T00891039",
           "KbOs 101852Z 29016G25KT 10SM FEW060 06/M08 A3020 RMK AO2 PK WND 31033/1812 SLP227 T00561078"
   })
   void detectAirportCode(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals("KBOS", weather.getIcaoCode());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "KIAD 101852Z 29016G25KT 10SM FEW060 06/M08 A3020 RMK AO2 PK WND 31033/1812 SLP227 T00561078",
           "KEWR 131559Z 33012G22KT 10SM SCT006 BKN013 OVC021 08/07 A3020 RMK AO2 DZE56 SCT V BKN P0000 T00830072 $",
           "KMDW 131553Z 31010KT 10SM SCT032 M04/M13 A3020 RMK AO2 SLP318 T10441133",
           "KORD 131551Z 34012KT 10SM SCT034 M04/M13 A3020 RMK AO2 SLP311 T10441133 $",
           "KMIA 131553Z 14012KT 10SM BKN019 29/24 A3020 RMK AO2 SLP184 CB DSNT NW MOV N TCU DSNT SW-W T02890244"
   })
   void detectAmericanAltimeter(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(1022, weather.getAltimeter());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "EDDW 141520Z 19005KT 9999 FEW022 11/08 Q1022 NOSIG",
           "EGSS 141520Z AUTO 17008KT 9999 NCD 12/08 Q1022",
           "ZGGG 141530Z 16003MPS 9999 BKN050 25/18 Q1022 NOSIG",
           "YSSY 141500Z 31005KT CAVOK 20/17 Q1022 NOSIG"
   })
   void detectAltimeter(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(1022, weather.getAltimeter());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "WSSS 141530Z 12003KT 9999 FEW016 BKN300 27/25 Q1011 NOSIG",
           "ENSB 141520Z 12003KT 9999 -SHSN FEW007 SCT018 BKN040 M02/M07 Q1009 RMK WIND 1400FT 20022KT",
           "LFPG 141530Z 12003KT 110V170 CAVOK 14/09 Q1024 NOSIG",
           "EDDM 141520Z 12003KT 050V110 8000 OVC004 05/05 Q1029 NOSIG"
   })
   void detectStableWindInKnots(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(120, weather.getWindDirection());
      assertEquals(3, weather.getWindSpeed());
      assertNull(weather.getWindGust());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "ZSPD 141630Z 07004MPS 360V100 CAVOK 16/09 Q1022 NOSIG",
           "ZBAA 141630Z 07004MPS 2200 BR NSC 06/05 Q1024 NOSIG",
           "ZJHK 141600Z 07004MPS 080V140 9999 FEW005 BKN040 24/23 Q1014 NOSIG",
           "ZUUU 141600Z 07004MPS 5000 -RA BR SCT040 14/12 Q1020 NOSIG",
           "ZSYN 202300Z 07004MPS 9999 SCT040 18/14 Q1015 NOSIG"
   })
   void detectStableWindInMps(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(70, weather.getWindDirection());
      assertEquals((int)(4 * 1.944), weather.getWindSpeed());
      assertNull(weather.getWindGust());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "WSSS 141630Z 15005G14KT 9999 FEW016 BKN300 27/25 Q1010 NOSIG",
           "EGLL 141650Z AUTO 15005G14KT 100V210 9999 NCD 12/08 Q1021",
           "EGKK 141650Z 15005G14KT CAVOK 11/09 Q1021",
           "KDFW 141653Z 15005G14KT 10SM FEW250 03/M08 A3050 RMK AO2 SLP330 T00331078 $",
           "KPHX 141651Z 15005G14KT 10SM SCT200 BKN250 16/M09 A3034 RMK AO2 SLP268 T01561094"
   })
   void detectGustingWindInKnots(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(150, weather.getWindDirection());
      assertEquals(5, weather.getWindSpeed());
      assertEquals(14, weather.getWindGust().intValue());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "ZSPD 141630Z 07004G08MPS 360V100 CAVOK 16/09 Q1022 NOSIG",
           "ZBAA 141630Z 07004G08MPS 2200 BR NSC 06/05 Q1024 NOSIG",
           "ZJHK 141600Z 07004G08MPS 080V140 9999 FEW005 BKN040 24/23 Q1014 NOSIG",
           "ZUUU 141600Z 07004G08MPS 5000 -RA BR SCT040 14/12 Q1020 NOSIG",
           "ZSYN 202300Z 07004G08MPS 9999 SCT040 18/14 Q1015 NOSIG"
   })
   void detectGustingWindInMps(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(70, weather.getWindDirection());
      assertEquals((int)(4 * 1.944), weather.getWindSpeed());
      assertEquals((int)(8 * 1.944), weather.getWindGust().intValue());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "KBWI 141754Z VRB07G17KT 10SM SCT160 BKN200 BKN250 07/M07 A3045 RMK AO2 SLP311 T00721067 10083 20022 58003",
           "EDDK 141850Z VRB07KT 080V140 CAVOK 08/05 Q1027 NOSIG",
           "LEMD 141830Z VRB07KT 9999 FEW035 BKN045 BKN060 15/12 Q1020 NOSIG",
           "ESSA 141850Z VRB07KT 9999 OVC017 07/05 Q1023 R01L/29//95 R08/29//95 R01R/29//95 NOSIG",
           "KSAN 141851Z VRB07KT 10SM FEW160 FEW200 SCT250 24/M07 A3023 RMK AO2 SLP237 T02391072",
           "ZGGG 150500Z VRB04MPS 110V180 4500 HZ BKN026 25/19 Q1014 NOSIG",
   })
   void detectVariableWind(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(-1, weather.getWindDirection());
      assertEquals(7, weather.getWindSpeed());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "KPWM 150451Z 29008KT 10SM CLR 01/M01 A3057 RMK AO2 SNB18E39 SLP352 LAST P0000 T10721144 400221078",
           "KBOS 150454Z 31006KT 10SM 01/M14 A3059 RMK AO2 SLP356 T10391144 400561039",
           "KBDL 150451Z 35006KT 10SM OVC250 01/M16 A3058 RMK AO2 SLP356 T10331156 400281039",
           "KPVD 150451Z 36008KT 10SM SCT140 BKN250 01/M14 A3058 RMK AO2 SLP354 LAST T10281144 400441028",
           "ZBAA 150500Z 02004MPS 350V050 CAVOK 01/M04 Q1027 NOSIG"
   })
   void detectNonNegativeTemperature(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(1, weather.getCelsius());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "ENSB 150350Z 13008KT 9999 FEW050 SCT120 M06/M10 Q1003 RMK WIND 1400FT VRB02KT",
           "ESSA 150520Z 21007KT 6000 OVC002 M06/M08 Q1025 R01L/19//95 R08/15//95 R01R/19//95 NOSIG",
           "KLGA 150451Z 04008KT 10SM OVC240 M06/M10 A3059 RMK AO2 SLP359 T00061100 400610006",
           "KPHL 150454Z 05009KT 10SM OVC220 M06/M09 A3060 RMK AO2 SLP360 T00111094 400670011 $",
           "EDDF 150520Z 15005KT 9000 OVC005 m06/M15 Q1028 TEMPO OVC004"
   })
   void detectNegativeTemperature(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals(-6, weather.getCelsius());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "KBED 152316Z 02004KT 10SM CLR M01/M09 A3039 RMK AO2 T10111094",
           "KDVO 152315Z AUTO 14005KT 1 3/4SM HZ CLR 17/01 A3009 RMK AO2",
           "KSAN 152251Z 32007KT 10SM SKC 23/M02 A3001 RMK AO2 SLP162 T02281022",
           "PHNL 152253Z 04010KT 10SM CLR 28/18 A2996 RMK AO2 SLP144 VCSH NE-E T02830178",
           "KTAN 152252Z AUTO 00000KT 10SM SKC 00/M04 A3038 RMK AO2 SLP289 T00001044"
   })
   void detectAmericanClearSkies(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals("Clear skies", weather.getSkyCondition());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "EFHF 061550Z 04006KT 330V100 CAVOK M02/M11 Q1021",
           "EFOU 152320Z AUTO 24008KT 9999 CAVOK 07/06 Q1022",
           "EGHI 152250Z 12006KT 090V150 8000 CAVOK 12/11 Q1021"
   })
   void detectWmoClearSkies(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals("Clear skies", weather.getSkyCondition());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "KSAN 152251Z 32007KT 10SM FEW060 SCT100 BKN150 OVC170 23/M02 A3001 RMK AO2 SLP162 T02281022",
           "KDEN 152253Z 09008KT 10SM FEW060 SCT100 BKN150 OVC170 12/M07 A3012 RMK AO2 SLP174 T01221072",
           "KSLC 152254Z 29006KT 10SM FEW060 SCT100 BKN150 OVC170 08/M03 A3027 RMK AO2 SLP253 T00831028",
           "KATL 152252Z 30012KT 10SM FEW060 SCT100 BKN150 OVC170 02/M01 A3007 RMK AO2 SLP188 T00171006",
           "KMHT 152253Z 09003KT 10SM FEW060 SCT100 BKN150 OVC170 M02/M16 A3040 RMK AO2 SLP315 T10171156"
   })
   void detectLayeredClouds(String metar)
   {
      Weather weather = parser.parse(metar);
      assertEquals("Few at 6000 ft, Scattered at 10000 ft, Broken at 15000 ft, Overcast at 17000 ft", weather.getSkyCondition());
   }

   @ParameterizedTest
   @ValueSource(strings = {
           "KSEA 160024Z 18004KT 10SM BKN027 OVC120 12/07 A3025 RMK AO2 T01220072: Broken at 2700 ft, Overcast at 12000 ft",
           "KPWM 152351Z 00000KT 10SM OVC150 M01/M11 A3044 RMK AO2 SLP309 T10111106 11006 21022 56018: Overcast at 15000 ft",
           "EDDM 160020Z VRB01KT 0500 FZFG NSC M02/M02 Q1028 NOSIG: Clear skies",
           "LIRF 160020Z 04005KT CAVOK 11/08 Q1020 NOSIG: Clear skies",
           "LEMD 160000Z VRB01KT 9999 -DZ VV001TCU 13/12 Q1020 NOSIG: Vertical Visibility 100 ft (Towering cumulus)",
   })
   void detectCloudLayerVarieties(String metarExpect)
   {
      String[] tokens = metarExpect.split(": ");
      Weather weather = parser.parse(tokens[0]);
      assertEquals(tokens[1], weather.getSkyCondition());
   }
}