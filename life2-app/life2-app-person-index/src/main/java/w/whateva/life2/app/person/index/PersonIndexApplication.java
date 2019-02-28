package w.whateva.life2.app.person.index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 */
@SpringBootApplication
@EnableAutoConfiguration (exclude = {
        DataSourceAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
})
@ComponentScan(basePackages = {
        "w.whateva.life2.service.person",
        "w.whateva.life2.service.user",
        "w.whateva.life2.job.person",
})
@EnableMongoRepositories(basePackages = {
        "w.whateva.life2.data.person.repository",
        "w.whateva.life2.data.user.repository"
})
@EntityScan(basePackages = {
        "w.whateva.life2.data.email.domain"
})
public class PersonIndexApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PersonIndexApplication.class, args);
    }
}
