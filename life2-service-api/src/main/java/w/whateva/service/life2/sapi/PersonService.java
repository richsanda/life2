package w.whateva.service.life2.sapi;

import w.whateva.service.life2.api.dto.DtoPerson;

import java.util.List;

public interface PersonService {

    void addPerson(DtoPerson dtoPerson);

    List<DtoPerson> allPersons();
}
