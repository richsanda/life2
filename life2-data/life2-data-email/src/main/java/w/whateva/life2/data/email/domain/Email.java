package w.whateva.life2.data.email.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 *
 */
@Document(collection = "email")
@TypeAlias("email")
@Getter
@Setter
public class Email {

    @Id
    private String id;

    @Indexed
    private LocalDateTime sent;
    private String from;
    private String to;
    @Indexed
    private Set<String> tos;
    private String subject;
    private String body;
}
