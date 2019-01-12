package w.whateva.life2.job.email.beans;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiGroupMessage;
import w.whateva.life2.xml.email.def.XmlEmail;
import w.whateva.life2.xml.email.def.XmlGroupMessage;

public class XmlEmailProcessor implements ItemProcessor<XmlEmail, ApiEmail> {

    private static final String KEY_SEPARATOR = ":";

    private final String defaultTo;

    public XmlEmailProcessor(String defaultTo) {
        this.defaultTo = defaultTo;
    }

    public ApiEmail process(XmlEmail xmlEmail) {

        ApiEmail apiEmail = xmlEmail instanceof XmlGroupMessage
                ? convert((XmlGroupMessage) xmlEmail)
                : convert(xmlEmail);

        if (null == apiEmail.getTo()) {
            apiEmail.setTo(defaultTo);
        }

        System.out.println(apiEmail.getKey());

        return apiEmail;
    }

    private static ApiEmail convert(XmlGroupMessage groupMessage) {

        ApiGroupMessage result = new ApiGroupMessage();
        BeanUtils.copyProperties(groupMessage, result);
        result.setKey(createKey(groupMessage));

        if (StringUtils.isEmpty(result.getSubject())) {
            result.setSubject(groupMessage.getTopic());
        }

        return result;
    }

    private static ApiEmail convert(XmlEmail email) {

        ApiEmail result = new ApiEmail();
        BeanUtils.copyProperties(email, result);
        return result;
    }

    private static String createKey(XmlGroupMessage groupMessage) {

        return groupMessage.getGroupType() +
                KEY_SEPARATOR +
                groupMessage.getGroupName() +
                KEY_SEPARATOR +
                groupMessage.getMessageId();
    }

    private static String createKey(XmlEmail email) {

        return email.getFrom() +
                KEY_SEPARATOR +
                email.getId();
    }
}
