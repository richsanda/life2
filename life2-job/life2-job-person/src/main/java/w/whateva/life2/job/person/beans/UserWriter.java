package w.whateva.life2.job.person.beans;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.life2.service.user.UserService;
import w.whateva.life2.xml.email.def.XmlUser;

public class UserWriter implements ItemWriter<XmlUser> {

    private final UserService userService;

    @Autowired
    public UserWriter(UserService userService) {
        this.userService = userService;
    }

    public void write(Chunk<? extends XmlUser> users) throws Exception {
        for (XmlUser user : users) {
            userService.addTestUser(user.getUsername(), user.getPassword());
        }
    }
}
