package w.whateva.service.email.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.whateva.service.email.data.domain.Person;
import w.whateva.service.email.data.domain.Email;
import w.whateva.service.email.data.repository.EmailRepository;
import w.whateva.service.email.data.repository.PersonRepository;
import w.whateva.service.email.sapi.PersonService;
import w.whateva.service.email.sapi.sao.ApiPerson;
import w.whateva.service.email.sapi.sao.ApiPerson;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;

    @Autowired
    public PersonServiceImpl(PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addPerson(ApiPerson apiPerson) {
        Person person = new Person();
        BeanUtils.copyProperties(apiPerson, person);
        repository.save(person);
    }

    @Override
    public ApiPerson readPerson(String key) {
        Person person = repository.findOne(key);
        if (null == person) return null;
        ApiPerson apiPerson = new ApiPerson();
        BeanUtils.copyProperties(person, apiPerson);
        return apiPerson;
    }

    @Override
    public List<String> allKeys() {
        return repository.findAll().stream().map(Person::getId).collect(Collectors.toList());
    }

    @Override
    public List<String> allNames() {
        return repository.findAll().stream().map(Person::getName).collect(Collectors.toList());
    }

    @Override
    public List<ApiPerson> allPersons() {
        return repository.findAllByOrderByNameAsc().stream().map(PersonServiceImpl::toApi).collect(Collectors.toList());
    }

    private static ApiPerson toApi(Email email) {
        if (null == email) return null;
        ApiPerson apiPerson = new ApiPerson();
        BeanUtils.copyProperties(email, apiPerson);
        return apiPerson;
    }

    private static ApiPerson toApi(Person person) {
        if (null == person) return null;
        ApiPerson apiPerson = new ApiPerson();
        BeanUtils.copyProperties(person, apiPerson);
        return apiPerson;
    }
}
