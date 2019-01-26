package w.whateva.life2.api.person.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ApiPerson {

    private String name;
    private String owner;
    private Set<String> emails;
}
