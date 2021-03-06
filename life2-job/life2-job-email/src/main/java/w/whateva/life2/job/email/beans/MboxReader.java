package w.whateva.life2.job.email.beans;

import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.EmbeddedDocumentUtil;
import org.apache.tika.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mbox.MboxParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.*;

public class MboxReader extends MboxParser implements ItemReader<MimeMessage> {

    private transient Logger log = LoggerFactory.getLogger(MboxReader.class);

    private static final int MAIL_MAX_SIZE = 100000000;

    private static final String charsetName = "utf-8";

    private final BufferedReader reader;
    private String curLine;

    public MboxReader(InputStream inputStream) throws IOException {

        try {
            Reader streamReader = new InputStreamReader(inputStream, charsetName);
            this.reader = new BufferedReader(streamReader);
            this.curLine = reader.readLine();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public MimeMessage read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (curLine == null || Thread.currentThread().isInterrupted()) {

            return close();

        } else if (curLine.startsWith(MBOX_RECORD_DIVIDER)) {

            if (curLine == null) {
                return close();
            }

            ByteArrayOutputStream message = new ByteArrayOutputStream(100000);
            do {
                message.write(curLine.getBytes(charsetName));
                message.write('\n');
                curLine = reader.readLine();
            }
            while (curLine != null && !curLine.startsWith(MBOX_RECORD_DIVIDER) && message.size() < MAIL_MAX_SIZE);

            ByteArrayInputStream messageStream = new ByteArrayInputStream(message.toByteArray());

            try {
                return buildMimeMessage(messageStream);
            } catch (Exception e) {
                log.error("Could not build mime message from message stream");
            }

        } else {

            curLine = reader.readLine();
            return read();
        }

        return close();
    }

    private MimeMessage buildMimeMessage(ByteArrayInputStream s) throws Exception {
        String content = IOUtils.toString(s);
        Session session = Session.getInstance(new Properties());
        InputStream is = new ByteArrayInputStream(content.getBytes());
        return new MimeMessage(session, is);
    }

    private MimeMessage close() {
        IOUtils.closeQuietly(reader);
        return null;
    }
}
