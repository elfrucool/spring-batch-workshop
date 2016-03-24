# PRACTICE 3: INTEGRATE WITH MY EXISTING APPLICATION

How to integrate a spring batch feature within an existing application? You will experiment that

To do it, we will go through 4 tasks that are the following:

1. Prepare environment
1. Use a custom database
1. Launch a batch job from a web request call
1. Launch a batch job periodically



## TASK 1: PREPARE ENVIRONMENT

Since we already practiced this in [exercise 1][EXERCISE-1] and [exercise 2][EXERCISE-2], and also, since we will need that code, you will continue this practice from the code in [exercise 2][EXERCISE-2].

## TASK 2: USE A CUSTOM DATABASE

For this practice, we will use a [postgresql](http://www.postgresql.org/) database. But feel free to use whatever [RDBMS](https://en.wikipedia.org/wiki/Relational_database_management_system) you want. 

To do so, follow the next 5 steps:

1. Create a database for this project in your RDBMS
1. Add a dependency to your RDBMS' JDBC driver and spring boot jdbc
1. Tell the application what database are you using
1. Modify the sql script used in [exercise 2][EXERCISE-2] to fit in your favourite RDBMS.
1. Run the application and see what happens

### Step 1. Create a database for this project in your RDBMS

Let's call it `batchworkshop` or whatever name you like, just remember it.

### Step 2. Add a dependency to your RDBMS' JDBC driver and spring boot JDBC

In `build.gradle` add a dependency to the JDBC driver (erase/comment the existent hsqldb dependency) and add another dependency to spring boot jdbc:

<strong>Note:</strong> take into account **two details** that are detailed below if you want to avoid a head ache ;)

```groovy
dependencies {
    compile("org.springframework.boot:spring-boot-starter-batch")
    compile("org.springframework.boot:spring-boot-starter-jdbc") <-- to create externalised DataSource beans
    compile("org.postgresql:postgresql:9.4.1208.jre7") // <-- instead of org.hsqldb:hsqldb
    testCompile("junit:junit")
}
```

Then refresh dependencies in your IDE

<strong>Two important details:</strong>

1. `hsqldb` dependency is transitively bring to the classpath, so not removing it from `build.gradle` has no side effects except readability.
1. You need to provide a DataSource connection pool, easiest way is adding the dependency `compile("org.springframework.boot:spring-boot-starter-jdbc")`. See section 29.1.2 of [http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html).


### Step 3. Tell the application what database are you using

Create a file named `src/main/resources/application.properties` with the following contents (modify the fields according to what you have configured in your RDBMS):

```properties
spring.datasource.url=jdbc:postgresql:batchworkshop
spring.datasource.username=postgres
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.continueOnError=true
```

<strong>Important Note:</strong> Dont forget to put the property `spring.datasource.continueOnError=true` it is your friend to launch your application again and again (see references below).

<strong>Rationale: </strong> Spring boot will do the following two tasks for you: a) It will create a DataSource bean in spring, b) It will execute your sql script (next step) in your configured database.

See more: 
* [Spring Boot Database Initialization][SPRING-BOOT-DATABASE-INITIALIZATION]
* [Spring Boot Externalising Configuration][SPRING-BOOT-EXTERNALISING-CONFIGURATION]

### Step 4. Create a contacts table in your RDBMS

Change the original contents of `src/main/resources/schema.sql` with the following content:

```sql
CREATE TABLE contacts (
  id    SERIAL PRIMARY KEY,
  name  VARCHAR(50) NOT NULL,
  email VARCHAR(75),
  phone VARCHAR(20)
);
```

<strong>Tip:</strong> you may test your script in your database directly and then erase the table before running the application. Or temporally set the property `spring.datasource.continueOnError=false` in `src/main/resources/application.properties` to test your script. See previous step for more information.

### Step 5. Run the application and see what happens

Follow the next five steps: (see [exercise 1][EXERCISE-1] for more details)

1. make the big fat executable jar file: `shell$ gradle build`
1. look for jar `build/libs/my-spring-batch-app-0.1.0.jar`
1. execute `shell$ java -jar build/libs/my-spring-batch-app-0.1.0.jar`
1. look at your batchworkshop database, examine all tables and learn
1. use the `contacts-big.csv` file instead of `contacts.csv` file and examine `batch_*` tables and learn a bit about pagination.

