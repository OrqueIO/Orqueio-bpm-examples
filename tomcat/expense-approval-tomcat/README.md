# Tomcat 11 WAR with JavaDelegate Example - Expense Approval Process

Professional WAR example demonstrating OrqueIO BPM on Apache Tomcat 11 with Jakarta EE 11 (Servlet 6.1).

## Purpose

This example demonstrates a real-world expense approval workflow using OrqueIO BPM on Tomcat 11 and Jakarta EE 11 (which uses the `jakarta.*` namespace instead of `javax.*`). It showcases professional BPM best practices including:

**Process Features:**
- **Conditional Routing:** Dynamic approval routing based on expense amount
- **User Forms:** Embedded Camunda forms with validation and read-only fields
- **Error Handling:** Boundary error events for validation failures
- **Asynchronous Processing:** Async service tasks for better scalability
- **Multi-path Flows:** Separate paths for approval, rejection, and validation errors
- **Automated Notifications:** Email notifications for all process outcomes

**Technical Components:**
- Complete expense approval BPMN process with 3 end states (Approved, Rejected, Validation Failed)
- JavaDelegates for expense validation, payment processing, and notifications
- Jakarta-based ProcessApplication using `JakartaServletProcessApplication`
- Comprehensive test coverage (5 test scenarios covering all paths)

## Prerequisites

- JDK 21 or later
- Apache Maven 3.6+
- Apache Tomcat 11.x (Jakarta EE 11 / Servlet 6.1)
- OrqueIO shared engine installation (see setup below)

## Build

Build the WAR from this module directory:

```bash
mvn clean package
```

The WAR will be created at `target/tomcat11-war-javadelegate-0.0.1-SNAPSHOT.war`.

## Tomcat 11 Shared Engine Setup

### 1. Install Tomcat 11

Download and extract Apache Tomcat 11.x from https://tomcat.apache.org/download-11.cgi

Set `CATALINA_HOME` environment variable:
```bash
export CATALINA_HOME=/path/to/tomcat11
```

### 2. Install OrqueIO Shared Engine

The OrqueIO engine and its dependencies must be placed in Tomcat's shared lib directory.

#### 2.1 Copy Engine JARs

Copy the following OrqueIO artifacts (version 2.0.6-SNAPSHOT or compatible) to `$CATALINA_HOME/lib`:

**Core Engine:**
- `orqueio-engine-2.0.6-SNAPSHOT.jar`
- `orqueio-engine-rest-core-jakarta-2.0.6-SNAPSHOT.jar` (optional, for REST API)
- `orqueio-engine-rest-jakarta-2.0.6-SNAPSHOT.jar` (optional, for REST API)

**Dependencies:**
- `mybatis-3.x.x.jar`
- `joda-time-2.x.x.jar`
- Database driver (e.g., `h2-2.x.x.jar` for H2, or PostgreSQL/MySQL driver)
- SLF4J JARs: `slf4j-api-2.x.x.jar`, `slf4j-jdk14-2.x.x.jar`

These JARs can be found in:
- OrqueIO Maven repository: `~/.m2/repository/io/orqueio/bpm/`
- OrqueIO distribution build (if available)

#### 2.2 Create bpm-platform.xml

Create `$CATALINA_HOME/conf/bpm-platform.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<bpm-platform xmlns="http://www.camunda.org/schema/1.0/BpmPlatform"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://www.camunda.org/schema/1.0/BpmPlatform http://www.camunda.org/schema/1.0/BpmPlatform">

  <job-executor>
    <job-acquisition name="default" />
  </job-executor>

  <process-engine name="default">
    <job-acquisition>default</job-acquisition>
    <configuration>io.orqueio.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration</configuration>
    <datasource>java:comp/env/jdbc/ProcessEngine</datasource>

    <properties>
      <property name="history">full</property>
      <property name="databaseSchemaUpdate">true</property>
      <property name="authorizationEnabled">false</property>
      <property name="jobExecutorDeploymentAware">true</property>
    </properties>
  </process-engine>

</bpm-platform>
```

**Note:** The exact XML namespace and schema location follow Camunda conventions (OrqueIO is a Camunda 7 fork and maintains compatibility). The configuration class `io.orqueio.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration` is from OrqueIO sources.

#### 2.3 Configure server.xml

Edit `$CATALINA_HOME/conf/server.xml`:

**Add the bootstrap listener** inside the `<Server>` tag (near other `<Listener>` elements):

```xml
<Listener className="io.orqueio.bpm.container.impl.tomcat.TomcatBpmPlatformBootstrap" />
```

