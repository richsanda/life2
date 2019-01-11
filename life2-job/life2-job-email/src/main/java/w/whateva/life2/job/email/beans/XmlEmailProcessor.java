package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemProcessor;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.xml.email.def.XmlEmail;

public class XmlEmailProcessor implements ItemProcessor<XmlEmail, ApiEmail> {

    @Override
    public ApiEmail process(XmlEmail email) throws Exception {

        ApiEmail result = new ApiEmail();
        result.setFrom(email.getFrom());
        result.setSent(email.getSent());
        result.setTo(email.getTo());
        result.setSubject(email.getSubject());
        result.setBody(email.getBody());
        return result;
    }
}
