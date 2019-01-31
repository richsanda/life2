package w.whateva.life2.job.person;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
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
import w.whateva.life2.job.person.beans.PersonProcessor;
import w.whateva.life2.job.person.beans.PersonWriter;
import w.whateva.life2.xml.email.def.XmlPerson;

@Configuration
@ConfigurationProperties(prefix = "person")
@EnableBatchProcessing
public class XmlPersonJobConfiguration extends DefaultBatchConfigurer {

    private transient Logger log = LoggerFactory.getLogger(XmlPersonJobConfiguration.class);

    @Getter
    @Setter
    private String owner;

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final PersonService personService;

    @Value("${person.xml.file}")
    private String personXmlFile;

    @Autowired
    public XmlPersonJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, PersonService personService) {
        this.jobs = jobs;
        this.steps = steps;
        this.personService = personService;
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
    public Jaxb2Marshaller personUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XmlPerson.class);
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
}