**Add the datasource** inside the `<GlobalNamingResources>` tag:

```xml
<Resource name="jdbc/ProcessEngine"
          auth="Container"
          type="javax.sql.DataSource"
          factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
          uniqueResourceName="jdbc/ProcessEngine"
          driverClassName="org.h2.Driver"
          url="jdbc:h2:./orqueio-h2-database;TRACE_LEVEL_FILE=0"
          username="sa"
          password=""
          maxActive="20"
          minIdle="5" />
```

**For production:** Replace H2 with PostgreSQL, MySQL, or Oracle. Example for PostgreSQL:
```xml
driverClassName="org.postgresql.Driver"
url="jdbc:postgresql://localhost:5432/orqueio"
username="your_db_user"
password="your_db_password"
```

**Important:** Ensure the JDBC driver JAR is in `$CATALINA_HOME/lib`.

### 3. Deploy the WAR

Copy the WAR to Tomcat's webapps directory:

```bash
cp target/tomcat11-war-javadelegate-0.0.1-SNAPSHOT.war $CATALINA_HOME/webapps/
```

### 4. Start Tomcat

```bash
$CATALINA_HOME/bin/catalina.sh run
```

Or on Windows:
```cmd
%CATALINA_HOME%\bin\catalina.bat run
```

### 5. Verify Deployment

Check `$CATALINA_HOME/logs/catalina.out` for:

**Engine bootstrap:**
```
INFO: Starting OrqueIO BPM Platform 2.0.6-SNAPSHOT on Apache Tomcat
INFO: Deploying process engine 'default'
INFO: Engine created successfully
```

**Process application deployment:**
```
INFO: Detected @ProcessApplication class: io.orqueio.bpm.example.tomcat11.ExpenseManagementApplication
INFO: Deployment summary for process archive 'expense-management-archive':
INFO:   expense-approval-process.bpmn
```

If you see `NoClassDefFoundError` with `javax.*` packages, the servlet JAR is using the wrong namespace. Verify:
- Tomcat 11 (not 10 or 9)
- `jakarta.servlet-api` version 6.1.0
- OrqueIO engine JARs are Jakarta-compatible (2.0.6-SNAPSHOT or later)

## Testing the Process

### Option 1: REST API (if orqueio-engine-rest-jakarta is deployed)

Deploy OrqueIO web apps (Cockpit, Tasklist, Admin) by copying `orqueio-webapp-jakarta.war` to `$CATALINA_HOME/webapps/`.

**Start a process instance:**

Low Amount (Manager Approval):
```bash
curl -X POST http://localhost:8080/engine-rest/process-definition/key/expense-approval-process/start \
  -H "Content-Type: application/json" \
  -d '{
    "variables": {
      "requester": {"value": "john.doe", "type": "String"},
      "requesterManager": {"value": "jane.manager", "type": "String"},
      "amount": {"value": 500.0, "type": "Double"},
      "category": {"value": "Travel", "type": "String"},
      "description": {"value": "Conference travel expenses", "type": "String"}
    }
  }'
```

High Amount (CFO Approval):
```bash
curl -X POST http://localhost:8080/engine-rest/process-definition/key/expense-approval-process/start \
  -H "Content-Type: application/json" \
  -d '{
    "variables": {
      "requester": {"value": "alice.smith", "type": "String"},
      "requesterManager": {"value": "bob.manager", "type": "String"},
      "amount": {"value": 5000.0, "type": "Double"},
      "category": {"value": "Equipment", "type": "String"},
      "description": {"value": "New laptop and accessories", "type": "String"}
    }
  }'
```

**Complete user tasks via REST:**

Get pending tasks:
```bash
curl http://localhost:8080/engine-rest/task?assignee=jane.manager
```

Approve expense:
```bash
curl -X POST http://localhost:8080/engine-rest/task/{taskId}/complete \
  -H "Content-Type: application/json" \
  -d '{
    "variables": {
      "approvalDecision": {"value": "approved", "type": "String"},
      "approverComments": {"value": "Expense approved", "type": "String"}
    }
  }'
```

Reject expense:
```bash
curl -X POST http://localhost:8080/engine-rest/task/{taskId}/complete \
  -H "Content-Type: application/json" \
  -d '{
    "variables": {
      "approvalDecision": {"value": "rejected", "type": "String"},
      "approverComments": {"value": "Missing receipts", "type": "String"}
    }
  }'
```

