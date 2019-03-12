package w.whateva.life2.job.email;

import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import w.whateva.life2.api.email.EmailService;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.HistoricEmailFileReader;
import w.whateva.life2.job.email.beans.MimeMessageProcessor;
import w.whateva.life2.job.email.beans.MimeMessageProcessorListener;

import javax.mail.internet.MimeMessage;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "email.historic.file.pattern")
@EnableBatchProcessing
public class HistoricEmailJobConfiguration extends DefaultBatchConfigurer {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EmailService emailService;

    @Value("${email.historic.file.pattern}")
    private String emailHistoricFilePattern;

    @Autowired
    public HistoricEmailJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, EmailService emailService) {
        this.jobs = jobs;
        this.steps = steps;
        this.emailService = emailService;
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
                .<MimeMessage, ApiEmail>chunk(200)
                .reader(emailReader())
                .processor(emailProcessor()).faultTolerant().skipLimit(100).skip(Exception.class)
                .listener(emailProcessorListener())
                .writer(emailWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<MimeMessage> emailReader() throws IOException {
        MultiResourceItemReader<MimeMessage> reader = new MultiResourceItemReader<>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources("file:" + emailHistoricFilePattern);
        reader.setResources(resources);
        reader.setDelegate(oneMessageFileReader());
        return reader;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<MimeMessage> oneMessageFileReader() {
        return new HistoricEmailFileReader();
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
        return new EmailWriter(emailService);
    }
}