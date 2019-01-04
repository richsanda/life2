package w.whateva.life2.data.email.repository;

import w.whateva.life2.data.email.domain.Email;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PersonDao {

    List<Email> getEmails(Set<String> names, LocalDateTime after, LocalDateTime before);
}