About `batch_*` tables see: [http://docs.spring.io/spring-batch/reference/html/metaDataSchema.html](http://docs.spring.io/spring-batch/reference/html/metaDataSchema.html)

<!--

##TASK 4: SET UP IMPORT ENVIRONMENT

You need to define where will the input fille be processed, and also we need the file itself, for this exercise, we will use the classpath, although in production environments, either fixed absolute paths are best or based on configurations/parameters.

Create a file named `contacts.csv` at `src/main/resources/` with the following contents:

```csv
name,email,phone
my name 1,name1@email.com,111-111-1111
my name 2,name2@email.com,222-222-2222
my name 3,name3@email.com,333-333-3333
```

##TASK 5: CREATE `Contact` OBJECT

You will use a POJO to store per-row information about the contacts to import to database:

```java
package importaddresslist;

public class Contact {
    private String name;
    private String email;
    private String phone;

    // getters & setters & toString/equals/hashCode
}

```

##TASK 6: DEFINE JOB

As in [Exercise 1][EXERCISE-1], just create a method to define the job bean inside `importaddresslist.BatchConfiguration` class:

```java
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
```

##TASK 7: DEFINE IMPORT STEP

Add the following method to the `BatchConfiguration` class:

```java
// package declaration
// several imports

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    // helloWorldJob() {...}

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
    
}
```

<strong>Remarks:</strong> `chunk` method needs to be parameterized in order to avoid compiler errors.

##TASK 8: DEFINE THE [ItemReader][BATCH-ITEM-READER]

Create a method to instantiate the reader:

```java
// package declaration
// several imports

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    // helloWorldJob() {...}

    // importAddressListStep() {...}

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

}
```

<strong>Rationale:</strong> Here are many things that need an explanation, although the names & javadoc are an excellent source of understanding.

##TASK 9: DEFINE THE [ItemProcessor][BATCH-ITEM-PROCESSOR]

For this moment, we only will use our own implementation of [PassThroughItemProcessor](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/item/support/PassThroughItemProcessor.html) which actually doesn't do anything except passing the object to the writer.

Feel free to add some custom code such as logging, changing case, etc.

```java
// package declaration
// several imports

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    // helloWorldJob() {...}

    // importAddressListStep() {...}

    // reader() {...}
    
    @Bean
    public ItemProcessor<Contact, Contact> processor() {
        return item -> item;
    }

}
```

##TASK 10: DEFINE THE [ItemWriter][BATCH-ITEM-WRITER]

Add a method to instantiate the `ItemWriter` object configured to write to database:

```java
// package declaration
// several imports

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    // helloWorldJob() {...}

    // importAddressListStep() {...}

    // reader() {...}
    
    // processor() {...}
    
    @Bean
    public ItemWriter<Contact> writer(DataSource dataSource) { // spring boot automatically will create dataSource bean
        JdbcBatchItemWriter<Contact> writer = new JdbcBatchItemWriter<>();
        
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO contacts (name, email, phone) VALUES (:name, :email, :phone)");
        writer.setDataSource(dataSource);
        
        return writer;
    }

}
```

<strong>Remarks: </strong> It is worth to check javadoc for each created class. Also, spring boot will automatically create a bean `dataSource` and autowire whenever it is required. See: [Spring Boot Database Initialization][SPRING-BOOT-DATABASE-INITIALIZATION]

##TASK 11: DEFINE VERIFY STEP

The final step of the job will add a step (_VerifyImportStep_) to verify that the content was correctly imported through executing a SQL query and dump the results.
 
You need to follow the next 2 steps:

1. Update job definition with the new expected step
1. Create the new step

### Details:

1. Update job definition with the new expected step

    ```java
    // package declaration
    // several imports
    
    @Configuration
    @EnableBatchProcessing
    public class BatchConfiguration {
    
        @Bean
        public Job helloWorldJob(JobBuilderFactory jobs, Step importAddressListStep, Step verifyImportStep) {
            return jobs.get("ImportAddressListJob") //
                    .incrementer(new RunIdIncrementer()) //
                    .start(importAddressListStep) //
                    .next(verifyImportStep) //
                    .build();
        }
    
        // importAddressListStep() {...}
    
        // reader() {...}
        
        // processor() {...}
        
        // writer() {...}
    
    }
    ```
    
    We are introducing two changes to `helloWorldJob` method:
    
    1. We are adding a new `verifyImportStep` parameter
    1. We are adding that step as `next` step
    
1. Create the new step

    ```java
    // package declaration
    // several imports
    
    @Configuration
    @EnableBatchProcessing
    public class BatchConfiguration {
    
        // helloWorldJob() {...}
    
        // importAddressListStep() {...}
    
        // reader() {...}
        
        // processor() {...}
        
        // writer() {...}

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

    }
    ```

    <strong>Remarks:</strong> This step is mainly based on: spring boot's [Accessing Relational Data using JDBC with Spring](https://spring.io/guides/gs/relational-data-access/).
    
**Note:** Spring boot makes spring smart enough to identify beans based on name instead of based on type.

##TASK 12: BUILD, RUN & ENJOY

Follow the next three steps: (see [exercise 1][EXERCISE-1] for more details)

1. make the big fat executable jar file: `shell$ gradle build`
1. look for jar `build/libs/my-spring-batch-app-0.1.0.jar`
1. execute `shell$ java -jar build/libs/my-spring-batch-app-0.1.0.jar`

##TASK 13: PLAY AROUND

Try to do the following four things:

1. Use `contacts-big.csv` file and see the results.
1. Add an additional column `birth_date` to both the csv file and the database, and import/dump it
1. Add a logic in ItemProcessor: when name is 'Echo' then return null (this will filter out that row)
1. Change the _VerifyImportStep_ to use: reader/processor/writer instead of single monolytic tasklet (you may need to rename some function/beans to differentiate readers/processors/writers for each step.
-->

<!-- global links -->

[FILE-BUILD-GRADLE]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-2/build.gradle
[FILE-GRADLE-PROPERTIES]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-2/gradle.properties
<!--[FILE-BATCH-INFRASTRUCTURE]: -->
[BATCH-ITEM-READER]: https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/item/ItemReader.html
[BATCH-ITEM-PROCESSOR]: https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/item/ItemProcessor.html
[BATCH-ITEM-WRITER]: https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/item/ItemWriter.html
[EXERCISE-1]: https://github.com/rvazquezglez/spring-batch-workshop/tree/master/exercise-1/README.md
[EXERCISE-2]: https://github.com/rvazquezglez/spring-batch-workshop/tree/master/exercise-2/README.md
[SPRING-BOOT-DATABASE-INITIALIZATION]: https://docs.spring.io/spring-boot/docs/current/reference/html/howto-database-initialization.html
[SPRING-BOOT-EXTERNALISING-CONFIGURATION]: https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html