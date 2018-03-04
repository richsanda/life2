package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import w.whateva.service.email.api.dto.DtoPerson;

import java.util.Set;
import java.util.stream.Collectors;

public class PersonProcessor implements ItemProcessor<DtoPerson, DtoPerson> {

    public DtoPerson process(DtoPerson dtoPerson) {
        dtoPerson.setEmails(processEmails(dtoPerson.getEmails()));
        return dtoPerson;
    }

    private Set<String> processEmails(Set<String> emails) {
        if (CollectionUtils.isEmpty(emails)) return emails;
        return emails.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }
}
