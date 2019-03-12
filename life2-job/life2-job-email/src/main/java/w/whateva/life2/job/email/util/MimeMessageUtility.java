package w.whateva.life2.job.email.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MimeMessageUtility {

    private static Logger log = LoggerFactory.getLogger(MimeMessageUtility.class);

    private static final String KEY_SEPARATOR = ".";

    private static final String charsetName = "utf-8";

    private static final List<SimpleDateFormat> DATE_FORMAT = new ArrayList<>();

    static {
        DATE_FORMAT.add(new MailDateFormat());
        DATE_FORMAT.add(new SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm:ss a Z"));
        DATE_FORMAT.add(new SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm:ss a"));
        DATE_FORMAT.add(new SimpleDateFormat("EEE, MMM d, yyyy hh:mm a"));
        DATE_FORMAT.add(new SimpleDateFormat("MM/dd/yy hh:mm:ss a Z"));
    }

    public static MimeMessage buildMimeMessage(ByteArrayOutputStream s) throws Exception {
        return buildMimeMessage(new ByteArrayInputStream(s.toByteArray()));
    }

    static MimeMessage buildMimeMessage(InputStream s) throws Exception {
        String content = IOUtils.toString(s);
        Session session = Session.getInstance(new Properties());
        InputStream is = new ByteArrayInputStream(content.getBytes());
        return new MimeMessage(session, is);
    }

    private static Date makeSentFromHeaders(Map<String, String> headers) {
        String value = getSentHeaderValue(headers);
        if (null == value) return null;
        return makeSentFromHeaderValue(value);
    }

    private static Date makeSentFromHeaderValue(String value) {

        for (DateFormat format : DATE_FORMAT) {
            try {
                return format.parse(value);
            } catch (Exception e) {
                // try the next one
            }
        }

        log.error("Could not parse date from: " + value);

        return null;
    }

    private static String getSentHeaderValue(Map<String, String> headers) {
        if (CollectionUtils.isEmpty(headers)) return null;
        if (headers.containsKey("Date")) return headers.get("Date");
        if (headers.containsKey("Sent")) return headers.get("Sent");
        return null;
    }

    public static String createKey(Map<String, String> headers, MimeMessageParser parser) throws IllegalArgumentException {

        if (headers.get("Message-ID") != null) return headers.get("Message-ID");

        try {

            Date sentDate = getSentDate(headers, parser);

            if (null == sentDate) return null;

            return "<" +
                    String.valueOf(sentDate.toInstant().getEpochSecond()) +
                    KEY_SEPARATOR +
                    getFrom(headers, parser).replace('%','_').replace(' ', '_') +
                    ">";

        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create a key for this one");
        }
    }

    public static String toString(MimeMessage message) throws IOException, MessagingException {
        OutputStream os = new ByteArrayOutputStream();
        message.writeTo(os);
        return os.toString();
    }

    public static Date getSentDate(Map<String, String> headers, MimeMessageParser parser) throws IllegalArgumentException {
        try {
            Date result = parser.getMimeMessage().getSentDate();
            if (null != result) return result;
            return makeSentFromHeaders(headers);
        } catch (Exception e) {
            try {
                return makeSentFromHeaders(headers);
            } catch (Exception e2) {

            }
        }
        throw new IllegalArgumentException("Could not parse date for this one");
    }

    public static String getFrom(Map<String, String> headers, MimeMessageParser parser) {

        String result = null;

        try {
            result = parser.getFrom();
            InternetAddress.parse(result);
            return result;
        } catch (MessagingException e) {
            log.error("Could not parse from using mime message parser: " + result + " (" + e.getMessage() + ")");
        } catch (Exception e) {
            log.error("Could not parse from using mime message parser: " + result + " (" + e.getMessage() + ")");
        }
        if (headers.containsKey("From")) {
            return headers.get("From");
        }

        log.error("Could not get from either from parser or from headers");

        return null;
    }

    public static String getTo(Map<String, String> headers, MimeMessageParser parser) {
        try {
            List<Address> result = parser.getTo();
            if (null != result) {
                return result
                        .stream()
                        .map(Address::toString)
                        .collect(Collectors.joining(", "));
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (headers.containsKey("To")) {
            return headers.get("To");
        }
        return null;
    }

    public static String getCc(Map<String, String> headers, MimeMessageParser parser) {
        try {
            List<Address> result = parser.getCc();
            if (null != result) {
                return result
                        .stream()
                        .map(Address::toString)
                        .collect(Collectors.joining(", "));
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (headers.containsKey("Cc")) {
            return headers.get("Cc");
        }
        return null;
    }

    public static String makeKeyFromHeaders(Map<String, String> headers) {
        Iterator<Map.Entry<String, String>> headerIterator = headers.entrySet().iterator();
        if (headerIterator.hasNext()) {
            Map.Entry<String, String> entry = headerIterator.next();
            return entry.getKey() + entry.getValue();
        }
        return null;
    }

    public static Map<String, String> getHeaders(MimeMessage message) {
        Map<String, String> headers = new HashMap<>();
        try {
            message.getAllHeaderLines();
            for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
                Header h = e.nextElement();
                headers.put(h.getName(), h.getValue());
            }
        } catch (Exception e) {

        }
        return headers;
    }

    public static ByteArrayOutputStream processHistoricEmail(InputStream inputStream) throws IOException, UnsupportedEncodingException {

        boolean headers = true;

        Reader streamReader = new InputStreamReader(inputStream, charsetName);
        BufferedReader reader = new BufferedReader(streamReader);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Map<String, String> headerLines = new LinkedHashMap<>();
        String lastHeader = null;
        boolean forwardedFromCadetSanda = false;

        String line = reader.readLine();
        while (line != null) {
            if (headers && isEmbeddedMessageStart(line)) {
                StringBuilder forwardedLine = new StringBuilder(line);
                line = reader.readLine();
                while (!StringUtils.isEmpty(line)) {
                    forwardedLine.append(line);
                    line = reader.readLine();
                }
                forwardedFromCadetSanda = forwardedStartFromCadetSanda(forwardedLine.toString());
                while (StringUtils.isEmpty(line)) {
                    line = reader.readLine();
                }
                continue;
            } else if (headers && line.contains(":")) {
                int indexOfColon = line.indexOf(':');
                String header = line.substring(0, indexOfColon).trim();
                headerLines.put(header, line.substring(indexOfColon + 1).trim());
                lastHeader = header;
                line = reader.readLine();
                continue;
            } else if (headers && StringUtils.isEmpty(line)) { // write all headers
                headers = false;
                for (Map.Entry<String, String> entry : headerLines.entrySet()) {
                    if (null == entry.getKey()) {
                        log.error("null header key");
                        continue;
                    }
                    outputStream.write(entry.getKey().getBytes(charsetName));
                    outputStream.write("\t: ".getBytes(charsetName));
                    String value = entry.getValue();
                    if (entry.getKey().equals("From") || entry.getKey().equals("To")) {
                       value = cleanEmail(value);
                    }
                    outputStream.write(value.getBytes(charsetName));
                    outputStream.write('\n');
                }
                outputStream.write('\n');

            } else if (headers) { // append
                String value = headerLines.get(lastHeader);
                headerLines.put(lastHeader, value + ' ' + line);
                // line = reader.readLine();
                // continue;
            } else if (isEmbeddedMessageStart(line)) {
                if (forwardedFromCadetSanda) { // restart
                    forwardedFromCadetSanda = false;
                    headers = true;
                    outputStream = new ByteArrayOutputStream();
                    line = reader.readLine();
                    continue;
                }
            } else if (isEmbeddedMessageEnd(line)) {
                line = reader.readLine();
                continue;
            }
            outputStream.write(line.getBytes(charsetName));
            outputStream.write('\n');
            line = reader.readLine();
        }

        return outputStream;
    }

    private static boolean isEmbeddedMessageStart(String line) {

        // if (line.startsWith("--- Forwarded mail")) return EmbeddedMessageFrame.FORWARDED;
        if (line.startsWith("--- Forwarded mail")) return true;
        // if (line.startsWith("-----Original Message")) return EmbeddedMessageFrame.ORIGINAL;
        if (line.startsWith("-----Original Message")) return true;
        // if (line.startsWith("------------------------[ Original Message ]")) return EmbeddedMessageFrame.ORIGINAL;
        if (line.startsWith("------------------------[ Original Message ]")) return true;

        return false;
    }

    private static boolean isEmbeddedMessageEnd(String line) {

        // / if (line.startsWith("---End of forwarded mail from")) return EmbeddedMessageFrame.FORWARDED;
        if (line.startsWith("---End of forwarded mail from")) return true;

        return false;
    }

    private static final String FORWARDED_MAIL_START = "--- Forwarded mail from ";
    private static final String FORWARDED_MAIL_END = "---End of forwarded mail from ";
    private static final String CADET_SANDA_FORWARDING_EMAIL = "SandaRD98%CS24%USAFA@cadetmail3.usafa.af.mil";

    private static String forwardedStartFrom(String line) {
        if (StringUtils.isEmpty(line)) return null;
        if (line.startsWith(FORWARDED_MAIL_START)) {
            return line.substring(FORWARDED_MAIL_START.length());
        }
        return null;
    }

    private static String forwardedEndFrom(String line) {
        if (StringUtils.isEmpty(line)) return null;
        if (line.startsWith(FORWARDED_MAIL_END)) {
            return line.substring(FORWARDED_MAIL_END.length());
        }
        return null;
    }

    private static boolean forwardedStartFromCadetSanda(String line) {
        return CADET_SANDA_FORWARDING_EMAIL.equals(forwardedStartFrom(line));
    }

    private static String cleanEmail(String in) {
        if (in.contains(" @ ")) {
            in = in.replaceAll(" @ ", "@");
        }
        if (in.contains("[") && in.contains("]")) {
            in = in.replaceAll("]", ">").replaceAll("\\[","<");
        }
        in = in.replaceAll("mailto:", "");

        return Arrays.stream(in.split(","))
                .map(MimeMessageUtility::substringBeforeSecondAt)
                .collect(Collectors.joining(","));
    }

    private static String substringBeforeSecondAt(String in) {
        if (in.indexOf("@", in.indexOf("@") + 1) > 0) {
            return in.substring(0, in.indexOf("@", in.indexOf("@") + 1));
        } else {
            return in;
        }
    }
}