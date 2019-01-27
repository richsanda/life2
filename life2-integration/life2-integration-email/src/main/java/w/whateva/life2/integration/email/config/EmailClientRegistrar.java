package w.whateva.life2.integration.email.config;

import feign.Feign;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.integration.email.impl.EmailProviderImpl;
import w.whateva.life2.integration.email.netflix.EmailFeignConfiguration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "life2.email")
public class EmailClientRegistrar {

    private static final String CLIENT_BEAN_SUFFIX = "EmailProvider";

    private final GenericWebApplicationContext context;
    private final EmailFeignConfiguration configuration;

    @Getter
    @Setter
    private Map<String, EmailConfiguration> sources;

    @Autowired
    EmailClientRegistrar(GenericWebApplicationContext context, EmailFeignConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;
    }

    @PostConstruct
    private void postConstruct() {

        if (CollectionUtils.isEmpty(sources)) {
            System.out.println("No email sources");
            return;
        }

        // dynamically register an artifact provider for each email client
        // TODO: at startup we could call these services to derive the "troves"... could be an aggregation
        // if we store the trove on each email... or a pointer to a trove on each email, if you want to be able
        // to update the trove name... gonna have to store "troves" somewhere so they can be managed by an owner
        for (Map.Entry<String, EmailConfiguration> entry : sources.entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue().getTroves()) || null == entry.getValue().getUrl()) {
                System.out.println("NOT loading source " + entry.getKey() + " because it's not fully configured");
                continue;
            }

            context.registerBean(entry.getKey() + CLIENT_BEAN_SUFFIX,
                    ArtifactProvider.class,
                    () -> {
                        EmailConfiguration config = entry.getValue();
                        EmailOperations client = emailClient(config.getUrl());
                        Map<String, List<String>> troves = config.getTroves();
                        return new EmailProviderImpl(client, troves);
                    });
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
