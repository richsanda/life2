package w.whateva.life2.job.email;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.email.EmailService;
import w.whateva.life2.api.email.PersonService;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.email.dto.ApiPerson;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.PersonProcessor;
import w.whateva.life2.job.email.beans.PersonWriter;
import w.whateva.life2.job.email.beans.XmlEmailProcessor;
import w.whateva.life2.xml.email.def.XmlEmail;
import w.whateva.life2.xml.email.def.XmlPerson;

@Configuration
@EnableBatchProcessing
@ConditionalOnProperty(name = "email.xml.file.pattern")
public class XmlEmailBatchConfiguration extends DefaultBatchConfigurer {

    private final EmailService emailService;
    private final PersonService personService;

    @Autowired
    public XmlEmailBatchConfiguration(EmailService emailService, PersonService personService) {
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
    ItemProcessor<XmlEmail, ApiEmail> emailProcessor() {
        return new XmlEmailProcessor();
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

    @Bean
    @StepScope
    Tasklet addGroupAddressToSendersTasklet() {
        MethodInvokingTaskletAdapter result = new MethodInvokingTaskletAdapter();
        result.setTargetObject(emailService);
        result.setTargetMethod("addGroupAddressToSenders");
        return result;
    }
}