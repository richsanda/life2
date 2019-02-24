package w.whateva.life2.data.email.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.email.domain.Email;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository(value = "emails")
public interface EmailRepository extends MongoRepository<Email, String> {

    List<Email> findAllByOrderBySentAsc();

    Email findUniqueByKey();

    List<Email> findByFromIndexInAndToIndexInAndSentBetween(Collection<String> fromIndices, Collection<String> toIndices, LocalDate after, LocalDate before);
}
