package w.whateva.life2.job.email.beans;

import org.apache.commons.mail.util.MimeMessageParser;
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
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MboxEmailProcesor implements ItemProcessor<MimeMessage, ApiEmail> {

    private static final String KEY_SEPARATOR = ".";

    @Override
    public ApiEmail process(MimeMessage message) throws Exception {

        MimeMessageParser parser = new MimeMessageParser(message).parse();
        Map<String, String> headers = new HashMap<>();
        message.getAllHeaderLines();
        for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
            Header h = e.nextElement();
            headers.put(h.getName(), h.getValue());
        }

        ApiEmail result = new ApiEmail();
        result.setKey(createKey(headers, parser));
        result.setMessageId(headers.get("Message-ID"));
        result.setFrom(headers.get("From"));
        result.setTo(headers.get("To"));
        result.setSubject(headers.get("Subject"));
        result.setSent(parser.getMimeMessage().getSentDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        result.setBody(!StringUtils.isEmpty(parser.getHtmlContent()) ? parser.getHtmlContent() : parser.getPlainContent());
        result.setMessage(toString(message));

        System.out.println(result.getKey());

        return result;
    }

    private static String createKey(Map<String, String> headers, MimeMessageParser parser) {

        if (headers.get("Message-ID") != null) return headers.get("Message-ID");

        try {

            return "<" +
                    getSentDate(parser) +
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

    private static String getSentDate(MimeMessageParser parser) {
        try {
            return String.valueOf(parser.getMimeMessage().getSentDate().toInstant().getEpochSecond());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}