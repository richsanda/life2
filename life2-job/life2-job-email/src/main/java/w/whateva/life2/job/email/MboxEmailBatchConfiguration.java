package w.whateva.life2.job.email;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.email.PersonOperations;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiPerson;
import w.whateva.life2.job.email.beans.EmailProcessor;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.PersonProcessor;
import w.whateva.life2.job.email.beans.PersonWriter;

@Configuration
@EnableBatchProcessing
@ConditionalOnProperty(name = "email.mbox.file")
public class MboxEmailBatchConfiguration extends DefaultBatchConfigurer {

    private final EmailOperations emailService;
    private final PersonOperations personService;

    @Value("${email.address.parser.type}")
    private String emailAddressParserType;

    @Value("${email.to.default}")
    private String emailToDefault;

    @Autowired
    public MboxEmailBatchConfiguration(EmailOperations emailService, PersonOperations personService) {
        this.emailService = emailService;
        this.personService = personService;
    }

    // ensure the job launched at startup uses the same transaction manager as the data module
    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    @StepScope
    ItemProcessor<ApiEmail, ApiEmail> emailProcessor() {
        return new EmailProcessor(emailAddressParserType, emailToDefault);
    }

    @Bean
    ItemWriter<ApiEmail> emailWriter() {
        return new EmailWriter(emailService);
    }

    @Bean
    @StepScope
    ItemProcessor<ApiPerson, ApiPerson> personProcessor() {
        return new PersonProcessor();
    }

    @Bean
    ItemWriter<ApiPerson> personWriter() {
        return new PersonWriter(personService);
    }
}