package w.whateva.life2.app.artifact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import w.whateva.life2.api.common.ArtifactOperations;
import w.whateva.service.life2.api.ShredOperations;
import w.whateva.service.utilities.controller.AutoControllers;

/**
 *
 */
@SpringBootApplication
@EnableAutoConfiguration (exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {
        "w.whateva.life2.app.artifact",
        "w.whateva.life2.service.artifact",
        "w.whateva.life2.integration"
})
@AutoControllers(apis = { ArtifactOperations.class })
public class Life2Application {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Life2Application.class, args);
    }
}
