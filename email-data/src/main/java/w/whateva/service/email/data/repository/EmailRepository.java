package w.whateva.service.email.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.service.email.data.domain.Email;

import java.util.List;

@Repository(value = "emails")
public interface EmailRepository extends MongoRepository<Email, String> {

    List<Email> findAllByOrderBySentAsc();
}
