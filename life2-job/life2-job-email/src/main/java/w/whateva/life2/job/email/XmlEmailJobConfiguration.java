package w.whateva.life2.job.email;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.xml.email.def.XmlEmail;
import w.whateva.life2.xml.email.def.XmlGroupMessage;
import w.whateva.life2.xml.email.def.XmlPerson;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "email.xml.file.pattern")
public class XmlEmailJobConfiguration {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final XmlEmailBatchConfiguration config;

    @Value("${email.xml.file.pattern}")
    private String emailXmlFilePattern;

    @Value("${person.xml.file}")
    private String personXmlFile;

    @Value("${email.xml.root}")
    private String fragmentRootElementName;

    @Autowired
    public XmlEmailJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, XmlEmailBatchConfiguration config) {
        this.jobs = jobs;
        this.steps = steps;
        this.config = config;
    }

    @Bean
    public Job loadEmailJob() throws Exception {
        return this.jobs.get("loadEmailJob")
                .start(loadEmailStep())
                .next(loadPersonStep())
                .next(addGroupAddressToSendersStep())
                .build();
    }

    @Bean
    public Step loadEmailStep() throws Exception {
        return this.steps.get("loadEmailStep")
                .<XmlEmail, ApiEmail>chunk(10)
                .reader(emailReader())
                .processor(config.emailProcessor())
                .writer(config.emailWriter())
                .build();
    }

    @Bean
    public Step loadPersonStep() throws Exception {
        return this.steps.get("loadPersonStep")
                .<XmlPerson, ApiPerson>chunk(10)
                .reader(personReader())
                .processor(config.personProcessor())
                .writer(config.personWriter())
                .build();
    }

    @Bean
    public Step addGroupAddressToSendersStep() {
        return this.steps.get("addGroupAddressToSendersStep")
                .tasklet(config.addGroupAddressToSendersTasklet())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<XmlEmail> emailReader() throws IOException {
        MultiResourceItemReader<XmlEmail> reader = new MultiResourceItemReader<XmlEmail>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources("file:" + emailXmlFilePattern);
        reader.setResources(resources);
        reader.setDelegate(oneEmailReader());
        return reader;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<XmlEmail> oneEmailReader() {
        StaxEventItemReader<XmlEmail> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName(fragmentRootElementName);
        //Resource resource = resourceLoader.getResource("file:/Users/rich/Downloads/games/2005.1.1.xml");
        //reader.setResource(resource);
        reader.setUnmarshaller(emailUnmarshaller());
        return reader;
    }

    @Bean
    public Jaxb2Marshaller emailUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XmlEmail.class, XmlPerson.class, XmlGroupMessage.class);
        marshaller.setCheckForXmlRootElement(true);
        return marshaller;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<XmlPerson> personReader() {
        StaxEventItemReader<XmlPerson> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName("person");
        reader.setResource(new FileSystemResource(personXmlFile));
        reader.setUnmarshaller(emailUnmarshaller());
        return reader;
    }
}