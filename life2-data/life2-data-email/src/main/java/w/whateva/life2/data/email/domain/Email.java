package w.whateva.life2.data.email.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
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
    private String key;

    @Indexed
    private ZonedDateTime sent;
    private String from;
    @Indexed
    private String fromIndex;
    private String to;
    @Indexed
    private Set<String> toIndex;
    @TextIndexed
    private String subject;
    @TextIndexed
    private String body;
    private boolean bodyHtml;
    private boolean group;
    @Indexed
    private String owner;
    @Indexed
    private String trove;
}
