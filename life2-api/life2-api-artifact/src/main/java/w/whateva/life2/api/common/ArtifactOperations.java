package w.whateva.life2.api.common;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import w.whateva.life2.api.common.dto.ApiArtifact;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@RequestMapping
public interface ArtifactOperations {

    @RequestMapping(value = "/email/{key}", method = RequestMethod.GET, produces = "application/json")
    ApiArtifact read(@PathVariable("key") String key);

    @RequestMapping(value = "/emails", method = RequestMethod.GET, produces = "application/json")
    List<ApiArtifact> search(@RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                          @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                          @RequestParam(value = "names", required = false) HashSet<String> names);
}
