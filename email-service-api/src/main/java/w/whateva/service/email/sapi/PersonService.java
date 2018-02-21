package w.whateva.service.email.sapi;

import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.sapi.sao.ApiPerson;

import java.util.List;

public interface PersonService {

    void addPerson(ApiPerson person);

    ApiPerson readPerson(String key);

    List<String> allKeys();

    List<String> allNames();

    List<ApiPerson> allPersons();
}
