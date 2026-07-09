# OrqueIO Hello World — Quarkus + Spring Boot Webapps

This example shows how to run a hello world BPMN process on **OrqueIO Quarkus** and visualize it using the **OrqueIO Spring Boot webapps** (Cockpit / Tasklist / Admin).

## Architecture

| App | Port | Role |
|-----|------|------|
| Quarkus hello-world-example | **8080** | Process engine + REST API |
| Spring Boot hello-world-webapp | **8090** | Cockpit / Tasklist / Admin |

Each app runs its own embedded OrqueIO engine with the same `hello-world` BPMN process deployed.

## BPMN Process

```
Start → UserTask ("do something", assignee=demo) → ServiceTask ("say hello") → End
```

## Running the Quarkus app (port 8080)

```bash
cd quarkus-extension/hello-world-example
mvn quarkus:dev
```

### REST API

**Start a process instance**
```bash
curl.exe -X POST http://localhost:8080/hello-world/start
# {"processInstanceId":"...","status":"started"}
```

**List active process instances**
```bash
curl.exe http://localhost:8080/hello-world/instances
# [{"id":"...","processDefinitionId":"hello-world:1:..."}]
```

**List pending user tasks**
```bash
curl.exe http://localhost:8080/hello-world/tasks
# [{"id":"...","name":"do something","assignee":"demo","processInstanceId":"..."}]
```

**Complete a user task** (copy `id` from the tasks response)
```bash
curl.exe -X POST http://localhost:8080/hello-world/tasks/<taskId>/complete
# {"taskId":"...","status":"completed"}
```

After completing the task, `SayHelloDelegate` logs `Hello World!` and the process ends.

## Running the Spring Boot webapp (port 8090)

```bash
cd spring-boot-starter/hello-world-webapp
mvn spring-boot:run
```

Open **http://localhost:8090** and log in with `demo` / `demo`.

- **Cockpit** — view active and completed process instances of `hello-world`
- **Tasklist** — claim and complete the `do something` user task
- **Admin** — manage users and groups

## Running the tests

```bash
cd quarkus-extension/hello-world-example
mvn test
```

Two tests run:
- `shouldStartProcessAndPauseAtUserTask` — verifies the process pauses at the user task
- `shouldCompleteProcessAfterUserTask` — completes the task and verifies the process ends
