# Descartes: A Mutation Engine for PIT
[![Build Status](https://travis-ci.org/STAMP-project/pitest-descartes.svg?branch=master)](https://travis-ci.org/STAMP-project/pitest-descartes)
[![Coverage Status](https://img.shields.io/coveralls/github/STAMP-project/pitest-descartes/master.svg)](https://coveralls.io/github/STAMP-project/pitest-descartes?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.stamp-project/descartes/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/eu.stamp-project/descartes)

## What is Descartes?
Descartes evaluates the capability of your test suite to detect bugs using extreme mutation testing.

Descartes is a mutation engine plugin for [PIT](http://pitest.org) which implements extreme mutation operators as proposed in the paper [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944).  

## Quick start with Maven

To target a Maven project, PIT has to be configured and Descartes should be declared as a dependency and as the mutation engine to use.
In the `pom.xml` file include the following:

``` xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.4.0</version>
  <configuration>
    <mutationEngine>descartes</mutationEngine>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>eu.stamp-project</groupId>
      <artifactId>descartes</artifactId>
      <version>1.2</version>
    </dependency>
  </dependencies>
</plugin>
```

Then, execute the regular mutation coverage goal in the folder of the project under test:

```
cd my-project-under-test
mvn clean package # ensures clean state
mvn org.pitest:pitest-maven:mutationCoverage -DmutationEngine=descartes
```

For more information and other options, see section ["Running Descartes on your project"](#running-descartes-on-your-project).

## Table of contents
  - [How does Descartes work?](#how-does-descartes-work)
  - [Descartes Output](#descartes-output)
  - [Running Descartes on your project](#running-descartes-on-your-project)
  - [Releases](#releases)
  - [More...](#more)

## How does Descartes work?
### Mutation testing
Mutation testing allows you to verify if your test suites can detect possible bugs.
Mutation testing does it by introducing small changes or faults into the original program. These modified versions are called **mutants**.
A good test suite should able to *kill* or detect a mutant. [Read more](https://en.wikipedia.org/wiki/Mutation_testing).
Traditional mutation testing works at the instruction level, e.g., replacing ">" by "<=", so the number of generated mutants is huge,
as the time required to check the entire test suite.
That's why the authors of [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944) proposed
an *Extreme Mutation* strategy, which works at the method level.

### Extreme Mutation Testing
In Extreme Mutation testing, the whole logic of a method under test is eliminated.
All statements in a `void` method are removed. In other case the body is replaced by a return statement. With this approach,
a smaller number of mutants is generated. Code from the authors can be found in [their GitHub repository](https://github.com/cqse/test-analyzer).

The goal of Descartes is to bring an effective implementation of this kind of mutation operator into the world of PIT and
check its performance in real world projects.

### Mutation operators
The goal of *extreme mutation operators* is to replace the body of a method by one simple return instruction
or just remove all instructions if is possible. The tool supports the following mutation operators:
#### `void` mutation operator
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

#### `null` mutation operator
This operator accepts a method with a reference return type and replaces all instructions
with `return null`. For example, using the following class as input:
``` java
class A {
    public A clone() {
        return new A();
    }
}
```
this operator will generate:

``` java
class A {
    public A clone() {
        return null;
    }
}
```
#### `empty` mutation operator

This is a special operator which targets methods that return arrays. It replaces the entire
body with a `return` statement that produces an empty array of the corresponding type.
For example, the following class:

``` java
class A {
  public int[] getRange(int count) {
    int[] result = new int[count];
    for(int i=0; i < count; i++) {
      result[i] = i;
    }
    return result;
  }
}
```
will become:

``` java
class A {
  public int[] getRange(int count) {
    return new int[];
  }
}
```

#### Constant mutation operator
This operator accepts any method with primitive or `String` return type. It replaces the method body
with a single instruction returning a defined constant.
For example, if the integer constant `3` is specified, then for the following class:

``` java
class A {
    int field;

    public int getAbsField() {
        if(field >= 0)
            return field;
        return -field;
    }
```
this operator will generate:

``` java
class A {
    int field;

    public int getAbsField() {
        return 3;
    }
}
```

### Stop Methods

Descartes avoids some methods that are generally not interesting and may
introduce false positives such as simple getters, simple setters, empty
void methods or methods returning constant values, delegation patterns as well as deprecated and compiler generated methods. Those methods are automatically detected by inspecting their code.
A complete list of examples can be found [here](src/test/java/eu/stamp_project/mutationtest/test/StopMethods.java).
The exclusion of stop methods can be configured. For more details see section: ["Running Descartes on your project"](#running-descartes-on-your-project).

## Descartes Output

PIT reporting extensions work with Descartes and include `XML`, `CSV` and `HTML` format. The `HTML` format is rather convinient since it also shows the line coverage.
Descartes also provides two new reporting extensions:
  - a general reporting extension supporting `JSON` files. It works also with **Gregor**, the default mutation engine for PIT. To use just set `JSON` as report format for PIT.
  - a reporting extension designed for Descartes that generates a JSON file with information about pseudo and partially tested methods. To use just set `METHOD` as report format for PIT.
  - Descartes can generate a human readable report containing only the list of methods with testing issues by using the `ISSUES` format.

## Running Descartes on your project

Stable releases of Descartes are available from Maven Central.

### Installing and building from source

In a terminal clone the repository:
```
git clone https://github.com/STAMP-project/pitest-descartes.git
```
switch to the cloned folder:
```
cd  pitest-descartes
```
and install Descartes using the regular [Apache Maven](https://maven.apache.org) commands:
```
mvn install
```
After installing the package, PIT should be able to find the Descartes mutation engine.


### Usage
Descartes is a plugin for PIT so they have to be used together.
PIT integrates with majors test and build tools such as [Maven](https://maven.apache.org),
[Ant](http://apache.ant.org) and [Gradle](https://gradle.org).


### Maven
Then, configure PIT for the project and specify `descartes` as the engine inside a `mutationEngine` tag in the `pom.xml` file.

``` xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.4.0</version>
  <configuration>
    <mutationEngine>descartes</mutationEngine>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>eu.stamp-project</groupId>
      <artifactId>descartes</artifactId>
      <version>1.2</version>
    </dependency>
  </dependencies>
</plugin>
```
With PIT and Descartes configured, just run the regular mutation coverage goal in the folder of the project under test:

```
cd my-project-under-test
mvn clean package # ensures clean state
mvn org.pitest:pitest-maven:mutationCoverage -DmutationEngine=descartes
```
### Specifying operators

The operators to be used must be specified in the `pom.xml`. Each operator identifier should be added
to the `mutators` element inside the `configuration` element. `void` and `null` operators are
identified by `void` and `null` respectively.
For the constant mutation operator, the values can be specified using the regular literal notation
used in a Java program. For example `true`, `1`, `2L`, `3.0f`, `4.0`, `'a'`, `"literal"`,
represent `boolean`, `int`, `long`, `float`, `double`, `char`, `string` constants.
Negative values and binary, octal and hexadecimal bases for integer constants are also supported
as stated by the [language specification](https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10).
In order to specify a `byte` or `short` value, a cast-like notation can be used: `(short) -1`, `(byte)0x1A`.

The following configuration:
``` xml
<mutators>
    <mutator>void</mutator>
    <mutator>4</mutator>
    <mutator>"some string"</mutator>
    <mutator>false</mutator>
</mutators>
```
will instruct the tool to use the `void` operator and the constant operator will replace the body of
every `int` returning method with `return 4;` and will use `"some string"` and `false` for every `string` and `boolean` method.
If no operator is specified, the tool will use `void` and `null` by default. Which is equivalent to:
``` xml
<mutators>
    <mutator>void</mutator>
    <mutator>null</mutator>
</mutators>
```

If no operator is specified Descartes will use the following configuration:

``` xml
<mutators>
  <mutator>void</mutator>
  <mutator>null</mutator>
  <mutator>true</mutator>
  <mutator>false</mutator>
  <mutator>empty</mutator>
  <mutator>0</mutator>
  <mutator>1</mutator>
  <mutator>(byte)0</mutator>
  <mutator>(byte)1</mutator>
  <mutator>(short)0</mutator>
  <mutator>(short)1</mutator>
  <mutator>0L</mutator>
  <mutator>1L</mutator>
  <mutator>0.0</mutator>
  <mutator>1.0</mutator>
  <mutator>0.0f</mutator>
  <mutator>1.0f</mutator>
  <mutator>'\40'</mutator>
  <mutator>'A'</mutator>
  <mutator>""</mutator>
  <mutator>"A"</mutator>
</mutators>

```

All the goals defined by the `pitest-maven` plugin should run in the same way without any
issues, see <http://pitest.org/quickstart/maven/>.

### Configuring stop methods

To configure the stop methods under consideration Descartes provide a `STOP_METHODS` [feature](http://pitest.org/quickstart/advanced/#mutation-interceptor").
This feature is enabled by default. The parameter `exclude` can be used to prevent certain methods to be treated as stop methods and bring them back to the analysis. This parameter can take any of the following values:


|`exclude`     | Method description                                                                     | Example                                                                |
|--------------|----------------------------------------------------------------------------------------|------------------------------------------------------------------------|
| `empty`      | `void` methods with no instruction.                                                    | `public void m() {}`                                                   |
| `enum`       | Methods generated by the compiler to support enum types (`values` and `valueOf`).      |                                                                        |
| `to_string`  | `toString` methods.                                                                    |                                                                        |
| `hash_code`  | `hashCode` methods.                                                                    |                                                                        |
| `deprecated` | Methods annotated with `@Deprecated` or belonging to a class with the same annotation. | `@Deprecated public void m() {...}`                                    |  
| `synthetic`  | Methods generated by the compiler.                                                     |                                                                        |
| `getter`     | Simple getters.                                                                        | `public int getAge() { return this.age; }`                             |
| `setter`     | Simple setters.                                                                        | `public void setAge(int value) { this.age = value; }`                  |
| `constant`   | Methods returning a literal constant.                                                  | `public double getPI() { return 3.14; }`                               |
| `delegate`   | Methods implementing simple delegation.                                                | `public int sum(int[] a, int i, int j) {return this.adder(a, i, j); }` |
| `clinit`     | Static class initializers.                                                             |                                                                        |

So, for example, if we don't want to exclude deprecated methods and mutate them the following snippet should be added under the `configuration` element:

``` xml
<features>
  <feature>
  <!-- This will allow descartes to mutate deprecated methods -->
    +STOP_METHODS(except[deprecated])
  </feature>
</features>
```

More than one group can be excluded at the same time:

``` xml
<features>
  <feature>
  <!-- This will allow descartes to mutate toString and enum generated methods -->
    +STOP_METHODS(except[to_string] except[enum])
  </feature>
</features>
```

The feature can be completely disabled:

``` xml
<features>
  <feature>
  <!--No method is considered as a stop method and therefore all of them will be mutated -->
    -STOP_METHODS()
  </feature>
</features>
```

### Configuring reports

As said before, there are several reporting options provided by Descartes:
- `JSON` for a general mutation testing report using that file format. It can be used with **Gregor**.
- `METHODS` that produces a `methods.json` file with the list of all methods analyzed and categorized according to the mutation testing result.
- `ISSUES` a human readable report containing only the methods with testing issues.

They can be configured and combined as regular PIT report formats:

``` xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.4.0</version>
  <configuration>
    <outputFormats>
      <value>JSON</value>
      <value>METHODS</value>
      <value>ISSUES</value>
    </outputFormats>
  </configuration>
    <dependencies>
    <dependency>
      <groupId>eu.stamp-project</groupId>
      <artifactId>descartes</artifactId>
      <version>1.2</version>
    </dependency>
  </dependencies>
</plugin>
```

### Gradle
Follow the [instructions](http://gradle-pitest-plugin.solidsoft.info/) to set up PIT
for a project that uses [Gradle](https://gradle.org/).
In the `build.gradle` file add the local Maven repository to the `buildscript` block and set
a `pitest` configuration element inside the same block. In the `dependencies` block put the artifact information:
```
pitest 'eu.stamp-project:descartes:1.2'
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
    classpath 'info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.1.9'
    pitest 'eu.stamp-project:descartes:1.2'
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
### Running from the command line
Descartes can be used when invoking PIT from the command line. To do this, 
follow [the instructions](http://pitest.org/quickstart/commandline/) for running PIT,
include Descartes in the classpath specification and add `--mutationEngine=descartes`.

### Maven repository
Compiled modules are available from [a custom Maven repository](https://stamp-project.github.io/stamp-maven-repository/).
Detailed instructions can be found [here](https://github.com/STAMP-project/stamp-maven-repository).

## Releases
* [Descartes 1.2](https://github.com/STAMP-project/pitest-descartes/releases/tag/descartes-1.2)
* [Descartes 1.1](https://github.com/STAMP-project/pitest-descartes/releases/tag/descartes-1.1)
* [Stable release candidate 0.2](https://github.com/STAMP-project/pitest-descartes/releases/tag/0.2-D1.2)


## More...

### Performance

A comparison on the number of mutants created and execution time between Descartes and Gregor, the default mutation engine for PITest is available [here](docs/performance-comparison.md).

### External Links

Articles mentioning Descartes:

* [Mutation testing with PIT and Descartes](http://massol.myxwiki.org/xwiki/bin/view/Blog/MutationTestingDescartes)
* [Controlling Test Quality](http://massol.myxwiki.org/xwiki/bin/view/Blog/ControllingTestQuality)

### License
Descartes is published under LGPL-3.0 (see [LICENSE.md](LICENSE.md) for further details).

### Funding

Descartes is partially funded by research project STAMP (European Commission - H2020)
![STAMP - European Commission - H2020](docs/logo_readme_md.png)