**Access web interfaces:**
- Tasklist: http://localhost:8080/orqueio-webapp-jakarta/app/tasklist/ (user: `demo` / `demo`)
- Cockpit: http://localhost:8080/orqueio-webapp-jakarta/app/cockpit/
- Admin: http://localhost:8080/orqueio-webapp-jakarta/app/admin/

### Option 2: Tasklist UI with Start Form (RECOMMENDED)

**This is the most user-friendly way to start the process!**

The process includes an **embedded start form** with pre-filled default values to ensure validation always succeeds:

1. Access Tasklist: http://localhost:8080/orqueio-webapp-jakarta/app/tasklist/
2. Login: `demo` / `demo`
3. Click **"Start Process"**
4. Select **"Expense Approval Process"**
5. The form will appear with **default values already filled**:
   - **Requester Email:** `john.doe` ✅
   - **Amount (USD):** `500` ✅
   - **Category:** `Travel` (pre-selected) ✅
   - **Description:** `Conference travel and accommodation` (42 characters - exceeds minimum) ✅
   - **Manager Email:** `jane.manager` ✅
6. Click **"Start"** (or modify values as needed)

**Why default values?** The validation delegate requires all fields to be properly filled. Default values ensure the process always succeeds when started from Tasklist, preventing common errors like "Description is required".

After starting, you'll see the user task appear in Tasklist, assigned to the manager or CFO depending on the amount.

### Option 3: Programmatic Test Servlet

The WAR includes a **diagnostic servlet** that programmatically starts the process and displays detailed debug information:

Access: http://localhost:8080/tomcat11-war-javadelegate-0.0.1-SNAPSHOT/test-start

This servlet will:
- ✅ Start the process with **all required variables** pre-filled
- ✅ Show whether the process is **ACTIVE (waiting at user task)** or **COMPLETED**
- ✅ Display all active tasks with assignees
- ✅ Show current process variables
- ✅ If the process completes immediately, show which end event it reached and why

**This is excellent for troubleshooting!** If the process completes immediately instead of waiting at the user task, the servlet will tell you exactly which path was taken (Validation Failed, Approved, or Rejected).

### Option 4: Embedded JUnit Tests

Run the JUnit tests from the project:
```bash
mvn test
```

This uses an embedded engine (H2 in-memory) and does not require Tomcat. The test suite includes:

**Happy Path Tests:**
- `shouldRouteToManagerForLowAmountExpense()`: Tests low-amount approval path with manager approval
  - Validates expense data
  - Routes to manager for amounts ≤ $1,000
  - Approves with comments
  - Processes payment
  - Sends confirmation notification
- `shouldRouteToCFOForHighAmountExpense()`: Tests high-amount approval path with CFO approval
  - Validates expense data
  - Routes to CFO for amounts > $1,000
  - Approves with comments
  - Processes payment
  - Sends confirmation notification

**Edge Case Tests:**
- `shouldSetDefaultManagerWhenNotProvided()`: Tests default manager assignment when no manager is specified

**Rejection Path Test:**
- `shouldHandleRejectionPath()`: Tests expense rejection workflow
  - Validates expense data
  - Manager rejects the expense
  - Sends rejection notification
  - Verifies no payment was processed

**Error Handling Test:**
- `shouldHandleValidationError()`: Tests validation error handling
  - Submits expense with missing required fields
  - Catches validation error via boundary event
  - Sends validation error notification
  - Process ends at validation failed end event

## Troubleshooting

### Process Completes Immediately Without Creating User Tasks

**Symptom:** Process instance completes immediately (same start and end time) in Cockpit, with no tasks appearing in Tasklist.

**Root Cause:** The validation delegate throws a `BpmnError` when required fields are missing or invalid. The boundary error event catches this and routes to the "Validation Failed" end event.

**Solution:** Ensure ALL required variables are provided when starting the process:

**Via Tasklist (easiest):**
- Use the **start form** which has default values pre-filled
- All fields are automatically valid by default
- Just click "Start" without changing anything to test

**Via REST API:**
```bash
# ALL fields are REQUIRED:
curl -X POST http://localhost:8080/engine-rest/process-definition/key/expense-approval-process/start \
  -H "Content-Type: application/json" \
  -d '{
    "variables": {
      "requester": {"value": "john.doe", "type": "String"},
      "requesterManager": {"value": "jane.manager", "type": "String"},
      "amount": {"value": 500, "type": "Long"},
      "category": {"value": "Travel", "type": "String"},
      "description": {"value": "Conference travel expenses", "type": "String"}
    }
  }'
```

**Validation rules:**
- ❌ `description` must be at least 10 characters
- ❌ `amount` must be between 1 and 50,000
- ❌ `category` and `requester` must not be empty

