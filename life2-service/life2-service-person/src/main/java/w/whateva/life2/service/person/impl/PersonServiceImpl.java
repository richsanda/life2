package w.whateva.life2.service.person.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.person.PersonOperations;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.data.person.domain.Person;
import w.whateva.life2.data.person.repository.PersonRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService, PersonOperations {

    private final PersonRepository personRepository;
    private final Map<String, String> emailsToPersons = new HashMap<>();

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


    private Set<String> emailToPersonNames(Set<String> emails) {
        Set<String> result = new LinkedHashSet<>();
        Set<String> lookup = new LinkedHashSet<>();
        emails.forEach(e -> {
            if (emailsToPersons.containsKey(e)) {
                result.add(emailsToPersons.get(e));
            } else {
                lookup.add(e);
            }
        });
        if (!CollectionUtils.isEmpty(lookup)) {
            Set<Person> persons = personRepository.findByEmailsIn(emails);
            if (!CollectionUtils.isEmpty(persons)) {
                persons.forEach(p -> {
                    p.getEmails().forEach(e -> {
                        emailsToPersons.put(e, p.getName());
                        result.add(p.getName());
                    });
                });
            }
        }
        return result;
    }

    @Override
    public String emailToPersonName(String email) {
        return emailToPersonNames(Collections.singleton(email)).stream().findFirst().orElse(null);
    }

    @Override
    public Set<String> findEmailAddresses(Set<String> persons) {

        if (CollectionUtils.isEmpty(persons)) return null; // null means unspecified

        return personRepository.findByNameIn(persons)
                .stream()
                .map(Person::getEmails)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
