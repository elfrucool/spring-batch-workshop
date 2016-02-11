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
    - http://cdn.infoq.com/statics\_s1\_20151224-0209/resource/news/2013/06/ee7-spring-batch/en/resources/jsr-352.jpg

### why spring batch?

* modelling batch processes / batch patterns & language
* validation
* listeners for each part to add custom logic
* parallelization
* traceability
* restart interrupted jobs
* good transaction model
* lots of already tuned components for reading/writing text files, csv, xml, database, jms, AMQP, web services, e-mail, etc...

## ENOUGH SPEAKING, START ACTION:

We will produce the classical _hello world_ in Spring Batch

You need to perform the following 5 tasks:

1. set up dependencies ([build.gradle][FILE-BUILD-GRADLE] + [gradle.properties][FILE-GRADLE-PROPERTIES])
1. configure infrastructure <-- job repo, etc.
1. build your beans: HelloWorlJob & Friends
1. build the application
1. launch the application

### Let's start

#### Task 1. set up dependencies ([build.gradle][FILE-BUILD-GRADLE] + [gradle.properties][FILE-GRADLE-PROPERTIES])

You only need to follow the next 9 steps:

1. In file [build.gradle][FILE-BUILD-GRADLE]:  download a special gradle plugin
1. In file [build.gradle][FILE-BUILD-GRADLE]:  apply gradle plugins
1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure jar packaging options
1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure repositories
1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure java version
1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure project dependencies
1. In file [gradle.properties][FILE-GRADLE-PROPERTIES]:  configure gradle daemon
1. verify if all dependencies are available
1. import to IDEA

Detail:

1. In file [build.gradle][FILE-BUILD-GRADLE]:  download a special gradle plugin
    -> download a special gradle plugin

    ```groovy
    buildscript {
        repositories {
            mavenCentral()
            mavenLocal()
        }
        dependencies {
            classpath("org.springframework.boot:spring-boot-gradle-plugin:1.3.1.RELEASE")
        }
    }
    ```

    <strong>Rationale:</strong> spring boot helps to build spring applications quickly, the gradle plugin includes some helpful tasks, see: [Spring Boot Gradle Plugin][SPRING-BOOT-GRADLE-PLUGIN]

1. In file [build.gradle][FILE-BUILD-GRADLE]:  apply gradle plugins

    ```groovy
    apply plugin: 'java' // for use java
    apply plugin: 'spring-boot' // special gradle tasks for spring-boot
    ```

    <strong>Rationale:</strong> See [About Gradle Plugins](https://docs.gradle.org/current/userguide/plugins.html), [Java Gradle Plugin][GRADLE-JAVA-PLUGIN] and [Spring Boot Gradle Plugin][SPRING-BOOT-GRADLE-PLUGIN]

1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure jar packaging options

    ```groovy
    jar {
        baseName = 'my-spring-batch-app'
        version = '0.1.0'
    }
    ```

    this is for making a single big fat jar: [more info](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.bundling.Jar.html)

1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure repositories

    ```groovy
    repositories {
        mavenCentral()
        mavenLocal()
    }
    ```

    This tells gradle where to look for [dependencies][GRADLE-DEPENDENCIES]

1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure java version

    ```groovy
    sourceCompatibility = 1.8
    targetCompatibility = 1.8
    ```

    This is for use java8 (see: [Gradle Java Plugin][GRADLE-JAVA-PLUGIN])

1. In file [build.gradle][FILE-BUILD-GRADLE]:  configure project dependencies

    ```groovy
    dependencies {
        // this dependency brings all we need: spring batch + in-memory database + tests
        compile("org.springframework.boot:spring-boot-starter-batch")
        testCompile("junit:junit")
    }
    ```

    See: [Gradle Dependencies][GRADLE-DEPENDENCIES]

1. In file [gradle.properties][FILE-GRADLE-PROPERTIES]:  configure gradle daemon

    ```groovy
    org.gradle.daemon=true
    org.gradle.workers.max=4 
    ```

    [To use gradle daemon and make builds faster](https://docs.gradle.org/current/userguide/gradle_daemon.html)

1. verify if all dependencies are available

    execute gradle tasks / gradle dependencies to see if everything is ok
    see output example [here][FILE-TASKS] and [here][FILE-DEPENDENCIES].

1. import to IDEA

    -> guess what ...

#### Task 2. configure infrastructure <-- job repo, etc.

1. create class hello.BatchConfiguration 
    -> this hill have spring batch configuration
1. annotate with @Configuration 
    -> to convert it to spring bean factory
1. annotate with @EnableBatchProcessing 
    -> to make spring batch infrastructure beans available

#### Task 3. build your beans: HelloWorlJob & Friends

1. @Bean: helloWorldTasklet (...) 
    -> our logic
1. @Bean: helloStep (...) 
    -> step with our logic
1. @Bean: helloWorldJob (...) 
    -> job that we will launch

#### Task 4. build the application

1. hello.Application 
    -> our main class
1. annotate with @SpringBootApplicatin 
    -> to make it able to run spring batch projects
1. add main method 
    -> to be executed
1. call SpringApplication.run(...) 
    -> to load spring context and execute job

#### Task 5. launch the application

1. gradle build
    -> make the executable jar
1. look for jar build/libs/my-spring-batch-app-0.1.0.jar
    -> to see if it was built
1. execute java -jar build/libs/my-spring-batch-app-0.1.0.jar
    -> guess what...

CONGRATULATIONS!!! now play around


<!-- global links -->
[FILE-BUILD-GRADLE]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/build.gradle
[FILE-GRADLE-PROPERTIES]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/gradle.properties
[FILE-TASKS]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/tasks.txt
[FILE-DEPENDENCIES]: https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/dependencies.txt
[SPRING-BOOT-GRADLE-PLUGIN]: https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-gradle-plugin.html
[GRADLE-DEPENDENCIES]: https://docs.gradle.org/current/userguide/artifact_dependencies_tutorial.html
[GRADLE-JAVA-PLUGIN]: https://docs.gradle.org/current/javadoc/org/gradle/api/plugins/JavaPlugin.html
