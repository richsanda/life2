package w.whateva.service.email.web;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import w.whateva.service.email.api.EmailOperations;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.email.api.dto.DtoEmailCount;
import w.whateva.service.email.api.dto.DtoPerson;
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.PersonService;
import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.web.mapper.EmailMapper;
import w.whateva.service.email.web.mapper.PersonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
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

    @Override
    public List<DtoEmailCount> allEmailCounts() {
        return emailService.emailCounts().stream().map(PersonMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<DtoEmail> allEmails(@RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                                    @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                                    @RequestParam(value = "names", required = false) HashSet<String> names) {
        if (null == after) after = LocalDate.MIN;
        if (null == before) before = LocalDate.MAX;
        return emailService.emails(names, LocalDateTime.of(after, LocalTime.parse("00:00:00")), LocalDateTime.of(before, LocalTime.parse("23:59:59"))).stream().map(EmailMapper::toDto).collect(Collectors.toList());
    }
}
