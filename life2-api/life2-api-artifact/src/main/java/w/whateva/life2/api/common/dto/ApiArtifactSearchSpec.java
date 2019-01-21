package w.whateva.life2.api.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiArtifactSearchSpec {

    private Set<ApiTroveKey> troves;
    private LocalDate after;
    private LocalDate before;
    private Set<ApiPersonKey> who;
    private Set<ApiPersonKey> from;
    private Set<ApiPersonKey> to;
}
