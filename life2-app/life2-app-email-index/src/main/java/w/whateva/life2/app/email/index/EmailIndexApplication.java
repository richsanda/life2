package w.whateva.life2.app.email.index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 */
@SpringBootApplication
@EnableAutoConfiguration (exclude = {
        DataSourceAutoConfiguration.class
})
@ComponentScan(basePackages = {
        "w.whateva.life2.service.email",
        "w.whateva.life2.service.person",
        "w.whateva.life2.data.email",
        "w.whateva.life2.data.person",
        "w.whateva.life2.data.pin",
        "w.whateva.life2.job.email",
        "w.whateva.life2.app.email.index"
})
@EnableMongoRepositories(basePackages = {
        "w.whateva.life2.data.email.repository",
        "w.whateva.life2.data.person.repository",
        "w.whateva.life2.data.pin.repository"
})
@EntityScan(basePackages = {
        "w.whateva.life2.data.email.domain"
})
public class EmailIndexApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(EmailIndexApplication.class, args);
    }
}
