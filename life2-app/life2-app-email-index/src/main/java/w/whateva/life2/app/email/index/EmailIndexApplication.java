package w.whateva.life2.app.email.index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
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
        "w.whateva.life2.data.email",
        "w.whateva.life2.job.email",
})
@EnableMongoRepositories(basePackages = {
        "w.whateva.life2.data.email.repository"
})
@EntityScan(basePackages = {
        "w.whateva.life2.data.email.domain"
})
public class EmailIndexApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(EmailIndexApplication.class, args);
    }
}
