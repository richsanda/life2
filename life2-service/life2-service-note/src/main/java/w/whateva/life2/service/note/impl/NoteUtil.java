package w.whateva.life2.service.note.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.domain.NoteMonthYearCount;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// https://www.baeldung.com/java-regex-token-replacement

public class NoteUtil {

    private static final Pattern artifactPattern = Pattern.compile("\\$\\[[a-zA-Z0-9]*]\\(artifact:([a-z]*)\\)");
    private static final Pattern fieldPattern = Pattern.compile("\\$\\[[a-zA-Z0-9: ]*]\\(field:([a-z]*)\\)([^\n]*)");
    private static final Pattern datePattern = Pattern.compile("([0-9]{1,2})\\.([0-9]{1,2})\\.([0-9]{2,4})");
    private static final Pattern datePattern2 = Pattern.compile("([0-9]{1,2})\\.?([a-z]{3})\\.?([0-9]{2,4})");

    private static final Pattern personPattern = Pattern.compile("@\\[[a-zA-Z0-9.: ]*]\\(user:([a-z.]*)\\)");
    private static final Pattern trovePattern = Pattern.compile("!\\[[a-zA-Z0-9-_]*]\\(trove:([a-zA-Z0-9-_]*)\\)");
    private static final Pattern nonTextPattern = Pattern.compile("[!@]\\[[a-zA-Z0-9-_:. ]*]\\([a-z]*:([a-zA-Z0-9-_:. ]*)\\)");

    public static List<String> artifacts(String input) {
        List<String> result = new ArrayList<>();
        Matcher artifactMatcher = artifactPattern.matcher(input);
        int i = 0;
        while (artifactMatcher.find()) {
            result.add(artifactMatcher.group(1));
        }
        return result;
    }

    public static List<String> people(String input) {
        List<String> result = new ArrayList<>();
        Matcher personMatcher = personPattern.matcher(input);
        int i = 0;
        while (personMatcher.find()) {
            result.add(personMatcher.group(1));
        }
        return result;
    }

