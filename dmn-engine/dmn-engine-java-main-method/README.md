# DMN Engine — Java Main Method Example

This example demonstrates how to use the OrqueIO DMN engine as a **standalone library** in a plain Java application. There is no process engine, no Spring Boot — just the DMN engine bootstrapped directly in a `main()` method.

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Project structure

```
dmn-engine-java-main-method/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/.../
    │   │   └── DishDecider.java             # Main class: bootstraps DMN engine, evaluates decision
    │   └── resources/.../
    │       ├── dish-decision.dmn11.xml      # DMN decision table: Dish
    │       └── dish-decision.png            # Decision table diagram
    └── test/
        └── java/.../
            └── DishDecisionTest.java        # 3 unit tests
```

---

## The Dish decision table

The DMN file models a single decision: **which dish to serve** based on the season and number of guests.

Hit policy: **UNIQUE** — exactly one rule matches per evaluation.

| Season | Guests | Dish |
|--------|--------|------|
| Fall | ≤ 8 | Spareribs |
| Winter | ≤ 8 | Roastbeef |
| Spring | ≤ 4 | Dry Aged Gourmet Steak |
| Spring | 5–8 | Steak |
| Fall / Winter / Spring | > 8 | Stew |
| Summer | any | Light Salad and a nice Steak |

---

## How it works

### 1. Maven dependency

Add the DMN engine BOM for version management and the engine itself:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.orqueio.bpm.dmn</groupId>
      <artifactId>orqueio-engine-dmn-bom</artifactId>
      <version>${version.orqueio}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>io.orqueio.bpm.dmn</groupId>
    <artifactId>orqueio-engine-dmn</artifactId>
  </dependency>
</dependencies>
```

### 2. Bootstrap the DMN engine

```java
DmnEngine dmnEngine = DmnEngineConfiguration
    .createDefaultDmnEngineConfiguration()
    .buildEngine();
```

The engine is created once. It can be reused for multiple evaluations.

### 3. Parse the decision

```java
InputStream inputStream = DishDecider.class
    .getResourceAsStream("dish-decision.dmn11.xml");

DmnDecision decision = dmnEngine.parseDecision("decision", inputStream);
```

The parsed `DmnDecision` can be cached and evaluated multiple times.

### 4. Evaluate the decision

```java
VariableMap variables = Variables
    .putValue("season", "Winter")
    .putValue("guestCount", 4);

DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);
String dish = result.getSingleResult().getSingleEntry();
```

`getSingleResult().getSingleEntry()` is used because the hit policy is UNIQUE — exactly one rule matches.

### 5. Testing with DmnEngineRule

`DishDecisionTest` uses `DmnEngineRule` — a JUnit 4 rule that bootstraps a default DMN engine for each test:

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

Runs with default arguments `Winter 4` and prints:

```
Dish Decision:
    I would recommend to serve: Roastbeef
```

Custom arguments:

```bash
mvn compile exec:java -Dexec.args="Summer 32"
```

### Build an executable JAR

```bash
mvn clean package
java -jar target/DishDecider.jar Spring 6
```

---

## Test scenarios

| Test | Season | Guests | Expected dish |
|------|--------|--------|---------------|
| `shouldServeDryAgedInSpringForFewGuests` | Spring | 4 | Dry Aged Gourmet Steak |
| `shouldServeSteakInSpringForSomeGuests` | Spring | 7 | Steak |
| `shouldServeStewInSpringForManyGuests` | Spring | 20 | Stew |

---

## UNIQUE vs COLLECT hit policy

This example uses hit policy **UNIQUE**: at most one rule can match, and the result is retrieved with `getSingleResult().getSingleEntry()`.

The [dmn-engine-drg](../dmn-engine-drg/) example uses hit policy **COLLECT**: multiple rules can match, and results are retrieved with `collectEntries("columnName")`.

---

## Source files

| File | Description |
|------|-------------|
| [dish-decision.dmn11.xml](src/main/resources/io/orqueio/bpm/example/dish-decision.dmn11.xml) | DMN decision table: Dish |
| [DishDecider.java](src/main/java/io/orqueio/bpm/example/DishDecider.java) | Main class: bootstraps the DMN engine and evaluates the decision |
| [DishDecisionTest.java](src/test/java/io/orqueio/bpm/example/DishDecisionTest.java) | Unit tests using DmnEngineRule |
