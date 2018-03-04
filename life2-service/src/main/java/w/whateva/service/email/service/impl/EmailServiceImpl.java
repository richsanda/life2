package w.whateva.service.email.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import w.whateva.service.email.api.EmailOperations;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.email.api.dto.DtoEmailCount;
import w.whateva.service.email.api.dto.DtoPerson;
import w.whateva.service.email.data.domain.Email;
import w.whateva.service.email.data.domain.EmailCount;
import w.whateva.service.email.data.repository.EmailRepository;
import w.whateva.service.email.data.repository.PersonDao;
import w.whateva.service.email.api.PersonService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
public class EmailServiceImpl implements EmailOperations {

    private final EmailRepository emailRepository;
    private final PersonService personService;
    private final PersonDao personDao;

    @Autowired
    public EmailServiceImpl(EmailRepository emailRepository, PersonService personService, PersonDao personDao) {
        this.emailRepository = emailRepository;
        this.personService = personService;
        this.personDao = personDao;
    }

    public void addEmail(DtoEmail dtoEmail) {
        Email email = new Email();
        BeanUtils.copyProperties(dtoEmail, email);
        emailRepository.save(email);
    }

    @Override
    public DtoEmail readEmail(String key) {
        Email email = emailRepository.findOne(key);
        if (null == email) return null;
        DtoEmail dtoEmail = new DtoEmail();
        BeanUtils.copyProperties(email, dtoEmail);
        return dtoEmail;
    }

    @Override
    public List<String> allKeys() {
        return emailRepository.findAll().stream().map(Email::getId).collect(Collectors.toList());
    }

    @Override
    public List<DtoEmail> allEmails() {
        return emailRepository.findAllByOrderBySentAsc().stream().map(EmailServiceImpl::toApi).collect(Collectors.toList());
    }

    @Override
    public List<DtoEmail> allEmails(LocalDate after, LocalDate before, HashSet<String> names) {
        return personDao.getEmails(names,
                LocalDateTime.of(after, LocalTime.parse("00:00")),
                LocalDateTime.of(before, LocalTime.parse("23:59")))
                .stream()
                .map(EmailServiceImpl::toApi)
                .collect(Collectors.toList());
    }

    @Override
    public List<DtoEmailCount> allEmailCounts() {
        return personDao.getEmailCount().stream().map(EmailServiceImpl::toApi).collect(Collectors.toList());
    }

    @Override
    public List<DtoPerson> allPersons() {
        return personService.allPersons();
    }

    private static DtoEmail toApi(Email email) {
        if (null == email) return null;
        DtoEmail dtoEmail = new DtoEmail();
        BeanUtils.copyProperties(email, dtoEmail);
        return dtoEmail;
    }

    private static DtoEmailCount toApi(EmailCount emailCount) {
        if (null == emailCount) return null;
        DtoEmailCount dtoEmailCount = new DtoEmailCount();
        BeanUtils.copyProperties(emailCount, dtoEmailCount);
        return dtoEmailCount;
    }
}
