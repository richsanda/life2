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
import w.whateva.service.email.api.EmailOperations;
import w.whateva.service.email.api.PersonService;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.email.api.dto.DtoPerson;
import w.whateva.service.email.job.beans.EmailProcessor;
import w.whateva.service.email.job.beans.EmailWriter;
import w.whateva.service.email.job.beans.PersonProcessor;
import w.whateva.service.email.job.beans.PersonWriter;

@Configuration
@EnableBatchProcessing
public class EmailBatchConfiguration extends DefaultBatchConfigurer {

    private final EmailOperations emailService;
    private final PersonService personService;

    @Autowired
    public EmailBatchConfiguration(EmailOperations emailService, PersonService personService) {
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
    ItemProcessor<DtoEmail, DtoEmail> emailProcessor() {
        return new EmailProcessor();
    }

    @Bean
    ItemWriter<DtoEmail> emailWriter() {
        return new EmailWriter(emailService);
    }

    @Bean
    @StepScope
    ItemProcessor<DtoPerson, DtoPerson> personProcessor() {
        return new PersonProcessor();
    }

    @Bean
    ItemWriter<DtoPerson> personWriter() {
        return new PersonWriter(personService);
    }
}