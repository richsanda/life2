package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.PersonService;
import w.whateva.service.email.sapi.sao.ApiPerson;

import java.util.List;

public class PersonWriter implements ItemWriter<ApiPerson> {

    private final PersonService personService;

    @Autowired
    public PersonWriter(PersonService personService) {
        this.personService = personService;
    }

    public void write(List<? extends ApiPerson> apiPersons) throws Exception {
        for (ApiPerson apiPerson : apiPersons) {
            personService.addPerson(apiPerson);
        }
    }
}
