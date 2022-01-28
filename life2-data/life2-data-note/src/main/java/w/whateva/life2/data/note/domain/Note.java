package w.whateva.life2.data.note.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "note")
@TypeAlias("note")
@Getter
@Setter
public class Note {

    @Id
    private String id;

    @Indexed
    private String trove; // folder

    @Indexed
    private ZonedDateTime sent;

    @Indexed
    private Integer index;
    private String title;
    private String text;

    private List<String> refs;

    private Map<String, Object> data;
}
