# MApp
A simple Micronaut application demonstrating a cloud native Microservices architecture for journal logging, audit logging, metrics collection, and tracing.

## Micronaut 4.2.1 Documentation
- [User Guide](https://docs.micronaut.io/4.2.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.2.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.2.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---
- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)

## Feature micronaut-aot documentation
- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)

## Feature serialization-jackson documentation
- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)

## Feature ksp documentation
- [Micronaut Kotlin Symbol Processing (KSP) documentation](https://docs.micronaut.io/latest/guide/#kotlin)
- [Kotlin Symbol Processing API](https://kotlinlang.org/docs/ksp-overview.html)

## Test levels aka the testing pyramid
*Unit tests* aim to verify the smallest unit of code; in Java this unit is a method. Unit tests usually do not interact with other parts of the system e.g., the file system or the Terracotta store; interactions with other parts of the system shall be cut off with the help of Stubs or Mocks.

*[Integration tests](tests/README.md)* verifies that multiple classes or sub-systems work together as a whole. The code under test may reach out to external sub-systems.

*Functional tests* are used to test the system from the end user’s perspective.
