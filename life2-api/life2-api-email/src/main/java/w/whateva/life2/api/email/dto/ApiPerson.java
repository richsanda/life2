package w.whateva.life2.api.email.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ApiPerson {

    private String name;
    private Set<String> emails;
}
