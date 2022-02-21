package w.whateva.life2.integration.dates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Integer.parseInt;

public enum Month {
    JANUARY,
    FEBRUARY,
    MARCH,
    APRIL,
    MAY,
    JUNE,
    JULY,
    AUGUST,
    SEPTEMBER,
    OCTOBER,
    NOVEMBER,
    DECEMBER;

    private static final Map<String, Month> MONTHS_BY_NAME = new HashMap<>();
    private static final Map<Integer, Month> MONTHS_BY_NUMBER = new HashMap<>();
    private static final Map<Month, Integer> NUMBERS_BY_MONTH = new HashMap<>();

    static {
        int i = 1;
        for (Month m : values()) {
            MONTHS_BY_NAME.put(m.name().toLowerCase(), m);
            MONTHS_BY_NAME.put(m.name().toLowerCase().substring(0, 3), m);
            MONTHS_BY_NUMBER.put(i, m);
            NUMBERS_BY_MONTH.put(m, i++);
        }
    }

    public static Month parse(String month) {
        Month result = MONTHS_BY_NAME.get(month);
        if (null != result) return result;
        try {
            return parse(parseInt(month));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Month parse(Integer i) {
        return MONTHS_BY_NUMBER.get(i);
    }

    public int toInt() {
        return NUMBERS_BY_MONTH.get(this);
    }

    public static int parse(Month m) {
        return NUMBERS_BY_MONTH.get(m);
    }

    public static Integer monthNumber(String s) {
        Month m = parse(s);
        if (null == m) return null;
        return parse(m);
    }

    public static Set<String> tokens() {
        return MONTHS_BY_NAME.keySet();
    }
}
