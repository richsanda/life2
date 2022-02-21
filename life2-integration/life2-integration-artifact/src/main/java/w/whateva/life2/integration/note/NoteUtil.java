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

import static w.whateva.life2.integration.dates.DateParsingUtil.parseDate;
import static w.whateva.life2.integration.dates.DateParsingUtil.reduceTokens;

// https://www.baeldung.com/java-regex-token-replacement

public class NoteUtil {

    public static final String NOTE_PIN_TYPE = "note";

    private static final Pattern artifactPattern = Pattern.compile("\\$\\[[a-zA-Z0-9]*]\\(artifact:([a-z]*)\\)");
    private static final Pattern fieldPattern = Pattern.compile("\\$\\[[a-zA-Z0-9: ]*]\\(field:([a-z]*)\\)([^\n]*)");

    private static final Pattern personPattern = Pattern.compile("@\\[[a-zA-Z0-9.: ]*]\\(user:([a-z.]*)\\)");
    private static final Pattern trovePattern = Pattern.compile("!\\[[a-zA-Z0-9-_]*]\\(trove:([a-zA-Z0-9-_]*)\\)");
    private static final Pattern nonTextPattern = Pattern.compile("[$@!]\\[[a-zA-Z0-9-_:. ]*]\\([a-z]*:([a-zA-Z0-9-_:. ]*)\\)");

    private static final Pattern tagPattern = Pattern.compile("#([a-zA-Z0-9-_]*)");

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
        Matcher tagMatcher = tagPattern.matcher(input);
        int i = 0;
        while (tagMatcher.find()) {
            result.add(tagMatcher.group(1));
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
            result.put("people", people);
        }
        List<String> tags = tags(input);
        if (!CollectionUtils.isEmpty(tags)) {
            result.put("tags", tags);
        }
        return result;
    }

    public static ApiArtifact toDto(Note note) {

        ZonedDateTime when = when(note);
        String title = prettyNoteText(note.getText().split("\n")[0]);

        String[] troveAndKey = note.getId().split("/");

        ApiArtifact result = new ApiArtifact();
        result.setWhen(null != when ? when.toLocalDateTime() : null);
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
            // output.append(matcher.group(1));
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
            output.append(matcher.group(1)).append(']');
            lastIndex = matcher.end();
        }
        if (lastIndex < text.length()) {
            output.append(text, lastIndex, text.length());
        }
        String result = output.toString();
        return StringUtils.isEmpty(result) ? null : result.trim();
    }

    public static String parseNoteTitle(String text) {
        if (StringUtils.isEmpty(text)) return null;
        text = text.split("\n")[0];
        if (StringUtils.isEmpty(text)) return null;
        text = text.trim();
        text = text.split("\\s")[0];
        return StringUtils.isEmpty(text) ? null : text;
    }

    public static List<Pin> toIndexPins(Note note) {

        Map<String, Object> data = Collections.emptyMap();
        String text = null;
        if (!StringUtils.isEmpty(note.getText())) {
            data = fields(note.getText());
            text = indexNoteText(note.getText());
        }

        ZonedDateTime when = when(data);
        ZonedDateTime when2 = when2(data);
        String whenDisplay = data.containsKey("whenDisplay") ? data.get("whenDisplay").toString() : null;
        //String title = title(data);
        String title = prettyNoteText(note.getText().split("\n")[0]);

        Pin result = Pin.builder()
                .type(NOTE_PIN_TYPE)
                .trove(note.getTrove())
                .key(noteKey(note))
                .title(title)
                .data(data)
                .text(text)
                .when(when)
                .when2(when2)
                .whenDisplay(whenDisplay)
                .build();

        return Collections.singletonList(result);
    }

    public static String noteKey(Note note) {
        return note.getId().startsWith(note.getTrove() + "/") ?
                note.getId().substring(note.getTrove().length() + 1) :
                note.getId();
    }
}
