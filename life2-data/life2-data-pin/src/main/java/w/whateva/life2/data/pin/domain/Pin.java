package w.whateva.life2.data.pin.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;

@Document(collection = "pin")
@TypeAlias("pin")
@Getter
@Setter
@Builder
public class Pin {

    @Id
    private String id;

    @Indexed
    private String owner;
    @Indexed
    private String type;
    @Indexed
    private String trove;
    @Indexed
    private String key;
    @Indexed
    private String source;

    @Indexed
    private String whenDisplay;
    @Indexed
    private ZonedDateTime when;
    @Indexed
    private ZonedDateTime when2;
    @Indexed
    private Set<String> from;
    @Indexed
    private Set<String> to;
    @TextIndexed
    private String title;
    @TextIndexed
    private String text;

    private Map<String, Object> data;
}
