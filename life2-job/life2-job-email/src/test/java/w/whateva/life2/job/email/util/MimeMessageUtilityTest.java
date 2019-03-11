package w.whateva.life2.job.email.util;

import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Test;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

public class MimeMessageUtilityTest extends MimeMessageUtility {

    private static final Class<?> thisClass = MimeMessageUtilityTest.class;

    private final static String cwFileLocation = "/cw2.txt";

    private static InputStream getResourceAsStream(String resource) {
        return thisClass.getResourceAsStream(resource);
    }

    @Test
    public void parseMessage() {

        try {

            ByteArrayOutputStream b = processHistoricEmail(getResourceAsStream(cwFileLocation));
            MimeMessage m = buildMimeMessage(b);
            MimeMessageParser parser;
            try {
                parser = new MimeMessageParser(m).parse();
            } catch (Exception e) {
                return;
            }
            Map<String, String> headers = getHeaders(m);
            Date date = getSentDate(headers, parser);
            String to = getTo(headers, parser);
            String from = getFrom(headers, parser);
            String key = createKey(headers, parser);

            System.out.println(key + ": " + from + " -> " + to + " " + date);

            // System.out.println( parser.getPlainContent());

        } catch (Exception e) {

        }
    }
}
