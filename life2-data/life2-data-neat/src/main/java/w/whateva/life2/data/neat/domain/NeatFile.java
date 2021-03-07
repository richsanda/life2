package w.whateva.life2.data.neat.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "neat")
@TypeAlias("neat")
@Getter
@Setter
public class NeatFile {

    @Id
    private String id;

    @Indexed
    private String folder;
    @Indexed
    private String filename;
    @Indexed
    private String extension;

    private Integer index;
    private Integer page;
    private String title;
    private String type;
}
