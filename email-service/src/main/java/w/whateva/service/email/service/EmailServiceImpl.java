package w.whateva.service.email.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import w.whateva.service.email.data.domain.Email;
import w.whateva.service.email.data.repository.EmailRepository;
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.sao.ApiEmail;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final EmailRepository repository;

    @Autowired
    public EmailServiceImpl(EmailRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addEmail(ApiEmail apiEmail) {
        Email email = new Email();
        BeanUtils.copyProperties(apiEmail, email);
        repository.save(email);
    }

    @Override
    public ApiEmail readEmail(String key) {
        Email email = repository.findOne(key);
        if (null == email) return null;
        ApiEmail apiEmail = new ApiEmail();
        BeanUtils.copyProperties(email, apiEmail);
        return apiEmail;
    }

    @Override
    public List<String> allKeys() {
        return repository.findAll().stream().map(Email::getId).collect(Collectors.toList());
    }

    @Override
    public List<ApiEmail> allEmails() {
        return repository.findAllByOrderBySentAsc().stream().map(EmailServiceImpl::toApi).collect(Collectors.toList());
    }

    private static ApiEmail toApi(Email email) {
        if (null == email) return null;
        ApiEmail apiEmail = new ApiEmail();
        BeanUtils.copyProperties(email, apiEmail);
        return apiEmail;
    }
}
