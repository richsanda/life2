package w.whateva.service.email.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.email.api.dto.DtoEmailCount;
import w.whateva.service.email.api.dto.DtoPerson;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@RequestMapping
public interface EmailOperations {

    @RequestMapping(value = "/keys", method = RequestMethod.GET, produces = "application/json")
    List<String> allKeys();

    void addEmail(DtoEmail email);

    @RequestMapping(value = "/email/{key}", method = RequestMethod.GET, produces = "application/json")
    DtoEmail readEmail(@PathVariable("key") String key);

    @RequestMapping(value = "/emails", method = RequestMethod.GET, produces = "application/json")
    List<DtoEmail> allEmails();

    @RequestMapping(value = "/persons", method = RequestMethod.GET, produces = "application/json")
    List<DtoPerson> allPersons();

    @RequestMapping(value = "/persons/count", method = RequestMethod.GET, produces = "application/json")
    List<DtoEmailCount> allEmailCounts();

    @RequestMapping(value = "/person/emails", method = RequestMethod.GET, produces = "application/json")
    List<DtoEmail> allEmails(@RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                             @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                             @RequestParam(value = "names", required = false) HashSet<String> names);
}