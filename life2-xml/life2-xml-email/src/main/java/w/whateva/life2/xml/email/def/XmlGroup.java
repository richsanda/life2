package w.whateva.life2.xml.email.def;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.Set;

@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class XmlGroup {

    @XmlAttribute
    private String name;
    @XmlElement(name = "member")
    private Set<String> members;
}
