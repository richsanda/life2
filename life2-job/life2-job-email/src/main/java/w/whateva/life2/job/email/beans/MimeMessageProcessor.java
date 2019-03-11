package w.whateva.life2.job.email.beans;

import org.apache.commons.mail.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.job.email.util.MimeMessageUtility;

import javax.mail.internet.MimeMessage;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

public class MimeMessageProcessor implements ItemProcessor<MimeMessage, ApiEmail> {

    private transient Logger log = LoggerFactory.getLogger(MimeMessageProcessor.class);

    @Override
    public ApiEmail process(MimeMessage message) throws Exception {

        MimeMessageParser parser;
        try {
            parser = new MimeMessageParser(message).parse();
        } catch (Exception e) {
            return null;
        }

        Map<String, String> headers = MimeMessageUtility.getHeaders(message);

        Date sentDate = MimeMessageUtility.getSentDate(headers, parser);

        ApiEmail result = new ApiEmail();
        result.setKey(MimeMessageUtility.createKey(headers, parser));
        result.setMessageId(headers.get("Message-ID"));
        result.setFrom(MimeMessageUtility.getFrom(headers, parser));
        result.setTo(MimeMessageUtility.getTo(headers, parser));
        result.setSubject(headers.get("Subject"));
        result.setSent(null == sentDate ? null : sentDate.toInstant().atOffset(ZoneOffset.UTC).toZonedDateTime());
        result.setBody(!StringUtils.isEmpty(parser.getHtmlContent()) ? parser.getHtmlContent() : parser.getPlainContent());
        result.setBodyHtml(!StringUtils.isEmpty(parser.getHtmlContent()));
        result.setMessage(MimeMessageUtility.toString(message));

        log.debug("Processed mime message resulting in key: " + result.getKey());

        return result;
    }
}