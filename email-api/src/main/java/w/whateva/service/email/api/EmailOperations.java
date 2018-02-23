package w.whateva.service.email.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import w.whateva.service.email.api.dto.DtoEmailCount;
import w.whateva.service.email.api.dto.DtoPerson;
import w.whateva.service.email.api.dto.DtoEmail;

import java.util.List;

@RequestMapping
public interface EmailOperations {

    @RequestMapping(value = "/keys", method= RequestMethod.GET, produces = "application/json")
    List<String> allKeys();

    @RequestMapping(value = "/email/{key}", method= RequestMethod.GET, produces = "application/json")
    DtoEmail readEmail(@PathVariable("key") String key);

    @RequestMapping(value = "/emails", method= RequestMethod.GET, produces = "application/json")
    List<DtoEmail> allEmails();

    @RequestMapping(value = "/persons", method= RequestMethod.GET, produces = "application/json")
    public List<DtoPerson> allPersons();

    @RequestMapping(value = "/persons/count", method= RequestMethod.GET, produces = "application/json")
    public List<DtoEmailCount> allEmailCounts();
}
