package w.whateva.life2.app.artifact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {
        "w.whateva.life2.app.artifact",
        "w.whateva.life2.service.artifact",
        "w.whateva.life2.service.user",
        "w.whateva.life2.service.person",
        "w.whateva.life2.service.neat",
        "w.whateva.life2.service.note",
        "w.whateva.life2.data.user",
        "w.whateva.life2.data.person",
        "w.whateva.life2.data.neat",
        "w.whateva.life2.data.note",
        "w.whateva.life2.integration",
        "w.whateva.life2.web"
})
@EnableMongoRepositories(basePackages = {
        "w.whateva.life2.data.user.repository",
        "w.whateva.life2.data.person.repository",
        "w.whateva.life2.data.neat.repository",
        "w.whateva.life2.data.note.repository"
})
@EntityScan(basePackages = {
        "w.whateva.life2.data.user.domain",
        "w.whateva.life2.data.person.domain",
        "w.whateva.life2.data.neat.domain",
        "w.whateva.life2.data.note.domain"
})
@EnableAsync
public class Life2Application {

    private static transient Logger log = LoggerFactory.getLogger(Life2Application.class);

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Life2Application.class, args);

        log.info("Started life2");
    }

    @Value("${async.core.pool.size}")
    private Integer asyncCorePoolSize;

    @Value("${async.max.pool.size}")
    private Integer asyncMaxPoolSize;

    @Value("${async.thread.name.prefix}")
    private String asyncThreadNamePrefix;

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncCorePoolSize);
        executor.setMaxPoolSize(asyncMaxPoolSize);
        executor.setThreadNamePrefix(asyncThreadNamePrefix);
        executor.initialize();
        return executor;
    }
}
