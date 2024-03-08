package w.whateva.life2.job.person;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.job.person.beans.GroupProcessor;
import w.whateva.life2.job.person.beans.PersonProcessor;
import w.whateva.life2.job.person.beans.PersonWriter;
import w.whateva.life2.job.person.beans.UserWriter;
import w.whateva.life2.service.user.UserService;
import w.whateva.life2.xml.email.def.XmlGroup;
import w.whateva.life2.xml.email.def.XmlPerson;
import w.whateva.life2.xml.email.def.XmlUser;

@Configuration
@ConfigurationProperties(prefix = "person")
public class XmlPersonJobConfiguration {

    private transient Logger log = LoggerFactory.getLogger(XmlPersonJobConfiguration.class);

    @Getter
    @Setter
    private String owner;

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final PersonService personService;
    private final UserService userService;

    @Value("${person.xml.file}")
    private String personXmlFile;

    @Value("${group.xml.file}")
    private String groupXmlFile;

    @Value("${user.xml.file}")
    private String userXmlFile;

    @Autowired
    public XmlPersonJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, PersonService personService, UserService userService) {
        this.jobs = jobs;
        this.steps = steps;
        this.personService = personService;
        this.userService = userService;
    }

    @Bean
    public Job loadPersonJob() throws Exception {
        return this.jobs.get("loadPersonJob")
                .start(loadPersonStep())
                .build();
    }

    @Bean
    public Step loadPersonStep() throws Exception {
        return this.steps.get("loadPersonStep")
                .<XmlPerson, ApiPerson>chunk(10)
                .reader(personReader())
                .processor(personProcessor())
                .writer(personWriter())
                .build();
    }

    @Bean
    public Job loadGroupJob() throws Exception {
        return this.jobs.get("loadGroupJob")
                .start(loadGroupStep())
                .build();
    }

    @Bean
    public Step loadGroupStep() throws Exception {
        return this.steps.get("loadPersonStep")
                .<XmlGroup, ApiPerson>chunk(10)
                .reader(groupReader())
                .processor(groupProcessor())
                .writer(personWriter())
                .build();
    }

    @Bean
    public Job loadUserJob() throws Exception {
        return this.jobs.get("loadUserJob")
                .start(loadUserStep())
                .build();
    }

    @Bean
    public Step loadUserStep() throws Exception {
        return this.steps.get("loadUserStep")
                .<XmlUser, XmlUser>chunk(10)
                .reader(userReader())
                .writer(userWriter())
                .build();
    }

    @Bean
    public Jaxb2Marshaller personUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XmlPerson.class, XmlGroup.class, XmlUser.class);
        marshaller.setCheckForXmlRootElement(true);
        return marshaller;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<XmlPerson> personReader() {

        log.info("reading person file from: " + personXmlFile);

        StaxEventItemReader<XmlPerson> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName("person");
        reader.setResource(new FileSystemResource(personXmlFile));
        reader.setUnmarshaller(personUnmarshaller());
        return reader;
    }

    @Bean
    @StepScope
    ItemProcessor<XmlPerson, ApiPerson> personProcessor() {
        return new PersonProcessor(owner);
    }

    @Bean
    ItemWriter<ApiPerson> personWriter() {
        return new PersonWriter(personService);
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<XmlGroup> groupReader() {

        log.info("reading group file from: " + groupXmlFile);

        StaxEventItemReader<XmlGroup> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName("group");
        reader.setResource(new FileSystemResource(groupXmlFile));
        reader.setUnmarshaller(personUnmarshaller());
        return reader;
    }

    @Bean
    @StepScope
    ItemProcessor<XmlGroup, ApiPerson> groupProcessor() {
        return new GroupProcessor(owner);
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<XmlUser> userReader() {

        log.info("reading user file from: " + userXmlFile);

        StaxEventItemReader<XmlUser> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName("user");
        reader.setResource(new FileSystemResource(userXmlFile));
        reader.setUnmarshaller(personUnmarshaller());
        return reader;
    }

    @Bean
    ItemWriter<XmlUser> userWriter() {
        return new UserWriter(userService);
    }
}