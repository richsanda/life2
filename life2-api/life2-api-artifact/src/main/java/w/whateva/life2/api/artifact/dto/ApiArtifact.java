package w.whateva.life2.api.artifact.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiArtifact {

    private String owner;
    private String trove;
    private String key;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime sent;
    private String from;
    private String to;
    private String subject;
    private String body;
    private String message;
}
