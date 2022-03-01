package w.whateva.life2.integration.note;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.domain.NoteMonthYearCount;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.integration.dates.Token;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static w.whateva.life2.integration.dates.DateParsingUtil.parseDate;
import static w.whateva.life2.integration.dates.DateParsingUtil.reduceTokens;

// https://www.baeldung.com/java-regex-token-replacement

public class NoteUtil {

    public static final String NOTE_PIN_TYPE = "note";

    private static final Pattern artifactPattern = Pattern.compile("\\$\\[[a-zA-Z0-9]*]\\(artifact:([a-z]*)\\)");
    private static final Pattern fieldPattern = Pattern.compile("\\$\\[[a-zA-Z0-9]*]\\(field:([a-z]*)\\)([^\n]*)");

    private static final Pattern personPattern = Pattern.compile("@\\[[a-zA-Z0-9.]*]\\(user:([a-z.]*)\\)");
    private static final Pattern trovePattern = Pattern.compile("!\\[[a-zA-Z0-9-_]*]\\(trove:([a-zA-Z0-9-_]*)\\)");

    private static final String nonTextPatternText = "([#$@!])\\[[a-zA-Z0-9-_:. ]*]\\([a-z]*:([a-zA-Z0-9-_:. ]*)\\)";
    private static final Pattern nonTextPattern = Pattern.compile(nonTextPatternText);
    private static final Pattern nonTextPatternAtStart = Pattern.compile("^" + nonTextPatternText);

    private static final String looseTagPatternText = "#([a-zA-Z0-9-_]+)";
    private static final Pattern looseTagPattern = Pattern.compile(looseTagPatternText);
    private static final Pattern looseTagPatternAtStart = Pattern.compile("^" + looseTagPatternText);

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

    public static List<String> tags(String input) {
        List<String> result = new ArrayList<>();
        Matcher tagMatcher = looseTagPattern.matcher(input);
        while (tagMatcher.find()) {
            result.add(tagMatcher.group(1));
        }
        return result;
    }

    public static Map<String, Object> fields(String input) {
        if (StringUtils.isEmpty(input)) return new LinkedHashMap<>();
        Map<String, Object> result = new LinkedHashMap<>();
        Matcher fieldMatcher = fieldPattern.matcher(input);
        while (fieldMatcher.find()) {
            String fieldName = fieldMatcher.group(1);
            String fieldValue = fieldMatcher.group(2).trim();
            if ("when".equals(fieldName) || "sent".equals(fieldName)) {

                result.put("whenDisplay", fieldValue);

                Token dateToken = reduceTokens(parseDate(fieldValue)).stream()
                        .findFirst()
                        .orElse(null);

                if (null != dateToken) {
                    if (null != dateToken.date()) {
                        result.put("when", dateToken.date().toString());
                    }
                    if (null != dateToken.endDate()) {
                        result.put("when2", dateToken.endDate().toString());
                    }
                }
            } else if ("from".equals(fieldName) || "to".equals(fieldName) || "author".equals(fieldName)) {
                List<String> people = people(fieldValue);
                result.put(fieldName, people);
            } else {
                result.put(fieldName, fieldValue);
            }
        }
        String type = artifacts(input).stream().findFirst().orElse(null);
        if (!StringUtils.isEmpty(type)) {
            result.put("type", type);
        }
        List<String> people = people(input);
        if (!CollectionUtils.isEmpty(people)) {
            result.put("who", people);
        }
        List<String> tags = tags(input);
        if (!CollectionUtils.isEmpty(tags)) {
            result.put("tags", tags);
        }
        return result;
    }

    public static ApiArtifact toDto(Note note) {

        ZonedDateTime when = when(note);
        ZonedDateTime when2 = when2(note);
        String title = prettyNoteText(note.getText().split("\n")[0]);

        String[] troveAndKey = note.getId().split("/");

        ApiArtifact result = new ApiArtifact();
        result.setWhen(null != when ? when.toLocalDateTime() : null);
        result.setWhen2(null != when2 ? when2.toLocalDateTime() : null);
        result.setTitle(title);
        result.setTrove(note.getTrove());
        result.setImage(imageLocation(note));
        result.setKey(troveAndKey[troveAndKey.length - 1]);
        result.setDescription(note.getText());
        result.setData(note.getData());
        result.setNotes(Collections.emptyList());
        return result;
    }

    public static ZonedDateTime when(Note note) {
        return note != null && note.getData() != null ? when(note.getData()) : null;
    }

    public static ZonedDateTime when(Map<String, Object> data) {
        return !CollectionUtils.isEmpty(data) && data.containsKey("when")
                ? ZonedDateTime.parse(data.get("when").toString() + "T00:00:00Z")
                : null;
    }

    public static ZonedDateTime when2(Note note) {
        return note != null && note.getData() != null ? when2(note.getData()) : null;
    }

