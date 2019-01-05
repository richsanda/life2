package w.whateva.life2.api.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ApiArtifact {

    private String id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime sent;
    private String from;
    private String to;
    private Set<String> tos;
    private String subject;
    private String body;
    private String message;
}
