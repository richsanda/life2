package w.whateva.life2.data.email.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.email.domain.Email;

import java.util.List;

@Repository(value = "emails")
public interface EmailRepository extends MongoRepository<Email, String> {

    Email findUniqueByKey(String key);

    List<Email> findAllByOwnerAndTrove(String owner, String trove);
}
