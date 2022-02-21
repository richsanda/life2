package w.whateva.life2.integration.dates;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class Token {
    private final Month month;
    private final Season season;
    private final DayOfWeek dayofWeek;
    private final DateModifier modifier;
    private final Integer day;
    private final Integer numericMonth;
    private final Integer year;
    private final boolean comma;
    private final String dash;
    private final LocalDate date;
    private final LocalDate endDate;
    private final boolean range;
    private final List<Token> subTokens;

    public LocalDate date() {
        return date;
    }

    public LocalDate endDate() {
        return null != endDate ? endDate : date;
    }

    public Integer durationInDays() {
        if (null == date) return null;
        if (null == endDate) return 0;
        return (int) Duration.between(date.atStartOfDay(), endDate.atStartOfDay()).toDays();
    }
}
