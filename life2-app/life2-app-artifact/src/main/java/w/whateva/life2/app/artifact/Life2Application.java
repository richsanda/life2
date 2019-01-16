package w.whateva.life2.app.artifact;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import w.whateva.life2.api.common.ArtifactOperations;
import w.whateva.service.utilities.controller.AutoControllers;

/**
 *
 */
@SpringBootApplication
@EnableAutoConfiguration (exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = {
        "w.whateva.life2.app.artifact",
        "w.whateva.life2.service.artifact",
        "w.whateva.life2.integration",
        "w.whateva.life2.web"
})
@EnableAsync
@AutoControllers(apis = { ArtifactOperations.class })
public class Life2Application {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Life2Application.class, args);
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
