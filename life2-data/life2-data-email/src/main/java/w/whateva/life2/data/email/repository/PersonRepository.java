package w.whateva.life2.data.email.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.email.domain.Person;

import java.util.List;

@Repository(value = "persons")
public interface PersonRepository extends MongoRepository<Person, String> {

    List<Person> findAllByOrderByNameAsc();
}
