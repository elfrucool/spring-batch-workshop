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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Bean
    public ItemReader<Contact> reader() {
        FlatFileItemReader<Contact> reader = new FlatFileItemReader<>();

        reader.setResource(new FileSystemResource("work/inbound/contacts.csv"));
        reader.setLinesToSkip(1); // we will skip column names row

        reader.setLineMapper(new DefaultLineMapper<Contact>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[]{"name", "email", "phone"});
            }});

            setFieldSetMapper(new BeanWrapperFieldSetMapper<Contact>() {{
                setTargetType(Contact.class);
            }});
        }});

        return reader;
    }

    /**
     * @see org.springframework.batch.item.support.PassThroughItemProcessor
     */
    @Bean
    public ItemProcessor<Contact, Contact> processor() {
        return item -> item;
    }

    @Bean
    public ItemWriter<Contact> writer(DataSource dataSource) {
        JdbcBatchItemWriter<Contact> writer = new JdbcBatchItemWriter<>();

        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO contacts (name, email, phone) VALUES (:name, :email, :phone)");
        writer.setDataSource(dataSource);

        return writer;
    }

    @Bean
    public Step importAddressListStep(
            StepBuilderFactory steps,
            ItemReader<Contact> reader,
            ItemProcessor<Contact, Contact> processor,
            ItemWriter<Contact> writer) //
    {
        return steps.get("ImportAddressListStep") //
                .<Contact, Contact>chunk(10) // process items in groups of 10
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step verifyImportStep(StepBuilderFactory steps, JdbcTemplate jdbcTemplate) {
        return steps //
                .get("VerifyImportStep") //
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.query(
                            "SELECT name,email,phone FROM contacts ORDER BY name",

                            (rs, rowNum) ->
                                    new HashMap<String, String>() {{
                                        put("name", rs.getString("name"));
                                        put("email", rs.getString("email"));
                                        put("phone", rs.getString("phone"));
                                    }}
                    ).forEach(System.out::println);

                    return RepeatStatus.FINISHED;
                }) //
                .build();
    }

    @Bean
    public Job importAddressListJob(
            JobBuilderFactory jobs,
            Step importAddressListStep,
            Step verifyImportStep) {
        return jobs.get("ImportAddressListJob") //
                .incrementer(new RunIdIncrementer()) //
                .start(importAddressListStep) //
                .next(verifyImportStep) //
                .build();
    }
}
