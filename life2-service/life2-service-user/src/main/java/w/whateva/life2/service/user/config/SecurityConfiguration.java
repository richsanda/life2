package w.whateva.life2.service.user.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import w.whateva.life2.data.user.domain.TroveAccess;
import w.whateva.life2.data.user.domain.TroveAccessRole;
import w.whateva.life2.data.user.domain.User;
import w.whateva.life2.service.user.UserService;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableConfigurationProperties
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserService userService;

    @Autowired
    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().httpBasic()
                .and().sessionManagement().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userService);
    }

    @PostConstruct
    void postConstruct() {

        //rich
        TroveAccess access = new TroveAccess("rich", "rich")
                .withTroves("billshwah", "billshwah_inbox", "rowecom", "redsox47")
                .withRoles(TroveAccessRole.OWNER);
        addTestUser("rich", "sydney", Collections.singletonList(access));

        //rsd
        access = new TroveAccess("rich", "rsd")
                .withTroves("billshwah", "billshwah_inbox")
                .withRoles(TroveAccessRole.FROM, TroveAccessRole.TO);
        addTestUser("rsd", "rsd47", Collections.singletonList(access));

        //trav

        access = new TroveAccess("rich", "trav")
                .withTroves("billshwah", "billshwah_inbox")
                .withRoles(TroveAccessRole.FROM, TroveAccessRole.TO);
        TroveAccess access2 = new TroveAccess("rich", "trav")
                .withTroves("redsox47")
                .withRoles(TroveAccessRole.MEMBER);
        addTestUser("trav", "trav47", Lists.newArrayList(access, access2));
    }

    private void addTestUser(String username, String password, List<TroveAccess> troveAccesses) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder().encode(password));
        user.setAccess(troveAccesses);

        userService.addUser(user);
    }
}