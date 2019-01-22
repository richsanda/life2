package w.whateva.life2.api.person;

import w.whateva.life2.api.person.dto.ApiPerson;

import java.util.List;

public interface PersonService {

    List<ApiPerson> allPersons();

    void addPerson(ApiPerson dtoPerson);

    ApiPerson readPerson(String key);
}
