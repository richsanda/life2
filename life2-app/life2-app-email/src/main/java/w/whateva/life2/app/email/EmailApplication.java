package w.whateva.life2.app.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.service.utilities.controller.AutoControllers;

/**
 *
 */
@SpringBootApplication
@EnableAutoConfiguration (exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {
        "w.whateva.life2.app.email.controller",
        "w.whateva.life2.service.email",
        "w.whateva.life2.data.email"
})
@EnableMongoRepositories(basePackages = {
        "w.whateva.life2.data.email.repository"
})
@EntityScan(basePackages = {
        "w.whateva.life2.data.email.domain"
})
@AutoControllers(apis = { EmailOperations.class })
public class EmailApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(EmailApplication.class, args);
    }
}
