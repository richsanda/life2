package w.whateva.life2.job.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.MboxReader;
import w.whateva.life2.job.email.beans.MimeMessageProcessor;
import w.whateva.life2.job.email.beans.MimeMessageProcessorListener;

import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@ConditionalOnProperty(name = "email.mbox.file")
@EnableBatchProcessing
public class MboxEmailJobConfiguration extends DefaultBatchConfigurer {

    private transient Logger log = LoggerFactory.getLogger(MboxEmailJobConfiguration.class);

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EmailOperations emailService;
    private final EmailLoadConfiguration configuration;

    @Value("${email.mbox.file}")
    private String emailMboxFile;

    @Autowired
    public MboxEmailJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, EmailOperations emailService, EmailLoadConfiguration emailLoadConfiguration) {
        this.jobs = jobs;
        this.steps = steps;
        this.emailService = emailService;
        this.configuration = emailLoadConfiguration;
    }

    @Bean
    public Job loadEmailJob() throws Exception {
        return this.jobs.get("loadEmailJob")
                .start(loadEmailStep())
                .build();
    }

    @Bean
    public Step loadEmailStep() throws Exception {
        return this.steps.get("loadEmailStep")
                .<MimeMessage, ApiEmail>chunk(20)
                .reader(emailReader())
                .processor(emailProcessor()).faultTolerant().skipLimit(100).skip(Exception.class)
                .writer(emailWriter())
                .listener(emailProcessorListener())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<MimeMessage> emailReader() throws IOException {

        log.info("reading email mbox from: " + emailMboxFile);

        InputStream input = new FileInputStream(emailMboxFile);
        MboxReader reader = new MboxReader(input);
        return reader;
    }

    @Bean
    @StepScope
    ItemProcessor<MimeMessage, ApiEmail> emailProcessor() {
        return new MimeMessageProcessor();
    }

    @Bean
    @StepScope
    ItemProcessListener<MimeMessage, ApiEmail> emailProcessorListener() {
        return new MimeMessageProcessorListener();
    }

    @Bean
    @StepScope
    ItemWriter<ApiEmail> emailWriter() {
        return new EmailWriter(
                emailService,
                configuration.getEmailTroveName(),
                configuration.getEmailTroveOwner());
    }
}