# Spring Boot with OrqueIO REST API — Embedded Engine Example

This example demonstrates how to build a **Spring Boot application with embedded OrqueIO Engine accessible via REST API** — perfect for headless process automation, microservices, or custom UIs.

---

## What You Get

- ✅ **Embedded OrqueIO Engine** — H2 in-memory database
- ✅ **REST API** — Full OrqueIO REST API at `/engine-rest/*`
- ✅ **Process Application** — Auto-deployment of BPMN processes from classpath
- ✅ **Spring Security** — Basic authentication (configurable in `application.yaml`)
- ✅ **Integration Tests** — Demonstrates REST API testing with `@SpringBootTest`

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Project Structure

```
example-web/
├── pom.xml                               # Spring Boot + OrqueIO REST dependency
└── src/
    ├── main/
    │   ├── java/.../web/
    │   │   └── RestApplication.java      # @SpringBootApplication + @EnableProcessApplication
    │   └── resources/
    │       ├── META-INF/
    │       │   └── processes.xml         # Process application descriptor
    │       ├── bpmn/
    │       │   └── sample.bpmn           # Sample BPMN process
    │       └── application.yaml          # Security config (demo/demo)
    └── test/
        └── java/.../web/
            ├── OrqueioBpmRestTest.java   # REST API integration test
            └── ... (2 more tests)
```

---

## How It Works

### 1. Add the OrqueIO Spring Boot Starter REST dependency

The `orqueio-bpm-spring-boot-starter-rest` provides the embedded engine + full REST API:

```xml
<dependency>
  <groupId>io.orqueio.bpm.springboot</groupId>
  <artifactId>orqueio-bpm-spring-boot-starter-rest</artifactId>
  <version>1.0.4</version>
</dependency>
```

### 2. Enable the Process Application

Annotate your main class with `@EnableProcessApplication`:

```java
@SpringBootApplication
@EnableProcessApplication
public class RestApplication {

  public static void main(final String... args) throws Exception {
    SpringApplication.run(RestApplication.class, args);
  }
}
```

### 3. Add a `processes.xml` descriptor

Place `META-INF/processes.xml` in `src/main/resources`:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<process-application
    xmlns="http://www.camunda.org/schema/1.0/ProcessApplication"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

  <process-archive name="restApplication">
    <process-engine>default</process-engine>
    <properties>
      <property name="isDeleteUponUndeploy">false</property>
      <property name="isScanForProcessDefinitions">true</property>
    </properties>
  </process-archive>

</process-application>
```

### 4. Configure Spring Security Basic Auth

In `application.yaml`:

```yaml
spring.security.user:
  name: demo
  password: demo
```

> **Note**: This is Spring Boot level authentication, NOT OrqueIO Engine authentication. For production, configure [OrqueIO REST API authentication](https://docs.orqueio.io/manual/7.23/reference/rest/overview/authentication/).

### 5. Add BPMN processes

All `.bpmn`, `.dmn`, and `.cmmn` files in `src/main/resources` are auto-deployed at startup.

---

## Running the Application

### Build and run

```bash
mvn clean install
java -jar target/orqueio-bpm-spring-boot-starter-example-web-0.0.1-SNAPSHOT.jar
```

Or use Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

### Access the REST API

The OrqueIO REST API is available at:

**http://localhost:8080/engine-rest/**

Login with:
- **Username**: `demo`
- **Password**: `demo`

---

## REST API Examples

### Get engine info

```bash
curl -u demo:demo http://localhost:8080/engine-rest/engine
```

Response:
```json
[
  {
    "name": "default"
  }
]
```

### Get deployed process definitions

```bash
curl -u demo:demo http://localhost:8080/engine-rest/engine/default/process-definition
```

Response:
```json
[
  {
    "id": "Sample:1:...",
    "key": "Sample",
    "name": "Sample Process",
    "version": 1,
    ...
  }
]
```

### Start a process instance

```bash
curl -u demo:demo \
  -H "Content-Type: application/json" \
  -X POST \
  -d '{"variables": {"myVar": {"value":"test","type":"String"}}}' \
  http://localhost:8080/engine-rest/engine/default/process-definition/key/Sample/start
```

---

## Testing

The project includes 3 integration tests using `@SpringBootTest` and `RestTemplate`:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrqueioBpmRestTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  public void testEngineRest() {
    ResponseEntity<String> response = restTemplate
        .withBasicAuth("demo", "demo")
        .getForEntity("/engine-rest/engine", String.class);
    
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
```

Run tests:

```bash
mvn clean test
```

Expected output:
```
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
```

---

## Available REST Endpoints

| Endpoint | Description |
|----------|-------------|
| `/engine-rest/engine` | List process engines |
| `/engine-rest/process-definition` | List process definitions |
| `/engine-rest/process-instance` | List/start process instances |
| `/engine-rest/task` | List/complete user tasks |
| `/engine-rest/deployment` | Manage deployments |
| `/engine-rest/history/*` | Query history |

Full REST API documentation: https://docs.orqueio.io/manual/7.23/reference/rest/

---

## Use Cases

- **Microservices** — Embed process automation in Spring Boot services
- **Custom UIs** — Build React/Angular/Vue frontends with REST API backend
- **Headless Automation** — Process orchestration without webapps
- **External Task Workers** — Coordinate distributed workers via REST API
- **Process-as-a-Service** — Expose BPMN workflows as RESTful services

---

## Next Steps

- Add your own BPMN processes to `src/main/resources/bpmn/`
- Configure [OrqueIO Engine REST authentication](https://docs.orqueio.io/manual/7.23/reference/rest/overview/authentication/)
- Switch to PostgreSQL/MySQL by adding datasource config
- Build a custom UI consuming the REST API

---

## Source Files

| File | Description |
|------|-------------|
| [RestApplication.java](src/main/java/io/orqueio/bpm/spring/boot/example/web/RestApplication.java) | Main Spring Boot application with `@EnableProcessApplication` |
| [processes.xml](src/main/resources/META-INF/processes.xml) | Process application descriptor |
| [sample.bpmn](src/main/resources/bpmn/sample.bpmn) | Sample BPMN process |
| [application.yaml](src/main/resources/application.yaml) | Spring Security basic auth config |
| [OrqueioBpmRestTest.java](src/test/java/io/orqueio/bpm/spring/boot/example/web/OrqueioBpmRestTest.java) | REST API integration test |
