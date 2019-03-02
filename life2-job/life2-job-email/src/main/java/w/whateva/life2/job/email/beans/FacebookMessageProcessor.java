package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemProcessor;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.xml.email.facebook.FacebookMessage;
import w.whateva.life2.xml.email.facebook.FacebookMessageContext;
import w.whateva.life2.xml.email.facebook.FacebookMessageFile;
import w.whateva.life2.xml.email.facebook.FacebookMessageParticipant;

import java.time.ZoneId;
import java.util.stream.Collectors;

public class FacebookMessageProcessor implements ItemProcessor<FacebookMessageContext, ApiEmail> {

    private static final String FACEBOOK = "FACEBOOK";
    private static final String KEY_SEPARATOR = ":";

    @Override
    public ApiEmail process(FacebookMessageContext context) throws Exception {

        FacebookMessage message = context.getMessage();
        FacebookMessageFile file = context.getFile();

        ApiEmail result = new ApiEmail();

        result.setFrom(message.getSender_name());
        result.setTo(file.getParticipants()
                .stream()
                .map(FacebookMessageParticipant::getName)
                .collect(Collectors.joining(", ")));
        result.setBody(message.getContent());
        result.setSent(message.getTimestamp_ms().toInstant().atZone(ZoneId.of("UTC")));
        result.setKey(composeKey(file, message));

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
}
