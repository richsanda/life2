package w.whateva.life2.api.email.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.Set;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class ApiPerson {

    @XmlAttribute
    private String name;
    @XmlElement(name = "email")
    private Set<String> emails;
}
