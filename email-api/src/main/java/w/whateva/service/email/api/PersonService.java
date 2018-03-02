package w.whateva.service.email.api;

import w.whateva.service.email.api.dto.DtoPerson;

import java.util.List;

public interface PersonService {

    void addPerson(DtoPerson dtoPerson);

    List<DtoPerson> allPersons();
}
