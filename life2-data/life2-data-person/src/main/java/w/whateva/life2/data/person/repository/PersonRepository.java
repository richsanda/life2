package w.whateva.life2.data.person.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.person.domain.Person;

import java.util.Collection;
import java.util.List;

@Repository(value = "persons")
public interface PersonRepository extends MongoRepository<Person, String> {

    List<Person> findAllByOrderByNameAsc();

    List<Person> findByNameIn(Collection<String> names);
}
