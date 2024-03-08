package w.whateva.life2.job.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.MboxReader;
import w.whateva.life2.job.email.beans.MimeMessageProcessor;
import w.whateva.life2.job.email.beans.MimeMessageProcessorListener;
import w.whateva.life2.service.email.EmailService;
import w.whateva.life2.service.email.dto.ApiEmail;

import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@ConditionalOnProperty(name = "email.mbox.file")
public class MboxEmailJobConfiguration {

    private transient Logger log = LoggerFactory.getLogger(MboxEmailJobConfiguration.class);

    private final JobRepository jobs;
    private final EmailService emailService;
    private final EmailLoadConfiguration configuration;

    @Value("${email.mbox.file}")
    private String emailMboxFile;

    @Autowired
    public MboxEmailJobConfiguration(JobRepository jobs, EmailService emailService, EmailLoadConfiguration emailLoadConfiguration) {
        this.jobs = jobs;
        this.emailService = emailService;
        this.configuration = emailLoadConfiguration;
    }

    @Bean
    public Job loadEmailJob(JobRepository jobRepository, Step loadEmailStep) throws Exception {
        return new JobBuilder("loadEmailJob", jobRepository)
                .start(loadEmailStep)
                .build();
    }

    @Bean
    public Step loadEmailStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            MboxReader mboxReader,
            MimeMessageProcessor mimeMessageProcessor,
            EmailWriter emailWriter,
            MimeMessageProcessorListener mimeMessageProcessorListener)
            throws Exception {
        return new StepBuilder("loadEmailStep", jobRepository)
                .<MimeMessage, ApiEmail>chunk(20, transactionManager)
                .reader(mboxReader)
                .processor(mimeMessageProcessor).faultTolerant().skipLimit(100).skip(Exception.class)
                .writer(emailWriter)
                .listener(mimeMessageProcessorListener)
                .build();
    }

    @Bean
    public MboxReader emailReader() throws IOException {

        log.info("reading email mbox from: " + emailMboxFile);

        InputStream input = new FileInputStream(emailMboxFile);
        MboxReader reader = new MboxReader(input);
        return reader;
    }

    @Bean
    MimeMessageProcessor emailProcessor() {
        return new MimeMessageProcessor();
    }

    @Bean
    MimeMessageProcessorListener emailProcessorListener() {
        return new MimeMessageProcessorListener();
    }

    @Bean
    EmailWriter emailWriter() {
        return new EmailWriter(
                emailService,
                configuration.getEmailTroveName(),
                configuration.getEmailTroveOwner());
    }
}