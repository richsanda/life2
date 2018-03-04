package w.whateva.service.email.data.repository;

import w.whateva.service.email.data.domain.Email;
import w.whateva.service.email.data.domain.EmailCount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PersonDao {

    List<EmailCount> getEmailCount();

    public List<Email> getEmails(Set<String> names, LocalDateTime after, LocalDateTime before);
}
