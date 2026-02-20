# OrqueIO BPM Assert — JUnit 5 Example

This example demonstrates how to write unit tests for OrqueIO processes using **JUnit 5** (`junit-jupiter`) combined with **OrqueIO BPM Assert** (`orqueio-bpm-assert`).

It shows two different ways to register the `ProcessEngineExtension` in a JUnit 5 test class.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Project structure

```
orqueio-bpm-junit-assert/
├── pom.xml
└── src/
    └── test/
        ├── java/.../unittest/
        │   ├── ProcessEngineExtensionExtendWithTest.java      # Extension via @ExtendWith
        │   └── ProcessEngineExtensionRegisterExtensionTest.java # Extension via @RegisterExtension
        └── resources/
            ├── testProcess.bpmn     # Simple process: Start → UserTask → End
            ├── orqueio.cfg.xml      # In-memory H2 engine configuration
            └── logback-test.xml     # Logging configuration
```

---

## The process under test

A minimal BPMN process with a single user task:

```
[Start] → [UserTask: Handle Request] → [End]
```

---

## How it works

### JUnit 4 vs JUnit 5 integration

| | JUnit 4 (`testing/assert`) | JUnit 5 (this project) |
|-|---------------------------|------------------------|
| Engine bootstrap | `@Rule ProcessEngineRule` | `ProcessEngineExtension` |
| Lifecycle hooks | `@Before` / `@After` | `@BeforeEach` / `@AfterEach` |
| Registration | `@Rule` field | `@ExtendWith` or `@RegisterExtension` |
| Test annotation | `@Test` (JUnit 4) | `@Test` (JUnit Jupiter) |

---

### Approach 1 — `@ExtendWith` (class-level annotation)

The extension is declared at the class level. Services are accessed via the static methods of `BpmnAwareTests`.

```java
@ExtendWith(ProcessEngineExtension.class)
public class ProcessEngineExtensionExtendWithTest {

  @Test
  @Deployment(resources = {"testProcess.bpmn"})
  public void shouldExecuteProcess() {
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess");

    assertThat(processInstance).isActive();
    assertThat(processInstanceQuery().count()).isEqualTo(1);
    assertThat(task(processInstance)).isNotNull();

    complete(task(processInstance));

    assertThat(processInstance).isEnded();
  }
}
```

- Engine is configured from `orqueio.cfg.xml`
- `runtimeService()`, `task()`, `complete()` are static imports from `BpmnAwareTests`
- `@Deployment` deploys the BPMN for the duration of the test, then undeploys it

---

### Approach 2 — `@RegisterExtension` (instance field)

The extension is declared as an instance field. Services are accessed via `extension.getXxxService()`.

```java
public class ProcessEngineExtensionRegisterExtensionTest {

  @RegisterExtension
  ProcessEngineExtension extension = ProcessEngineExtension.builder().build();

  @Test
  @Deployment(resources = {"testProcess.bpmn"})
  public void shouldExecuteProcess() {
    ProcessInstance processInstance = extension.getRuntimeService()
        .startProcessInstanceByKey("testProcess");

    assertThat(processInstance).isActive();
    complete(task(processInstance));
    assertThat(processInstance).isEnded();
  }
}
```

- Engine can be configured programmatically via `ProcessEngineExtension.builder()`
- Useful when you need direct access to the extension object or programmatic configuration

---

### Choosing between the two approaches

| | `@ExtendWith` | `@RegisterExtension` |
|-|---------------|----------------------|
| Configuration source | `orqueio.cfg.xml` | `orqueio.cfg.xml` or builder |
| Service access | Static methods (`BpmnAwareTests.*`) | Instance methods (`extension.getXxxService()`) |
| Typical use case | Standard tests | Tests needing programmatic engine setup |

---

## Running the example

### Known requirement — Java 21

Maven must use JDK 21. If your default `JAVA_HOME` points to an older JDK, set it explicitly:

**Linux / Git Bash:**
```bash
JAVA_HOME="/path/to/jdk-21" mvn clean test
```

**PowerShell:**
```powershell
$env:JAVA_HOME = 'C:\Path\To\jdk-21'
mvn clean test
```

### Run the tests

```bash
mvn clean test
```

Expected output:
```
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
```

---

## Test scenarios

| Test class | Approach | What it verifies |
|------------|----------|-----------------|
| `ProcessEngineExtensionExtendWithTest` | `@ExtendWith` | Process starts active, single instance, task exists, completes, ends |
| `ProcessEngineExtensionRegisterExtensionTest` | `@RegisterExtension` | Same flow using `extension.getRuntimeService()` |

---

## Source files

| File | Description |
|------|-------------|
| [ProcessEngineExtensionExtendWithTest.java](src/test/java/io/orqueio/bpm/unittest/ProcessEngineExtensionExtendWithTest.java) | Test using `@ExtendWith(ProcessEngineExtension.class)` |
| [ProcessEngineExtensionRegisterExtensionTest.java](src/test/java/io/orqueio/bpm/unittest/ProcessEngineExtensionRegisterExtensionTest.java) | Test using `@RegisterExtension` |
| [testProcess.bpmn](src/test/resources/testProcess.bpmn) | Minimal BPMN process: Start → UserTask → End |
| [orqueio.cfg.xml](src/test/resources/orqueio.cfg.xml) | In-memory H2 engine configuration |
