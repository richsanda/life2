package w.whateva.life2.job.email.beans;

import org.apache.commons.mail.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.email.dto.ApiEmail;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MboxEmailProcesor implements ItemProcessor<MimeMessage, ApiEmail> {

    private transient Logger log = LoggerFactory.getLogger(MboxEmailProcesor.class);

    private static final String KEY_SEPARATOR = ".";

    @Override
    public ApiEmail process(MimeMessage message) throws Exception {

        MimeMessageParser parser;

        try {
            parser = new MimeMessageParser(message).parse();
        } catch (Exception e) {
            return null;
        }
        Map<String, String> headers = new HashMap<>();
        message.getAllHeaderLines();
        for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
            Header h = e.nextElement();
            headers.put(h.getName(), h.getValue());
        }
        Date sentDate = getSentDate(headers, parser);

        ApiEmail result = new ApiEmail();
        result.setKey(createKey(headers, parser));
        result.setMessageId(headers.get("Message-ID"));
        result.setFrom(headers.get("From"));
        result.setTo(headers.get("To"));
        result.setSubject(headers.get("Subject"));
        result.setSent(null == sentDate ? null : sentDate.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        result.setBody(!StringUtils.isEmpty(parser.getHtmlContent()) ? parser.getHtmlContent() : parser.getPlainContent());
        result.setMessage(toString(message));

        log.debug("Processed mime message resulting in key: " + result.getKey());

        return result;
    }

    private String createKey(Map<String, String> headers, MimeMessageParser parser) {

        if (headers.get("Message-ID") != null) return headers.get("Message-ID");

        try {

            Date sentDate = getSentDate(headers, parser);

            if (null == sentDate) return null;

            return "<" +
                    String.valueOf(sentDate.toInstant().getEpochSecond()) +
                    KEY_SEPARATOR +
                    parser.getFrom() +
                    ">";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String toString(MimeMessage message) throws IOException, MessagingException {
        OutputStream os = new ByteArrayOutputStream();
        message.writeTo(os);
        return os.toString();
    }

    private Date getSentDate(Map<String, String> headers, MimeMessageParser parser) {
        try {
            return parser.getMimeMessage().getSentDate();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            String result =  makeKeyFromHeaders(headers);
            log.warn("Could not parse date so tried using headers: " + result);
            return null;
        }
        return null;
    }

    private static String makeKeyFromHeaders(Map<String, String> headers) {
        Iterator<Map.Entry<String, String>> headerIterator = headers.entrySet().iterator();
        if (headerIterator.hasNext()) {
            Map.Entry<String, String> entry = headerIterator.next();
            return entry.getKey() + entry.getValue();
        }
        return null;
    }
}