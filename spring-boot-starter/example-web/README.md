# Spring Boot Web application with embedded Orqueio engine

This example demonstrates how you can build a Spring Boot Web application with the following configured:
* Embedded Orqueio engine accessible via REST
* Process application and one BPMN process deployed
* Spring Boot Security basic authentication

It also contains a couple of integration tests, showing how this can be tested.

## Prerequisites
* Java 17/21

## How is it done

1. To embed the Orqueio Engine accessible via REST API you must add following dependency to your `pom.xml`:

```xml
...
<dependency>
  <groupId>io.orqueio.bpm.springboot</groupId>
  <artifactId>orqueio-bpm-spring-boot-starter-rest</artifactId>
  <version>1.0.4</version>
</dependency>
...
```

2. With Spring Boot you usually create an "application" class annotated with `@SpringBootApplication`. In order to have the Orqueio process application
registered, you can simply add an annotation `@EnableProcessApplication` to the same class and also include `processes.xml` file to your `META-INF` folder:

```java
@SpringBootApplication
@EnableProcessApplication
public class RestApplication {

  public static void main(final String... args) throws Exception {
    SpringApplication.run(RestApplication.class, args);
  }

}
```

3. You can also put BPMN, CMMN and DMN files in your classpath, they will be automatically deployed and registered within a process application.

>Note: Basic authentication is configured on Spring Boot level, this is NOT a Orqueio Engine authentication. Check [this docs](https://docs.orqueio.io/manual/7.23/reference/rest/overview/authentication/) 
to configure Basic Authentication for Orqueio Engine REST API.
 
## Run the application and call the REST API

You can then build the application with `mvn clean install` and then run it with `java -jar` command.

Then you can access the REST API in the browser through: `http://localhost:8080/engine-rest/engine` (provide login/password from `application.yaml`, default: demo/demo).

Another endpoint to test: `http://localhost:8080/engine-rest/engine/default/process-definition` - this will show you the deployed process definition.
