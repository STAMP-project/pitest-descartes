# Descartes Mutation Engine for PIT
A mutation engine plugin for [PIT](http://pitest.org) which implements some of the mutation operators proposed in [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944).

## Mutation Testing
Unit test suites need to be verified to see if they can detect possible bugs. Mutation testing does it by introducing small changes or faults into the original program. These modified versions are called **mutants**. A good test suite should able to *kill* or detect a mutant. [Read more](https://en.wikipedia.org/wiki/Mutation_testing).

## PIT
[PIT](http://pitest.org) is a mutation testing system for Java. It is actively developed, scalable and targets real world projects. It also provides a framework to extend its core functionality using plugins. PIT integrates with majors test and build tools such as [Maven](https://maven.apache.org), [Ant](http://apache.ant.org) and [Gradle](https://gradle.org). A list of built-in mutation operators can be found in the [tool's web page](http://pitest.org/quickstart/mutators/).

## Descartes
The authors of [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944) proposed an *extreme mutation* strategy in which the whole logic of a method under test is eliminated. All statements in a void method are removed. In other case the body is replaced by a return statement. With this approach, a smaller number of mutants is generated. Code from the authors can be found in [their GitHub repository](https://github.com/cqse/test-analyzer).

The goal of Descartes is to bring an effective implementation of this kind of mutation operator into the world of PIT and check its performance in real world projects.
