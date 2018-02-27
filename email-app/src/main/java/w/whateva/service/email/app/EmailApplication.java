package w.whateva.service.email.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import w.whateva.service.email.api.EmailOperations;
import w.whateva.service.email.web.mapper.WController;

/**
 *
 */
@SpringBootApplication
@EnableAutoConfiguration (exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {
        "w.whateva.service.email"
})
@EnableMongoRepositories(basePackages = {
        "w.whateva.service.email.data.repository"
})
@EntityScan(basePackages = {
        "w.whateva.service.email.data.domain"
})
@WController(api = EmailOperations.class)
public class EmailApplication {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(EmailApplication.class, args);
    }
}
