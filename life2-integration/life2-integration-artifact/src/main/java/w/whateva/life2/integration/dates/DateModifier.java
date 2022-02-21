package w.whateva.life2.integration.dates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum DateModifier {
    FROM, BETWEEN, AFTER, BEGINNING, BEGIN, START, STARTING, BEFORE, ENDING, END, AND,
    TO, THROUGH, THRU, DASH, UNTIL,
    EARLY, MID, LATE, SEMESTER, VACATION, VACA;

    private static final Map<String, DateModifier> DATE_MODIFIERS_BY_NAME = new HashMap<>();
    private static final Set<DateModifier> RANGE_MODIFIERS = Set.of(TO, THROUGH, THRU, DASH, UNTIL);

    static {
        int i = 1;
        for (DateModifier m : values()) {
            DATE_MODIFIERS_BY_NAME.put(m.name().toLowerCase(), m);
            if (m.name().length() > 3) {
                DATE_MODIFIERS_BY_NAME.put(m.name().toLowerCase().substring(0, 3), m);
            }
        }
    }

    public static DateModifier parse(String mod) {
        return DATE_MODIFIERS_BY_NAME.get(mod);
    }

    public static Set<String> tokens() {
        return DATE_MODIFIERS_BY_NAME.keySet();
    }

    public boolean isRange() {
        return RANGE_MODIFIERS.contains(this);
    }
}
