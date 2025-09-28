# Executing Orqueio Platform Assert tests

This project demonstrates how to setup a minimal project to run Orqueio Platform Assert tests

## Prerequisites
* Java 17/21

## How to run it

1. Checkout the project with Git
2. Read and run the [unit tests][1]

### Running the test with maven

In order to run the testsuite with maven you can use:

```
mvn clean test
```

## Further reading
If you want to read more about [Orqueio Platform Assert][assert], go to the [testing user guide](https://docs.orqueio.io/manual/7.23/user-guide/testing/) in the Orqueio docs.


[assert]: https://github.com/orqueio/orqueio-bpm-platform/tree/master/test-utils/assert
[1]: src/test/java/io/orqueio/bpm/engine/test/assertions/examples
