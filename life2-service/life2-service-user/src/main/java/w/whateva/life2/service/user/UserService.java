package w.whateva.life2.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;
import w.whateva.life2.data.user.domain.User;

public interface UserService extends UserDetailsService {

    User addUser(User user);

    User addTestUser(String username, String password);

    User getCurrentUser();
}
