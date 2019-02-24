package w.whateva.life2.job.email;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
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
import w.whateva.life2.api.email.EmailService;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.job.email.beans.CsvEmailProcessor;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.XmlEmailProcessor;
import w.whateva.life2.xml.email.csv.CsvEmail;
import w.whateva.life2.xml.email.def.XmlEmail;
import w.whateva.life2.xml.email.def.XmlGroupMessage;

import java.io.IOException;
import java.util.Date;

@Configuration
@ConditionalOnProperty(name = "email.csv.file")
@EnableBatchProcessing
public class CsvEmailJobConfiguration extends DefaultBatchConfigurer {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EmailService emailService;

    @Value("${email.csv.file}")
    private String emailCsvFile;

    @Autowired
    public CsvEmailJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, EmailService emailService) {
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
                .<CsvEmail, ApiEmail>chunk(200)
                .reader(emailReader())
                .processor(emailProcessor())
                .writer(emailWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<CsvEmail> emailReader() throws IOException {
        FlatFileItemReader<CsvEmail> reader = new FlatFileItemReader<>();
        //Set input file location
        reader.setResource(new FileSystemResource(emailCsvFile));

        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(1);

        reader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());

        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper<CsvEmail>() {
            {

                setLineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB) {
                    {
                        setNames("Importance",
                                "Icon",
                                "Priority",
                                "Subject",
                                "From",
                                "Message To Me",
                                "Message CC to Me",
                                "Sender Name",
                                "CC",
                                "To",
                                "Received",
                                "Message_Size",
                                "Contents",
                                "Created",
                                "Modified",
                                "Subject Prefix",
                                "Has Attachments",
                                "Normalized Subject",
                                "Object Type",
                                "Content Unread");
                    }
                });

                setFieldSetMapper(new BeanWrapperFieldSetMapper<CsvEmail>() {
                    {
                        setTargetType(CsvEmail.class);
                    }
                });
            }
        });
        return reader;
    }

    ItemProcessor<CsvEmail, ApiEmail> emailProcessor() {
        return new CsvEmailProcessor();
    }

    @Bean
    @StepScope
    ItemWriter<ApiEmail> emailWriter() {
        return new EmailWriter(emailService);
    }


    @Bean
    public Jaxb2Marshaller emailUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(XmlEmail.class, XmlGroupMessage.class);
        marshaller.setCheckForXmlRootElement(true);
        return marshaller;
    }
}