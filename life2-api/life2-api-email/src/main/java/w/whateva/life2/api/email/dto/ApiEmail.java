package w.whateva.life2.api.email.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ApiEmail {

    private String key;

    private String messageId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime sent;
    private String from;
    private String fromEmail;
    private String to;
    private Set<String> toEmails;
    private String subject;
    private String body;
    private String message;
    private boolean bodyHtml;
}
