#PRACTICE 1: HELLO WORLD

We will go through a rough introduction and then will produce the classical _hello world_ in Spring Batch

## ABOUT SPRING BATCH

We will answer two questions:

1. What's spring batch?
1. Why spring batch?

So:

### What's spring batch?

1. some stories about batch processing:
    http://docs.spring.io/spring-batch/trunk/reference/html/spring-batch-intro.html
1. architecture big overview
    http://docs.spring.io/spring-batch/trunk/reference/html/images/spring-batch-layers.png
1. jobs/steps/tasklets/chunks
    - http://image.slidesharecdn.com/con2818-javaee7batchprocessingintherealworld-141001120216-phpapp02/95/java-ee-7-batch-processing-in-the-real-world-11-638.jpg?cb=1412165041
    - http://cdn.infoq.com/statics_s1_20151224-0209/resource/news/2013/06/ee7-spring-batch/en/resources/jsr-352.jpg

### why spring batch?

Eight reasons to use spring batch:

1. modelling batch processes / batch patterns & language
1. validation
1. listeners for each part to add custom logic
1. parallelization
1. traceability
1. restart interrupted jobs
1. good transaction model
1. lots of already tuned components for reading/writing text files, csv, xml, database, jms, AMQP, web services, e-mail, etc...

## ENOUGH SPEAKING, START ACTION:

We will produce the classical _hello world_ in Spring Batch

You need to perform the following 5 tasks:

1. set up environment ([build.gradle][FILE-BUILD-GRADLE] + [gradle.properties][FILE-GRADLE-PROPERTIES])
1. configure infrastructure beans
1. build your beans: HelloWorlJob & Friends <-- they will have your program's logic
1. build the application code (who will launch your spring batch jobs?)
1. launch the application

So let's start

### Task 1. set up environment ([build.gradle][FILE-BUILD-GRADLE] + [gradle.properties][FILE-GRADLE-PROPERTIES])

We will set up a gradle project w/spring batch and import it to IntellijIDEA (or your favourite IDE). The two main files ([build.gradle][FILE-BUILD-GRADLE] and [gradle.properties][FILE-GRADLE-PROPERTIES]) have already all the necessary things, but it is worth for you if you try to write them from scratch.

So for setting up the gradle project from scratch you only need to follow the next 10 steps:

1. Create directory structure
1. In file [build.gradle][FILE-BUILD-GRADLE]:  download a special gradle plugin
1. In file [build.gradle][FILE-BUILD-GRADLE]:  apply gradle plugins
1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure jar packaging options
1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure repositories
1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure java version
1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure project dependencies
1. In file [gradle.properties][FILE-GRADLE-PROPERTIES]:  configure gradle daemon
1. verify if all dependencies are available
1. import to IDEA

Steps in detail:

1. Create directory structure

    If you are in a bash terminal, you can create all of them with the following command:

    ```sh
    shell$ mkdir -pv src/{main,test}/{java,resources}
    ```

    The following directories must exist:

    ```
    src/main/java
    src/main/resources
    src/test/java
    src/test/resources
    ```

1. In file [build.gradle][FILE-BUILD-GRADLE]:  download a special gradle plugin
    -> download a special gradle plugin

    ```groovy
    buildscript {
        repositories {
            mavenCentral()
            mavenLocal()
        }
        dependencies {
            classpath("org.springframework.boot:spring-boot-gradle-plugin:2.0.3.RELEASE")
        }
    }
    ```

    <strong>Rationale:</strong> spring boot helps to build spring applications quickly, the gradle plugin includes some helpful tasks, see: [Spring Boot Gradle Plugin][SPRING-BOOT-GRADLE-PLUGIN]

