package w.whateva.life2.service.email.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
public class ApiEmail {

    private String key;

    private String messageId;
    private String owner;
    private String trove;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime sent;
    private String from;
    private String fromEmail;
    private String to;
    private Set<String> toEmails;
    private String subject;
    private String body;
    private String message;
    private boolean bodyHtml;
}
