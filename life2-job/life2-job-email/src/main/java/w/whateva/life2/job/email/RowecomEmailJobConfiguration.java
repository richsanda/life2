package w.whateva.life2.job.email;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import w.whateva.life2.api.email.EmailService;
import w.whateva.life2.api.email.dto.ApiEmail;
import w.whateva.life2.job.email.beans.RowecomInboxEmailProcessor;
import w.whateva.life2.job.email.beans.EmailWriter;
import w.whateva.life2.job.email.beans.RowecomSentEmailProcessor;
import w.whateva.life2.xml.email.csv.RowecomInboxEmail;
import w.whateva.life2.xml.email.csv.RowecomSentEmail;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "rowecom.email.folder")
@EnableBatchProcessing
public class RowecomEmailJobConfiguration extends DefaultBatchConfigurer {

    private static final String SENT_FOLDER_NAME = "SENT";
    
    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final EmailService emailService;

    @Value("${email.csv.file}")
    private String emailCsvFile;
    
    @Value("${rowecom.email.folder}")
    private String rowecomEmailFolder;

    @Value("${rowecom.email.sender}")
    private String rowecomEmailSender;

    @Autowired
    public RowecomEmailJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, EmailService emailService) {
        this.jobs = jobs;
        this.steps = steps;
        this.emailService = emailService;
    }

    @Bean
    @ConditionalOnProperty(name = "rowecom.email.folder", havingValue = "inbox")
    public Job loadInboxEmailJob() throws Exception {
        return this.jobs.get("loadEmailJob")
                .start(loadInboxEmailStep())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "rowecom.email.folder", havingValue = "inbox")
    public Step loadInboxEmailStep() throws Exception {
        return this.steps.get("loadEmailStep")
                .<RowecomInboxEmail, ApiEmail>chunk(200)
                .reader(rowecomInboxEmailReader())
                .processor(rowecomInboxEmailProcessor())
                .writer(emailWriter())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "rowecom.email.folder", havingValue = "sent")
    public Job loadSentEmailJob() throws Exception {
        return this.jobs.get("loadEmailJob")
                .start(loadSentEmailStep())
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "rowecom.email.folder", havingValue = "sent")
    public Step loadSentEmailStep() throws Exception {
        return this.steps.get("loadEmailStep")
                .<RowecomSentEmail, ApiEmail>chunk(200)
                .reader(rowecomSentEmailReader())
                .processor(rowecomSentEmailProcessor())
                .writer(emailWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RowecomInboxEmail> rowecomInboxEmailReader() throws IOException {
        FlatFileItemReader<RowecomInboxEmail> reader = new FlatFileItemReader<>();
        //Set input file location
        reader.setResource(new FileSystemResource(emailCsvFile));

        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(1);

        reader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());

        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper<RowecomInboxEmail>() {
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

                setFieldSetMapper(new BeanWrapperFieldSetMapper<RowecomInboxEmail>() {
                    {
                        setTargetType(RowecomInboxEmail.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RowecomSentEmail> rowecomSentEmailReader() throws IOException {
        FlatFileItemReader<RowecomSentEmail> reader = new FlatFileItemReader<>();
        //Set input file location
        reader.setResource(new FileSystemResource(emailCsvFile));

        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(1);

        reader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());

        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper<RowecomSentEmail>() {
            {

                setLineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB) {
                    {
                        setNames("Importance",
                                "Icon",
                                "Priority",
                                "Subject",
                                "Message To Me",
                                "Message CC to Me",
                                "Sender Name",
                                "CC",
                                "To",
                                "Sent",
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

                setFieldSetMapper(new BeanWrapperFieldSetMapper<RowecomSentEmail>() {
                    {
                        setTargetType(RowecomSentEmail.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    @StepScope
    ItemProcessor<RowecomInboxEmail, ApiEmail> rowecomInboxEmailProcessor() {
        return new RowecomInboxEmailProcessor();
    }

    @Bean
    @StepScope
    ItemProcessor<RowecomSentEmail, ApiEmail> rowecomSentEmailProcessor() {
        return new RowecomSentEmailProcessor(rowecomEmailSender);
    }

    @Bean
    @StepScope
    ItemWriter<ApiEmail> emailWriter() {
        return new EmailWriter(emailService);
    }
}