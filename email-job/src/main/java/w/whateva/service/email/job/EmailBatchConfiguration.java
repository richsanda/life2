package w.whateva.service.email.job;

import generated.Email;
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
import w.whateva.service.email.sapi.EmailService;
import w.whateva.service.email.sapi.sao.ApiEmail;

@Configuration
@EnableBatchProcessing
public class EmailBatchConfiguration extends DefaultBatchConfigurer {

    //private final EntityManagerFactory entityManagerFactory;
    private final EmailService emailService;

    @Autowired
    public EmailBatchConfiguration(EmailService emailService) {
        // this.entityManagerFactory = entityManagerFactory;
        this.emailService = emailService;
    }

    // ensure the job launched at startup uses the same transaction manager as the data module
    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    /*
    @Override
    public PlatformTransactionManager getTransactionManager() {
        return platformTransactionManager();
    }
    */

    @Bean
    @StepScope
    ItemProcessor<ApiEmail, ApiEmail> emailProcessor() {
        return new EmailProcessor();
    }

    @Bean
    ItemWriter<ApiEmail> emailWriter() {
        return new EmailWriter(emailService);
    }
}