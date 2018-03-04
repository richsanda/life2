package w.whateva.service.life2.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import w.whateva.service.life2.api.PieceOperations;
import w.whateva.service.utilities.controller.AutoControllers;

/**
 *
 */
@SpringBootApplication
@EnableAutoConfiguration (exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {
        "w.whateva.service.life2"
})
@AutoControllers(apis = { PieceOperations.class })
public class Life2Application {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Life2Application.class, args);
    }
}
