package w.whateva.life2.job.file;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
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
import w.whateva.life2.api.neat.NeatService;
import w.whateva.life2.api.neat.dto.ApiNeatFile;
import w.whateva.life2.job.file.beans.NeatFileReader;
import w.whateva.life2.job.file.beans.NeatFileWriter;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "neat.item.file.pattern")
@EnableBatchProcessing
public class NeatJobConfiguration extends DefaultBatchConfigurer {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final NeatService neatService;

    @Value("${neat.item.file.pattern}")
    private String neatItemFilePattern;

    @Autowired
    public NeatJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, NeatService neatService) {
        this.jobs = jobs;
        this.steps = steps;
        this.neatService = neatService;
    }

    @Bean
    public Job loadNeatJob() throws Exception {
        return this.jobs.get("loadNeatJob")
                .start(loadNeatStep())
                .build();
    }

    @Bean
    public Step loadNeatStep() throws Exception {
        return this.steps.get("loadNeatStep")
                .<ApiNeatFile, ApiNeatFile>chunk(200)
                .reader(neatReader())
                //.processor(neatProcessor())
                .writer(neatWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<ApiNeatFile> neatReader() throws IOException {
        MultiResourceItemReader<ApiNeatFile> reader = new MultiResourceItemReader<>();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        Resource[] resources = resolver.getResources("file:" + neatItemFilePattern);
        reader.setResources(resources);
        reader.setDelegate(oneNeatFileReader());
        return reader;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<ApiNeatFile> oneNeatFileReader() {
        return new NeatFileReader();
    }

//    @Bean
//    @StepScope
//    ItemProcessor<NeatFile, ApiFile> neatProcessor() {
//        return new NeatFileProcessor();
//    }

    @Bean
    @StepScope
    ItemWriter<ApiNeatFile> neatWriter() {
        return new NeatFileWriter(neatService);
    }
}