package w.whateva.life2.service.person.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.data.person.domain.Person;
import w.whateva.life2.data.person.repository.PersonRepository;

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
    public void updatePerson(ApiPerson apiPerson) {

        if (null == apiPerson.getName()) return;

        Person person = personRepository.findById(apiPerson.getName()).orElse(createPerson(apiPerson.getName()));

        if (!CollectionUtils.isEmpty(apiPerson.getEmails())) {
            person.getEmails().addAll(apiPerson.getEmails());
        }

        if (!CollectionUtils.isEmpty(apiPerson.getGroups())) {
            person.getGroups().addAll(apiPerson.getGroups());
        }

        if (apiPerson.isGroup() && !CollectionUtils.isEmpty(apiPerson.getMembers())) {
            // person.getMembers().addAll(apiPerson.getMembers());
            apiPerson.getMembers().forEach(m -> {
                ApiPerson p = new ApiPerson();
                p.setName(m);
                p.setGroups(Collections.singleton(apiPerson.getName()));
                updatePerson(p);
            });
        }

        if (null != apiPerson.getOwner()) {
            person.setOwner(apiPerson.getOwner());
        }
        if (null != apiPerson.getUsername()) {
            person.setUsername(apiPerson.getUsername());
        }

        personRepository.save(person);
    }

    private static Person createPerson(String name) {
        Person result = new Person();
        result.setId(name.toLowerCase());
        result.setName(name);
        return result;
    }

    @Override
    public ApiPerson readPerson(String key) {
        Person person = personRepository.findById(key.toLowerCase()).orElse(null);
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
        return personRepository.findAllByOrderByNameAsc()
                .stream()
                .map(PersonServiceImpl::toApi)
                .collect(Collectors.toList());
    }

    public List<ApiPerson> findOwnerPersons(String owner) {
        return personRepository.findAllByOwner(owner)
                .stream()
                .map(PersonServiceImpl::toApi)
                .collect(Collectors.toList());
    }


    public ApiPerson findMeAmongTheirs(String myUsername, String theirUsername) {
        return toApi(personRepository.findByUsernameAndOwner(myUsername, theirUsername));
    }

    private static ApiPerson toApi(Person person) {
        if (null == person) return null;
        ApiPerson ApiPerson = new ApiPerson();
        BeanUtils.copyProperties(person, ApiPerson);
        return ApiPerson;
    }
}
