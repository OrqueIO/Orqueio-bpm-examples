# BPMN parsing using the BPMN model API

This example demonstrates how to use the [Orqueio BPMN model API][bpmn-model] to parse
a BPMN process. It shows how simple you can access attributes, child elements and
extension elements. Also it illustrates how to use references between model elements
to traverse the model.

Please have a look at the source code and comments of the [unit test case][test-case] to get
an impression about this feature of the [Orqueio BPMN model API][bpmn-model].

Also be aware that you can use:
* [Orqueio XML model API][xml-model] to process XML in a generic way
* [Orqueio CMMN model API][cmmn-model] to handle CMMN models
* [Orqueio DMN model API][dmn-model] to handle DMN models

## How to use it?

1. Checkout the project with Git
2. Read and run the [unit test case][test-case]

[bpmn-model]: https://github.com/orqueio/Orqueio/tree/master/model-api/bpmn-model
[xml-model]: https://github.com/orqueio/Orqueio/tree/master/model-api/xml-model
[cmmn-model]: https://github.com/orqueio/Orqueio/tree/master/model-api/cmmn-model
[dmn-model]: https://github.com/orqueio/Orqueio/tree/master/model-api/dmn-model
[test-case]: src/test/java/org/orqueio/bpm/example/modelapi/ParseBpmnTest.java