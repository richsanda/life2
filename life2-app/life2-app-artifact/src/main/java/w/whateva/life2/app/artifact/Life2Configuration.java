package w.whateva.service.life2.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@EnableAsync
public class Life2Configuration {

    @Value("${async.core.pool.size}")
    private Integer asyncCorePoolSize;

    @Value("${async.max.pool.size}")
    private Integer asyncMaxPoolSize;

    @Value("${async.thread.name.prefix}")
    private String asyncThreadNamePrefix;

    /*
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                ObjectMapper objectMapper = jsonMessageConverter.getObjectMapper();
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                break;
            }
        }
    }

    @Bean
    DateTimeFormatterRegistrar dateTimeFormatterRegistrar() {
        DateTimeFormatterRegistrar result = new DateTimeFormatterRegistrar();
        result.setUseIsoFormat(true);
        return result;
    }
    */

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
