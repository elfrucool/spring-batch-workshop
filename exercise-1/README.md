#PRACTICE 1: HELLO WORLD

We will go through a rough introduction and then will produce the classical _hello world_ in Spring Batch

## ABOUT SPRING BATCH

We will answer two questions:

1. What's spring batch?
1. Why spring batch?

So:

1. What's spring batch?
    1. some stories about batch processing:
        http://docs.spring.io/spring-batch/trunk/reference/html/spring-batch-intro.html
    1. architecture big overview
        http://docs.spring.io/spring-batch/trunk/reference/html/images/spring-batch-layers.png
    1. jobs/steps/tasklets/chunks
        - http://image.slidesharecdn.com/con2818-javaee7batchprocessingintherealworld-141001120216-phpapp02/95/java-ee-7-batch-processing-in-the-real-world-11-638.jpg?cb=1412165041
        - http://cdn.infoq.com/statics_s1_20151224-0209/resource/news/2013/06/ee7-spring-batch/en/resources/jsr-352.jpg
1. why spring batch?
    * modelling batch processes / batch patterns & language
    * validation
    * listeners for each part to add custom logic
    * parallelization
    * traceability
    * restart interrupted jobs
    * good transaction model
    * lots of already tuned components for reading/writing text files, csv, xml, database, jms, AMQP, web services, e-mail, etc...

## ENOUGH SPEAKING, START ACTION:

We will produce the _must have_ practice when learning a new technology: _hello world_ in Spring Batch

You need to perform the following 5 tasks:

1. set up dependencies (build.gradle + gradle.properties)
1. configure infrastructure <-- job repo, etc.
1. build your beans: HelloWorlJob & Friends
1. build the application
1. launch the application

### Let's start

1. set up dependencies ([build.gradle](https://github.com/rvazquezglez/spring-batch-workshop/blob/master/exercise-1/build.gradle) + [gradle.properties](gradle.properties))
    1. **build.gradle:** at the top: buildscript {...}
        -> download a special gradle plugin
    1. **build.gradle:** apply plugin: 'java' 
        -> for use java
    1. **build.gradle:** apply plugin: 'spring-boot' 
        -> special gradle tasks for spring-boot
    1. **build.gradle:** jar { ... baseName ... version } 
        -> single big fat jar
    1. **build.gradle:** repositories { ... } 
        -> tell gradle where to look for dependencies
    1. **build.gradle:** source/target compatibility: 1.8 
        -> use java8
    1. **build.gradle:** dependencies { ... } 
        -> spring batch + in-memory database + tests
    1. gradle.properties ... 
        -> to use gradle daemon
    1. execute gradle tasks / gradle dependencies 
        -> to see if everything is ok
    1. import to IDEA
        -> guess what ...

1. configure infrastructure <-- job repo, etc.
    1. create class hello.BatchConfiguration 
        -> this hill have spring batch configuration
    1. annotate with @Configuration 
        -> to convert it to spring bean factory
    1. annotate with @EnableBatchProcessing 
        -> to make spring batch infrastructure beans available

1. build your beans: HelloWorlJob & Friends
    1. @Bean: helloWorldTasklet (...) 
        -> our logic
    1. @Bean: helloStep (...) 
        -> step with our logic
    1. @Bean: helloWorldJob (...) 
        -> job that we will launch

1. build the application
    1. hello.Application 
        -> our main class
    1. annotate with @SpringBootApplicatin 
        -> to make it able to run spring batch projects
    1. add main method 
        -> to be executed
    1. call SpringApplication.run(...) 
        -> to load spring context and execute job

1. launch the application
    1. gradle build
        -> make the executable jar
    1. look for jar build/libs/my-spring-batch-app-0.1.0.jar
        -> to see if it was built
    1. execute java -jar build/libs/my-spring-batch-app-0.1.0.jar
        -> guess what...

CONGRATULATIONS!!! now play around

