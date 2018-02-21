package w.whateva.service.email.job;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import w.whateva.service.email.job.beans.EmailProcessor;
import w.whateva.service.email.job.beans.EmailWriter;
import w.whateva.service.email.job.beans.PersonProcessor;
import w.whateva.service.email.job.beans.PersonWriter;
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.PersonService;
import w.whateva.service.email.sapi.sao.ApiEmail;
import w.whateva.service.email.sapi.sao.ApiPerson;

@Configuration
@EnableBatchProcessing
public class EmailBatchConfiguration extends DefaultBatchConfigurer {

    private final EmailService emailService;
    private final PersonService personService;

    @Autowired
    public EmailBatchConfiguration(EmailService emailService, PersonService personService) {
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
        return new EmailProcessor();
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