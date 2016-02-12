package hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Bean
    public Tasklet helloTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("=================================================");
                System.out.println("HELLO WORLD");
                System.out.println("=================================================");
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step helloStep(StepBuilderFactory steps, Tasklet helloTasklet) {
        return steps.get("helloStep") //
                .tasklet(helloTasklet) //
                .build();
    }

    @Bean
    public Job helloWorldJob(JobBuilderFactory jobs, Step helloStep) {
        return jobs.get("helloWorldJob") //
                .incrementer(new RunIdIncrementer()) //
                .start(helloStep)
                .build();
    }
}
