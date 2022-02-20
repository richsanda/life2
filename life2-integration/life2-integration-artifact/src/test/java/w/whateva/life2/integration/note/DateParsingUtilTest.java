package w.whateva.life2.integration.note;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static w.whateva.life2.integration.note.DateParsingUtil.*;

public class DateParsingUtilTest {

    private static final String dateWord = "4jan05 2020-01-05";

    @Test
    public void months() {

    }

    @Test
    public void parseIntWithLeadZero() {
        System.out.println(DateParsingUtil.Month.parse(2));
        System.out.println(DateParsingUtil.Month.parse("02"));
        System.out.println(DateParsingUtil.Month.parse(13));
    }

    @Test
    public void dateTokens() {
        String[] dateStrs = new String[]{
                "early jun 23 95",
                "summer 95",
                "8.4-11.95",
                "8.4-9.26.99",
                "aug 23 to sep 29, 1995",
                "early feb 1999 to aug 2000"
        };
        Arrays.stream(dateStrs).forEach(d -> {
            List<DateParsingUtil.Token> tokens = parseDate(d);
            System.out.println("\n\"" + d + "\": " + tokens.size() + " tokens");
            System.out.println(firstMatch(tokens, isMonth, isDay, isYear)
                    + " " + firstMatch(tokens, isSeason, isYear)
                    + " " + firstMatch(tokens, isMonth, isDay, isRange, isDay, isYear)
                    + " " + firstMatch(tokens, isMonth, isDay, isRange, isMonth, isDay, isComma, isYear)
                    + " " + firstMatch(tokens, isRange)
            );
        });
    }

    @Test
    public void doTheTokensReduce() {
        String[] dateStrs = new String[]{
                "early jun 23 95",
                "summer 95",
                "8.4-11.95",
                "8.4-9.26.99",
                "aug 23 to sep 29, 1995",
                "jan 2019",
                "early summer 1999",
                "early feb 1999 to aug 2000",
                "12.3.91 -> 6.30.94",
                "mid 99",
                "mid 98 - fall 2000"
        };
        Arrays.stream(dateStrs).forEach(d -> {
            List<Token> tokens = reduceTokens(parseDate(d));
            Token first = tokens.get(0);
            System.out.println(d + ": " + first.getDate() + " - " + first.getEndDate());
        });
    }

    @Test
    public void testOneReduce() {
        String dateStr = "early feb 1999 to aug 2000";
        List<Token> tokens = reduceTokens(parseDate(dateStr));
        Token first = tokens.get(0);
        System.out.println(dateStr + ": " + first.getDate() + " - " + first.getEndDate());
    }
}
