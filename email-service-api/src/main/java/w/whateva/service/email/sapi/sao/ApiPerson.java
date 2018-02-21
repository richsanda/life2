package w.whateva.service.email.sapi.sao;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApiPerson {

    @XmlAttribute
    private String name;
    @XmlElement(name = "email")
    private List<String> emails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
