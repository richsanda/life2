package w.whateva.life2.api.artifact.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiArtifactSearchSpec {

    private String owner;
    private Set<String> troves;
    private LocalDate after;
    private LocalDate before;
    private Set<String> who;
    private Set<String> from;
    private Set<String> to;
}
