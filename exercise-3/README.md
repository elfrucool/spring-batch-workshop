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
    compile("org.springframework.boot:spring-boot-starter-jdbc") // <-- to create externalised DataSource beans
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

Follow the next five actions: (see [exercise 1][EXERCISE-1] for more details)

1. make the big fat executable jar file: `shell$ gradle build`
1. look for jar `build/libs/my-spring-batch-app-0.1.0.jar`
1. execute `shell$ java -jar build/libs/my-spring-batch-app-0.1.0.jar`
1. look at your batchworkshop database, examine all tables and learn
1. use the `contacts-big.csv` file instead of `contacts.csv` file and examine `batch_*` tables and learn a bit about pagination.

About `batch_*` tables see: [http://docs.spring.io/spring-batch/reference/html/metaDataSchema.html](http://docs.spring.io/spring-batch/reference/html/metaDataSchema.html)

## TASK 3: LAUNCH A BATCH JOB FROM A WEB REQUEST CALL

Until now, the examples are assuming that the batch job is the main purpose of the entire application. But in many cases the batch job is part of another application; perhaps to import a catalog on a regular basis or to generate ond depand reports, etc.

In this task we will learn how to launch a batch job programmatically from an MVC controller call. This is a situation for an on-demand launching of the job (it could be a user clicking a button "run now" or a REST service call that is invoked in certain conditions).

For this task, you need to follow the next 7 steps:

1. Add dependencies to spring MVC
1. Prevent the application to auto-launch spring batch jobs
1. Create a controller to launch the batch job through clicking a button
1. Create a view with a button to launch the job
1. Launch the job when the button is clicked
1. See job execution result in a view
1. Build & test & enjoy

### STEP 1. Add dependencies to spring MVC

This step is mainly based on: https://spring.io/guides/gs/serving-web-content/

Open `build.gradle` file and add the following dependency two dependencies: `org.springframework.boot:spring-boot-devtools` and `org.springframework.boot:spring-boot-starter-thymeleaf`.
 
See https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-devtools.html, http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-web-applications.html and http://www.thymeleaf.org/ for further reference.

```groovy
dependencies {
    compile("org.springframework.boot:spring-boot-starter-thymeleaf") // <-- spring mvc with thymeleaf template engine
    compile("org.springframework.boot:spring-boot-devtools") // <-- to restart/reload if something changes
    compile("org.springframework.boot:spring-boot-starter-batch")
    compile("org.springframework.boot:spring-boot-starter-jdbc")
    compile("org.postgresql:postgresql:9.4.1208.jre7")
    testCompile("junit:junit")
}
```

Then refresh dependencies in your IDE

### STEP 2. Prevent the application to auto-launch spring batch jobs

Spring boot automatically launches batch application unless you set this property to false in file `src/main/resources/application.properties`:

```properties
# ... other properties ...
spring.batch.job.enabled=false
```

See: http://stackoverflow.com/questions/23447948/how-spring-boot-run-batch-jobs

### STEP 3. Create a controller to launch the batch job through clicking a button

In package `importaddresslist` create a class named `ImportAddressController` with the following contents:

```java
package importaddresslist;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ImportAddressController {
    @RequestMapping(value = "/importaddress", method = RequestMethod.GET)
    public String index() {
        return "index";
    }
}
```

### STEP 4. Create a view with a button to launch the job

Create the following folder: `src/main/resources/templates`, then create inside it a file named `index.html` with the following contents:

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Import Address List batch job</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<h1>Import Address List</h1>

<form action="#" th:action="@{/importaddress}" method="post">
    <p>Import Address List:</p>
    <input type="submit" value="Now!"/>
</form>

</body>
</html>
```

### STEP 5. Launch the job when the button is clicked

We will process a POST request without arguments, we will send to the view the job execution results to render it:

```java
package importaddresslist;

// imports...

import java.util.Date;

@Controller
public class ImportAddressController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importAddressListJob; // the job to be launched identified by its class (Job.class)

    // existing index() method

    @RequestMapping(value = "/importaddress", method = RequestMethod.POST)
    public String importAddress(Model model) throws Exception {
    
        JobParameters parameters = new JobParametersBuilder() //
                .addDate("timestamp", new Date(), true) // to allow repeated executions of the job
                .toJobParameters();
                
        JobExecution execution = jobLauncher.run(importAddressListJob, parameters);
        
        System.out.println("job execution: " + execution);
        model.addAttribute("execution", execution);
        
        return "result";
    }
}

