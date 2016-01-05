# spring-batch-workshop
Repositorio de recursos para workshop de spring batch en nearsoft, primer cuarto de 2016
 
SPRING BATCH WORKSHOP
=====================

1. PRACTICE 1: HELLO WORLD
    1.0. what's spring batch
        1.0.1. some stories about batch processing:
            http://docs.spring.io/spring-batch/trunk/reference/html/spring-batch-intro.html
        1.0.2. architecture big overview
            http://docs.spring.io/spring-batch/trunk/reference/html/images/spring-batch-layers.png
        1.0.3. jobs/steps/tasklets/chunks
            - http://image.slidesharecdn.com/con2818-javaee7batchprocessingintherealworld-141001120216-phpapp02/95/java-ee-7-batch-processing-in-the-real-world-11-638.jpg?cb=1412165041
            - http://cdn.infoq.com/statics_s1_20151224-0209/resource/news/2013/06/ee7-spring-batch/en/resources/jsr-352.jpg
        1.0.4. why spring batch?
            * modelling batch processes / batch patterns & language
            * validation
            * listeners for each part to add custom logic
            * parallelization
            * traceability
            * restart interrupted jobs
            * good transaction model
            * lots of already tuned components for reading/writing text files, csv, xml, database, jms, AMQP, web services, e-mail, etc...

    1.1. dependencies (build.gradle + gradle.properties)
        1.1.1. at the top: buildscript {...}
            -> download a special gradle plugin
        1.1.2. apply plugin: 'java' 
            -> for use java
        1.1.3. apply plugin: 'spring-boot' 
            -> special gradle tasks for spring-boot
        1.1.4. jar { ... baseName ... version } 
            -> single big fat jar
        1.1.5. repositories { ... } 
            -> tell gradle where to look for dependencies
        1.1.6. source/target compatibility: 1.8 
            -> use java8
        1.1.7. dependencies { ... } 
            -> spring batch + in-memory database + tests
        1.1.8. gradle.properties ... 
            -> to use gradle daemon
        1.1.9. execute gradle tasks / gradle dependencies 
            -> to see if everything is ok
        1.1.10. import to IDEA
            -> guess what ...

    1.2. infrastructure <-- job repo, etc.
        1.2.1. create class hello.BatchConfiguration 
            -> this hill have spring batch configuration
        1.2.2. annotate with @Configuration 
            -> to convert it to spring bean factory
        1.2.3. annotate with @EnableBatchProcessing 
            -> to make spring batch infrastructure beans available

    1.3. our beans: HelloWorlJob & Friends
        1.3.1. @Bean: helloWorldTasklet (...) 
            -> our logic
        1.3.2. @Bean: helloStep (...) 
            -> step with our logic
        1.3.3. @Bean: helloWorldJob (...) 
            -> job that we will launch

    1.4. application
        1.4.1. hello.Application 
            -> our main class
        1.4.2. annotate with @SpringBootApplicatin 
            -> to make it able to run spring batch projects
        1.4.3. add main method 
            -> to be executed
        1.4.4. call SpringApplication.run(...) 
            -> to load spring context and execute job

    1.5. launch application
        1.5.1. gradle build
            -> make the executable jar
        1.5.2. look for jar build/libs/my-spring-batch-app-0.1.0.jar
            -> to see if it was built
        1.5.3. execute java -jar build/libs/my-spring-batch-app-0.1.0.jar
            -> guess what...

    CONGRATULATIONS

2. PRACTICE 2: IMPORT ADDRESS LIST TO DATABASE
    ....
    x -> export ....

3. PRACTICE 3: INTEGRATE WITH MY EXISTING APPLICATION

WORKSHOP 2

4. PRACTICE 4: MORE CUSTOMIZATIONS
    4.1. validating using valang
    4.2. passing values using contexts

5. PRACTICE 5: PARALLELIZATION

6. PRACTICE 6: UNIT/INTEGRATION TESTING