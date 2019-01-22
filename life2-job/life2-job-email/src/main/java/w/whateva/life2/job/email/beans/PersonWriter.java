package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.api.person.dto.ApiPerson;

import java.util.List;

public class PersonWriter implements ItemWriter<ApiPerson> {

    private final PersonService personService;

    @Autowired
    public PersonWriter(PersonService personService) {
        this.personService = personService;
    }

    public void write(List<? extends ApiPerson> ApiPersons) throws Exception {
        for (ApiPerson ApiPerson : ApiPersons) {
            personService.addPerson(ApiPerson);
        }
    }
}
