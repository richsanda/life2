package w.whateva.life2.api.artifact;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequestMapping
public interface ArtifactOperations {

    @RequestMapping(value = "/artifact/{owner}/{trove}/{key}", method = RequestMethod.GET, produces = "application/json")
    ApiArtifact read(@PathVariable("owner") String owner, @PathVariable("trove") String trove, @PathVariable("key") String key);

    @RequestMapping(value = "/artifacts", method = RequestMethod.GET, produces = "application/json")
    List<ApiArtifact> search(
            @RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
            @RequestParam(value = "who", required = false) Set<String> who,
            @RequestParam(value = "from", required = false) Set<String> from,
            @RequestParam(value = "to", required = false) Set<String> to);

    @RequestMapping(value = "/artifacts", method = RequestMethod.POST, produces = "application/json")
    List<ApiArtifact> search(@RequestBody ApiArtifactSearchSpec searchSpec);


    @RequestMapping(value = "/artifact/counts", method = RequestMethod.GET, produces = "application/json")
    List<ApiArtifactCount> count(
            @RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
            @RequestParam(value = "who", required = false) Set<String> who,
            @RequestParam(value = "troves", required = false) Set<String> troves);

    @RequestMapping(value = "/artifact/counts", method = RequestMethod.POST, produces = "application/json")
    List<ApiArtifactCount> count(@RequestBody ApiArtifactSearchSpec searchSpec);
}
