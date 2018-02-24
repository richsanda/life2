package w.whateva.service.email.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.whateva.service.email.data.domain.Email;
import w.whateva.service.email.data.domain.EmailCount;
import w.whateva.service.email.data.domain.Person;
import w.whateva.service.email.data.repository.EmailRepository;
import w.whateva.service.email.data.repository.PersonDao;
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.sapi.sao.ApiEmailCount;
import w.whateva.service.email.sapi.sao.ApiPerson;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    private final PersonDao personDao;

    @Autowired
    public EmailServiceImpl(EmailRepository emailRepository, PersonDao personDao) {
        this.emailRepository = emailRepository;
        this.personDao = personDao;
    }

    @Override
    public void addEmail(ApiEmail apiEmail) {
        Email email = new Email();
        BeanUtils.copyProperties(apiEmail, email);
        emailRepository.save(email);
    }

    @Override
    public ApiEmail readEmail(String key) {
        Email email = emailRepository.findOne(key);
        if (null == email) return null;
        ApiEmail apiEmail = new ApiEmail();
        BeanUtils.copyProperties(email, apiEmail);
        return apiEmail;
    }

    @Override
    public List<String> allKeys() {
        return emailRepository.findAll().stream().map(Email::getId).collect(Collectors.toList());
    }

    @Override
    public List<ApiEmail> allEmails() {
        return emailRepository.findAllByOrderBySentAsc().stream().map(EmailServiceImpl::toApi).collect(Collectors.toList());
    }

    @Override
    public List<ApiEmailCount> emailCounts() {
        return personDao.getEmailCount().stream().map(EmailServiceImpl::toApi).collect(Collectors.toList());
    }

    public List<ApiEmail> emails(Set<String> names, LocalDateTime after, LocalDateTime before) {
        return personDao.getEmails(names, after, before).stream().map(EmailServiceImpl::toApi).collect(Collectors.toList());
    }

    private static ApiEmail toApi(Email email) {
        if (null == email) return null;
        ApiEmail apiEmail = new ApiEmail();
        BeanUtils.copyProperties(email, apiEmail);
        return apiEmail;
    }

    private static ApiPerson toApi(Person person) {
        if (null == person) return null;
        ApiPerson apiPerson = new ApiPerson();
        BeanUtils.copyProperties(person, apiPerson);
        return apiPerson;
    }

    private static ApiEmailCount toApi(EmailCount emailCount) {
        if (null == emailCount) return null;
        ApiEmailCount apiEmailCount = new ApiEmailCount();
        BeanUtils.copyProperties(emailCount, apiEmailCount);
        return apiEmailCount;
    }
}
