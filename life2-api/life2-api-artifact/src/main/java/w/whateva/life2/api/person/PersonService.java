package w.whateva.life2.api.person;

import w.whateva.life2.api.person.dto.ApiPerson;

import java.util.List;

public interface PersonService {

    List<ApiPerson> findOwnerPersons(String owner);

    ApiPerson findMeAmongTheirs(String me, String them);

    void addPerson(ApiPerson dtoPerson);

    void updatePerson(ApiPerson dtoPerson);

    ApiPerson readPerson(String key);
}
