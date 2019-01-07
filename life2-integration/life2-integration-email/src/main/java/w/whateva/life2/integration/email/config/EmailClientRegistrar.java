package w.whateva.life2.integration.email.config;

import feign.Feign;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.integration.email.impl.EmailProviderImpl;
import w.whateva.life2.integration.email.netflix.EmailFeignConfiguration;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "life2.email")
public class EmailClientRegistrar {

    private static final String CLIENT_BEAN_SUFFIX = "EmailProvider";

    private final GenericWebApplicationContext context;
    private final EmailFeignConfiguration configuration;

    @Getter
    @Setter
    private Map<String, EmailConfiguration> clients;

    @Autowired
    EmailClientRegistrar(GenericWebApplicationContext context, EmailFeignConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;
    }

    @PostConstruct
    private void postConstruct() {

        // dynamically register an artifact provider for each email client
        for (Map.Entry<String, EmailConfiguration> entry : clients.entrySet()) {
            context.registerBean(entry.getKey() + CLIENT_BEAN_SUFFIX,
                    ArtifactProvider.class,
                    () -> new EmailProviderImpl(emailClient(entry.getValue().getUrl())));
        }
    }

    private EmailOperations emailClient(String url) {
        return Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(configuration.feignEncoder())
                .decoder(configuration.feignDecoder())
                .target(EmailOperations.class, url);
    }
}