    public static Map<String, Object> fields(String input) {
        Map<String, Object> result = new LinkedHashMap<>();
        Matcher fieldMatcher = fieldPattern.matcher(input);
        while (fieldMatcher.find()) {
            String fieldName = fieldMatcher.group(1);
            String fieldValue = fieldMatcher.group(2).trim();
            if ("when".equals(fieldName) || "sent".equals(fieldName)) {
                Matcher dateMatcher = datePattern.matcher(fieldValue);
                Matcher dateMatcher2 = datePattern2.matcher(fieldValue);
                Date date = null;
                if (dateMatcher.find()) {
                    date = date(dateMatcher.group(3), dateMatcher.group(1), dateMatcher.group(2));
                } else if (dateMatcher2.find()) {
                    date = date(dateMatcher2.group(3), month(dateMatcher2.group(2)), dateMatcher2.group(1));
                }
                if (null != date) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    result.put("when", df.format(date));
                }
            } else {
                result.put(fieldName, fieldValue);
            }
        }
        return result;
    }

    public static Note enhanceNote(Note note) {
        note.setTrove(note.getId().split("/")[0]);
        note.setId(note.getId().split("/")[1]);
        note.setData(fields(note.getText()));
        if (note.getData().containsKey("when")) {
            note.setSent(ZonedDateTime.parse(note.getData().get("when").toString() + "T00:00:00Z"));
        }
        note.getData().put("type", artifacts(note.getText()).stream().findFirst().orElse(null));
        note.getData().put("people", String.join(", ", people(note.getText())));
        return note;
    }

    public static List<Note> splitNote(Note note) {
        String[] sep = note.getText().split("---");
        return IntStream.range(0, sep.length)
                .mapToObj(i -> {
                    Note copy = new Note();
                    BeanUtils.copyProperties(note, copy);
                    copy.setText(sep[i]);
                    if (i != 0) copy.setId(copy.getId() + "." + i);
                    return copy;
                })
                .collect(Collectors.toList());
    }

    private static Date date(String yearStr, String monthStr, String dayStr) {

        monthStr = (monthStr.length() == 3) ? month(monthStr) : (monthStr.length() == 2 && monthStr.startsWith("0")) ? monthStr.substring(1, 2) : monthStr;
        dayStr = (dayStr.length() == 2 && dayStr.startsWith("0")) ? dayStr.substring(1, 2) : dayStr;
        yearStr = (yearStr.length() == 4) ? yearStr : yearStr.startsWith("9") ? ("19" + yearStr) : ("20" + yearStr);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);
        int year = Integer.parseInt(yearStr);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        return calendar.getTime();
    }

    private static String month(String monthStr) {
        switch (monthStr) {
            case "jan":
                return "1";
            case "feb":
                return "2";
            case "mar":
                return "3";
            case "apr":
                return "4";
            case "may":
                return "5";
            case "jun":
                return "6";
            case "jul":
                return "7";
            case "aug":
                return "8";
            case "sep":
                return "9";
            case "oct":
                return "10";
            case "nov":
                return "11";
            case "dec":
                return "12";
        }
        return "0";
    }

    public static ApiArtifact toDto(Note note) {
        String[] troveAndKey = note.getId().split("/");
        ApiArtifact result = new ApiArtifact();
        result.setWhen(note.getSent().toLocalDateTime());
        result.setTitle(note.getData().get("type").toString());
        result.setTrove(note.getTrove());
        result.setImage(imageLocation(note));
        result.setKey(troveAndKey[1]);
        result.setDescription(note.getText());
        result.setData(note.getData());
        return result;
    }

    public static ApiArtifact toDto(Note note, List<String> relatives, int index) {
        ApiArtifact result = new ApiArtifact();
        result.setTypes(new HashSet<>());
        result.getTypes().add("note");
        result.setWhen(null != note.getSent() ? note.getSent().toLocalDateTime() : null);
        result.setTitle(note.getData().containsKey("type") ? note.getData().get("type").toString() : null);
        result.setTrove(note.getTrove());
        result.setImage(imageLocation(note));
        result.setKey(note.getId());
        result.setDescription(note.getText());
        result.setData(note.getData());
        result.setRelativeKeys(relatives);
        result.setRelativeKeyIndex(index);
        return result;
    }

    public static ApiArtifactCount toDto(NoteMonthYearCount count) {
        ApiArtifactCount result = new ApiArtifactCount();
        result.setCount(count.getCount());
        result.setMonth(count.getMonth());
        result.setYear(count.getYear());
        return result;
    }

    private static String imageLocation(Note note) {
        return "w/neat/" + note.getId();
    }

    public static String updateNoteText(String input) {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = artifactPattern.matcher(input);
        while (matcher.find()) {
            output.append(input, lastIndex, matcher.start())
                    .append(composeField(matcher.group(1)));

            lastIndex = matcher.end();
        }
        if (lastIndex < input.length()) {
            output.append(input, lastIndex, input.length());
        }
        return output.toString();
    }

    private static String composeField(String fieldName, String fieldValue) {
        return "$[" + fieldName + ":](field:" + fieldName + ")" + fieldValue;
    }

    private static String composeField(String type) {
        return "$[" + type + "](artifact:" + type + ")";
    }

    public static Set<String> parseWho(String searchText) {
        Set<String> result = new LinkedHashSet<>();
        Matcher matcher = personPattern.matcher(searchText);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return Collections.unmodifiableSet(result);
    }

    public static Set<String> parseTroves(String searchText) {
        Set<String> result = new LinkedHashSet<>();
        Matcher matcher = trovePattern.matcher(searchText);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return Collections.unmodifiableSet(result);
    }

    public static String parseSearchText(String searchText) {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = nonTextPattern.matcher(searchText);
        while (matcher.find()) {
            output.append(searchText, lastIndex, matcher.start()).append(' ');
            // output.append(matcher.group(1));
            lastIndex = matcher.end();
        }
        if (lastIndex < searchText.length()) {
            output.append(searchText, lastIndex, searchText.length());
        }
        String result = output.toString();
        return StringUtils.isEmpty(result) ? null : result.trim();
    }
}
