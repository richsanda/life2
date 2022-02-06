package w.whateva.life2.service.email.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiGroupMessage extends ApiEmail {

    private String groupType;
    private String groupName;
    private String messageId;
    private String topic;
}
