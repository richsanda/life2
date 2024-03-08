package w.whateva.life2.job.person.beans;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.api.person.dto.ApiPerson;

public class PersonWriter implements ItemWriter<ApiPerson> {

    private final PersonService personService;

    @Autowired
    public PersonWriter(PersonService personService) {
        this.personService = personService;
    }

    public void write(Chunk<? extends ApiPerson> ApiPersons) throws Exception {
        for (ApiPerson ApiPerson : ApiPersons) {
            personService.updatePerson(ApiPerson);
        }
    }
}
