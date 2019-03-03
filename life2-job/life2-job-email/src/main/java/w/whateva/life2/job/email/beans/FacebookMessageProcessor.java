package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemProcessor;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.xml.email.facebook.FacebookMessage;
import w.whateva.life2.xml.email.facebook.FacebookMessageThread;
import w.whateva.life2.xml.email.facebook.FacebookMessageFile;
import w.whateva.life2.xml.email.facebook.FacebookMessageParticipant;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FacebookMessageProcessor implements ItemProcessor<FacebookMessageThread, ApiEmail> {

    private static final String FACEBOOK = "FACEBOOK";
    private static final String KEY_SEPARATOR = ":";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public ApiEmail process(FacebookMessageThread thread) throws Exception {

        List<FacebookMessage> messages = thread.getMessages();
        FacebookMessage firstMessage = messages.get(0);
        FacebookMessageFile file = thread.getFile();

        ApiEmail result = new ApiEmail();

        result.setFrom(messages.get(0).getSender_name());
        result.setTo(file.getParticipants()
                .stream()
                .map(FacebookMessageParticipant::getName)
                .collect(Collectors.joining(", ")));
        result.setBody(composeBody(thread.getMessages()));
        result.setSent(firstMessage.getTimestamp_ms().toInstant().atZone(ZoneId.of("UTC")));
        result.setKey(composeKey(file, firstMessage));
        result.setSubject(thread.getFile().getTitle());

        return result;
    }

    private String composeKey(FacebookMessageFile file, FacebookMessage message) {

        return
                FACEBOOK
                        + KEY_SEPARATOR
                        + composeFileKey(file)
                        + KEY_SEPARATOR
                        + String.valueOf(message.getTimestamp_ms().toInstant().getEpochSecond());
    }

    private String composeFileKey(FacebookMessageFile file) {
        return file.getThread_type() + KEY_SEPARATOR + file.getThread_path();
    }

    private String composeBody(List<FacebookMessage> messages) {

        StringBuilder sb = new StringBuilder();

        for (FacebookMessage message : messages) {
            sb.append(message.getSender_name());
            sb.append(" [");
            sb.append(formatTime(message.getTimestamp_ms()));
            sb.append("]: ");
            sb.append(message.getContent());
            sb.append("\n\n");
        }

        return sb.toString();
    }

    private String formatTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).toLocalTime().format(formatter);
    }
}
