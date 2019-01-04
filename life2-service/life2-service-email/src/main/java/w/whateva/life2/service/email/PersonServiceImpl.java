package w.whateva.life2.service.email;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import w.whateva.life2.api.email.PersonOperations;
import w.whateva.life2.api.email.dto.ApiPerson;
import w.whateva.life2.data.email.domain.Person;
import w.whateva.life2.data.email.repository.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonOperations {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void addPerson(ApiPerson ApiPerson) {
        Person person = new Person();
        BeanUtils.copyProperties(ApiPerson, person);
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
