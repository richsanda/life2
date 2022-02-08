package w.whateva.life2.api.artifact.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiArtifact {

    private String owner;
    private String trove;
    private String key;

    private Set<String> types;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime when;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime when2;

    private String title;
    private String description;
    private List<String> notes;
    private String image;

    private List<String> relativeKeys;
    private int relativeKeyIndex;

    private Map<String, Object> data;
}
