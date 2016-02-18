package importaddresslist;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Bean
    public Job helloWorldJob(JobBuilderFactory jobs, Step importAddressListStep) {
        return jobs.get("ImportAddressListJob") //
                .incrementer(new RunIdIncrementer()) //
                .start(importAddressListStep)
                .build();
    }
}
