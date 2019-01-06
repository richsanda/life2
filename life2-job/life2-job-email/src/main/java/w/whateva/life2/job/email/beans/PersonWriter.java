package w.whateva.life2.job.email.beans;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.life2.api.email.PersonOperations;
import w.whateva.life2.api.email.dto.ApiPerson;

import java.util.List;

public class PersonWriter implements ItemWriter<ApiPerson> {

    private final PersonOperations personService;

    @Autowired
    public PersonWriter(PersonOperations personService) {
        this.personService = personService;
    }

    public void write(List<? extends ApiPerson> ApiPersons) throws Exception {
        for (ApiPerson ApiPerson : ApiPersons) {
            personService.addPerson(ApiPerson);
        }
    }
}
