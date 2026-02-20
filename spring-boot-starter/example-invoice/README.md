# OrqueIO Invoice Example — Spring Boot Edition

This example demonstrates the classic **OrqueIO Invoice approval workflow** packaged as a Spring Boot application — providing a complete, production-ready BPM solution with webapps and REST API.

---

## What You Get

- ✅ **Invoice Approval Workflow** — Complete BPMN process with user tasks, service tasks, and DMN decisions
- ✅ **OrqueIO Webapps** — Cockpit, Tasklist, Admin UI
- ✅ **REST API** — Full OrqueIO REST API at `/engine-rest`
- ✅ **Embedded HTML Forms** — Invoice creation, approval, and review forms
- ✅ **H2 In-Memory Database** — Zero-config out-of-the-box experience
- ✅ **Pre-configured Admin User** — `demo/demo`

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 21+ |
| Maven | 3.6+ |

---

## Version Compatibility

| OrqueIO Version | Spring Boot Version |
|-----------------|---------------------|
| **2.0.0+**      | **4.0.0+**          |

**Important**: OrqueIO 2.0.0+ requires Spring Boot 4.0.0 or higher.

---

## Project Structure

```
example-invoice/
├── pom.xml                                    # OrqueIO Invoice + Spring Boot
└── src/
    ├── main/
    │   ├── java/.../invoice/
    │   │   └── Application.java               # @SpringBootApplication + Invoice deployment
    │   └── resources/
    │       ├── META-INF/
    │       │   └── processes.xml              # Process application descriptor
    │       ├── static/forms/                  # Invoice HTML forms
    │       └── application.yaml               # Admin user + datasource config
```

---

## The Invoice Workflow

```
[Start: Invoice Received]
   │
[UserTask: Assign Approver]
   │
[UserTask: Approve Invoice] ◄──────────────────────────────┐
   │                                                       │
[Gateway: Approved?]                                        │
  ├── Yes → [UserTask: Prepare Bank Transfer]              │
  │         → [ServiceTask: Archive Invoice]               │
  │         → [End: Invoice Processed]                     │
  └── No  → [UserTask: Review Invoice]                     │
            → [Gateway: Review Successful?]                │
              ├── Yes ──────────────────────────────────────┘ (loop)
              └── No  → [End: Invoice Not Processed]
```

---

## How It Works

### 1. Add OrqueIO Spring Boot Starters

The project uses two starters for complete functionality:

```xml
<dependency>
  <groupId>io.orqueio.bpm.springboot</groupId>
  <artifactId>orqueio-bpm-spring-boot-starter-webapp</artifactId>
</dependency>
<dependency>
  <groupId>io.orqueio.bpm.springboot</groupId>
  <artifactId>orqueio-bpm-spring-boot-starter-rest</artifactId>
</dependency>
```

### 2. Add the Invoice Example Dependency

The OrqueIO Invoice example is provided as a reusable artifact:

```xml
<dependency>
  <groupId>io.orqueio.bpm.example</groupId>
  <artifactId>orqueio-example-invoice-jakarta</artifactId>
  <version>${orqueio.version}</version>
  <classifier>classes</classifier>
</dependency>
```

### 3. Deploy Invoice Processes Programmatically

The main application class deploys the Invoice BPMN and DMN files at startup:

```java
@SpringBootApplication
@EnableProcessApplication
public class Application {

  @Autowired
  protected ProcessEngine processEngine;

  protected InvoiceProcessApplication invoicePa = new InvoiceProcessApplication();

  @PostConstruct
  public void deployInvoice() {
    ClassLoader classLoader = invoicePa.getClass().getClassLoader();

    processEngine.getRepositoryService()
        .createDeployment()
        .addInputStream("invoice.v1.bpmn", classLoader.getResourceAsStream("invoice.v1.bpmn"))
        .addInputStream("reviewInvoice.bpmn", classLoader.getResourceAsStream("reviewInvoice.bpmn"))
        .deploy();
  }

  @EventListener
  public void onPostDeploy(PostDeployEvent event) {
    invoicePa.startFirstProcess(event.getProcessEngine());
  }
}
```

