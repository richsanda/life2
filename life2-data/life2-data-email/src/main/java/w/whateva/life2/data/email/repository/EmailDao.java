package w.whateva.life2.data.email.repository;

import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.person.domain.Person;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EmailDao {

    List<Email> getEmails(Set<String> who, Set<String> from, Set<String> to, LocalDateTime after, LocalDateTime before);

    List<Person> getSenders();
}
