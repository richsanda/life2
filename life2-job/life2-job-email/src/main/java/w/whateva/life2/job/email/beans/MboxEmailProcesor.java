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
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MboxEmailProcesor implements ItemProcessor<MimeMessage, ApiEmail> {


    @Override
    public ApiEmail process(MimeMessage message) throws Exception {

        MimeMessageParser parser = new MimeMessageParser(message).parse();
        Map<String, String> headers = new HashMap<>();
        message.getAllHeaderLines();
        for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
            Header h = e.nextElement();
            headers.put(h.getName(), h.getValue());
        }

        if (!headers.containsKey("Message-ID")) {
            System.out.println(String.format("%s has no message id", "THISONE"));
        } else {
            System.out.println(headers.get("Message-ID"));
        }

        ApiEmail result = new ApiEmail();
        result.setId(headers.get("Message-ID"));
        result.setFrom(headers.get("From"));
        result.setTo(headers.get("To"));
        result.setSubject(headers.get("Subject"));
        result.setSent(parser.getMimeMessage().getSentDate().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        result.setBody(!StringUtils.isEmpty(parser.getHtmlContent()) ? parser.getHtmlContent() : parser.getPlainContent());
        result.setMessage(toString(message));
        return result;
    }

    private static String toString(MimeMessage message) throws IOException, MessagingException {
        OutputStream os = new ByteArrayOutputStream();
        message.writeTo(os);
        return os.toString();
    }
}