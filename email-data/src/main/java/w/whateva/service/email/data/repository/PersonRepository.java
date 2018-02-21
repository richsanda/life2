package w.whateva.service.email.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.service.email.data.domain.Email;
import w.whateva.service.email.data.domain.Person;

import java.util.List;

@Repository(value = "persons")
public interface PersonRepository extends MongoRepository<Person, String> {

    List<Person> findAllByOrderByNameAsc();
}
