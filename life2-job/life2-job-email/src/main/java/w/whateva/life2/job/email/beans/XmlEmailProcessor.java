package w.whateva.life2.job.email.beans;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import w.whateva.life2.service.email.dto.ApiEmail;
import w.whateva.life2.service.email.dto.ApiGroupMessage;
import w.whateva.life2.xml.email.def.XmlEmail;
import w.whateva.life2.xml.email.def.XmlGroupMessage;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class XmlEmailProcessor implements ItemProcessor<XmlEmail, ApiEmail> {

    private transient Logger log = LoggerFactory.getLogger(XmlEmailProcessor.class);

    private static final String KEY_SEPARATOR = ".";

    public ApiEmail process(XmlEmail xmlEmail) {

        ApiEmail apiEmail = xmlEmail instanceof XmlGroupMessage
                ? convert((XmlGroupMessage) xmlEmail)
                : convert(xmlEmail);

        log.debug("Processing XML email with key: " + apiEmail.getKey());

        return apiEmail;
    }

    private static ApiEmail convert(XmlGroupMessage groupMessage) {

        ApiGroupMessage result = new ApiGroupMessage();
        BeanUtils.copyProperties(groupMessage, result);
        result.setSent(getSent(groupMessage));
        result.setKey(createKey(groupMessage));

        if (StringUtils.isEmpty(result.getSubject())) {
            result.setSubject(groupMessage.getTopic());
        }

        return result;
    }

    private static ApiEmail convert(XmlEmail email) {

        ApiEmail result = new ApiEmail();
        BeanUtils.copyProperties(email, result);
        result.setKey(createKey(email));
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

        return email.getId();

        //return email.getFrom() +
        //        KEY_SEPARATOR +
        //        email.getId();
    }

    private static ZonedDateTime getSent(XmlEmail email) {
        if (null == email.getSent()) return null;
        return email.getSent().toInstant(ZoneOffset.UTC).atZone(ZoneId.of("UTC"));
    }
}