**Diagnostic tools:**
1. Use the test servlet: http://localhost:8080/tomcat11-war-javadelegate-0.0.1-SNAPSHOT/test-start
   - Shows exactly which path the process took
   - Displays validation error messages
2. Check Tomcat logs (`catalina.out`) for validation error messages:
   ```
   SEVERE: Expense validation failed: Description is required
   ```

**Quick fix for Windows users:**
Run `redeploy.bat` to redeploy the WAR with the corrected form configuration.

### NoClassDefFoundError: javax.servlet.*

**Cause:** Tomcat 11 uses `jakarta.servlet.*`, not `javax.servlet.*`. Either:
- You're using Tomcat 10.0 or earlier (upgrade to 11.x)
- The OrqueIO engine JARs are compiled against `javax.*` (ensure version 2.0.6-SNAPSHOT or later)

**Solution:** Verify Tomcat version (`$CATALINA_HOME/RELEASE-NOTES`) and OrqueIO version in `$CATALINA_HOME/lib`.

### Process Application Not Detected

**Cause:** Missing `processes.xml` or incorrect `META-INF` location.

**Solution:** Verify `src/main/resources/META-INF/processes.xml` is packaged in the WAR at `WEB-INF/classes/META-INF/processes.xml`.

### Engine Fails to Start

**Symptoms:**
```
SEVERE: Exception starting process engine 'default'
```

**Common causes:**
- Missing JDBC driver in `$CATALINA_HOME/lib`
- Incorrect datasource JNDI name in `bpm-platform.xml` (must match `server.xml`)
- Database connection refused (check URL, credentials, and DB running)

**Debug:** Set Tomcat logging to FINE:
```bash
# In $CATALINA_HOME/conf/logging.properties
io.orqueio.bpm.level = FINE
```

### Job Executor Not Starting

**Cause:** Job executor requires `job-executor-activation = true` in `bpm-platform.xml` (uncomment if needed).

### Compare with Tomcat 10.1 (Jakarta EE 10)

If Tomcat 11 deployment fails due to Jakarta EE 11 incompatibilities, test with Tomcat 10.1 (Jakarta EE 10 / Servlet 6.0) as a control:
- Change `jakarta.servlet-api` to version `6.0.0`
- Use Tomcat 10.1.x
- Follow the same setup steps

This isolates whether the issue is specific to Jakarta EE 11 or a general configuration problem.

## Process Details

**BPMN Process ID:** `expense-approval-process`

**Business Process Flow:**

### Happy Path (Approval)
1. **Start Event** - Expense Request Submitted
2. **Service Task** (Async) - Validate Expense Data (`ValidateExpenseDelegate`)
   - Validates required fields (description, amount, category, requester)
   - Ensures amount is positive and does not exceed maximum ($50,000)
   - Assigns default manager if none provided
   - Sets validation status and timestamp
   - **Error Handling:** Throws `BpmnError` for validation failures
3. **Exclusive Gateway** - Check Amount (> $1,000?)
4. **User Task** - Manager Approval (for amounts ≤ $1,000)
   - Assigned to requester's manager
   - **Form Fields:** Decision (Approve/Reject), Comments, Amount (readonly), Description (readonly), Category (readonly)
5. **User Task** - CFO Approval (for amounts > $1,000)
   - Assigned to finance-department group
   - **Form Fields:** Decision (Approve/Reject), Comments, Amount (readonly), Description (readonly), Category (readonly), Requester (readonly)
6. **Exclusive Gateway** - Check Approval Decision
7. **Service Task** (Async) - Process Reimbursement (`ProcessPaymentDelegate`)
   - Generates unique transaction ID
   - Calculates processing fee (1% of amount)
   - Simulates payment processing
   - Sets payment status, transaction ID, and processed timestamp
8. **Service Task** - Send Confirmation Email (`SendNotificationDelegate`)
   - Sends detailed approval confirmation with payment information
9. **End Event** - Expense Approved & Paid

### Rejection Path
1-5. Same as Happy Path
6. **Exclusive Gateway** - Decision is "Rejected"
7. **Service Task** (Async) - Notify Rejection (`SendNotificationDelegate`)
   - Sends rejection notification with approver comments
8. **End Event** - Expense Rejected

### Validation Error Path
1. **Start Event** - Expense Request Submitted
2. **Service Task** - Validate Expense Data
   - **Boundary Error Event** - Catches validation errors
3. **Service Task** - Notify Validation Error (`SendNotificationDelegate`)
   - Sends validation error notification
