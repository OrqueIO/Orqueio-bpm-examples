# External Task Client + Spring Boot — Loan Granting Example

This example demonstrates how to build a **complete External Task Client application** with Spring Boot — combining the OrqueIO Engine, Webapps, REST API, and External Task workers in a single application.

---

## What You Get

- ✅ **External Task Client** — Polls and executes external tasks via REST API  
- ✅ **OrqueIO Webapps** — Cockpit, Tasklist, Admin UI  
- ✅ **REST API** — Full OrqueIO REST API  
- ✅ **Loan Granting Workflow** — Credit score checking and loan approval  
- ✅ **@ExternalTaskSubscription** — Annotation-based topic subscriptions  
- ✅ **Spring Configuration** — All settings in application.yml  

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## The Loan Workflow

```
[Start] → [ExternalTask: Check Credit Score]  
       → [ExternalTask: Grant/Reject Loan] → [End]
```

---

## Running the Application

### Build and run

```bash
mvn clean install
mvn spring-boot:run
```

### Access

Open **http://localhost:8080** (demo/demo)

---

## Key Features

### Topic Subscription in YAML

```yaml
camunda.bpm.client:
  base-url: http://localhost:8080/engine-rest
  worker-id: spring-boot-client
  subscriptions:
    creditScoreChecker:
      process-definition-key: loan_process
      auto-open: false
    loanGranter:
      lock-duration: 10000
```

### Handler with @ExternalTaskSubscription

```java
@ExternalTaskSubscription("creditScoreChecker")
@Bean
public ExternalTaskHandler creditScoreChecker() {
  return (externalTask, externalTaskService) -> {
    int defaultScore = externalTask.getVariable("defaultScore");
    List<Integer> scores = Arrays.asList(defaultScore, 9, 1, 4, 10);
    
    ObjectValue scoresObject = Variables.objectValue(scores).create();
    externalTaskService.complete(externalTask, 
        Variables.putValueTyped("creditScores", scoresObject));
  };
}
```

---

## Architecture

**Embedded Worker Pattern** — Engine and workers in same JVM

✅ Single deployable JAR  
✅ Simple local development  
✅ No network latency  

---

## Source Files

| File | Description |
|------|-------------|
| [Application.java](src/main/java/io/orqueio/bpm/spring/boot/example/loangranting/Application.java) | Main application |
| [HandlerConfiguration.java](src/main/java/io/orqueio/bpm/spring/boot/example/loangranting/HandlerConfiguration.java) | External task handlers |
| [loan-granting.bpmn](src/main/resources/bpmn/loan-granting.bpmn) | BPMN with external tasks |
| [application.yml](src/main/resources/application.yml) | Client configuration |
