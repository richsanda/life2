package w.whateva.life2.job.email;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
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
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.FacebookMessageFileReader;
import w.whateva.life2.job.email.beans.FacebookMessageProcessor;
import w.whateva.life2.service.email.EmailService;
import w.whateva.life2.service.email.dto.ApiEmail;
import w.whateva.life2.xml.email.facebook.FacebookMessageThread;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "facebook.message.file.pattern")
public class FacebookMessageJobConfiguration {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EmailService emailService;
    private final EmailLoadConfiguration configuration;

    @Value("${facebook.message.file.pattern}")
    private String facebookMessageFilePattern;

    @Autowired
    public FacebookMessageJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, EmailService emailService, EmailLoadConfiguration configuration) {
        this.jobs = jobs;
        this.steps = steps;
        this.emailService = emailService;
        this.configuration = configuration;
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
                .<FacebookMessageThread, ApiEmail>chunk(200)
                .reader(emailReader())
                .processor(emailProcessor())
                .writer(emailWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<FacebookMessageThread> emailReader() throws IOException {
        MultiResourceItemReader<FacebookMessageThread> reader = new MultiResourceItemReader<>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources("file:" + facebookMessageFilePattern);
        reader.setResources(resources);
        reader.setDelegate(oneMessageFileReader());
        return reader;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<FacebookMessageThread> oneMessageFileReader() {
        return new FacebookMessageFileReader();
    }

    @Bean
    @StepScope
    ItemProcessor<FacebookMessageThread, ApiEmail> emailProcessor() {
        return new FacebookMessageProcessor();
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