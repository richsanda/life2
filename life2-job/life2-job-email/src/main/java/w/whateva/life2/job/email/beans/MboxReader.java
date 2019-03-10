package w.whateva.life2.job.email.beans;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.io.IOUtils;
import org.apache.tika.parser.mbox.MboxParser;
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

    private static final int MAIL_INIT_SIZE = 100000;
    private static final int MAIL_MAX_SIZE = 100000000;

    private static final String charsetName = "utf-8";

    private final BufferedReader reader;
    private String curLine;
    private ByteArrayOutputStream message = new ByteArrayOutputStream(MAIL_INIT_SIZE);
    private Stack<EmbeddedMessage> currentEmbeddedMessages = new Stack<>();
    private Stack<EmbeddedMessage> embeddedMessages = new Stack<>();
    private List<String> mimeMessages = new ArrayList<>();

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

        /*
        if (currentEmbeddedMessages.size() != 0) {
            log.warn(String.format("%d dangling embedded messages...", currentEmbeddedMessages.size()));
            return buildMimeMessage(currentEmbeddedMessages.pop().outputStream);
        }

        if (embeddedMessages.size() != 0) {
            return buildMimeMessage(embeddedMessages.pop().outputStream);
        }
        */

        if (curLine == null || Thread.currentThread().isInterrupted()) {

            return close();

        } else if (curLine.startsWith(MBOX_RECORD_DIVIDER)) {

            if (curLine == null) {
                return close();
            }

            do {
                message.write(curLine.getBytes(charsetName));
                message.write('\n');

                /*
                if (null != embeddedMessageStart(curLine)) {
                    currentEmbeddedMessages.add(new EmbeddedMessage(embeddedMessageStart(curLine)));
                } else if (EmbeddedMessageFrame.FORWARDED.equals(embeddedMessageEnd(curLine))) {

                    List<EmbeddedMessage> children = new ArrayList<>();

                    // close off all embedded messages until you get back to the forwarded one...
                    while (!currentEmbeddedMessages.isEmpty()) {
                        EmbeddedMessage currentEmbeddedMessage = currentEmbeddedMessages.pop();
                        embeddedMessages.add(currentEmbeddedMessage);
                        if (EmbeddedMessageFrame.FORWARDED.equals(currentEmbeddedMessage.frame)) {
                            while (!currentEmbeddedMessages.isEmpty() && EmbeddedMessageFrame.FORWARDED.equals(currentEmbeddedMessages.peek().frame)) {
                                children.add(currentEmbeddedMessages.pop()); // get layered forwards
                            }
                            currentEmbeddedMessage.setChildren(children);
                            break;
                        } else {
                            children.add(currentEmbeddedMessage);
                        }
                    }
                }

                for (EmbeddedMessage currentEmbeddedMessage : currentEmbeddedMessages) {
                    currentEmbeddedMessage.write(curLine);
                }
                */

                curLine = reader.readLine();
            }
            while (curLine != null && !curLine.startsWith(MBOX_RECORD_DIVIDER) && message.size() < MAIL_MAX_SIZE);

            try {
                return buildMimeMessage(message);
            } catch (Exception e) {
                log.error("Could not build mime message from message stream");
            }

        } else {

            curLine = reader.readLine();
            return read();
        }

        return close();
    }

    private MimeMessage buildMimeMessage(ByteArrayOutputStream s) throws Exception {
        return buildMimeMessage(new ByteArrayInputStream(s.toByteArray()));
    }

    private MimeMessage buildMimeMessage(ByteArrayInputStream s) throws Exception {
        String content = IOUtils.toString(s);
        mimeMessages.add(content);
        Session session = Session.getInstance(new Properties());
        InputStream is = new ByteArrayInputStream(content.getBytes());
        return new MimeMessage(session, is);
    }

    private MimeMessage close() {
        IOUtils.closeQuietly(reader);
        return null;
    }

    private static EmbeddedMessageFrame embeddedMessageStart(String line) {
        if (line.startsWith("--- Forwarded mail")) return EmbeddedMessageFrame.FORWARDED;
        if (line.startsWith("-----Original Message")) return EmbeddedMessageFrame.ORIGINAL;
        if (line.startsWith("------------------------[ Original Message ]")) return EmbeddedMessageFrame.ORIGINAL;
        return null;
    }

    private static EmbeddedMessageFrame embeddedMessageEnd(String line) {
        if (line.startsWith("---End of forwarded mail from")) return EmbeddedMessageFrame.FORWARDED;
        return null;
    }

    static enum EmbeddedMessageFrame {
        FORWARDED,
        ORIGINAL,
        WHITESPACE
    }

    static class EmbeddedMessage {

        private final EmbeddedMessageFrame frame;
        private final ByteArrayOutputStream outputStream;
        private boolean initialized = false;
        private boolean headers = true;
        private List<EmbeddedMessage> children;

        EmbeddedMessage(EmbeddedMessageFrame frame) {
            this.frame = frame;
            this.outputStream = new ByteArrayOutputStream();
        }

        void write(String line) {

            if (!initialized && StringUtils.isEmpty(line.trim())) return;

            try {
                initialized = true;
                if (headers && null != embeddedMessageStart(line)) {
                    return;
                } else if (headers && line.startsWith("From:")) {
                    line = line.replace("From:", "From\t:");
                } else if (headers && line.startsWith("To:")) {
                    line = line.replace("To:", "To\t:");
                } else if (headers && line.startsWith("Date:")) {
                    line = line.replace("Date:", "Date\t:");
                } else if (headers && line.startsWith("Cc:")) {
                    line = line.replace("Cc:", "Cc\t:");
                } else if (headers && line.startsWith("Subject:")) {
                    line = line.replace("Subject:", "Subject\t:");
                } else {
                    headers = false;
                }
                outputStream.write(line.getBytes(charsetName));
                outputStream.write('\n');
            } catch (IOException e) {

            }
        }

        public List<EmbeddedMessage> getChildren() {
            return children;
        }

        public void setChildren(List<EmbeddedMessage> children) {
            this.children = children;
        }
    }
}
