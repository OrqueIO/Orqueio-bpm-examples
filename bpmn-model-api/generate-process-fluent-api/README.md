# BPMN Model API — Fluent Process Builder Example

This example demonstrates how to create a **complete BPMN process entirely in Java** using the OrqueIO fluent BPMN Model API — without any `.bpmn` XML file. The process is built programmatically, deployed to an in-memory engine, and executed within a single JUnit test.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Project structure

```
generate-process-fluent-api/
├── pom.xml
└── src/
    ├── main/
    │   └── java/.../invoice/service/
    │       └── ArchiveInvoiceService.java    # JavaDelegate: archives the invoice
    └── test/
        ├── java/.../quickstart/
        │   └── CreateInvoiceProcessTest.java # Builds process in Java, deploys and runs it
        └── resources/
            └── orqueio.cfg.xml               # In-memory engine configuration
```

> No `.bpmn` file — the process definition is generated entirely by Java code at test time.

---

## The generated process

The test builds and executes an invoice approval process:

![Invoice Process](invoice.png)

```
[Start: Invoice received]
        │
[UserTask: Assign Approver]
        │
[UserTask: Approve Invoice] ◄──────────────────────────────┐
        │                                                   │
[Gateway: Invoice approved?]                                │
  ├── yes → [UserTask: Prepare Bank Transfer]               │
  │         → [ServiceTask: Archive Invoice]                │
  │         → [End: Invoice processed]                      │
  └── no  → [UserTask: Review Invoice]                      │
            → [Gateway: Review successful?]                 │
              ├── yes ──────────────────────────────────────┘ (loop)
              └── no  → [End: Invoice not processed]
```

---

## How it works

### 1. Entry point — `Bpmn.createExecutableProcess()`

```java
BpmnModelInstance modelInstance = Bpmn.createExecutableProcess("invoice")
    .orqueioHistoryTimeToLive(180)
    .name("BPMN API Invoice Process")
    .startEvent().name("Invoice received")
    // ... add tasks, gateways, events
    .done();
```

`.done()` finalizes the builder and returns the `BpmnModelInstance`.

### 2. Adding tasks

Each method returns the builder, allowing chaining. OrqueIO-specific attributes use the `orqueio*` prefix:

```java
.userTask().name("Assign Approver").orqueioAssignee("demo")
.userTask().id("approveInvoice").name("Approve Invoice")
.userTask().name("Prepare Bank Transfer")
    .orqueioFormKey("embedded:app:forms/prepare-bank-transfer.html")
    .orqueioCandidateGroups("accounting")
.serviceTask().name("Archive Invoice")
    .orqueioClass("io.orqueio.bpm.example.invoice.service.ArchiveInvoiceService")
```

### 3. Exclusive gateways and conditions

```java
.exclusiveGateway().name("Invoice approved?")
    .gatewayDirection(GatewayDirection.Diverging)
.condition("yes", "${approved}")
.userTask().name("Prepare Bank Transfer")
    // ... first path
.endEvent().name("Invoice processed")
.moveToLastGateway()              // jump back to the gateway
.condition("no", "${!approved}")
.userTask().name("Review Invoice") // second path
```

### 4. Three navigation methods

| Method | Purpose |
|--------|---------|
| `.moveToLastGateway()` | Jump back to the last gateway to add a new outgoing path |
| `.moveToNode(id)` | Jump to any node by ID |
| `.connectTo(id)` | Connect current position to an existing node (loops, joins) |

The loop back to `approveInvoice` is created with `.connectTo()`:

```java
.condition("yes", "${clarified}")
.connectTo("approveInvoice")   // loops back to the Approve Invoice user task
```

### 5. Deploy and execute

The model instance is deployed directly without a file:

```java
processEngine.getRepositoryService()
    .createDeployment()
    .addModelInstance("invoice.bpmn", modelInstance)
    .deploy();

processEngine.getRuntimeService().startProcessInstanceByKey("invoice");
```

### 6. Inspect the generated XML

To print the generated BPMN XML to the console, uncomment this line in the test:

```java
Bpmn.writeModelToStream(System.out, modelInstance);
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
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

The log will also confirm the service task executed:
```
... Now archiving invoice
```

---

## Test scenario

| Step | Action | Assertion |
|------|--------|-----------|
| 1 | Start process | 1 active task: "Assign Approver" |
| 2 | Complete "Assign Approver" | 1 active task: "Approve Invoice" |
| 3 | Complete "Approve Invoice" with `approved=true` | 1 active task: "Prepare Bank Transfer" |
| 4 | Complete "Prepare Bank Transfer" | `ArchiveInvoiceService.wasExecuted = true` |
| 5 | After archive | 0 active process instances — process ended |

---

## BPMN file vs Fluent API

| | `.bpmn` file | Fluent API (this project) |
|-|-------------|--------------------------|
| Process defined in | XML | Java code |
| Dynamic generation | No | Yes |
| Tooling (modeler) | Yes | No |
| Versionable in code | Requires XML | Native Java |
| Use case | Standard workflows | Generated/dynamic processes |

---

## Source files

| File | Description |
|------|-------------|
| [CreateInvoiceProcessTest.java](src/test/java/io/orqueio/bpm/quickstart/CreateInvoiceProcessTest.java) | Builds, deploys and executes the invoice process using the Fluent API |
| [ArchiveInvoiceService.java](src/main/java/io/orqueio/bpm/example/invoice/service/ArchiveInvoiceService.java) | JavaDelegate executed by the Archive Invoice service task |
| [orqueio.cfg.xml](src/test/resources/orqueio.cfg.xml) | In-memory engine configuration |
