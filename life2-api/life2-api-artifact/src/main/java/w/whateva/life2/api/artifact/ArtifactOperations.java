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
    ApiArtifact read(
            @PathVariable("owner") String owner,
            @PathVariable("trove") String trove,
            @PathVariable("key") String key,
            @RequestParam(value = "relatives", required = false) Boolean relatives);

    @RequestMapping(value = "/note/{owner}/{trove}/{key}", method = RequestMethod.GET, produces = "application/json")
    default ApiArtifact readNote(
            @PathVariable("owner") String owner,
            @PathVariable("trove") String trove,
            @PathVariable("key") String key) {
        return null;
    }

    @RequestMapping(value = "/artifacts", method = RequestMethod.GET, produces = "application/json")
    List<ApiArtifact> search(
            @RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
            @RequestParam(value = "who", required = false) Set<String> who,
            @RequestParam(value = "troves", required = false) Set<String> troves,
            @RequestParam(value = "from", required = false) Set<String> from,
            @RequestParam(value = "to", required = false) Set<String> to,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "source", required = false) String source);

    @RequestMapping(value = "/artifacts", method = RequestMethod.POST, produces = "application/json")
    List<ApiArtifact> search(@RequestBody ApiArtifactSearchSpec searchSpec);


    @RequestMapping(value = "/artifacts/counts", method = RequestMethod.GET, produces = "application/json")
    List<ApiArtifactCount> count(
            @RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
            @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
            @RequestParam(value = "who", required = false) Set<String> who,
            @RequestParam(value = "troves", required = false) Set<String> troves,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "source", required = false) String source);

    @RequestMapping(value = "/artifacts/counts", method = RequestMethod.POST, produces = "application/json")
    List<ApiArtifactCount> count(@RequestBody ApiArtifactSearchSpec searchSpec);

    @RequestMapping(value = "/artifacts/index/{owner}/{trove}", method = RequestMethod.POST, produces = "application/json")
    Integer index(@PathVariable("owner") String owner, @PathVariable("trove") String trove);
}
