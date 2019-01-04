package w.whateva.life2.data.email.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "person")
@TypeAlias("person")
@Getter
@Setter
public class Person {

    @Id
    private String id;

    private String name;
    @Indexed
    private Set<String> emails;
}
