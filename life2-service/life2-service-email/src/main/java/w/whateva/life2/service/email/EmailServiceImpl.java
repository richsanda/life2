package w.whateva.life2.service.email;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.email.PersonOperations;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.repository.EmailRepository;
import w.whateva.life2.data.email.repository.PersonDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
public class EmailServiceImpl implements EmailOperations {

    private final EmailRepository emailRepository;
    private final PersonOperations personService;
    private final PersonDao personDao;

    @Autowired
    public EmailServiceImpl(EmailRepository emailRepository, PersonOperations personService, PersonDao personDao) {
        this.emailRepository = emailRepository;
        this.personService = personService;
        this.personDao = personDao;
    }

    public void add(ApiEmail ApiEmail) {
        Email email = new Email();
        BeanUtils.copyProperties(ApiEmail, email);
        emailRepository.save(email);
    }

    @Override
    public ApiEmail read(String key) {
        Email email = emailRepository.findById(key).orElse(null);
        if (null == email) return null;
        ApiEmail ApiEmail = new ApiEmail();
        BeanUtils.copyProperties(email, ApiEmail);
        return ApiEmail;
    }

    @Override
    public List<ApiEmail> search(LocalDate after, LocalDate before, HashSet<String> names) {
        return personDao.getEmails(names,
                LocalDateTime.of(after, LocalTime.parse("00:00")),
                LocalDateTime.of(before, LocalTime.parse("23:59")))
                .stream()
                .map(EmailServiceImpl::toApi)
                .collect(Collectors.toList());
    }

    private static ApiEmail toApi(Email email) {
        if (null == email) return null;
        ApiEmail ApiEmail = new ApiEmail();
        BeanUtils.copyProperties(email, ApiEmail);
        return ApiEmail;
    }
}
