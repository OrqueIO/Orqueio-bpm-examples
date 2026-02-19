# OrqueIO BPM Assert — Testing Example

This example demonstrates how to write expressive BPMN process tests using **OrqueIO BPM Assert** (`orqueio-bpm-assert`) — a fluent assertion library built on top of AssertJ that greatly simplifies testing process instances, tasks, and variables.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Project structure

```
job-announcement-publication-process/
├── pom.xml
└── src/
    ├── main/
    │   └── java/.../jobannouncement/
    │       ├── JobAnnouncement.java          # Domain interface
    │       └── JobAnnouncementService.java   # Service interface (mocked in tests)
    └── test/
        ├── java/.../
        │   ├── JobAnnouncementProcessTest.java             # 4 tests — main process
        │   └── JobAnnouncementPublicationProcessTest.java  # 4 tests — publication subprocess
        └── resources/
            ├── orqueio.cfg.xml                                  # In-memory engine configuration
            ├── orqueio-testing-job-announcement.bpmn            # Main process definition
            └── orqueio-testing-job-announcement-publication.bpmn # Publication subprocess definition
```

---

## The two processes

### Process 1 — Job Announcement (main)

```
[Start: New open position]
      │
[UserTask: Describe job role]      ← candidateGroup: engineering
      │
[UserTask: Review job role]        ← assignee: requester (from service)
      │
[Gateway: OK?]
  ├── yes → [UserTask: Publish announcement] → [CallActivity: Execute announcement]
  └── no  → [UserTask: Improve job role description] ──────────────────────────┘
                                                              │
                                               [ServiceTask: Send confirmation]
                                                              │
                                                           [End]
```

### Process 2 — Job Announcement Publication (called subprocess)

```
[Start]
      │
[ServiceTask: Publish on website]     ← always executed
      │
[Parallel Gateway]
  ├── [Gateway: Twitter?] → yes → [Publish on Twitter]
  └── [Gateway: Facebook?] → yes → [Publish on Facebook]
      │
[End]
```

---

## How it works

### 1. OrqueIO BPM Assert

Without `orqueio-bpm-assert`, asserting a task requires multiple API calls:

```java
// Without assert library — verbose and brittle
List<Task> tasks = taskService.createTaskQuery()
    .processInstanceId(pi.getId()).list();
assertEquals(1, tasks.size());
assertEquals("edit", tasks.get(0).getTaskDefinitionKey());
assertNull(tasks.get(0).getAssignee());
```

With `orqueio-bpm-assert` — fluent and readable:

```java
// With assert library
assertThat(processInstance).task().hasDefinitionKey("edit").isNotAssigned();
```

Key assertions available:

| Assertion | Usage |
|-----------|-------|
| Process started / ended | `assertThat(pi).isStarted()` / `.isEnded()` |
| Current task | `assertThat(pi).task().hasDefinitionKey("edit")` |
| Task assigned to | `assertThat(pi).task().isAssignedTo("fozzie")` |
| Candidate group | `assertThat(pi).task().hasCandidateGroup("engineering")` |
| Variables | `assertThat(pi).hasVariables("jobAnnouncementId", "approved")` |
| Execution path | `assertThat(pi).hasPassedInOrder(findId("Describe job role"), ...)` |

Helper methods for interacting with tasks:

```java
claim(task(), "fozzie");                                   // assign a task
complete(task());                                          // complete current task
complete(task(), withVariables("approved", true));         // complete with variables
complete(claim(task(), "fozzie"), withVariables(...));     // claim and complete in one call
```

### 2. Mocking services with Mockito + Mocks.register()

Services called by the process engine (via UEL expressions like `#{jobAnnouncementService.findRequester(...)}`) are mocked with Mockito and registered in OrqueIO's mock registry:

```java
@Mock
public JobAnnouncementService jobAnnouncementService;

@Before
public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mocks.register("jobAnnouncementService", jobAnnouncementService);
}

@After
public void tearDown() {
    Mocks.reset();
}
```

After the test, Mockito `verify()` confirms that the expected service methods were called:

```java
verify(jobAnnouncementService).postToWebsite(jobAnnouncement.getId());
verify(jobAnnouncementService).postToTwitter(jobAnnouncement.getId());
verifyNoMoreInteractions(jobAnnouncementService);
```

### 3. MockExpressionManager

The `orqueio.cfg.xml` configures a `MockExpressionManager` that resolves UEL expressions against registered mocks instead of a real Spring context:

```xml
<property name="expressionManager">
    <bean class="io.orqueio.bpm.engine.test.mock.MockExpressionManager" />
</property>
```

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
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
```

---

## Test scenarios

### JobAnnouncementProcessTest (4 tests)

| Test | What it verifies |
|------|-----------------|
| `testHappyPath` | Full process flow: edit → review → approve → publish → execute → confirm → ended |
| `testCandidateGroupAssociated` | Task still has candidate group after being claimed |
| `should_fail_if_processInstance_is_not_waiting_at_expected_task` | Assert throws error when process is not at expected task |
| `should_not_fail_if_processInstance_is_waiting_at_expected_task` | Assert passes when process is at correct task |

### JobAnnouncementPublicationProcessTest (4 tests)

| Test | twitter | facebook | Expected service calls |
|------|---------|----------|------------------------|
| `testPublishOnlyOnCompanyWebsite` | false | false | website only |
| `testPublishAlsoOnTwitter` | true | false | website + Twitter |
| `testPublishAlsoOnFacebook` | false | true | website + Facebook |
| `testPublishAlsoOnFacebookAndTwitter` | true | true | website + Twitter + Facebook |

---

## Source files

| File | Description |
|------|-------------|
| [orqueio-testing-job-announcement.bpmn](src/test/resources/orqueio-testing-job-announcement.bpmn) | Main job announcement process |
| [orqueio-testing-job-announcement-publication.bpmn](src/test/resources/orqueio-testing-job-announcement-publication.bpmn) | Publication subprocess |
| [JobAnnouncementProcessTest.java](src/test/java/io/orqueio/bpm/engine/test/assertions/examples/JobAnnouncementProcessTest.java) | Tests for the main process |
| [JobAnnouncementPublicationProcessTest.java](src/test/java/io/orqueio/bpm/engine/test/assertions/examples/JobAnnouncementPublicationProcessTest.java) | Tests for the publication subprocess |
| [orqueio.cfg.xml](src/test/resources/orqueio.cfg.xml) | In-memory engine configuration with MockExpressionManager |
