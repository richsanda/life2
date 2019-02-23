package w.whateva.life2.job.person.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.xml.email.def.XmlGroup;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupProcessor implements ItemProcessor<XmlGroup, ApiPerson> {

    private transient Logger log = LoggerFactory.getLogger(GroupProcessor.class);

    private String owner;

    public GroupProcessor(String owner) {
        this.owner = owner;
    }

    public ApiPerson process(XmlGroup xmlGroup) {

        ApiPerson result = new ApiPerson();

        result.setGroup(true);
        result.setOwner(owner);
        result.setName(xmlGroup.getName());
        result.setMembers(processMembers(xmlGroup.getMembers()));

        log.info("Processing group with name: " + result.getName());

        return result;
    }

    private Set<String> processMembers(Set<String> emails) {
        if (CollectionUtils.isEmpty(emails)) return emails;
        return emails.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }
}
