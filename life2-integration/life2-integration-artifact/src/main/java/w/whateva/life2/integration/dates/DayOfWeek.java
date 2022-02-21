package w.whateva.life2.integration.dates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum DayOfWeek {
    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    private static final Map<String, DayOfWeek> DAYS_BY_NAME = new HashMap<>();

    static {
        int i = 1;
        for (DayOfWeek d : values()) {
            DAYS_BY_NAME.put(d.name().toLowerCase(), d);
            DAYS_BY_NAME.put(d.name().toLowerCase().substring(0, 3), d);
        }
    }

    public static DayOfWeek parse(String day) {
        return DAYS_BY_NAME.get(day);
    }

    public static Set<String> tokens() {
        return DAYS_BY_NAME.keySet();
    }
}
