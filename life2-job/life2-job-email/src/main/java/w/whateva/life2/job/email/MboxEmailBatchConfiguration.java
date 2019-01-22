package w.whateva.life2.job.email;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.MboxEmailProcesor;
import w.whateva.life2.job.email.beans.PersonProcessor;
import w.whateva.life2.job.email.beans.PersonWriter;
import w.whateva.life2.xml.email.def.XmlPerson;

import javax.mail.internet.MimeMessage;

@Configuration
@EnableBatchProcessing
@ConditionalOnProperty(name = "email.mbox.file")
public class MboxEmailBatchConfiguration extends DefaultBatchConfigurer {

    private final EmailOperations emailService;
    private final PersonService personService;

    @Autowired
    public MboxEmailBatchConfiguration(EmailOperations emailService, PersonService personService) {
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
    ItemProcessor<MimeMessage, ApiEmail> emailProcessor() {
        return new MboxEmailProcesor();
    }

    @Bean
    ItemWriter<ApiEmail> emailWriter() {
        return new EmailWriter(emailService);
    }

    @Bean
    @StepScope
    ItemProcessor<XmlPerson, ApiPerson> personProcessor() {
        return new PersonProcessor();
    }

    @Bean
    ItemWriter<ApiPerson> personWriter() {
        return new PersonWriter(personService);
    }
}