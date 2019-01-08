package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiGroupMessage;

import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailProcessor implements ItemProcessor<ApiEmail, ApiEmail> {

    private final String emailAddressParserType;
    private final String defaultTo;

    public EmailProcessor(String emailAddressParserType, String defaultTo) {
        this.emailAddressParserType = emailAddressParserType;
        this.defaultTo = defaultTo;
    }

    public ApiEmail process(ApiEmail apiEmail) {

        if (null == apiEmail.getTo()) {
            apiEmail.setTo(defaultTo);
        }

        switch (emailAddressParserType.toLowerCase()) {
            case "simple":
                apiEmail.setTos(toSimpleAddresses(apiEmail.getTo()));
                break;
            case "internet":
                apiEmail.setTos(toEmailAddresses(apiEmail.getTo()));
                break;
            default:
                throw new IllegalArgumentException("Unknown email address parser type");
        }

        if (apiEmail instanceof ApiGroupMessage && null == apiEmail.getSubject()) {
            apiEmail.setSubject(apiEmail.getSubject());
        }

        /*
        if (!StringUtils.isEmpty(ApiEmail.getMessage())) {
            try {
                String content = ApiEmail.getMessage();
                Session s = Session.getInstance(new Properties());
                InputStream is = new ByteArrayInputStream(content.getBytes());
                MimeMessage message = new MimeMessage(s, is);
                Map<String, String> headers = new HashMap<>();
                message.getAllHeaderLines();
                for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
                    Header h = e.nextElement();
                    headers.put(h.getName(), h.getValue());
                }
                if (!headers.containsKey("Message-ID")) {
                    System.out.println(String.format("%s has no message id", ApiEmail.getSubject()));
                } else {
                    System.out.println(headers.get("Message-ID"));
                }
            } catch (MessagingException e) {

            }
        }
        */

        return apiEmail;
    }

    private static Set<String> toSimpleAddresses(String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        return Arrays
                .stream(addressList.split("\\s*[,;]\\s*"))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private static Set<String> toEmailAddresses (String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        try {
            return Arrays
                    .stream(InternetAddress.parse(addressList))
                    .map(InternetAddress::getAddress)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println("Problem with: " + addressList);
            // e.printStackTrace();
        }
        return new HashSet<>();
    }
}