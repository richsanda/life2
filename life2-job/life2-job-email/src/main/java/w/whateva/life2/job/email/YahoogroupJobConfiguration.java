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
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.XmlEmailProcessor;
import w.whateva.life2.job.email.beans.YahoogroupMessageFileReader;
import w.whateva.life2.service.email.EmailService;
import w.whateva.life2.service.email.dto.ApiEmail;
import w.whateva.life2.xml.email.def.XmlEmail;
import w.whateva.life2.xml.email.def.XmlGroupMessage;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "yahoogroup.message.file.pattern")
public class YahoogroupJobConfiguration {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EmailService emailService;
    private final EmailLoadConfiguration configuration;

    @Value("${yahoogroup.message.file.pattern}")
    private String yahoogroupMessageFilePattern;

    @Autowired
    public YahoogroupJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, EmailService emailService, EmailLoadConfiguration configuration) {
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
                .<XmlGroupMessage, ApiEmail>chunk(200)
                .reader(emailReader())
                .processor(emailProcessor())
                .writer(emailWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<XmlGroupMessage> emailReader() throws IOException {
        MultiResourceItemReader<XmlGroupMessage> reader = new MultiResourceItemReader<>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources("file:" + yahoogroupMessageFilePattern);
        reader.setResources(resources);
        reader.setDelegate(oneMessageFileReader());
        return reader;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<XmlGroupMessage> oneMessageFileReader() {
        return new YahoogroupMessageFileReader(emailUnmarshaller());
    }

    @Bean
    @StepScope
    ItemProcessor<XmlEmail, ApiEmail> emailProcessor() {
        return new XmlEmailProcessor();
    }

    @Bean
    @StepScope
    ItemWriter<ApiEmail> emailWriter() {
        return new EmailWriter(
                emailService,
                configuration.getEmailTroveName(),
                configuration.getEmailTroveOwner());
    }

    @Bean
    public Jaxb2Marshaller emailUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XmlGroupMessage.class);
        marshaller.setCheckForXmlRootElement(true);
        return marshaller;
    }
}