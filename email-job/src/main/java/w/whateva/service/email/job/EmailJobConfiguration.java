package w.whateva.service.email.job;

import generated.Email;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import w.whateva.service.email.sapi.sao.ApiEmail;

import java.io.IOException;

@Configuration
public class EmailJobConfiguration {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EmailBatchConfiguration config;

    @Value("${email.xml.file.pattern}")
    private String emailXmlFilePattern;

    @Autowired
    public EmailJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, EmailBatchConfiguration config) {
        this.jobs = jobs;
        this.steps = steps;
        this.config = config;
    }

    @Bean
    public Job loadEmailJob() throws Exception {
        return this.jobs.get("loadEmailJob").start(loadEmailStep()).build();
    }

    @Bean
    public Step loadEmailStep() throws Exception {
        return this.steps.get("loadEmailStep")
                .<ApiEmail, ApiEmail>chunk(10)
                .reader(emailReader())
                .processor(config.emailProcessor())
                .writer(config.emailWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<ApiEmail> emailReader() throws IOException {
        MultiResourceItemReader<ApiEmail> reader = new MultiResourceItemReader<ApiEmail>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources("file:" + emailXmlFilePattern);
        reader.setResources(resources);
        reader.setDelegate(oneEmailReader());
        return reader;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<ApiEmail> oneEmailReader() {
        StaxEventItemReader<ApiEmail> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName("Email");
        //Resource resource = resourceLoader.getResource("file:/Users/rich/Downloads/games/2005.1.1.xml");
        //reader.setResource(resource);
        reader.setUnmarshaller(emailUnmarshaller());
        return reader;
    }

    @Bean
    public Jaxb2Marshaller emailUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(ApiEmail.class);
        marshaller.setCheckForXmlRootElement(true);
        return marshaller;
    }
}