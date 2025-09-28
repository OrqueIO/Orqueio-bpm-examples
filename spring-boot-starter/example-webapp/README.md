# Spring Boot Web application with Orqueio Webapps

This example demonstrates how you can build Spring Boot Web application having following configured:
* Embedded Orqueio engine
* Orqueio web applications automatically deployed
* Process application and one BPMN process deployed
* Admin user configured with login and password configured in `application.yaml`

It also contains a simple integration test, showing how this can be tested.

## Prerequisites
* Java 17/21

## How is it done

1. To embed Orqueio Engine with webapps you must add following dependency to your `pom.xml`:

```xml
...
<dependency>
  <groupId>io.orqueio.bpm.springboot</groupId>
  <artifactId>orqueio-bpm-spring-boot-starter-webapp</artifactId>
  <version>0.25.0-SNAPSHOT</version>
</dependency>
...
```

2. With Spring Boot you usually create an "application" class annotated with `@SpringBootApplication`. In order to have a Orqueio process application
registered, you can simply add an annotation `@EnableProcessApplication` to the same class and also include a `processes.xml` file in your `META-INF` folder:

```java
@SpringBootApplication
@EnableProcessApplication
public class WebappExampleApplication {

  public static void main(String... args) {
    SpringApplication.run(WebappExampleApplication.class, args);
  }
}
```

3. You can also put BPMN, CMMN and DMN files in your classpath, they will be automatically deployed and registered within a process application.


## Run the application and use Orqueio Webapps

You can build the application with `mvn clean install` and then run it with `java -jar` command.

Then you can access Orqueio Webapps in browser: `http://localhost:8080` (provide login/password from `application.yaml`, default: demo/demo)

