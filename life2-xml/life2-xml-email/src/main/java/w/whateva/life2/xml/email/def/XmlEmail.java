package w.whateva.life2.xml.email.def;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import w.whateva.life2.xml.email.adapter.LocalDateTimeXmlAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.Set;

@XmlRootElement(name = "Email")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class XmlEmail {

    private String id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @XmlElement(name = "Sent")
    @XmlJavaTypeAdapter(type = LocalDateTime.class, value = LocalDateTimeXmlAdapter.class)
    private LocalDateTime sent;
    @XmlElement(name = "From")
    private String from;
    @XmlElement(name = "To")
    private String to;
    private Set<String> tos;
    @XmlElement(name = "Subject")
    private String subject;
    @XmlElement(name = "Body")
    private String body;
    @XmlElement(name = "Message")
    private String message;
}