```

<strong>Important: </strong> A job can only be successfully executed ONCE with the same set of parameters, so we are using a 'timestamp' parameter with the current date/time to allow subsequent executions of the job.

### STEP 6: See job execution result in a view

Put the following content into `src/main/resources/templates/result.html`:

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Import Address List batch job Results</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<h1>Import Address List Results</h1>

<pre th:text="${execution}"></pre>

<a href="#" th:href="@{/importaddress}">Run the job again</a>

</body>
</html>
```

### STEP 7. Build & test & enjoy

Follow the next five actions: (see [exercise 1][EXERCISE-1] for more details)

1. clean tables in `batchworkshop` database (or delete/create it again)
1. make the big fat executable jar file: `shell$ gradle build`
1. look for jar `build/libs/my-spring-batch-app-0.1.0.jar`
1. execute `shell$ java -jar build/libs/my-spring-batch-app-0.1.0.jar`
1. Open http://localhost:8080/importaddress in a browser and click the _Now!_ button
1. look at your batchworkshop database, examine all tables and learn
1. use the `contacts-big.csv` file instead of `contacts.csv` file and examine `batch_*` tables and learn a bit about pagination.
1. how could you list the `contacts` table records in a view (e.g. index.html) ?

## TASK 4: LAUNCH A BATCH JOB PERIODICALLY

Often, the batch jobs run periodically without direct human intervention; examples: a daily report, a hourly feed processing; a monthly clean up process...

In this task, you will make your job to run periodically (every 10 seconds). Imagine this is a system that continuously imports a csv into a database (perhaps the file is sent from an external system through SFTP)

Follow the next seven steps:

1. Define `inbound`/`outbound` folders
1. Modify the job to read the file from `inbound` folder
1. Add a step to move the processed file from `inbound` to `outbound`
1. Enable tasks framework
1. Create a service that will be called periodically
1. Use the service to launch the job
1. Build & test & enjoy

### STEP 1. Define inbound/outbound folders

Yo will need a directory to put the new csv file to import (inbound), and a directory to put the processed file (outbound), there may be the case in which the job end with an error, in such case, you can take the file in outbound directory and after fixing it, put it again in inbound directory to be processed other time.

Create the following directories:

```sh
work/inbound
work/outbound
```

The `inbound` directory is for putting the `contacts.csv` files to be processed, while the `outbound` directory is the place where the job will put the already processed `contacts.csv` files with the name convention: `contacts-YYYYMMDD_HHMMSS.csv`.
 
The reason to append the date-time to the name of the file is for, in case of a problem, easily identify which file caused the error.

### STEP 2. Modify the job to read the file from `inbound` folder

We will change the location of the `contacts.csv` file from classpath to `work/inbound` folder.

```java
package importaddresslist;

// imports...

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Bean
    public ItemReader<Contact> reader() {
        FlatFileItemReader<Contact> reader = new FlatFileItemReader<>();

        reader.setResource(new FileSystemResource("work/inbound/contacts.csv")); // <-- change ClassPathResource("contacts.csv") with this 
        reader.setLinesToSkip(1); // we will skip column names row
        
        // other stuff...
        
        return reader;
    }
    // other methods
}
```

**Notes:**

