package w.whateva.life2.data.person.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "person")
@TypeAlias("person")
@Getter
@Setter
public class Person {

    @Id
    private String id; // handle for this person in the scope of owner

    @Indexed
    private String name; // nickname for this person
    @Indexed
    private String owner; // which username owns this person
    @Indexed
    private String username; // which username this person maps to, if any

    @Indexed
    private Set<String> emails = new HashSet<>();
}
