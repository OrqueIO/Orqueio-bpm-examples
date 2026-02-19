# Programmatic Process Engine — JUnit 5 Example

This example demonstrates how to create a **ProcessEngine programmatically** (without a configuration file) and inject it into the JUnit 5 `ProcessEngineExtension` via `useProcessEngine()`.

This is the most flexible way to configure the engine for testing — useful when sharing an engine instance, integrating with Spring, or when you need full control over the configuration in code.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Project structure

```
orqueio-bpm-junit-use-engine/
├── pom.xml
└── src/
    └── test/
        ├── java/.../unittest/
        │   └── UseProcessEngineTest.java    # Engine created in code, injected via useProcessEngine()
        └── resources/
            ├── testProcess.bpmn             # Simple process: Start → UserTask → End
            └── logback-test.xml             # Logging configuration
```

> No `orqueio.cfg.xml` — the engine is configured entirely in Java code.

---

## The process under test

A minimal BPMN process with a single user task:

```
[Start] → [UserTask: Handle Request] → [End]
```

---

## How it works

### Programmatic engine creation with `useProcessEngine()`

Instead of relying on `orqueio.cfg.xml`, the engine is created directly in the test class using the `ProcessEngineConfiguration` builder:

```java
ProcessEngine usedProcessEngine = ProcessEngineConfiguration
    .createStandaloneInMemProcessEngineConfiguration()
    .setJdbcUrl("jdbc:h2:mem:orqueio;DB_CLOSE_DELAY=1000")
    .buildProcessEngine();

@RegisterExtension
ProcessEngineExtension extension = ProcessEngineExtension.builder()
    .useProcessEngine(usedProcessEngine)
    .build();
```

The `ProcessEngineExtension` then uses this engine for deployment, test lifecycle, and cleanup — just like if it had been configured via XML.

### What the test verifies

The test starts a process instance with a **businessKey** and verifies it is correctly associated:

```java
extension.getRuntimeService().startProcessInstanceByKey("testProcess", "myBusinessKey");

ProcessInstance processInstance = extension.getRuntimeService()
    .createProcessInstanceQuery().singleResult();

assertThat(processInstance.getBusinessKey()).isEqualTo("myBusinessKey");
```

A **businessKey** is an external identifier attached to a process instance — typically a domain ID like an order number or case ID — used to correlate the process instance with an entity in an external system.

---

## Comparison: three ways to use ProcessEngineExtension

| | `@ExtendWith` | `@RegisterExtension` + builder | `useProcessEngine()` (this project) |
|-|--------------|-------------------------------|-------------------------------------|
| Configuration | `orqueio.cfg.xml` | `orqueio.cfg.xml` or builder | **100% in Java code — no XML** |
| Engine lifecycle | Managed by extension | Managed by extension | **Engine pre-created, passed in** |
| Typical use case | Standard tests | Programmatic config | Shared engine, Spring context, integration tests |

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
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

---

## Test scenarios

| Test | What it verifies |
|------|-----------------|
| `testHappyPath` | Process instance started with `myBusinessKey` — `getBusinessKey()` returns the expected value |

---

## Source files

| File | Description |
|------|-------------|
| [UseProcessEngineTest.java](src/test/java/io/orqueio/bpm/unittest/UseProcessEngineTest.java) | Test class: creates engine in code and injects it via `useProcessEngine()` |
| [testProcess.bpmn](src/test/resources/testProcess.bpmn) | Minimal BPMN process: Start → UserTask → End |
