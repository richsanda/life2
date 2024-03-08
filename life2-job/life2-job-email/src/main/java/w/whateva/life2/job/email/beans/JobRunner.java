package w.whateva.life2.job.email.beans;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JobRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @PostConstruct
    public void runJob() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters());
        System.out.println("Job Execution Status: " + jobExecution.getStatus());
    }
}
