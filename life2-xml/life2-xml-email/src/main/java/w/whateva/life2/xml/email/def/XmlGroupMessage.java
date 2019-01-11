package w.whateva.life2.xml.email.def;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "GroupMessage")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class XmlGroupMessage extends XmlEmail {

    @XmlElement(name = "Topic")
    private String topic;
    @XmlElement(name = "SourceKey")
    private GroupKey groupKey;

    @Getter
    @Setter
    @XmlRootElement(name = "GroupKey")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GroupKey {

        @XmlAttribute(name = "type")
        private String groupType;
        @XmlElement(name = "Group")
        private String groupName;
        @XmlElement(name = "Number")
        private String messageId;
    }

    public String getGroupType() {
        return groupKey.getGroupType();
    }

    public String getGroupName() {
        return groupKey.getGroupName();
    }

    public String getMessageId() {
        return groupKey.getMessageId();
    }
}