1. About resources in spring, see: [http://docs.spring.io/autorepo/docs/spring/3.2.x/spring-framework-reference/html/resources.html](http://docs.spring.io/autorepo/docs/spring/3.2.x/spring-framework-reference/html/resources.html)

1. The folder location is **relative to where the application is executed**. If you got an exception about resource not available, either check where is the application being executed or set an absolute path. In production, either you can [externalize the configuration][SPRING-BOOT-EXTERNALISING-CONFIGURATION] or set an absolute path (a short an easy-to-remember one) e.g. `/var/my-job/inbound` & `/var/my-job/outbound`.

1. Since you are no longer reading the resource from classpath, you need a mechanism for Task #2 for identifying if the file already exist so you don't get an exception if it is absent, this is the suggested change in `ImportAddressController.java`:

```java
package importaddresslist;

// several imports

@Controller
public class ImportAddressController {

    // other stuff

    @RequestMapping(value = "/importaddress", method = RequestMethod.POST)
    public String importAddress(Model model) throws Exception {
        if (new File("work/inbound/contacts.csv").exists()) { // <-- add this if
            JobParameters parameters = new JobParametersBuilder() //
                    .addDate("timestamp", new Date(), true) // to allow repeated executions of the job
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(importAddressListJob, parameters);
            System.out.println("job execution: " + execution);
            model.addAttribute("execution", execution);
        } else {
            model.addAttribute("execution", "there was no file to be processed, so nothing done.");
        }
        return "result";
    }
}
```

### STEP 3. Add a step to move the processed file from inbound to outbound

You need to take two actions:

1. Create the step, and
1. Add the step into the job

#### Action 1: create the step

```java
package importaddresslist;

//imports...

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    // other stuff...

    @Bean
    public Step moveProcessedFileToOutboundStep(StepBuilderFactory steps) {
        return steps
                .get("MoveProcessedFileToOutboundStep")
                .tasklet((contribution, chunkContext) -> {
                    Path from = new File("work/inbound/contacts.csv").toPath();
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String toFileName = String.format("work/outbound/contacts-%s.csv", dateFormat.format(new Date()));
                    Path toPath = new File(toFileName).toPath();
                    Files.move(from, toPath);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    // other stuff...
}
```

**Further reading:** about moving files using java.nio.* see: [https://docs.oracle.com/javase/tutorial/essential/io/move.html](https://docs.oracle.com/javase/tutorial/essential/io/move.html) and [https://docs.oracle.com/javase/tutorial/essential/io/pathClass.html](https://docs.oracle.com/javase/tutorial/essential/io/pathClass.html)

#### Action 2: add the step into the job

```java
package importaddresslist;

//imports...

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    // other stuff...

    @Bean
    public Step moveProcessedFileToOutboundStep(StepBuilderFactory steps) {
        // implementation
    }

    @Bean
    public Job importAddressListJob(
            JobBuilderFactory jobs,
            Step importAddressListStep,
            Step verifyImportStep,
            Step moveProcessedFileToOutboundStep) { // <-- this last parameter
        return jobs.get("ImportAddressListJob") //
                .incrementer(new RunIdIncrementer()) //
                .start(importAddressListStep) //
                .next(verifyImportStep) //
                .next(moveProcessedFileToOutboundStep) // <-- this 'next' call
                .build();
    }

    // other stuff...
}
```

**Note:** you man see that the date/time is not the current one (it happened to me) this is because the JVM is probably running in other time zone (in my case GMT+0)

### STEP 4. Enable tasks framework

In `Application.java` add the [@EnableScheduling](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#scheduling-enable-annotation-support) annotation:

```java
package importaddresslist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // <-- this will ensure that a background task executor is created
public class Application {
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
}

```

**Rationale:** see: [Spring Boot Scheduled tasks][SPRING-BOOT-SCHEDULED], also: [http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html)

### STEP 5. Create a service that will be called periodically

### STEP 6. Use the service to launch the job

### STEP 7. Build & test & enjoy

Follow the next nine actions: (see [exercise 1][EXERCISE-1] for more details)

1. clean tables in `batchworkshop` database (or delete/create it again)
1. make the big fat executable jar file: `shell$ gradle build`
1. look for jar `build/libs/my-spring-batch-app-0.1.0.jar`
1. execute `shell$ java -jar build/libs/my-spring-batch-app-0.1.0.jar`
1. look at the logs at an interval of ten seconds, examine the `outbound` folder
1. look at your batchworkshop database, examine all tables and learn
1. put another file in the `inbound` folder and see what happens.
1. how could you list the `contacts` table records in a view (e.g. index.html) ?
1. how do you read files named `contacts*.csv` ?

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
[SPRING-BOOT-SCHEDULED]: https://spring.io/guides/gs/scheduling-tasks/