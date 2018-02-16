package w.whateva.service.email.web;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import w.whateva.service.email.api.EmailOperations;
import w.whateva.service.email.api.dto.Email;
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.web.mapper.EmailMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmailRestController implements EmailOperations {

    private final EmailService emailService;

    @Autowired
    public EmailRestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    @RequestMapping(value = "/keys", method= RequestMethod.GET, produces = "application/json")
    public List<String> allKeys() {
        return emailService.allKeys();
    }

    @Override
    @RequestMapping(value = "/email/{key}", method= RequestMethod.GET, produces = "application/json")
    public Email readEmail(@PathVariable("key") String key) {
        ApiEmail apiEmail = emailService.readEmail(key);
        if (null == apiEmail) return null;
        Email email = new Email();
        BeanUtils.copyProperties(apiEmail, email);
        return email;
    }

    @Override
    public List<Email> allEmails() {
        return emailService.allEmails().stream().map(EmailMapper::toDto).collect(Collectors.toList());
    }
}
