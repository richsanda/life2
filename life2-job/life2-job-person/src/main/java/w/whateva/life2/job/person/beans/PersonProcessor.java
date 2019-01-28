package w.whateva.life2.job.person.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.xml.email.def.XmlPerson;

import java.util.Set;
import java.util.stream.Collectors;

public class PersonProcessor implements ItemProcessor<XmlPerson, ApiPerson> {

    private transient Logger log = LoggerFactory.getLogger(PersonProcessor.class);

    private String owner;

    public PersonProcessor(String owner) {
        this.owner = owner;
    }

    public ApiPerson process(XmlPerson xmlPerson) {

        ApiPerson result = new ApiPerson();

        result.setOwner(owner);
        result.setName(xmlPerson.getName());
        result.setUsername(xmlPerson.getUsername());
        result.setEmails(processEmails(xmlPerson.getEmails()));

        log.info("Processing person with name: " + result.getName());

        return result;
    }

    private Set<String> processEmails(Set<String> emails) {
        if (CollectionUtils.isEmpty(emails)) return emails;
        return emails.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }
}
