package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;
import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.sapi.sao.ApiPerson;

import java.util.Arrays;
import java.util.HashSet;

public class PersonProcessor implements ItemProcessor<ApiPerson, ApiPerson> {

    public ApiPerson process(ApiPerson apiPerson) throws Exception {
        return apiPerson;
    }
}