4. **End Event** - Validation Failed

**JavaDelegate Components:**

**ValidateExpenseDelegate** (`io.orqueio.bpm.example.tomcat11.ValidateExpenseDelegate`):
- **Purpose:** Validates expense request data against business rules
- Input variables:
  - `description` (String): Expense description
  - `amount` (Double): Expense amount
  - `category` (String): Expense category (Travel, Equipment, Office Supplies, etc.)
  - `requester` (String): Employee requesting reimbursement
  - `requesterManager` (String, optional): Approving manager
- Output variables:
  - `validationStatus`: "APPROVED"
  - `validatedAt`: Timestamp (Long)
  - `requesterManager`: Assigned manager (if not provided, defaults to "default-manager")
- Error handling:
  - Throws `BpmnError("VALIDATION_ERROR")` for missing or invalid data
  - Throws `BpmnError("AMOUNT_EXCEEDED")` if amount > $50,000

**ProcessPaymentDelegate** (`io.orqueio.bpm.example.tomcat11.ProcessPaymentDelegate`):
- **Purpose:** Processes reimbursement payment and calculates fees
- Input variables:
  - `requester` (String): Payee
  - `amount` (Double): Expense amount
  - `category` (String): Expense category
  - `description` (String): Expense description
- Output variables:
  - `transactionId` (String): Unique transaction ID (format: TXN-XXXXXXXX)
  - `processingFee` (Double): 1% of expense amount
  - `totalAmount` (Double): Expense amount + processing fee
  - `paymentStatus`: "COMPLETED"
  - `processedAt`: Timestamp (Long)
- Features:
  - Generates unique transaction IDs
  - Calculates 1% processing fee
  - Simulates 500ms payment gateway delay

**SendNotificationDelegate** (`io.orqueio.bpm.example.tomcat11.SendNotificationDelegate`):
- **Purpose:** Sends email notifications for various process events
- Automatically detects notification type based on current activity:
  - `Task_NotifyValidationError`: Sends validation error notification
  - `Task_NotifyRejection`: Sends rejection notification with approver comments
  - `Task_SendConfirmation`: Sends approval confirmation with payment details
- Input variables (varies by notification type):
  - `requester` (String): Email recipient
  - `description`, `amount`, `category`: Expense details
  - `approverComments` (String, optional): Comments from approver
  - `transactionId`, `totalAmount`, `processingFee`: Payment details (for confirmation)
- Output variables:
  - `notificationSent` (Boolean): true
  - `notificationType` (String): "VALIDATION_ERROR", "REJECTION", or "APPROVAL_CONFIRMATION"
  - `notificationSentAt`: Timestamp (Long)
- Features:
  - Professional email formatting with detailed information
  - Context-aware messaging based on process state
  - Includes relevant details (amounts, transaction IDs, comments)

**Process Variables:**

_Input Variables (provided at process start):_
- `description` (String): Expense description
- `amount` (Double): Expense amount in dollars
- `category` (String): Expense category (Travel, Equipment, Office Supplies, etc.)
- `requester` (String): Employee username/email
- `requesterManager` (String, optional): Manager username

_Validation Variables:_
- `validationStatus` (String): "APPROVED" if validation succeeds
- `validatedAt` (Long): Validation timestamp

_Approval Variables (set by user tasks):_
- `approvalDecision` (String): "approved" or "rejected"
- `approverComments` (String): Comments from approver

_Payment Variables:_
- `transactionId` (String): Payment transaction ID (format: TXN-XXXXXXXX)
- `processingFee` (Double): Payment processing fee (1% of amount)
- `totalAmount` (Double): Total amount including processing fee
- `paymentStatus` (String): "COMPLETED"
- `processedAt` (Long): Payment processing timestamp

_Notification Variables:_
- `notificationSent` (Boolean): true when notification is sent
- `notificationType` (String): "VALIDATION_ERROR", "REJECTION", or "APPROVAL_CONFIRMATION"
- `notificationSentAt` (Long): Notification timestamp

## References

- OrqueIO Docs: https://orqueio.io/docs (or internal repo docs)
- Apache Tomcat 11: https://tomcat.apache.org/tomcat-11.0-doc/
- Jakarta EE 11 Spec: https://jakarta.ee/specifications/platform/11/
- Camunda 7 Tomcat Integration: https://docs.camunda.org/manual/latest/installation/full/tomcat/ (OrqueIO follows similar patterns)

## License

Apache License 2.0 (inherited from OrqueIO BPM and Camunda).
