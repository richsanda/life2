package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.email.dto.ApiPerson;

import java.util.Set;
import java.util.stream.Collectors;

public class PersonProcessor implements ItemProcessor<ApiPerson, ApiPerson> {

    public ApiPerson process(ApiPerson ApiPerson) {
        ApiPerson.setEmails(processEmails(ApiPerson.getEmails()));
        return ApiPerson;
    }

    private Set<String> processEmails(Set<String> emails) {
        if (CollectionUtils.isEmpty(emails)) return emails;
        return emails.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }
}
