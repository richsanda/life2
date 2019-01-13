package w.whateva.life2.service.email;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.whateva.life2.api.email.PersonService;
import w.whateva.life2.api.email.dto.ApiPerson;
import w.whateva.life2.data.email.domain.Person;
import w.whateva.life2.data.email.repository.PersonRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void addPerson(ApiPerson apiPerson) {

        if (null == apiPerson.getName()) return;

        Person person = new Person();
        person.setId(apiPerson.getName());
        BeanUtils.copyProperties(apiPerson, person);
        personRepository.save(person);
    }

    @Override
    public ApiPerson readPerson(String key) {
        Person person = personRepository.findById(key).orElse(null);
        if (null == person) return null;
        ApiPerson ApiPerson = new ApiPerson();
        BeanUtils.copyProperties(person, ApiPerson);
        return ApiPerson;
    }

    public List<String> allPersonKeys() {
        return personRepository.findAll().stream().map(Person::getId).collect(Collectors.toList());
    }

    public List<String> allNames() {
        return personRepository.findAll().stream().map(Person::getName).collect(Collectors.toList());
    }

    @Override
    public List<ApiPerson> allPersons() {
        return personRepository.findAllByOrderByNameAsc().stream().map(PersonServiceImpl::toApi).collect(Collectors.toList());
    }

    private static ApiPerson toApi(Person person) {
        if (null == person) return null;
        ApiPerson ApiPerson = new ApiPerson();
        BeanUtils.copyProperties(person, ApiPerson);
        return ApiPerson;
    }
}