### 4. Configure Admin User

In `application.yaml`:

```yaml
camunda.bpm.admin-user:
  id: demo
  password: demo
  firstName: Demo
```

### 5. Add Invoice HTML Forms

Place form HTML files in `src/main/resources/static/forms/`:
- `start-form.html` — Invoice creation form
- `assign-approver.html` — Approver assignment form
- `approve-invoice.html` — Approval decision form
- `prepare-bank-transfer.html` — Payment details form

---

## Running the Application

### Build and run

```bash
mvn clean install
java -jar target/orqueio-bpm-spring-boot-starter-example-invoice-0.0.1-SNAPSHOT.jar
```

Or use Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

### Access the Application

#### Webapps (Cockpit, Tasklist, Admin)

Open your browser at:

**http://localhost:8080**

Login with:
- **Username**: `demo`
- **Password**: `demo`

#### REST API

The OrqueIO REST API is available at:

**http://localhost:8080/engine-rest**

---

## Using the Invoice Process

### 1. Start an Invoice Process

Go to **Tasklist** → **Start Process** → **Invoice Approval**

Fill in the invoice details:
- **Creditor**: Supplier name
- **Amount**: Invoice amount
- **Invoice Number**: Unique identifier

### 2. Assign Approver

Complete the "Assign Approver" task by selecting a user from the dropdown.

### 3. Approve or Reject

The assigned approver reviews the invoice and decides:
- **Approve** → Proceeds to payment preparation
- **Reject** → Sends to review

### 4. Review Rejected Invoices

If rejected, a "Review Invoice" task is created where the invoice can be:
- **Clarified** → Loops back to approval
- **Cancelled** → Ends the process

### 5. Prepare Bank Transfer

Once approved, complete the payment details form.

### 6. Archive Invoice

The "Archive Invoice" service task automatically executes, logging the completion.

---

## REST API Examples

### Get deployed processes

```bash
curl -u demo:demo http://localhost:8080/engine-rest/process-definition
```

### Start an invoice process

```bash
curl -u demo:demo \
  -H "Content-Type: application/json" \
  -X POST \
  -d '{
    "variables": {
      "creditor": {"value":"ACME Corp","type":"String"},
      "amount": {"value":1500,"type":"Long"},
      "invoiceNumber": {"value":"INV-2024-001","type":"String"}
    }
  }' \
  http://localhost:8080/engine-rest/process-definition/key/invoice/start
```

---

## Switching to Production Database

### PostgreSQL Configuration

Add PostgreSQL driver to `pom.xml`:

```xml
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
</dependency>
```

Update `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/orqueio
    username: orqueio
    password: orqueio
    driver-class-name: org.postgresql.Driver
```

### MySQL Configuration

Add MySQL driver to `pom.xml`:

```xml
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
</dependency>
```

Update `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/orqueio
    username: orqueio
    password: orqueio
    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

## Use Cases

- **Invoice Approval Workflows** — Automate AP processes
- **Multi-Stage Approvals** — Configurable approval chains
- **Process Monitoring** — Track invoice status in Cockpit
- **REST API Integration** — Connect external systems
- **Custom Forms** — Embedded HTML forms in Tasklist

---

## Next Steps

- Customize the approval workflow (add budget thresholds, multi-level approval)
- Integrate with ERP systems via REST API
- Add email notifications on task assignment
- Configure production database (PostgreSQL/MySQL)
- Add DMN decision tables for automatic approval rules

---

## Source Files

| File | Description |
|------|-------------|
| [Application.java](src/main/java/io/orqueio/bpm/spring/boot/example/invoice/Application.java) | Main Spring Boot application with Invoice deployment |
| [processes.xml](src/main/resources/META-INF/processes.xml) | Process application descriptor |
| [application.yaml](src/main/resources/application.yaml) | Spring Boot configuration |
| [static/forms/](src/main/resources/static/forms/) | Invoice HTML forms |
