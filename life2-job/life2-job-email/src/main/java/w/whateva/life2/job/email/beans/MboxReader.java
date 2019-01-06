package w.whateva.life2.job.email.beans;

import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.EmbeddedDocumentUtil;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mbox.MboxParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.email.dto.ApiEmail;

import javax.mail.Header;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.time.ZoneId;
import java.util.*;

public class MboxReader extends MboxParser implements ItemReader<ApiEmail> {

    private static final String charsetName = "utf-8";

    private final BodyContentHandler handler;
    private final EmbeddedDocumentExtractor extractor;
    private final BufferedReader reader;
    private String curLine;

    public MboxReader(InputStream inputStream) throws IOException {

        this.handler = new BodyContentHandler();
        this.extractor = EmbeddedDocumentUtil.getEmbeddedDocumentExtractor(new ParseContext());
        try {
            Reader streamReader = new InputStreamReader(inputStream, charsetName);
            this.reader = new BufferedReader(streamReader);
            this.curLine = reader.readLine();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public ApiEmail read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (curLine == null || Thread.currentThread().isInterrupted()) {

            return close();

        } else if (curLine.startsWith(MBOX_RECORD_DIVIDER)) {

            Metadata mailMetadata = new Metadata();
            Queue<String> multiline = new LinkedList<>();

            XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, mailMetadata);
            xhtml.startDocument();

            if (curLine == null) {
                return close();
            }

            ByteArrayOutputStream message = new ByteArrayOutputStream(100000);
            do {
                if (curLine.startsWith(" ") || curLine.startsWith("\t")) {
                    String latestLine = multiline.poll();
                    latestLine += " " + curLine.trim();
                    multiline.add(latestLine);
                } else {
                    multiline.add(curLine);
                }

                message.write(curLine.getBytes(charsetName));
                message.write('\n');
                curLine = reader.readLine();
            }
            while (curLine != null && !curLine.startsWith(MBOX_RECORD_DIVIDER) && message.size() < MAIL_MAX_SIZE);

            ByteArrayInputStream messageStream = new ByteArrayInputStream(message.toByteArray());

            try {
                return buildEmail(messageStream);
            } catch (Exception e) {
                System.out.println("OWELL");
            }

            if (extractor.shouldParseEmbedded(mailMetadata)) {
                extractor.parseEmbedded(messageStream, xhtml, mailMetadata, true);
            }

            xhtml.endDocument();

        } else {

            curLine = reader.readLine();
            return read();
        }

        return close();
    }

    private ApiEmail buildEmail(ByteArrayInputStream s) throws Exception {
        String content = IOUtils.toString(s);
        Session session = Session.getInstance(new Properties());
        InputStream is = new ByteArrayInputStream(content.getBytes());
        MimeMessage message = new MimeMessage(session, is);
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
        result.setMessage(content);
        return result;
    }

    private ApiEmail close() {
        IOUtils.closeQuietly(reader);
        return null;
    }
}
