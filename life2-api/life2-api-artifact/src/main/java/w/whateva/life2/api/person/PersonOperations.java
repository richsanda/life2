package w.whateva.life2.api.person;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import w.whateva.life2.api.person.dto.ApiPerson;

import java.util.List;

@RequestMapping
public interface PersonOperations {

    @RequestMapping(value = "/persons", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<ApiPerson> allPersons();
}
