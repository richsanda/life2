package w.whateva.life2.xml.email.facebook;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class FacebookMessageFile {

    private Set<FacebookMessageParticipant> participants;
    private List<FacebookMessage> messages;
    private String title;
    private boolean is_still_participant;
    private String thread_type;
    private String thread_path;
}
