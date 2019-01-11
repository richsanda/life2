package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.email.dto.ApiPerson;
import w.whateva.life2.xml.email.def.XmlPerson;

import java.util.Set;
import java.util.stream.Collectors;

public class PersonProcessor implements ItemProcessor<XmlPerson, ApiPerson> {

    public ApiPerson process(XmlPerson xmlPerson) {
        ApiPerson result = new ApiPerson();
        result.setName(xmlPerson.getName());
        result.setEmails(processEmails(xmlPerson.getEmails()));
        return result;
    }

    private Set<String> processEmails(Set<String> emails) {
        if (CollectionUtils.isEmpty(emails)) return emails;
        return emails.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }
}
