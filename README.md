# Descartes Mutation Engine for PIT
A mutation engine plugin for [PIT](http://pitest.org) which implements some of the mutation operators proposed in [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944).

## Mutation Testing
Unit test suites need to be verified to see if they can detect possible bugs. Mutation testing does it by introducing small changes or faults into the original program. These modified versions are called **mutants**. A good test suite should able to *kill* or detect a mutant. [Read more](https://en.wikipedia.org/wiki/Mutation_testing).

## PIT
[PIT](http://pitest.org) is a mutation testing system for Java. It is actively developed, scalable and targets real world projects. It also provides a framework to extend its core functionality using plugins. PIT integrates with majors test and build tools such as [Maven](https://maven.apache.org), [Ant](http://apache.ant.org) and [Gradle](https://gradle.org). A list of built-in mutation operators can be found in the [tool's web page](http://pitest.org/quickstart/mutators/).

## Descartes
The authors of [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944) proposed an *extreme mutation* strategy in which the whole logic of a method under test is eliminated. All statements in a void method are removed. In other case the body is replaced by a return statement. With this approach, a smaller number of mutants is generated. Code from the authors can be found in [their GitHub repository](https://github.com/cqse/test-analyzer).

The goal of Descartes is to bring an effective implementation of this kind of mutation operator into the world of PIT and check its performance in real world projects.

### Mutation operators
The goal of *extreme mutation operators* is to replace the body of a method by one simple return instruction or just remove all instructions if is possible. Right now, only a `void` mutation operator is included in the tool.

### `void` mutation operator
This operator accepts a `void` method and removes all the instructions on its body. For example, with the following class as input:

``` java
class A {

  int field = 3;

  public void Method(int inc) {
    field += 3;
  }

}
```
the mutation operator will generate:

``` java
class A {

  int field = 3;

  public void Method(int inc) { }

}
```
## Compiling the code

In a terminal clone the repository and switch to the cloned folder
```
git clone https://github.com/STAMP-project/pitest-descartes.git
cd  pitest-descartes
```
the code can be compiled and tested using the usual [Apache Maven](https://maven.apache.org) commands:
```
mvn compile
mvn test
```

## Usage

Descartes is a plugin for PIT so they have to be used together. The easiest setup is to install the compiled package in the local Apache Maven repository. In a terminal switch to the `pitest-descartes` folder as before and run:

```
mvn install
```
After installing the package, PIT should be configured to use Descartes. This step depends on which build system is being used for the project under test.

### Maven
Follow the [instructions](http://pitest.org/quickstart/maven/) to configure PIT for the project. Then specify `descartes` as the engine inside a `mutationEgine` tag in the `pom.xml` file:

```xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.1.11</version>  
  <configuration>
    ...
    <mutationEngine>descartes</mutationEngine>  
    ...
  </configuration>
  ...
</plugin>
```
after this, add the artifact information as a dependency to PIT:
```xml
<dependency>
  <groupId>fr.inria.stamp</groupId>
  <artifactId>descartes</artifactId>
  <version>0.1-SNAPSHOT</version>
</dependency>
```

An example of final configuration could be:

```xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.1.11</version>  
  <configuration>
    <mutationEngine>descartes</mutationEngine>  
  </configuration>
  <dependencies>
    <dependency>
      <groupId>fr.inria.stamp</groupId>
      <artifactId>descartes</artifactId>
      <version>0.1-SNAPSHOT</version>
    </dependency>
  </dependencies>
</plugin>
```
With PIT and Descartes configured, just run the regular mutation coverage goal in the folder of the project under test:

```
mvn org.pitest:pitest-maven:mutationCoverage
```
The mutation engine could be also specified when invoking the goal from the command line. To achieve this just add `-DmutationEngine=descartes` to the invocation line:
```
mvn org.pitest:pitest-maven:mutationCoverage -DmutationEngine=descartes
```
Thi will still require to specify Descartes as a dependency for PIT in the `pom.xml` file.

The rest of the goals defined by the `pitest-maven` plugin should run in the same way without any issues.

### Gradle

Follow the [instructions](http://gradle-pitest-plugin.solidsoft.info/) to set up PIT for a project that uses [Gradle](https://gradle.org/).
In the `build.gradle` file add the local Maven repository to the `buildscript` block and set a `pitest` configuration element inside the same block. In the `dependencies` block put the artifact information:
```
pitest 'fr.inria.stamp:descartes:0.1-SNAPSHOT'
```
then specify `descartes` in the `mutationEngine` option inside the plugin configuration.
An example of the final configuration could be:
```
buildscript {
  repositories {
    mavenCentral()
    mavenLocal()
  }

  configurations.maybeCreate("pitest")

  dependencies {
    classpath 'info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.1.11'
    pitest 'fr.inria.stamp:descartes:0.1-SNAPSHOT'
  }
}

apply plugin: "info.solidsoft.pitest"

pitest {
  mutationEngine = "descartes"
}
```
At last, run the `pitest` task for the project under test.
```
gradle pitest
```
## Running from the command line

Descartes can be used when invoking PIT from the command line. To do this, follow [the instructions](http://pitest.org/quickstart/commandline/) for running PIT, include Descartes in the classpath specification and add `--mutationEngine=descartes`.
