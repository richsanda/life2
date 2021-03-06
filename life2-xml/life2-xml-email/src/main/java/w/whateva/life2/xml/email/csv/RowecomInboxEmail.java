package w.whateva.life2.xml.email.csv;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class RowecomInboxEmail {

/*
"Importance"	"Icon"	"Priority"	"Subject"	"From"	"Message To Me"	"Message CC to Me"	"Sender Name"	"CC"	"To"	"Received"	"Message Size"	"Contents"	"Created"	"Modified"	"Subject Prefix"	"Has Attachments"	"Normalized Subject"	"Object Type"	"Content Unread"
 */
    private Integer Importance;
    private String Icon;
    private Integer Priority;
    private String Subject;
    private String From;
    private Integer Message_To_Me;
    private Integer Message_CC_to_Me;
    private String Sender_Name;
    private String CC;
    private String To;
    private Date Received;
    private Integer Message_Size;
    private String Contents;
    private Date Created;
    private Date Modified;
    private String Subject_Prefix;
    private Integer Has_Attachments;
    private String Normalized_Subject;
    private String Object_Type;
    private Integer Content_Unread;
}
