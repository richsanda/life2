package w.whateva.life2.xml.email.def;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.Set;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class XmlPerson {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String username;
    @XmlElement(name = "email")
    private Set<String> emails;
}
