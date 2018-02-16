package w.whateva.service.email.sapi.sao;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@XmlRootElement(name = "Email")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApiEmail {

    /*
    <xs:element name="Sent" type="xs:dateTime"/>
    <xs:element name="From" type="xs:string"/>
    <xs:element name="To" type="xs:string"/>
    <xs:element name="Subject" type="xs:string"/>
    <xs:element name="Body" type="xs:string"/>
     */

    @XmlElement(name = "Sent")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime sent;
    @XmlElement(name = "From")
    private String from;
    @XmlElement(name = "To")
    private String to;
    @XmlElement(name = "Subject")
    private String subject;
    @XmlElement(name = "Body")
    private String body;

    public LocalDateTime getSent() {
        return sent;
    }

    public void setSent(LocalDateTime sent) {
        this.sent = sent;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private static class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

        @Override
        public LocalDateTime unmarshal(String v) throws Exception {
            return LocalDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME);
        }

        @Override
        public String marshal(LocalDateTime v) throws Exception {
            return v.toString();
        }
    }
}
