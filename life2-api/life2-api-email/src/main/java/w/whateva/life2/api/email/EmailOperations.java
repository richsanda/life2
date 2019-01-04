package w.whateva.life2.api.email;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import w.whateva.life2.api.email.dto.ApiEmail;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@RequestMapping
public interface EmailOperations {

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    void add(ApiEmail email);

    @RequestMapping(value = "/email/{key}", method = RequestMethod.GET, produces = "application/json")
    ApiEmail read(@PathVariable("key") String key);

    @RequestMapping(value = "/emails", method = RequestMethod.GET, produces = "application/json")
    List<ApiEmail> search(@RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                          @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                          @RequestParam(value = "names", required = false) HashSet<String> names);
}