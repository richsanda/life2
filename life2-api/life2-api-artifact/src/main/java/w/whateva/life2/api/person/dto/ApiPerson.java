package w.whateva.life2.api.person.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ApiPerson {

    private String name;
    private String username;
    private String owner;
    private boolean group = false;
    private Set<String> emails;
    private Set<String> groups;
    private Set<String> members;
}
