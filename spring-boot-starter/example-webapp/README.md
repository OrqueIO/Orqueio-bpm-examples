# Spring Boot with OrqueIO Webapps — Complete Example

This example demonstrates how to build a **Spring Boot application with embedded OrqueIO webapps** (Cockpit, Tasklist, Admin) — providing a complete BPM solution with zero external dependencies.

---

## What You Get

- ✅ **Embedded OrqueIO Engine** — H2 in-memory database
- ✅ **OrqueIO Webapps** — Cockpit, Tasklist, Admin UI at `http://localhost:8080`
- ✅ **Process Application** — auto-deployment of BPMN processes from classpath
- ✅ **Pre-configured Admin User** — `demo/demo` (configurable in `application.yaml`)
- ✅ **Integration Test** — demonstrates how to test the embedded engine

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Version Compatibility

| OrqueIO Version | Spring Boot Version |
|-----------------|---------------------|
| **2.0.0+**      | **4.0.0+**          |

**Important**: OrqueIO 2.0.0+ requires Spring Boot 4.0.0 or higher.

---

## Project Structure

```
example-webapp/
├── pom.xml                                    # Spring Boot + OrqueIO webapps dependency
└── src/
    ├── main/
    │   ├── java/.../webapp/
    │   │   └── WebappExampleApplication.java  # @SpringBootApplication + @EnableProcessApplication
    │   └── resources/
    │       ├── META-INF/
    │       │   └── processes.xml              # Process application descriptor
    │       ├── bpmn/
    │       │   └── sample.bpmn                # Sample BPMN process
    │       └── application.yaml               # Admin user config (demo/demo)
    └── test/
        └── java/.../webapp/
            └── ApplicationTest.java           # Integration test with @SpringBootTest
```

---

## How It Works

### 1. Add the OrqueIO Spring Boot Starter Webapp dependency

The `orqueio-bpm-spring-boot-starter-webapp` includes the embedded engine + all three webapps (Cockpit, Tasklist, Admin):

```xml
<dependency>
  <groupId>io.orqueio.bpm.springboot</groupId>
  <artifactId>orqueio-bpm-spring-boot-starter-webapp</artifactId>
  <version>2.0.1</version>
</dependency>
```

### 2. Enable the Process Application

Annotate your main class with `@EnableProcessApplication` to auto-deploy BPMN/DMN/CMMN files from the classpath:

```java
@SpringBootApplication
@EnableProcessApplication
public class WebappExampleApplication {

  public static void main(String... args) {
    SpringApplication.run(WebappExampleApplication.class, args);
  }
}
```

### 3. Add a `processes.xml` descriptor

Place `META-INF/processes.xml` in `src/main/resources` to register the process application:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<process-application
    xmlns="http://www.camunda.org/schema/1.0/ProcessApplication"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <process-archive name="webappExampleApplication">
    <process-engine>default</process-engine>
    <properties>
      <property name="isDeleteUponUndeploy">false</property>
      <property name="isScanForProcessDefinitions">true</property>
    </properties>
  </process-archive>

</process-application>
```

### 4. Configure admin user (optional)

In `application.yaml`, customize the admin user credentials:

```yaml
camunda.bpm.admin-user:
  id: demo
  password: demo
  firstName: Demo
```

### 5. Add BPMN processes

All `.bpmn`, `.dmn`, and `.cmmn` files in `src/main/resources` are automatically deployed at startup.

---

## Running the Application

### Build and run

```bash
mvn clean install
java -jar target/orqueio-bpm-spring-boot-starter-example-webapp-0.0.1-SNAPSHOT.jar
```

Or use Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

### Access the Webapps

Open your browser at:

**http://localhost:8080**

Login with:
- **Username**: `demo`
- **Password**: `demo`

You will see:
- **Cockpit** — process monitoring and instance tracking
- **Tasklist** — user task management
- **Admin** — user and group administration

---

## Testing

The project includes an integration test using `@SpringBootTest`:

```java
@SpringBootTest(classes = WebappExampleApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

  @Autowired
  private RuntimeService runtimeService;

  @Test
  public void testDeployment() {
    // Verify the process engine is running
    assertThat(runtimeService).isNotNull();
  }
}
```

Run tests:

```bash
mvn clean test
```

Expected output:
```
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

---

## What's Included

| Component | Description |
|-----------|-------------|
| **Cockpit** | Process instance monitoring, deployment management |
| **Tasklist** | User task assignment and completion |
| **Admin** | User/group management, authorization |
| **Embedded Engine** | In-memory H2 database, auto-configured |
| **Process Application** | Auto-deploy BPMN/DMN/CMMN from classpath |

---

## Next Steps

- Add your own BPMN processes to `src/main/resources/bpmn/`
- Customize admin user in `application.yaml`
- Switch to PostgreSQL/MySQL by adding datasource config
- Deploy to production with external database

---

## Source Files

| File | Description |
|------|-------------|
| [WebappExampleApplication.java](src/main/java/io/orqueio/bpm/spring/boot/example/webapp/WebappExampleApplication.java) | Main Spring Boot application with `@EnableProcessApplication` |
| [processes.xml](src/main/resources/META-INF/processes.xml) | Process application descriptor |
| [sample.bpmn](src/main/resources/bpmn/sample.bpmn) | Sample BPMN process |
| [application.yaml](src/main/resources/application.yaml) | Admin user configuration |
| [ApplicationTest.java](src/test/java/io/orqueio/bpm/spring/boot/example/webapp/ApplicationTest.java) | Integration test |
