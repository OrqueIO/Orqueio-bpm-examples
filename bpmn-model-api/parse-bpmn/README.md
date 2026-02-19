# BPMN Model API — Parse BPMN Example

This example demonstrates how to **read and navigate an existing BPMN file** using the OrqueIO BPMN Model API — without deploying the process or starting a process engine. The model is parsed into a traversable Java object graph, giving you programmatic access to every element, attribute, and extension.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Project structure

```
parse-bpmn/
├── pom.xml
└── src/
    └── test/
        ├── java/.../quickstart/
        │   └── ParseBpmnTest.java     # 6 tests covering every aspect of BPMN model traversal
        └── resources/
            └── testProcess.bpmn      # Process with user tasks, service tasks, gateways, scripts
```

> No `orqueio.cfg.xml` — the BPMN Model API is a **standalone library** that requires no process engine.

---

## The parsed process

The test file reads and navigates this process:

```
[Start]
   │
[UserTask: Handle Request]
   │
[ExclusiveGateway: split]
  ├── [ServiceTask: do something]
  │         │
  │   [ExclusiveGateway: join]
  │         │
  └── [ScriptTask: do something else]
              │
        [ExclusiveGateway: join]
                 │
             [End]
```

---

## How it works

### 1. Parse the BPMN file

The entire model is loaded from a resource stream in a single call — no engine, no deployment:

```java
InputStream stream = getClass().getResourceAsStream("testProcess.bpmn");
BpmnModelInstance modelInstance = Bpmn.readModelFromStream(stream);
```

### 2. Find an element by ID

```java
ModelElementInstance element = modelInstance.getModelElementById("startEvent");
assertThat(element).isNotNull();
```

### 3. Find elements by type

Retrieve all elements of a given BPMN type from anywhere in the model:

```java
Collection<ServiceTask> serviceTasks =
    modelInstance.getModelElementsByType(ServiceTask.class);

assertThat(serviceTasks).hasSize(1);
```

### 4. Read standard and OrqueIO-specific attributes

Both standard BPMN attributes and OrqueIO extension attributes are accessible directly on the element:

```java
ServiceTask serviceTask = (ServiceTask) modelInstance.getModelElementById("serviceTask");

// OrqueIO extension attribute
String expression = serviceTask.getOrqueioExpression();

// Raw namespace attribute
String delegateExpression = serviceTask.getAttributeValueNs(
    "http://orqueio.io/schema/1.0/bpmn", "delegateExpression");
```

### 5. Read child elements

Access nested BPMN elements such as conditions on sequence flows and script bodies on script tasks:

```java
// Condition expression on a sequence flow
SequenceFlow flow4 = (SequenceFlow) modelInstance.getModelElementById("flow4");
String condition = flow4.getConditionExpression().getTextContent(); // "${test}"

// Script body on a script task
ScriptTask scriptTask = (ScriptTask) modelInstance.getModelElementById("scriptTask");
String script = scriptTask.getScript().getTextContent(); // "return \"hello world\";"
```

### 6. Read extension elements

OrqueIO-specific extensions (form data, form fields, execution listeners) are accessible via the extensions API:

```java
UserTask userTask = (UserTask) modelInstance.getModelElementById("userTask");
OrqueioFormData formData = userTask.getExtensionElements()
    .getElementsQuery()
    .filterByType(OrqueioFormData.class)
    .singleResult();

List<OrqueioFormField> formFields = new ArrayList<>(formData.getOrqueioFormFields());
assertThat(formFields).hasSize(4);

OrqueioExecutionListener listener = userTask.getExtensionElements()
    .getElementsQuery()
    .filterByType(OrqueioExecutionListener.class)
    .singleResult();
assertThat(listener.getOrqueioEvent()).isEqualTo("create");
```

### 7. Navigate references (source, target, incoming, outgoing)

Traverse the graph by following references between nodes:

```java
// Sequence flow source and target
SequenceFlow flow1 = (SequenceFlow) modelInstance.getModelElementById("flow1");
assertThat(flow1.getSource().getId()).isEqualTo("startEvent");
assertThat(flow1.getTarget().getId()).isEqualTo("userTask");

// All previous nodes of a gateway (incoming flows)
ExclusiveGateway join = (ExclusiveGateway) modelInstance.getModelElementById("join");
Collection<FlowNode> previousNodes = join.getPreviousNodes().list();
assertThat(previousNodes).hasSize(2);

// Access the BPMN diagram element (shape/bounds) from a flow node
BpmnShape shape = serviceTask.getDiagramElement();
assertThat(shape.getBounds().getX()).isEqualTo(648);
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
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

---

## Test scenarios

| Test method | What it verifies |
|-------------|-----------------|
| `findElementById` | Retrieves the start event by its XML `id` attribute |
| `findElementByType` | Retrieves all `ServiceTask` elements from the model |
| `readAttributes` | Reads OrqueIO extension attributes from a service task |
| `readChildElements` | Reads condition expressions and script body from child elements |
| `readExtensionElements` | Reads OrqueIO form fields and execution listeners via extension API |
| `useReferences` | Navigates source/target on sequence flows, previous nodes on gateways, and diagram shapes |

---

## Parse vs Generate — when to use each

| | `parse-bpmn` (this project) | `generate-process-fluent-api` |
|-|-----------------------------|-------------------------------|
| Direction | **Read** an existing `.bpmn` file | **Write** a new process in Java |
| Input | `.bpmn` XML file | No file — built in code |
| Process engine required | No | No |
| Typical use case | Tooling, validation, analysis, migration | Dynamic/generated process definitions |
| Entry point | `Bpmn.readModelFromStream()` | `Bpmn.createExecutableProcess()` |

---

## Source files

| File | Description |
|------|-------------|
| [ParseBpmnTest.java](src/test/java/io/orqueio/bpm/quickstart/ParseBpmnTest.java) | 6 tests covering all BPMN model traversal capabilities |
| [testProcess.bpmn](src/test/resources/testProcess.bpmn) | BPMN process with user tasks, service task, gateway, script task, form fields, and execution listeners |
