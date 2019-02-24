package w.whateva.life2.job.email.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.xml.email.csv.RowecomInboxEmail;

import java.time.ZoneOffset;
import java.util.Date;

public class RowecomInboxEmailProcessor implements ItemProcessor<RowecomInboxEmail, ApiEmail> {

    private transient Logger log = LoggerFactory.getLogger(RowecomInboxEmailProcessor.class);

    private static final String KEY_SEPARATOR = ".";

    @Override
    public ApiEmail process(RowecomInboxEmail csvEmail) throws Exception {

        ApiEmail result = new ApiEmail();
        result.setSent(csvEmail.getReceived().toInstant().atOffset(ZoneOffset.UTC).toZonedDateTime());
        result.setSubject(csvEmail.getSubject());
        result.setBody(csvEmail.getContents());
        result.setKey(createKey(csvEmail));
        result.setFrom(csvEmail.getFrom());
        result.setTo(csvEmail.getTo());

        log.info(result.getKey());

        return result;
    }

    private String createKey(RowecomInboxEmail csvEmail) {

        try {

            Date sentDate = csvEmail.getReceived();
            String from = csvEmail.getFrom();

            if (null == sentDate) return null;

            return "<" +
                    String.valueOf(sentDate.toInstant().getEpochSecond()) +
                    KEY_SEPARATOR +
                    from +
                    ">";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
