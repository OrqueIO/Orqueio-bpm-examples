# Orqueio Task Forms

This quickstart demonstrates how to use the [Orqueio Forms][5] feature. Orqueio Forms are `.form` files created in the Camunda Modeler and embedded inside the orqueio Tasklist:

![Orqueio Forms Screenshot][1]

# Overview
This example uses Jakarta API. It is compatible with the latest releases of Orqueio Tomcat and WildFly distributions.

## Where are Orqueio Forms added?

Orqueio Forms can be added to the web resources of a web application. As we use maven, they are added to the [src/main/webapp][4] folder of your project.

## How are Orqueio Forms referenced?

Orqueio Forms are referenced using the `camunda:formKey` property of a BPMN `<startEvent>` or a BPMN `<userTask>`:

```xml
<startEvent id="startEvent" camunda:formKey="orqueio-forms:app:start-form.form" name="Invoice Received">
  ...
</startEvent>
```

The attribute can also be set through the properties panel using the Camunda Modeler:

![Orqueio Forms Screenshot Modeler][2]

## How to use it?

1. Checkout the project with Git
2. Build the project with maven
3. Deploy the war file to a Orqueio Platform Runtime distribution
4. Go the the Tasklist and start a process instance for the process named "Orqueio Forms Quickstart"

[1]: docs/screenshot.png
[2]: docs/screenshot-modeler.png
[3]: src/main/webapp/start-form.html
[4]: src/main/webapp
[5]: https://docs.orqueio.io/manual/7.23/user-guide/task-forms/#orqueio-task-forms
