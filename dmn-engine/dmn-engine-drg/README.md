# Decision Requirements Graph (DRG) — OrqueIO DMN Engine Example

This example demonstrates how to use the OrqueIO DMN engine as a standalone library to evaluate a **Decision Requirements Graph (DRG)** — a graph of multiple interdependent decisions defined in a single DMN file.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Project structure

```
dmn-engine-drg/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/.../drg/
    │   │   └── BeveragesDecider.java        # Main class: bootstraps DMN engine, evaluates decision
    │   └── resources/.../drg/
    │       ├── dinnerDecisions.dmn          # DMN file: 2 chained decisions (Dish + Beverages)
    │       ├── dinnerDecisions.png          # DRG diagram
    │       ├── dish.png                     # Dish decision table diagram
    │       └── beverages.png               # Beverages decision table diagram
    └── test/
        └── java/.../drg/
            └── DrgDecisionTest.java         # 3 unit tests
```

---

## What is a DRG?

A **Decision Requirements Graph** is a DMN model that contains multiple decisions, where the output of one decision can be used as input to another.

This example models a dinner planning scenario with two chained decisions:

```
Inputs
  season             ──┐
  guestCount         ──┴──► [Decision: Dish]  ──────────┐
                                                         ▼
  guestsWithChildren ──────────────────────► [Decision: Beverages]
```

### Decision 1 — Dish

Determines what dish to serve based on the **season** and **number of guests**.

| Season | Guests | Dish |
|--------|--------|------|
| Spring | ≤ 4 | Dry Aged Gourmet Steak |
| Spring | 5–8 | Steak |
| Spring | > 8 | Stew |
| Winter | ≤ 8 | Roastbeef |
| Winter | > 8 | Stew |
| Fall | ≤ 8 | Spareribs |
| Fall | > 8 | Stew |
| Summer | any | Light Salad and a nice Steak |

### Decision 2 — Beverages

Determines what beverages to serve based on the **dish** (output of Decision 1) and whether **guests have children**.

Hit policy: **COLLECT** — all matching rules contribute to the result.

| Dish | Guests with children | Beverage |
|------|----------------------|----------|
| Spareribs | any | Aecht Schlenkerla Rauchbier |
| Stew | any | Guiness |
| Roastbeef | any | Bordeaux |
| Steak / Dry Aged Steak / Salad & Steak | any | Pinot Noir |
| any | true | Apple Juice |
| any | any | Water |

---

## How it works

### 1. Bootstrapping the DMN engine

```java
DmnEngine dmnEngine = DmnEngineConfiguration
    .createDefaultDmnEngineConfiguration()
    .buildEngine();
```

The engine is created once and can be reused for multiple evaluations.

### 2. Parsing the DRG

```java
InputStream inputStream = BeveragesDecider.class
    .getResourceAsStream("dinnerDecisions.dmn");

DmnDecision decision = dmnEngine.parseDecision("beverages", inputStream);
```

`parseDecision("beverages", ...)` targets the `beverages` decision by ID. The engine automatically resolves its dependency on the `dish` decision within the same DMN file.

### 3. Evaluating the decision

```java
VariableMap variables = Variables
    .putValue("season", "Spring")
    .putValue("guestCount", 10)
    .putValue("guestsWithChildren", false);

DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
List<String> beverages = result.collectEntries("beverages");
```

The engine evaluates `dish` first, then feeds its output into `beverages`. Only the final result is returned.

### 4. Testing with DmnEngineRule

`DrgDecisionTest` uses `DmnEngineRule` — a JUnit 4 rule that bootstraps a default DMN engine for each test:

```java
@Rule
public DmnEngineRule dmnEngineRule = new DmnEngineRule();
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
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
```

### Run the main class directly

```bash
mvn compile exec:java
```

This runs `BeveragesDecider` with the default arguments `Spring 10 false` and prints:

```
Beverages:
    I would recommend to serve: [Guiness, Water]
```

You can pass custom arguments:

```bash
mvn compile exec:java -Dexec.args="Winter 7 true"
```

### Build an executable JAR

```bash
mvn clean package
java -jar target/BeveragesDecider.jar Spring 10 false
```

---

## Test scenarios

| Test | Inputs | Expected beverages |
|------|--------|--------------------|
| `shouldServeGuiness` | Spring, 10, false | Guiness, Water |
| `shouldServeBordeauxAndAppleJuice` | Winter, 7, true | Bordeaux, Apple Juice, Water |
| `shouldServePinotNoir` | Summer, 14, false | Pinot Noir, Water |

---

## DMN Engine vs BPMN Engine

The OrqueIO DMN engine (`orqueio-engine-dmn`) is a **standalone library** — it does not require the BPMN process engine. You can use it independently to evaluate business rules in any Java application.

| | DMN Engine | BPMN Engine |
|-|------------|-------------|
| Purpose | Evaluate decision tables / DRGs | Execute process flows |
| Deployment | Library dependency only | Requires engine infrastructure |
| Use case | Business rules, scoring, routing | Workflow orchestration |
| Standalone use | Yes | Typically no |

---

## Source files

| File | Description |
|------|-------------|
| [dinnerDecisions.dmn](src/main/resources/io/orqueio/bpm/example/drg/dinnerDecisions.dmn) | DMN file with two chained decisions (Dish and Beverages) |
| [BeveragesDecider.java](src/main/java/io/orqueio/bpm/example/drg/BeveragesDecider.java) | Main class: bootstraps the DMN engine and evaluates the decision |
| [DrgDecisionTest.java](src/test/java/io/orqueio/bpm/example/drg/DrgDecisionTest.java) | Unit tests using DmnEngineRule |
