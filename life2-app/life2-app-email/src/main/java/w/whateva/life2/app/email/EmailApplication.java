package w.whateva.life2.app.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import w.whateva.life2.integration.email.netflix.EmailObjectMapper;

/**
 *
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {
        "w.whateva.life2.service.email",
        "w.whateva.life2.data.email"
})
@EnableMongoRepositories(basePackages = {
        "w.whateva.life2.data.email.repository",
        "w.whateva.life2.data.person.repository"
})
@EntityScan(basePackages = {
        "w.whateva.life2.data.email.domain"
})
public class EmailApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(EmailApplication.class, args);
    }

    @Bean
    ObjectMapper objectMapper() { return new EmailObjectMapper(); }
}
