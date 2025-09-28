Orqueio Platform examples
====================

> Looking for the "invoice" example contained in the distribution?  You can find it here: https://github.com/orqueio/orqueio-bpm-platform/tree/master/examples/invoice

Orqueio Platform examples is a collection of focused usage examples for the [Orqueio Platform](https://github.com/orqueio/orqueio-bpm-platform), intended to get you started quickly. The sources on the master branch work with the current Orqueio release. Follow the links below to browse the examples for the Orqueio version you use:

## Overview

* [Getting Started with Orqueio Platform](#getting-started-with-orqueio-platform)
* [BPMN 2.0 & Process Implementation](#bpmn-20--process-implementation-examples)
* [Deployment & Project Setup](#deployment--project-setup-examples)
* [Process Engine Plugin](#process-engine-plugin-examples)
* [Bpmn 2.0 Model API](#bpmn-20-model-api-examples)
* [Cmmn 1.1 Model API Examples](#cmmn-11-model-api-examples)
* [Cockpit](#cockpit-examples)
* [Tasklist](#tasklist-examples)
* [Multi-Tenancy](#multi-tenancy-examples)
* [Spin](#spin-examples)
* [DMN](#dmn-examples)
* [Process Instance Migration](#process-instance-migration-examples)
* [SDK-JS Examples](#sdk-js-examples)
* [Authentication](#authentication)
* [Spring Boot Starter examples](#spring-boot-starter-examples)
* [Quarkus Extension Examples](#quarkus-extension-examples)
* [External Task Client](#external-task-client)
* [External Task Client Spring](#external-task-client-spring)
* [External Task Client Spring Boot](#external-task-client-spring-boot)
* [Testing](#testing)

### Getting Started with Orqueio Platform

| Name                                                                                          | Container          |
|-----------------------------------------------------------------------------------------------|--------------------|
| [Simple Process Applications](https://docs.orqueio.io/)                                       | All                |
| [Orqueio Platform and the Spring Framework](https://docs.orqueio.io/get-started/spring/) [^1] | Tomcat             |
| [Process Applications with JavaEE 6](https://docs.orqueio.io/get-started/javaee6/) [^1]       | JavaEE Containers  |

### BPMN 2.0 & Process Implementation Examples

| Name                                                                                                             | Container         | Keywords                  |
|------------------------------------------------------------------------------------------------------------------|-------------------|---------------------------|
| [Service Task REST HTTP](/servicetask/rest-service)                                                              | Unit Test         | Rest Scripting, classless |
| [Service Task SOAP HTTP](/servicetask/soap-service) [^1]                                                         | Unit Test         | SOAP Scripting, classless |
| [Service Task SOAP CXF HTTP](/servicetask/soap-cxf-service) [^1]                                                 | Unit Test         | SOAP, CXF, Spring, Spin   |
| [Service Invocation Synchronous](/servicetask/service-invocation-synchronous)                                    | Unit Test         | Java Delegate, Sync       |
| [Service Invocation Asynchronous](/servicetask/service-invocation-asynchronous)                                  | Unit Test         | Signal, Async             |
| [User Task Assignment Email](/usertask/task-assignment-email) [^1][^2]                                           | All               | Email, Usertask           |
| [User Task Form Embedded](/usertask/task-form-embedded) [^2]                                                     | All               | Html, Form, Usertask      |
| [User Task Form Embedded - Serialized Java Object](/usertask/task-form-embedded-serialized-java-object) [^1][^2] | All               | Html, Form, Usertask      |
| [User Task Form Embedded - JSON](/usertask/task-form-embedded-json-variables) [^2]                               | All               | Html, Form, Usertask      |
| [User Task Form Embedded - Bpmn Elements](/usertask/task-form-embedded-bpmn-events) [^2]                         | All               | Html, Form, Usertask      |
| [User Task Form Embedded - React](/usertask/task-form-embedded-react) [^2]                                       | All               | Html, Form, Usertask      |
| [User Task Form - Orqueio Forms](/usertask/task-orqueio-forms) [^2]                                              | All               | Html, Form, Usertask      |
| [User Task Form Generated](/usertask/task-form-generated) [^1][^2]                                               | All               | Html, Form, Usertask      |
| [User Task Form JSF](/usertask/task-form-external-jsf) [^1][^2]                                                  | JavaEE Containers | JSF, Form, Usertask       |
| [Script Task XSLT](/scripttask/xslt-scripttask)                                                                  | Unit Test         | XSLT Scripttask           |
| [Script Task XQuery](/scripttask/xquery-scripttask) [^1]                                                         | Unit Test         | XQuery Scripttask         |
| [Start Event - Message](/startevent/message-start)                                                               | Unit Test         | Message Start Event       |
| [Start Process - SOAP CXF](/startevent/soap-cxf-server-start) [^1]                                               | War               | SOAP, CXF, Spring         |

### Deployment & Project Setup Examples

| Name                                                                                            | Container                |  Keywords                 |
|-------------------------------------------------------------------------------------------------|--------------------------|---------------------------|
| [Process Application - Servlet](deployment/servlet-pa)                                          | Jakarta EE Containers    | War, Servlet              |
| [Process Application - EJB](deployment/ejb-pa) [^1]                                             | JavaEE Containers        | Ejb, War                  |
| [Process Application - Jakarta EJB](deployment/ejb-pa-jakarta)                                  | Jakarta EE Containers    | Ejb, War                  |
| [Process Application - Spring 6 Servlet - WildFly](deployment/spring-servlet-pa-wildfly)        | WildFly                  | Spring, Servlet, War      |
| [Process Application - Spring 6 Servlet - Embedded Tomcat](deployment/spring-servlet-pa-tomcat) | Tomcat 10                | Spring, Servlet, War      |
| [Embedded Spring 6 with embedded REST](deployment/embedded-spring-rest)                         | vanilla Apache Tomcat 10 | Spring, Rest, Embedded    |
| [Plain Spring 6 Web application - WildFly](deployment/spring-wildfly-non-pa)                    | WildFly                  | Spring, Jndi, War         |
| [Process Application - Spring Boot](deployment/spring-boot) [^1]                                | Spring Boot              | Spring                    |

### Process Engine Plugin Examples

| Name                                                                                            | Container            |  Keywords                                   |
|-------------------------------------------------------------------------------------------------|----------------------|---------------------------------------------|
| [BPMN Parse Listener](process-engine-plugin/bpmn-parse-listener)                                | Unit Test            | Process Engine Plugin, Bpmn Parse Listener  |
| [BPMN Parse Listener on User Task](process-engine-plugin/bpmn-parse-listener-on-user-task) [^1] | Unit Test            | Process Engine Plugin, Bpmn Parse Listener  |
| [Command Interceptor - Blocking](process-engine-plugin/command-interceptor-blocking)            | Unit Test            | Maintenance, Interceptor, Configuration     |
| [Custom History Level](process-engine-plugin/custom-history-level)                              | Unit Test            | Process Engine Plugin, Custom History Level |
| [Failed Job Retry Profile](process-engine-plugin/failed-job-retry-profile)                      | Unit Test            | Process Engine Plugin, Failed Job Retry     |

### Bpmn 2.0 Model API Examples

| Name                                                                 | Container            | Keywords                  |
|----------------------------------------------------------------------|----------------------|---------------------------|
| [Generate JSF forms](/bpmn-model-api/generate-jsf-form) [^1]         | JavaEE Containers    | JSF, Usertask             |
| [Generate BPMN process](/bpmn-model-api/generate-process-fluent-api) | Unit Test            | Fluent API                |
| [Parse BPMN model](/bpmn-model-api/parse-bpmn)                       | Unit Test            | BPMN                      |

### Cmmn 1.1 Model API Examples

| Name                                                                                             | Container            | Keywords                  |
|--------------------------------------------------------------------------------------------------|----------------------|---------------------------|
| [Strongly-typed Access to Custom Extension Elements](/cmmn-model-api/typed-custom-elements) [^1] | Unit Test            | CMMN, TransformListener   |


### Multi-Tenancy Examples

| Name                                                                                                                       | Container | Keywords      |
|----------------------------------------------------------------------------------------------------------------------------|-----------|---------------|
| [Multi-Tenancy with Database Schema Isolation](multi-tenancy/schema-isolation)                                             | Wildfly   | Multi-Tenancy |
| [Multi-Tenancy with Tenant Identifiers for Embedded Process Engine](multi-tenancy/tenant-identifier-embedded)              | Unit Test | Multi-Tenancy |
| [Multi-Tenancy with Tenant Identifiers for Shared Process Engine](multi-tenancy/tenant-identifier-shared)                  | All       | Multi-Tenancy |
| [Multi-Tenancy with Tenant Identifiers and Shared Process Definitions](multi-tenancy/tenant-identifier-shared-definitions) | Unit Test | Multi-Tenancy |

### Spin Examples

| Name                                                                                                                                           | Container          | Keywords                           |
|------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|------------------------------------|
| [Global Data Format Configuration to Customize JSON serialization](spin/dataformat-configuration-global)                                       | Unit Test          | Spin, Configuration                |
| [Process-Application-Specific Data Format Configuration to Customize JSON serialization](spin/dataformat-configuration-in-process-application) | Application Server | Spin, Configuration, Shared Engine |

### DMN Examples

| Name                                                                                  | Container | Keywords        |
|---------------------------------------------------------------------------------------|-----------|-----------------|
| [Embed Decision Engine - Dish Decision Maker](dmn-engine/dmn-engine-java-main-method) | Jar       | DMN, Embed      |
| [Decision Requirements Graph(DRG) Example](dmn-engine/dmn-engine-drg)                 | Jar       | DMN, DRG, Embed |


### Spring Boot Starter examples

| Name                                                              | Container                                              | Keywords                              |
|-------------------------------------------------------------------|--------------------------------------------------------|---------------------------------------|
| [Plain Orqueio Engine](spring-boot-starter/example-simple)        | Jar                                                    | Spring Boot Starter                   |
| [Webapps](spring-boot-starter/example-webapp)                     | Spring boot with embedded engine and Webapps           | Spring Boot Starter, Webapps          |
| [Webapps EE](spring-boot-starter/example-webapp-ee)               | Spring boot with embedded engine and Webapps           | Spring Boot Starter, Webapps          |
| [REST API](spring-boot-starter/example-web)                       | Spring boot with embedded engine and Webapps           | Spring Boot Starter, REST API         |
| [Twitter](spring-boot-starter/example-twitter)                    | Spring boot with embedded engine and Webapps           | Spring Boot Starter, Webapps, Twitter |
| [Orqueio Invoice Example](spring-boot-starter/example-invoice)    | Spring boot with embedded engine, Webapps and Rest API | Spring Boot Starter, REST API         |
| [Autodeployment](spring-boot-starter/example-autodeployment) [^1] | Spring boot with embedded engine and Webapps           | Spring Boot Starter                   |
| [REST API DMN](spring-boot-starter/example-dmn-rest) [^1]         | Spring boot with embedded engine and Webapps           | Spring Boot Starter, REST API         |

### Quarkus Extension Examples

| Name                                                              | Container                                                                  | Keywords                |
|-------------------------------------------------------------------|----------------------------------------------------------------------------|-------------------------|
| [Datasource Example](quarkus-extension/datasource-example)        | Uber-Jar                                                                   |  Quarkus Extension      |
| [Spin Plugin Example](quarkus-extension/spin-plugin-example)      | Uber-Jar                                                                   |  Quarkus Extension      |
| [Simple REST Example](quarkus-extension/simple-rest-example) [^1] | Uber-Jar                                                                   |  Quarkus Extension      |

### External Task Client

| Name                                                                                                                         | Environment                         | Keywords                          |
|------------------------------------------------------------------------------------------------------------------------------|-------------------------------------|-----------------------------------|
| [Order Handling - Java](./clients/java/order-handling)                                                                       | Java External Task Client           | External Task Client, Servicetask |
| [Loan Granting - Java](./clients/java/loan-granting) [^1]                                                                    | Java External Task Client           | External Task Client, Servicetask |
| [Dataformat - Java](./clients/java/dataformat) [^1]                                                                          | Java External Task Client           | External Task Client, Servicetask |
| [Loan Granting - JavaScript](https://github.com/orqueio/orqueio-external-task-client-js/tree/master/examples/granting-loans) | JavaScript External Task Client     | External Task Client, Servicetask |
| [Order Handling - JavaScript](https://github.com/orqueio/orqueio-external-task-client-js/tree/master/examples/order)         | JavaScript External Task Client     | External Task Client, Servicetask |
 
### External Task Client Spring

| Name                                                                                           | Keywords                          |
|------------------------------------------------------------------------------------------------|-----------------------------------|
| [Loan Granting Example](./spring-boot-starter/external-task-client/loan-granting-spring) [^1]  | External Task Client, Servicetask |

### External Task Client Spring Boot

| Name                                                                                                                      | Container                                           | Keywords                                               |
|---------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------|--------------------------------------------------------|
| [Loan Granting w/ REST API & Webapp Example](./spring-boot-starter/external-task-client/loan-granting-spring-boot-webapp) | Spring Boot with embedded Client, REST API & Webapp | External Task Client, Servicetask, Spring Boot Starter |
| [Order Handling Example](./spring-boot-starter/external-task-client/order-handling-spring-boot) [^1]                      | Spring Boot with embedded Client                    | External Task Client, Servicetask, Spring Boot Starter |
| [Request Interceptor Example](./spring-boot-starter/external-task-client/request-interceptor-spring-boot) [^1]            | Spring Boot with embedded Client                    | External Task Client, Servicetask, Spring Boot Starter |

### Testing

| Name                                                                                                 | Keywords                 |
|------------------------------------------------------------------------------------------------------|--------------------------|
| [Assert](testing/assert/job-announcement-publication-process)                                        | Testing, Junit 4, Assert |
| [Assert and JUnit 5](testing/junit5/orqueio-bpm-junit-assert/)                                       | Testing, Junit 5, Assert |
| [Assert and Junit 5: configure a custom process engine](testing/junit5/orqueio-bpm-junit-use-engine) | Testing, Junit 5, Assert |

## Contributing

Have a look at our [contribution guide](https://github.com/orqueio/orqueio-bpm-platform/blob/master/CONTRIBUTING.md) for how to contribute to this repository.

## License
The source files in this repository are made available under the [Apache License Version 2.0](./LICENSE).

