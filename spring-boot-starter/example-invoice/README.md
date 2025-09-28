# The Orqueio Invoice example in a Spring Boot Web Application

The Invoice example is provided in all of the pre-packaged distros that Orqueio provides.
This Orqueio example provides the Invoice application inside a Spring Boot application together with all
the necessary adjustments needed to run it out of the box. This includes:

* The Orqueio EE Webapps
* The Orqueio Rest API

You will need:

* credentials to access the enterprise repo in your `settings.xml`
* a valid orqueio-license key file in your classpath in the file `orqueio-license.txt`

## Prerequisites
* Java 17/21

## How is it done

1. To embed the Orqueio Engine with the Enterprise webapps and Rest API you must add the following maven coordinates 
to your `pom.xml`:

```xml
...
  <properties>
    <orqueio.version>7.23.0-ee</orqueio.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      ...
      <dependency>
        <groupId>io.orqueio.bpm</groupId>
        <artifactId>orqueio-bom</artifactId>
        <version>${orqueio.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>io.orqueio.bpm.springboot</groupId>
      <artifactId>orqueio-bpm-spring-boot-starter-webapp</artifactId>
    </dependency>
    <dependency>
      <groupId>io.orqueio.bpm.springboot</groupId>
      <artifactId>orqueio-bpm-spring-boot-starter-rest</artifactId>
    </dependency>
    ...
  </dependencies>
...
```

2. For the example Invoice application, the following dependency needs to be added in your `pom.xml` file:

```xml
<dependency>
  <groupId>io.orqueio.bpm.example</groupId>
  <artifactId>orqueio-example-invoice-jakarta</artifactId>
  <version>${orqueio.version}</version>
  <classifier>classes</classifier>
  <scope>compile</scope>
</dependency>
```

3. An "application" class annotated with `@SpringBootApplication` needs to be created. In order to have the Invoice 
process application registered, add the annotation `@EnableProcessApplication` to the same class, as well as include 
an empty `processes.xml` file to your `META-INF` folder. To ensure that all of necessary BPMN and DMN files are deployed, 
add the following code in your "application" class:

```java
@SpringBootApplication
@EnableProcessApplication
public class Application {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }

  @Autowired
  protected ProcessEngine processEngine;

  protected InvoiceProcessApplication invoicePa = new InvoiceProcessApplication();

  @PostConstruct
  public void deployInvoice() {
    ClassLoader classLoader = invoicePa.getClass().getClassLoader();

    if(processEngine.getIdentityService().createUserQuery().list().isEmpty()) {
      processEngine.getRepositoryService()
          .createDeployment()
          .addInputStream("invoice.v1.bpmn", classLoader.getResourceAsStream("invoice.v1.bpmn"))
          .addInputStream("reviewInvoice.bpmn", classLoader.getResourceAsStream("reviewInvoice.bpmn"))
          .deploy();
    }
  }

  @EventListener
  public void onPostDeploy(PostDeployEvent event) {
    invoicePa.startFirstProcess(event.getProcessEngine());
  }

}
```

4. You can also put additional BPMN, CMMN and DMN files in your classpath, they will be automatically deployed and 
registered within the process application. Forms HTML needs to be added in the `/resources/static/forms` directory.

5. If you want your Camunda license automatically used on Engine startup, just put the file with the name 
`orqueio-license.txt` on your classpath. 

6. Adjust the `src/main/resources/application.yaml` file according to your preferences. The default setup will use an
 embedded H2 instance.

## Run the application and use Orqueio Platform

You can build the application with `mvn clean install` and then run it with the `java -jar` command.
You can also execute the application with `mvn spring-boot:run`.

Then you can access the Orqueio Webapps in your browser: `http://localhost:8080/` (provide login/password 
from `application.yaml`, default: demo/demo). The Rest API can be available through `http://localhost:8080/engine-rest`.
 
