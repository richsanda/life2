package w.whateva.life2.integration.dates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum Season {
    WINTER,
    SPRING,
    SUMMER,
    FALL,
    LENT,
    HALLOWEEN,
    THANKSGIVING,
    TG,
    CHRISTMAS,
    CMAS,
    XMAS,
    HOLIDAYS;

    private static final Map<String, Season> SEASONS_BY_NAME = new HashMap<>();

    static {
        int i = 1;
        for (Season s : values()) {
            SEASONS_BY_NAME.put(s.name().toLowerCase(), s);
            if (s.name().length() > 3) {
                SEASONS_BY_NAME.put(s.name().toLowerCase().substring(0, 3), s);
            }
        }
    }

    public static Season parse(String season) {
        return SEASONS_BY_NAME.get(season);
    }

    public static Set<String> tokens() {
        return SEASONS_BY_NAME.keySet();
    }
}
