package w.whateva.service.life2.service.impl;

import org.springframework.stereotype.Service;
import w.whateva.service.life2.api.dto.DtoPerson;
import w.whateva.service.life2.sapi.PersonService;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    @Override
    public void addPerson(DtoPerson dtoPerson) {

    }

    @Override
    public List<DtoPerson> allPersons() {
        return null;
    }
}
