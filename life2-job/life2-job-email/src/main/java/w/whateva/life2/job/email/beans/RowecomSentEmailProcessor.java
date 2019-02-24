package w.whateva.life2.job.email.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.xml.email.csv.RowecomInboxEmail;
import w.whateva.life2.xml.email.csv.RowecomSentEmail;

import java.time.ZoneOffset;
import java.util.Date;

public class RowecomSentEmailProcessor implements ItemProcessor<RowecomSentEmail, ApiEmail> {

    private transient Logger log = LoggerFactory.getLogger(RowecomSentEmailProcessor.class);

    private static final String KEY_SEPARATOR = ".";

    private final String sender;

    public RowecomSentEmailProcessor(String sender) {
        this.sender = sender;
    }

    @Override
    public ApiEmail process(RowecomSentEmail csvEmail) throws Exception {

        ApiEmail result = new ApiEmail();
        result.setSent(csvEmail.getSent().toInstant().atOffset(ZoneOffset.UTC).toZonedDateTime());
        result.setSubject(csvEmail.getSubject());
        result.setBody(csvEmail.getContents());
        result.setKey(createKey(csvEmail));
        result.setFrom(sender);
        result.setTo(csvEmail.getTo());

        log.info(result.getKey());

        return result;
    }

    private String createKey(RowecomSentEmail csvEmail) {

        try {

            Date sentDate = csvEmail.getSent();

            if (null == sentDate) return null;

            return "<" +
                    String.valueOf(sentDate.toInstant().getEpochSecond()) +
                    KEY_SEPARATOR +
                    sender +
                    ">";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