    public static ZonedDateTime when2(Map<String, Object> data) {
        return !CollectionUtils.isEmpty(data) && data.containsKey("when2")
                ? ZonedDateTime.parse(data.get("when2").toString() + "T00:00:00Z")
                : null;
    }

    public static String title(Note note) {
        return note != null && note.getData() != null ? title(note.getData()) : null;
    }

    public static String title(Map<String, Object> data) {
        return !CollectionUtils.isEmpty(data) && data.containsKey("type") && data.get("type") != null ?
                data.get("type").toString() : null;
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
            if (matcher.group(1).equals("#")) {
                // put hash tags in quotes, to match exactly
                output.append('"').append(matcher.group(1)).append(matcher.group(2)).append('"');
            }
            lastIndex = matcher.end();
        }
        if (lastIndex < searchText.length()) {
            output.append(searchText, lastIndex, searchText.length());
        }
        String result = output.toString();
        return StringUtils.isEmpty(result) ? null : result.trim();
    }

    public static String indexNoteText(String text) {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = nonTextPattern.matcher(text);
        while (matcher.find()) {
            output.append(text, lastIndex, matcher.start()).append(' ');
            output.append(matcher.group(1));
            output.append(matcher.group(2));
            lastIndex = matcher.end();
        }
        if (lastIndex < text.length()) {
            output.append(text, lastIndex, text.length());
        }
        String result = output.toString();
        return StringUtils.isEmpty(result) ? null : result.trim();
    }

    public static String prettyNoteText(String text) {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = nonTextPattern.matcher(text);
        while (matcher.find()) {
            output.append(text, lastIndex, matcher.start()).append('[');
            if (matcher.group(1).equals("#")) {
                output.append(matcher.group(1));
            }
            output.append(matcher.group(2)).append(']');
            lastIndex = matcher.end();
        }
        if (lastIndex < text.length()) {
            output.append(text, lastIndex, text.length());
        }
        String result = output.toString();
        return StringUtils.isEmpty(result) ? null : result.trim();
    }

    public static String parseNoteTitleForNewKey(String text) {
        if (StringUtils.isEmpty(text)) return null;
        text = text.split("\n")[0];
        if (StringUtils.isEmpty(text)) return null;
        text = text.trim();

        // first let's see if the title begins with a hash tag
        Matcher looseTagMatcher = looseTagPatternAtStart.matcher(text);
        if (looseTagMatcher.find()) {
            return looseTagMatcher.group();
        }
        // now let's see if the title starts with a markup term
        Matcher nonTextMatcher = nonTextPatternAtStart.matcher(text);
        if (nonTextMatcher.find()) {
            return nonTextMatcher.group(1) + nonTextMatcher.group(2);
        }
        // finally, let's just join all non-markup words with dashes
        text = indexNoteText(text);
        if (null == text) return null;
        text = String.join("-", text.split("\\s+"));
        return text;
    }

    public static List<Pin> toIndexPins(Note note) {

        String trove = note.getTrove();
        String key = noteKey(note);

        // primary
        List<Pin> result = new ArrayList<>();
        Pin primary = toPin(trove, key, note.getText());
        if (null != primary) {
            result.add(primary);
        }

        // secondary
        if (!CollectionUtils.isEmpty(note.getNotes())) {
            result.addAll(note.getNotes().stream()
                    .map(n -> toPin(trove, key, n, "secondary"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableList()));
        }

        return result;
    }

    private static Pin toPin(String trove, String key, String text) {
        return toPin(trove, key, text, null);
    }

    private static Pin toPin(String trove, String key, String text, String source) {

        if (StringUtils.isEmpty(text)) {
            return null;
        }

        Map<String, Object> data = fields(text);
        String indexText = indexNoteText(text);

        // don't like the special treatment of life2, but eh, whatever
        if ("life2".equals(trove)) {
            if (data.containsKey("tags")) {
                ((List<String>)data.get("tags")).add(key);
            } else {
                data.put("tags", List.of(key));
            }
        }

        ZonedDateTime when = when(data);
        ZonedDateTime when2 = when2(data);
        String whenDisplay = data.containsKey("whenDisplay") ? data.get("whenDisplay").toString() : null;
        Set<String> from = data.containsKey("from") ? new HashSet<String>((Collection)data.get("from")) : null;
        Set<String>  to = data.containsKey("to") ? new HashSet<String>((Collection)data.get("to")) : null;
        //String title = title(data);
        String title = !StringUtils.isEmpty(text) ? prettyNoteText(text.split("\n")[0]) : null;

        return Pin.builder()
                .type(NOTE_PIN_TYPE)
                .trove(trove)
                .key(key)
                .title(title)
                .data(data)
                .from(from)
                .to(to)
                .text(indexText)
                .source(source)
                .when(when)
                .when2(when2)
                .whenDisplay(whenDisplay)
                .build();
    }

    public static String noteKey(Note note) {
        return note.getId().startsWith(note.getTrove() + "/") ?
                note.getId().substring(note.getTrove().length() + 1) :
                note.getId();
    }
}
