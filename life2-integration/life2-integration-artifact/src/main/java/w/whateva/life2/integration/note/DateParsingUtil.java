package w.whateva.life2.integration.note;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;

public class DateParsingUtil {

    private static LocalDate firstOfWinter(int year) {
        return LocalDate.of(year - 1, 12, 21);
    }

    private static LocalDate firstOfSpring(int year) {
        return LocalDate.of(year, 3, 21);
    }

    private static LocalDate firstOfSummer(int year) {
        return LocalDate.of(year , 6, 21);
    }

    private static LocalDate firstOfFall(int year) {
        return LocalDate.of(year, 9, 21);
    }

    private static LocalDate lastOfMonth(int year, int month) {
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        return firstOfMonth.withDayOfMonth(
                firstOfMonth.getMonth().length(firstOfMonth.isLeapYear()));
    }

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
            for (Month m: values()) {
                MONTHS_BY_NAME.put(m.name().toLowerCase(), m);
                MONTHS_BY_NAME.put(m.name().toLowerCase().substring(0, 3), m);
                MONTHS_BY_NUMBER.put(i, m);
                NUMBERS_BY_MONTH.put(m, i++);
            }
        }

//        private final MonthDay start;
//        private final MonthDay end;
//
//        Month(MonthDay start, MonthDay end) {
//            this.start = start;
//            this.end = end;
//        }

        public boolean isMonthName(String month) {
            return MONTHS_BY_NAME.containsKey(month);
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
            for (Season s: values()) {
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
            for (DayOfWeek d: values()) {
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

    public enum DateModifier {
        FROM, BETWEEN, AFTER, BEGINNING, BEGIN, START, STARTING, BEFORE, ENDING, END, AND,
        TO, THROUGH, THRU, DASH, UNTIL,
        EARLY, MID, LATE, SEMESTER, VACATION, VACA;

        private static final Map<String, DateModifier> DATE_MODIFIERS_BY_NAME = new HashMap<>();
        private static final Set<DateModifier> RANGE_MODIFIERS = Set.of(TO, THROUGH, THRU, DASH, UNTIL);

        static {
            int i = 1;
            for (DateModifier m: values()) {
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

    private static final String monthsRe = "(" + String.join("|", Month.tokens()) + ")";
    private static final Pattern monthsPattern = Pattern.compile(monthsRe);

    private static final String threePartDateToken = "([0-9]{1,2})[./]([0-9]{1,2})[./]([0-9]{2,4})";
    private static final String threePartDateToken2 = "([0-9]{1,2})(" + String.join("|", Month.tokens()) + ")([0-9]{2,4})";
    private static final String numericToken = "[0-9]{1,8}";
    private static final String commaToken = ",";
    private static final String dashToken = "-|->|--";

    private static final String dateRe =
            "(?<=[^(a-z)]|^)(" +
                    String.join("|", Month.tokens()) + "|" +
                    String.join("|", Season.tokens()) + "|" +
                    String.join("|", DayOfWeek.tokens()) + "|" +
                    String.join("|", DateModifier.tokens()) +
                    ")(?=[^(a-z)]|$)";
    private static final Pattern datePattern = Pattern.compile("(" + dateRe + "|" +
            threePartDateToken + "|" +
            threePartDateToken2 + "|" +
            numericToken  + "|" +
            commaToken  + "|" +
            dashToken
            + ")");

    private static final Pattern distillAlphaNumeric = Pattern.compile("(?=\\d)(?<=[a-zA-Z])|(?=[a-zA-Z])(?<=\\d)");

    private static boolean isDate(String s) {
        return threePartDateToken.matches(s) || threePartDateToken2.matches(s);
    }

    private static boolean isMonthName(String s) {
        return Month.tokens().contains(s);
    }

    private static Integer month(String s) {
        Month month = Month.parse(s);
        if (null != month) return month.toInt();
        try {
            int i = parseInt(s);
            if (i >= 1 && i <= 12) {
                return i;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    private static boolean isMonth(String s) {
        return null != month(s);
    }

    private static Integer dayOfMonth(String s) {
        try {
            int i = parseInt(s);
            if (i >= 1 && i <= 31) {
                return i;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    private static Integer year(String s) {
        if (s.length() != 2 && s.length() != 4) return null;
        try {
            int i = parseInt(s);
            return i > 99 ? i : (i < (LocalDateTime.now().getYear() + 10) % 100) ? 2000 + i : 1900 + i;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String dash(String s) {
        Matcher m = Pattern.compile(dashToken).matcher(s);
        if (m.find()) {
            return m.group(0);
        }
        return null;
    }

    private static boolean comma(String s) {
        Matcher m = Pattern.compile(commaToken).matcher(s);
        return m.find();
    }

    private static LocalDate date(String s) {
        Matcher m = Pattern.compile(threePartDateToken).matcher(s);
        if (m.find()) {
            return LocalDate.of(year(m.group(3)), parseInt(m.group(1)), parseInt(m.group(2)));
        }
        Matcher m2 = Pattern.compile(threePartDateToken2).matcher(s);
        if (m2.find()) {
            return LocalDate.of(parseInt(m2.group(3)), Month.monthNumber(m2.group(2)), parseInt(m.group(1)));
        }
        return null;
    }

    public static List<Token> parseDate(String input) {
        Matcher m = datePattern.matcher(input);
        List<Token> result = new ArrayList<>();
        while (m.find()) {
            result.add(parseString(m.group(0)));
        }
        return result;
    }

    private static Token parseString(String input) {
        return Token.builder()
                .month(Month.parse(input))
                .season(Season.parse(input))
                .dayofWeek(DayOfWeek.parse(input))
                .modifier(DateModifier.parse(input))
                .day(dayOfMonth(input))
                .numericMonth(month(input))
                .year(year(input))
                .date(date(input))
                .dash(dash(input))
                .comma(comma(input))
                .build();
    }

    @Builder
    @Getter
    public static class Token {
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
            return (int)Duration.between(date.atStartOfDay(), endDate.atStartOfDay()).toDays();
        }
    }

    @Builder
    @Getter
    public static class DateExpression {

        private final List<Token> tokens;
        private final LocalDate date;
    }

    @SafeVarargs
    public static int firstMatch(List<Token> tokens, Predicate<Token>... predicates) {
        int i = 0;
        int tokenLen = tokens.size();
        int predLen = predicates.length;
        while (i + predLen <= tokenLen) {
            if (matches(i, tokens, predicates)) return i;
            i++;
        }
        return -1;
    }

    @SafeVarargs
    public static boolean matches(int startIndex, List<Token> tokens, Predicate<Token>... predicates) {
        return matches(tokens.subList(startIndex, startIndex + predicates.length), predicates);
    }

    @SafeVarargs
    public static boolean matches(List<Token> tokens, Predicate<Token>... predicates) {
        Iterator<Predicate<Token>> predicateIterator = Arrays.stream(predicates).iterator();
        return predicates.length ==
                tokens.stream()
                        .takeWhile(e -> predicateIterator.hasNext() && predicateIterator.next().test(e))
                        .count();
    }

    public static Predicate<Token> isDay = (t) -> null != t.getDay();
    public static Predicate<Token> isMonth = (t) -> null != t.getMonth() || null != t.getNumericMonth();
    public static Predicate<Token> isYear = (t) -> null != t.getYear();
    public static Predicate<Token> isSeason = (t) -> null != t.getSeason();
    public static Predicate<Token> isDateModifier = (t) -> null != t.getModifier() && !t.getModifier().isRange();
    public static Predicate<Token> isDayOfWeek = (t) -> null != t.getDayofWeek();
    public static Predicate<Token> isComma = Token::isComma;
    public static Predicate<Token> isDash = (t) -> null != t.getDash();
    public static Predicate<Token> isDate = (t) -> null != t.getDate();
    public static Predicate<Token> isDateExpr = (t) -> null != t.getDate() || !CollectionUtils.isEmpty(t.getSubTokens());
    public static Predicate<Token> isRange = (t) -> null != t.getDash() || (null != t.getModifier() && t.getModifier().isRange());

    @Getter
    @Builder
    public static class TokenReplacer {

        private final Predicate<Token>[] predicates;
        private final Function<List<Token>, Token> replacer;

        List<Token> replace(List<Token> tokens) {
            int firstMatch = firstMatch(tokens, predicates);
            if (firstMatch < 0) return null;
            List<Token> result = new ArrayList<>();
            result.addAll(tokens.subList(0, firstMatch));
            result.add(replacer.apply(tokens.subList(firstMatch, firstMatch + predicates.length)));
            result.addAll(tokens.subList(firstMatch + predicates.length, tokens.size()));
            return Collections.unmodifiableList(result);
        }
    }

    @SafeVarargs
    private static Predicate<Token>[] predicates(Predicate<Token>... predicates) {
        return predicates;
    }

    private static LocalDate buildDate(Token yearToken, Token monthToken, Token dayToken) {
        return LocalDate.of(year(yearToken), monthToken.getNumericMonth(), dayToken.getDay());
    }

    private static int year(Token token) {
        return null != token.getYear() ? token.getYear() : null != token.getDate() ? token.getDate().getYear() : 0;
    }

    public final static TokenReplacer standardDateReplacer = TokenReplacer.builder()
            .predicates(predicates(isMonth, isDay, isYear))
            .replacer((tokens) -> Token.builder()
                    .date(buildDate(tokens.get(2), tokens.get(0), tokens.get(1)))
                    .build())
            .build();

    public final static TokenReplacer standardDateReplacerWithComma = TokenReplacer.builder()
            .predicates(predicates(isMonth, isDay, isComma, isYear))
            .replacer((tokens) -> Token.builder()
                    .date(buildDate(tokens.get(3), tokens.get(0), tokens.get(1)))
                    .build())
            .build();

    public final static TokenReplacer monthAndDayRangeReplacer = TokenReplacer.builder()
            .predicates(predicates(isMonth, isDay, isRange, isDate))
            .replacer((tokens) ->
                    Token.builder()
//                            .subTokens(List.of(
//                                    Token.builder()
//                                            .date(buildDate(tokens.get(3), tokens.get(0), tokens.get(1)))
//                                            .build(),
//                                    tokens.get(3)))
                            .date(buildDate(tokens.get(3), tokens.get(0), tokens.get(1)))
                            .endDate(tokens.get(3).getDate())
                            .build())
            .build();

    public final static TokenReplacer dayRangeReplacer = TokenReplacer.builder()
            .predicates(predicates(isMonth, isDay, isRange, isDay, isYear))
            .replacer((tokens) ->
                    Token.builder()
//                            .subTokens(List.of(
//                                    Token.builder()
//                                            .date(buildDate(tokens.get(4), tokens.get(0), tokens.get(1)))
//                                            .build(),
//                                    Token.builder()
//                                            .date(buildDate(tokens.get(4), tokens.get(0), tokens.get(3)))
//                                            .build()))
                            .date(buildDate(tokens.get(4), tokens.get(0), tokens.get(1)))
                            .endDate(buildDate(tokens.get(4), tokens.get(0), tokens.get(3)))
                            .build())
            .build();

    public final static TokenReplacer modifierReplacer = TokenReplacer.builder()
            .predicates(predicates(isDateModifier, isDateExpr))
            .replacer((tokens) -> {

                DateModifier modifier = tokens.get(0).getModifier();
                LocalDate date = tokens.get(1).date();
                LocalDate endDate = tokens.get(1).endDate();
                int duration = tokens.get(1).durationInDays();

                switch(modifier) {
                    case EARLY:
                        return Token.builder()
                                .date(date)
                                .endDate(date.plusDays(duration / 2))
                                .build();
                    case LATE:
                        return Token.builder()
                                .date(endDate.minusDays(duration / 2))
                                .endDate(endDate)
                                .build();
                    case MID:
                        return Token.builder()
                                .date(date.plusDays(duration / 4))
                                .endDate(endDate.minusDays(duration / 4))
                                .build();
                }

                return Token.builder()
                    .date(tokens.get(1).getDate())
                    .endDate(tokens.get(1).getEndDate())
                    .build();
            })
            .build();

    public final static TokenReplacer seasonReplacer = TokenReplacer.builder()
            .predicates(predicates(isSeason, isYear))
            .replacer((tokens) -> {
                Season season = tokens.get(0).getSeason();
                int year = tokens.get(1).getYear();
                switch (season) {
                    case WINTER:
                        return Token.builder()
                                .date(firstOfWinter(year))
                                .endDate(firstOfSpring(year))
                                .build();
                    case SPRING:
                        return Token.builder()
                                .date(firstOfSpring(year))
                                .endDate(firstOfSummer(year))
                                .build();
                    case SUMMER:
                        return Token.builder()
                                .date(firstOfSummer(year))
                                .endDate(firstOfFall(year))
                                .build();
                    case FALL:
                        return Token.builder()
                                .date(firstOfFall(year))
                                .endDate(firstOfWinter(year + 1))
                                .build();
                    case LENT:
                        return Token.builder()
                                .date(LocalDate.of(year, 2, 15))
                                .endDate(LocalDate.of(year, 4, 15))
                                .build();
                    case HALLOWEEN:
                        return Token.builder()
                                .date(LocalDate.of(year, 10, 15))
                                .endDate(LocalDate.of(year, 10, 31))
                                .build();
                    case TG: case THANKSGIVING:
                        return Token.builder()
                                .date(LocalDate.of(year, 11, 15))
                                .endDate(LocalDate.of(year, 11, 30))
                                .build();
                    case CHRISTMAS: case XMAS: case CMAS:
                        return Token.builder()
                                .date(LocalDate.of(year, 12, 15))
                                .endDate(LocalDate.of(year, 12, 30))
                                .build();
                    case HOLIDAYS:
                        return Token.builder()
                                .date(LocalDate.of(year, 11, 15))
                                .endDate(LocalDate.of(year + 1, 1, 6))
                                .build();
                    default:
                        return null;
                }
            })
            .build();

    public final static TokenReplacer monthReplacer = TokenReplacer.builder()
            .predicates(predicates(isMonth, isYear))
            .replacer((tokens) -> {
                int year = tokens.get(1).getYear();
                int month = tokens.get(0).getNumericMonth();
                return Token.builder()
                        .date(LocalDate.of(year, month, 1))
                        .endDate(lastOfMonth(year, month))
                        .build();
            })
            .build();

    public final static TokenReplacer yearReplacer = TokenReplacer.builder()
            .predicates(predicates(isYear))
            .replacer((tokens) -> {
                int year = tokens.get(0).getYear();
                return Token.builder()
                        .date(LocalDate.of(year, 1, 1))
                        .endDate(lastOfMonth(year, 12))
                        .build();
            })
            .build();

    public final static TokenReplacer standardRangeReplacer = TokenReplacer.builder()
            .predicates(predicates(isDate, isRange, isDate))
            .replacer((tokens) -> Token.builder()
                    .date(tokens.get(0).date())
                    .endDate(tokens.get(2).endDate())
                    .build())
            .build();

    public static List<Token> reduceTokens(List<Token> tokens) {
        return reduceTokens(tokens,
                dayRangeReplacer,
                monthAndDayRangeReplacer,
                standardDateReplacerWithComma,
                standardDateReplacer,
                seasonReplacer,
                monthReplacer,
                yearReplacer,
                modifierReplacer,
                standardRangeReplacer
        );
    }

    public static List<Token> reduceTokens(List<Token> tokens, TokenReplacer... replacers) {
        for (TokenReplacer replacer : replacers) {
            List<Token> reduced = replacer.replace(tokens);
            if (null != reduced) {
                return reduceTokens(reduced, replacers);
            }
        }
        return tokens;
    }
}
