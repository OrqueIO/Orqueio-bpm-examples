# OrqueIO Order Process — BPMN + DMN Integration Example

This example demonstrates **BPMN process orchestration with DMN decision automation** using OrqueIO BPM and Spring Boot — automating an e-commerce order fulfillment workflow with intelligent customer notification routing.

---

## What You Get

- ✅ **Complete Order Workflow** — BPMN process from order creation to fulfillment  
- ✅ **DMN Decision Table** — Automatic notification type selection based on customer premium status  
- ✅ **MySQL Integration** — Production-ready database configuration with Docker Compose  
- ✅ **OrqueIO Webapps** — Cockpit, Tasklist, Admin UI  
- ✅ **REST API** — Full OrqueIO REST API  
- ✅ **JPA Entities** — Order persistence with Spring Data JPA  

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| Docker | Optional |

---

## Version Compatibility

| OrqueIO Version | Spring Boot Version |
|----------------|---------------------|
| **2.0.1+** | **4.0.1+** |

**Important**: OrqueIO 2.0.1+ requires Spring Boot 4.0.0 or higher.

---

## Running the Application

### 1. Start MySQL

```bash
docker-compose up -d
```

### 2. Build and run

```bash
mvn clean install
mvn spring-boot:run
```

### 3. Access

Open **http://localhost:8080** (demo/demo)

---

## Source Files

| File | Description |
|------|-------------|
| [OrderApplication.java](src/main/java/io/orqueio/bpm/exemple/dmn/OrderApplication.java) | Main application |
| [EvaluateNotificationDecisionDelegate.java](src/main/java/io/orqueio/bpm/exemple/dmn/EvaluateNotificationDecisionDelegate.java) | DMN evaluation |
| [OrderProcess.bpmn](src/main/resources/bpmn/OrderProcess.bpmn) | BPMN workflow |
| [NotificationDecision.dmn](src/main/resources/dmn/NotificationDecision.dmn) | DMN decision table |
| [docker-compose.yml](docker-compose.yml) | MySQL setup |
