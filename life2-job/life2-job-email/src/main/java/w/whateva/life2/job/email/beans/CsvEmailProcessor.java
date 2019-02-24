package w.whateva.life2.job.email.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.xml.email.csv.CsvEmail;

import java.time.ZoneId;
import java.util.Date;

public class CsvEmailProcessor implements ItemProcessor<CsvEmail, ApiEmail> {

    private transient Logger log = LoggerFactory.getLogger(CsvEmailProcessor.class);

    private static final String KEY_SEPARATOR = ".";

    @Override
    public ApiEmail process(CsvEmail csvEmail) throws Exception {

        ApiEmail result = new ApiEmail();
        result.setSent(csvEmail.getReceived().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        result.setSubject(csvEmail.getSubject());
        result.setBody(csvEmail.getContents());
        result.setKey(createKey(csvEmail));
        result.setFrom(csvEmail.getFrom());
        result.setTo(csvEmail.getTo());

        log.info(result.getKey());

        return result;
    }

    private String createKey(CsvEmail csvEmail) {

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