1. In file [build.gradle][FILE-BUILD-GRADLE]:  apply gradle plugins

    ```groovy
    apply plugin: 'java' // for use java
    apply plugin: 'org.springframework.boot' // special gradle tasks for spring-boot
    ```

    <strong>Rationale:</strong> See [About Gradle Plugins](https://docs.gradle.org/current/userguide/plugins.html), [Java Gradle Plugin][GRADLE-JAVA-PLUGIN] and [Spring Boot Gradle Plugin][SPRING-BOOT-GRADLE-PLUGIN]

1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure jar packaging options

    ```groovy
    jar {
        baseName = 'my-spring-batch-app'
        version = '0.1.0'
    }
    ```

    <strong>Rationale:</strong> this is for making a single big fat jar: [more info](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html)

1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure repositories

    ```groovy
    repositories {
        mavenCentral()
        mavenLocal()
    }
    ```

    <strong>Rationale:</strong> This tells gradle where to look for [dependencies][GRADLE-DEPENDENCIES]

1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure java version

    ```groovy
    sourceCompatibility = 1.8
    targetCompatibility = 1.8
    ```

    <strong>Rationale:</strong> This is for use java8 (see: [Gradle Java Plugin][GRADLE-JAVA-PLUGIN])

1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure project dependencies

    ```groovy
    dependencies {
        // this dependency brings all we need: spring batch + in-memory database + tests
        compile("org.springframework.boot:spring-boot-starter-batch")
        compile("org.hsqldb:hsqldb") // if you miss this: spring batch will complain for missing data source
        testCompile("junit:junit")
    }
    ```

    <strong>Rationale:</strong> See: [Gradle Dependencies][GRADLE-DEPENDENCIES]

1. In file [gradle.properties][FILE-GRADLE-PROPERTIES]:  configure gradle daemon

    ```groovy
    org.gradle.daemon=true
    org.gradle.workers.max=4 
    ```

    <strong>Rationale:</strong> [To use gradle daemon and make builds faster](https://docs.gradle.org/current/userguide/gradle_daemon.html)

1. verify if all dependencies are available

    execute gradle tasks / gradle dependencies to see if everything is ok
    see output example [here][FILE-TASKS] and [here][FILE-DEPENDENCIES].

1. import to IDEA

    See [this link](https://www.jetbrains.com/idea/help/importing-project-from-gradle-model.html)

### Task 2. configure infrastructure <-- job repo, etc.

Spring Batch projects are based on several beans as explained above, those beans are automatically configured based on annotations in a given class, so we need three things:

1. a class (hello.BatchConfiguration)
1. annotated with @Configuration
1. annotated with @EnableBatchProcessing

Details:

1. create class hello.BatchConfiguration 

    ```java
    package hello;

    public class BatchConfiguration {
    }
    ```

    <strong>Rationale:</strong> we can use any class for enable spring batch infrastructure, for our example, this class hill have spring batch configuration

1. annotate with @Configuration 

    ```java
    package hello;

    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class BatchConfiguration {
    }
    ```

    <strong>Rationale:</strong> classes annotated as [@Configuration](http://docs.spring.io/autorepo/docs/spring/4.1.1.RELEASE/javadoc-api/org/springframework/context/annotation/Configuration.html) are considered bean factories by spring.

1. annotate with @EnableBatchProcessing 

    ```java
    package hello;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

    @Configuration
    @EnableBatchProcessing
    public class BatchConfiguration {
    }
    ```

    <strong>Rationale:</strong> to make [spring batch infrastructure beans available][ENABLE-BATCH-PROCESSING]

Now we have our spring batch infrastructure complete, it's time to build our job with our custom logic

### Task 3. build your beans: HelloWorlJob & Friends <-- they will have your program's logic

We will build three beans from the smallest level of execution to the birdview one:

1. @Bean: helloTasklet contains the core of our custom logic
1. @Bean: helloStep is the step that wraps our logic
1. @Bean: helloWorldJob is the job that executes the step with our logic

Details:

1. @Bean: helloTasklet contains the core of our custom logic

    ```java
    // ... package declaration & imports ...
    @Configuration
    @EnableBatchProcessing
    public class BatchConfiguration {
        @Bean
        public Tasklet helloTasklet() {
            return (contribution, chunkContext) -> {
                System.out.println("=================================================");
                System.out.println("HELLO WORLD");
                System.out.println("=================================================");
                return RepeatStatus.FINISHED;
            };
        }
    }
    ```

    <strong>Rationale:</strong> Batch steps come in two flavours: [tasklet][TASKLET-JAVADOC] oriented and [chunk](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/step/item/ChunkOrientedTasklet.html) oriented (a tasklet that uses an [ItemReader](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/item/ItemReader.html), an [ItemProcessor](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/item/ItemProcessor.html) and an [ItemWriter](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/item/ItemWriter.html) to process chunks of data in a single transaction, we will see more in next exercises, for the impatients: see [Configuring Step](https://docs.spring.io/spring-batch/reference/html/configureStep.html))

    For this example we used tasklet oriented step, see the following four links for further reference:

    1. [Tasklet][TASKLET-JAVADOC]
    1. [StepContribution](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/StepContribution.html)
    1. [ChunkContext](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/scope/context/ChunkContext.html)
    1. [RepeatStatus](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/repeat/RepeatStatus.html)

1. @Bean: helloStep is the step that wraps our logic

    ```java
    // ... package declaration & imports ...
    @Configuration
    @EnableBatchProcessing
    public class BatchConfiguration {
        // helloTasklet() {...}

        @Bean
        public Step helloStep(StepBuilderFactory steps, Tasklet helloTasklet) {
            return steps.get("helloStep") //
                    .tasklet(helloTasklet) //
                    .build();
        }
    }
    ```

    <strong>Rationale:</strong> We are creating a step with name "helloStep", it will execute the helloTasklet. We use an [already-configured][ENABLE-BATCH-PROCESSING] bean: [StepBuilderFactory](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/configuration/annotation/StepBuilderFactory.html)  to create the step #theEasyWay.

1. @Bean: helloWorldJob is the job that executes the step with our logic

    ```java
    // ... package declaration & imports ...
    @Configuration
    @EnableBatchProcessing
    public class BatchConfiguration {
        // helloTasklet() {...}
        // helloStep() {...}

        @Bean
        public Job helloWorldJob(JobBuilderFactory jobs, Step helloStep) {
            return jobs.get("helloWorldJob") //
                    .incrementer(new RunIdIncrementer()) //
                    .start(helloStep) //
                    .build();
        }
    }
    ```

    <strong>Rationale:</strong> We are creating a job (named "helloWorldJob") with a single step (helloStep), we are using an [already-configured][ENABLE-BATCH-PROCESSING] bean: [JobBuilderFactory](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/configuration/annotation/JobBuilderFactory.html) to create the job #theEasyWay.

    Two important details to consider, we will practice them in future activities:

    1. We are setting a [RunIdIncrementer](https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/launch/support/RunIdIncrementer.html), more info: [Configuring and Running a Job](https://docs.spring.io/spring-batch/reference/html/configureJob.html)
    1. A job is built as a sequence of steps, hence the method _start_ which means: _this job starts whith step 'helloStep'_

### Task 4. build the application code (who will launch your spring batch jobs?)

There are many ways to start a batch job, we will use a very simple one taking advantage of spring boot, we need to follow the next four steps:

1. Create a main class (hello.Application)
1. annotate with @SpringBootApplication
1. add main method
1. call SpringApplication.run(...) inside `main()` method

Details:

1. Create a main class (hello.Application)

    ```java
    package hello;

    public class Application {
    }
    ```

    <strong>Rationale:</strong> Any class can be used, se are using hello.Application
1. annotate with @SpringBootApplication 

    ```java
    package hello;

    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication
    public class Application {
    }
    ```

    <strong>Rationale:</strong> We are using [@SpringBootApplication](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-using-springbootapplication-annotation.html) to load spring context easyly.

1. add main method

    ```java
    package hello;

    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication
    public class Application {
        public static void main(String... args) {
        }
    }
    ```

    <strong>Rationale:</strong> We will launch the program as standalone application.

1. call SpringApplication.run(...) inside `main()` method

    ```java
    package hello;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication
    public class Application {
        public static void main(String... args) {
            SpringApplication.run(Application.class, args);
        }
    }
    ```

    <strong>Rationale:</strong> This will load the spring context and execute job. See: [SpringApplication](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-spring-application.html)

### Task 5. launch the application

We will perform three actions to see the application run:

1. make the big fat executable jar file: `shell$ gradle build`
1. look for jar `build/libs/my-spring-batch-app-0.1.0.jar`
1. execute `shell$ java -jar build/libs/my-spring-batch-app-0.1.0.jar`

Details:

1. make the big fat executable jar file: `shell$ gradle build`

    Look for any error, ask the instructor, the output should be like this: [build.txt][FILE-BUILD]

1. look for jar `build/libs/my-spring-batch-app-0.1.0.jar`
    
    Issue: `shell$ ls -l build/libs/`

    You should see something like the following:

    ```
    shell$ ls build/libs/my-spring-batch-app-0.1.0.jar -l
    -rw-r--r-- 1 user staff 10153647 Feb 12 02:23 build/libs/my-spring-batch-app-0.1.0.jar
    -rw-r--r-- 1 user staff     2809 Feb 12 02:29 my-spring-batch-app-0.1.0.jar.original
    ```

1. execute `shell$ java -jar build/libs/my-spring-batch-app-0.1.0.jar`
    
    You should see an output like the following: [batch-output.txt][FILE-BATCH-OUTPUT]

    Take your time to understand the log, look where is your code starting, where is the step/job starting/finishing, what other information do you have at hand (time, status, etc).

CONGRATULATIONS!!! now play around


<!-- global links -->
[FILE-BUILD-GRADLE]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/build.gradle
[FILE-GRADLE-PROPERTIES]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/gradle.properties
[FILE-TASKS]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/tasks.txt
[FILE-DEPENDENCIES]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/dependencies.txt
[SPRING-BOOT-GRADLE-PLUGIN]: https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-gradle-plugin.html
[GRADLE-DEPENDENCIES]: https://docs.gradle.org/current/userguide/artifact_dependencies_tutorial.html
[GRADLE-JAVA-PLUGIN]: https://docs.gradle.org/current/javadoc/org/gradle/api/plugins/JavaPlugin.html
[TASKLET-JAVADOC]: https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/step/tasklet/Tasklet.html
[ENABLE-BATCH-PROCESSING]: https://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/configuration/annotation/EnableBatchProcessing.html
[FILE-BUILD]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/build.txt
[FILE-BATCH-OUTPUT]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/batch-output.txt
