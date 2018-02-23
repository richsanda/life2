package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.sapi.sao.ApiPerson;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PersonProcessor implements ItemProcessor<ApiPerson, ApiPerson> {

    public ApiPerson process(ApiPerson apiPerson) {
        apiPerson.setEmails(processEmails(apiPerson.getEmails()));
        return apiPerson;
    }

    private Set<String> processEmails(Set<String> emails) {
        if (CollectionUtils.isEmpty(emails)) return emails;
        return emails.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }
}
