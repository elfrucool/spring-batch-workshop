package importaddresslist;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public ItemReader<Contact> reader() {
        FlatFileItemReader<Contact> reader = new FlatFileItemReader<>();

        reader.setResource(new ClassPathResource("contacts.csv"));
        reader.setLinesToSkip(1); // we will skip column names row

        reader.setLineMapper(new DefaultLineMapper<Contact>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"name", "email", "phone"});
            }});

            setFieldSetMapper(new BeanWrapperFieldSetMapper<Contact>(){{
                setTargetType(Contact.class);
            }});
        }});

        return reader;
    }

    @Bean
    public Step importAddressListStep(
            StepBuilderFactory steps,
            ItemReader<Contact> reader,
            ItemProcessor<Contact, Contact> processor,
            ItemWriter<Contact> writer) //
    {
        return steps.get("ImportAddressListStep") //
                .chunk(10) // process items in groups of 10
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    @Bean
    public Job helloWorldJob(JobBuilderFactory jobs, Step importAddressListStep) {
        return jobs.get("ImportAddressListJob") //
                .incrementer(new RunIdIncrementer()) //
                .start(importAddressListStep)
                .build();
    }
}
