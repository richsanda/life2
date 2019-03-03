package w.whateva.life2.xml.email.facebook;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class FacebookMessageThread {

    private Set<String> senders;
    private Set<String> participants;
    private List<FacebookMessage> messages;
    private FacebookMessageFile file;
}
