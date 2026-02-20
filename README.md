# OrqueIO Platform Examples

> Looking for the "invoice" example contained in the distribution? You can find it here: https://github.com/orqueio/orqueio/tree/master/examples/invoice

**OrqueIO Platform Examples** is a collection of focused, production-ready examples for the [OrqueIO Platform](https://github.com/orqueio/orqueio) — a powerful BPMN 2.0 process engine built on Camunda 7 with modern enhancements.

These examples demonstrate best practices for process design, testing, deployment, and integration. The sources on the master branch work with the current OrqueIO release.

---

## Table of Contents

- [BPMN 2.0 & Process Implementation](#bpmn-20--process-implementation)
  - [Service Tasks](#service-tasks)
  - [Script Tasks & Events](#script-tasks--events)
- [BPMN 2.0 Model API](#bpmn-20-model-api)
- [DMN (Decision Engine)](#dmn-decision-engine)
- [Spring Boot Starter](#spring-boot-starter)
- [External Task Client](#external-task-client)
  - [JavaScript Examples](#javascript-examples)
  - [Spring Boot Integration](#spring-boot-integration)
- [Testing](#testing)

---

## BPMN 2.0 & Process Implementation

### Service Tasks

| Example | Container | Description |
|---------|-----------|-------------|
| [REST Service Task](service-task/rest-service) | Unit Test | Invoke REST endpoints via HTTP connector without Java class |
| [Synchronous Service Invocation](service-task/service-invocation-synchronous) | Unit Test | JavaDelegate pattern for synchronous service calls |
| [Asynchronous Service Invocation](service-task/service-invocation-asynchronous) | Unit Test | Signal-based async service invocation |

### Script Tasks & Events

| Example | Container | Description |
|---------|-----------|-------------|
| [XSLT Script Task](script-task/xslt-scripttask) | Unit Test | XML transformations with XSLT |
| [Message Start Event](start-event/message-start) | Unit Test | Process instantiation via message events |

---

## BPMN 2.0 Model API

The **BPMN Model API** allows you to create, read, and manipulate BPMN process definitions programmatically — without a modeler or process engine.

| Example | Description |
|---------|-------------|
| [Generate BPMN Process via Fluent API](bpmn-model-api/generate-process-fluent-api) | Create complete BPMN processes in Java code — no XML files |
| [Parse BPMN Model](bpmn-model-api/parse-bpmn) | Read and navigate existing BPMN files programmatically |

---

## DMN (Decision Engine)

**OrqueIO DMN Engine** evaluates DMN 1.3 decision tables independently of the BPMN engine.

| Example | Container | Description |
|---------|-----------|-------------|
| [Dish Decision Maker](dmn-engine/dmn-engine-java-main-method) | JAR | Standalone DMN evaluation with a single decision table |
| [Decision Requirements Graph (DRG)](dmn-engine/dmn-engine-drg) | JAR | Chain multiple DMN decisions (Dish → Beverages) |

---

## Spring Boot Starter

OrqueIO provides a **Spring Boot Starter** for zero-config embedded process engine integration.

| Example | Description |
|---------|-------------|
| [Webapps (Cockpit, Tasklist, Admin)](spring-boot-starter/example-webapp) | Spring Boot with embedded webapps |
| [REST API](spring-boot-starter/example-web) | Embedded REST API server |
| [Twitter Integration](spring-boot-starter/example-twitter) | Social media workflow example |
| [Invoice Process](spring-boot-starter/example-invoice) | Complete invoice approval workflow with REST API |
| [Order Process](spring-boot-starter/example-order-process) | E-commerce order fulfillment workflow |

---

## External Task Client

The **OrqueIO External Task Client** allows you to implement service tasks as external workers that poll the OrqueIO REST API.

### JavaScript Examples

| Example | Description |
|---------|-------------|
| [Loan Granting - JavaScript](https://github.com/orqueio/orqueio-external-task-client-js/tree/master/examples/granting-loans) | Loan approval workflow with JavaScript workers |
| [Order Handling - JavaScript](https://github.com/orqueio/orqueio-external-task-client-js/tree/master/examples/order) | Order processing with JavaScript workers |

### Spring Boot Integration

| Example | Description |
|---------|-------------|
| [Loan Granting with REST API & Webapp](spring-boot-starter/external-task-client/loan-granting-spring-boot-webapp) | Complete external task client app with UI and REST API |

---

## Testing

OrqueIO provides powerful testing utilities for unit and integration tests.

| Example | Framework | Description |
|---------|-----------|-------------|
| [OrqueIO BPM Assert - JUnit 4](testing/assert/job-announcement-publication-process) | JUnit 4 | Fluent assertions with `orqueio-bpm-assert` and Mockito integration |
| [OrqueIO BPM Assert - JUnit 5](testing/junit5/orqueio-bpm-junit-assert) | JUnit 5 | JUnit 5 integration with `ProcessEngineExtension` |
| [Programmatic Process Engine - JUnit 5](testing/junit5/orqueio-bpm-junit-use-engine) | JUnit 5 | Create process engine in code with `useProcessEngine()` |

---

## Contributing

Have a look at our [contribution guide](https://github.com/orqueio/orqueio/blob/master/CONTRIBUTING.md) for how to contribute to this repository.

---

## License

The source files in this repository are made available under the [Apache License Version 2.0](./LICENSE).
