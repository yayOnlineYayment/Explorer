package explorer.weather;

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
           "EDDM 101850Z 07004KT 030V100 6000 OVC004 08/07 Q1011 BECMG 4000 BR",
           "LFPG 101830Z 20015KT 9999 SCT013 BKN026 12/10 Q1002 NOSIG",
           "OMDB 101900Z 10005KT CAVOK 28/18 Q1014 NOSIG",
           "VHHH 101900Z 08008KT 9999 FEW018 23/20 Q1017 NOSIG",
           "ZBAA 101830Z 14001MPS CAVOK 07/M01 Q1025 NOSIG",
           "RJAA 101830Z VRB02KT 9999 FEW020 11/09 Q1019 NOSIG RMK 1CU020 A3011",
           "ZHHH 101800Z VRB01MPS 6000 OVC030 12/11 Q1021 NOSIG",
           "RCTP 101830Z 21003KT 9999 FEW020 20/19 Q1016 NOSIG RMK A3003"
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

   private static Stream<String> metarProvider()
   {
      return Stream.of(METARS);
   }

   private static Stream<String> badMetarProvider()
   {
      return Stream.of(BAD_METARS);
   }

   @ParameterizedTest
   @ValueSource(strings = {"", "  ", "\t  \n", "\n\t\n\r\t\n  "})
   void emptyInput(String whitespace)
   {
      assertThrows(MetarParser.MissingValueException.class,
              () -> new MetarParser().parse(whitespace));
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
      assertFalse(MetarParser.Validator.isValid(badMetar));
   }


}