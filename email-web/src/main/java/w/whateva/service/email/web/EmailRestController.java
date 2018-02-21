package w.whateva.service.email.web;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import w.whateva.service.email.api.EmailOperations;
import w.whateva.service.email.api.dto.DtoPerson;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.PersonService;
import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.web.mapper.PersonMapper;
import w.whateva.service.email.web.mapper.EmailMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmailRestController implements EmailOperations {

    private final EmailService emailService;
    private final PersonService personService;

    @Autowired
    public EmailRestController(EmailService emailService, PersonService personService) {
        this.emailService = emailService;
        this.personService = personService;
    }

    @Override
    @RequestMapping(value = "/keys", method= RequestMethod.GET, produces = "application/json")
    public List<String> allKeys() {
        return emailService.allKeys();
    }

    @Override
    @RequestMapping(value = "/email/{key}", method= RequestMethod.GET, produces = "application/json")
    public DtoEmail readEmail(@PathVariable("key") String key) {
        ApiEmail apiEmail = emailService.readEmail(key);
        if (null == apiEmail) return null;
        DtoEmail dtoEmail = new DtoEmail();
        BeanUtils.copyProperties(apiEmail, dtoEmail);
        return dtoEmail;
    }

    @Override
    public List<DtoEmail> allEmails() {
        return emailService.allEmails().stream().map(EmailMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<DtoPerson> allPersons() {
        return personService.allPersons().stream().map(PersonMapper::toDto).collect(Collectors.toList());
    }
}
