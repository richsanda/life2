package w.whateva.life2.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import w.whateva.life2.data.user.domain.User;
import w.whateva.life2.data.user.repository.UserRepository;
import w.whateva.life2.service.user.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("user"));
        user.setAuthorities(authorities);
        return user;
    }

    @Override
    public User addUser(User user) {
        return repository.save(user);
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            User user = (User)principal;
            System.out.println(user.getUsername());
            return user;
        } else {
            System.out.println(principal.toString());
        }
        return null;
    }
}