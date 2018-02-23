package w.whateva.service.email.data.repository;

import w.whateva.service.email.data.domain.EmailCount;

import java.util.List;

public interface PersonDao {

    List<EmailCount> getEmailCount();
}
