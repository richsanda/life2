package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiGroupMessage;
import w.whateva.life2.xml.email.def.XmlEmail;
import w.whateva.life2.xml.email.def.XmlGroupMessage;

import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class XmlEmailProcessor implements ItemProcessor<XmlEmail, ApiEmail> {

    private final String emailAddressParserType;
    private final String defaultTo;

    public XmlEmailProcessor(String emailAddressParserType, String defaultTo) {
        this.emailAddressParserType = emailAddressParserType;
        this.defaultTo = defaultTo;
    }

    public ApiEmail process(XmlEmail xmlEmail) {

        ApiEmail apiEmail = xmlEmail instanceof XmlGroupMessage
                ? convert((XmlGroupMessage) xmlEmail)
                : convert(xmlEmail);

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
            apiEmail.setSubject(((ApiGroupMessage) apiEmail).getTopic());
        }

        return apiEmail;
    }

    private static ApiEmail convert(XmlGroupMessage groupMessage) {

        ApiGroupMessage result = new ApiGroupMessage();

        result.setFrom(groupMessage.getFrom());
        result.setSent(groupMessage.getSent());
        result.setTo(groupMessage.getTo());
        result.setSubject(groupMessage.getSubject());
        result.setBody(groupMessage.getBody());

        result.setGroupType(groupMessage.getGroupType());
        result.setGroupName(groupMessage.getGroupName());
        result.setMessageId(groupMessage.getMessageId());

        result.setTopic(groupMessage.getTopic());

        return result;
    }

    private static ApiEmail convert(XmlEmail email) {

        ApiEmail result = new ApiEmail();
        result.setFrom(email.getFrom());
        result.setSent(email.getSent());
        result.setTo(email.getTo());
        result.setSubject(email.getSubject());
        result.setBody(email.getBody());
        return result;
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
