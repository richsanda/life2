package w.whateva.life2.app.person.index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
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
