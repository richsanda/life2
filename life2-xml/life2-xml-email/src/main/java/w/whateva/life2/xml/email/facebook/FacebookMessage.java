package w.whateva.life2.xml.email.facebook;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class FacebookMessage {

    private String sender_name;
    private Date timestamp_ms;
    private String content;
    private String type;
}
