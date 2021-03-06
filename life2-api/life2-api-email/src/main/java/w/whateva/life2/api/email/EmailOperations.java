package w.whateva.life2.api.email;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiEmailCount;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequestMapping
public interface EmailOperations {

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    void add(ApiEmail email);

    @RequestMapping(value = "/email/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    ApiEmail read(@PathVariable("key") String key);

    @RequestMapping(value = "/emails", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<ApiEmail> search(@RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                          @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                          @RequestParam(value = "who", required = false) Set<String> who,
                          @RequestParam(value = "from", required = false) Set<String> from,
                          @RequestParam(value = "to", required = false) Set<String> to);

    @RequestMapping(value = "/email/counts", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<ApiEmailCount> count(@RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                              @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                              @RequestParam(value = "who", required = false) Set<String> who,
                              @RequestParam(value = "from", required = false) Set<String> from,
                              @RequestParam(value = "to", required = false) Set<String> to);
}