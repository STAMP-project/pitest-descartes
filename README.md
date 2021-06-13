# Descartes: A Mutation Engine for PIT

![build-on-push](https://github.com/STAMP-project/pitest-descartes/workflows/build-on-push/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.stamp-project/descartes/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/eu.stamp-project/descartes)

## What is Descartes?
Descartes evaluates the capability of your test suite to detect bugs using extreme mutation testing. It is able to find your worst tested methods.

Descartes is a mutation engine plugin for [PIT](http://pitest.org) which implements extreme mutation operators as proposed in the paper [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944).  

## Quick start with Maven

To use Descartes in a Maven project, add the following plugin configuration to your `pom.xml`:

```xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.6.6</version>
  <configuration>
    <mutationEngine>descartes</mutationEngine>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>eu.stamp-project</groupId>
      <artifactId>descartes</artifactId>
      <version>1.3.1</version>
    </dependency>
  </dependencies>
</plugin>
```

Then, execute the regular mutation coverage goal in the root folder of the project under test:

```
cd my-project-under-test
mvn clean package org.pitest:pitest-maven:mutationCoverage
```

All options from PIT can be used. For more details check the [the PIT documentation](https://pitest.org/quickstart/maven/). 

Check ["Running Descartes on your project"](#running-descartes-on-your-project) for additional Descartes configuration options. 

For Gradle support check [Using Gradle](#using-gradle). 

For quick configuration snippets showing how to use other testing frameworks such as JUnit 5 or TestNG and how to change Descartes' behavior check [How to...](docs/how-to.md). 

If you use a multi-module project take a look at [PitMP](https://github.com/STAMP-project/pitmp-maven-plugin).

## Table of contents
  - [How does Descartes work?](#how-does-descartes-work)
  - [Descartes Output](#descartes-output)
  - [Running Descartes on your project](#running-descartes-on-your-project)
    - [Using Maven](#using-maven)
        - [Specifying operators](#specifying-operators)
        - [Configuring stop methods](#configuring-stop-methods)
        - [Configuring reports](#configuring-reports)
    - [Using Gradle](#using-gradle)
    - [Running from the command line](#running-from-the-command-line)
    - [Installing and building from source](#installing-and-building-from-source)
  - [Releases](https://github.com/STAMP-project/pitest-descartes/releases)
  - [More...](#more)

## How does Descartes work?
### Mutation testing
Mutation testing allows you to verify if your test suite can detect possible bugs.
The technique works by introducing small changes or faults into the original program. These modified versions are called **mutants**.
A good test suite should able to detect or *kill* a mutant. That is, at least one test case should fail when the test suite is executed with the mutant. 
[Read more](https://en.wikipedia.org/wiki/Mutation_testing).
Traditional mutation testing works at the instruction level, e.g., replacing ">" by "<=", so the number of generated mutants is huge, as the time required to check the entire test suite.
That's why the authors of [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944) proposed an *Extreme Mutation* strategy, which works at the method level.

### Extreme Mutation Testing
In Extreme Mutation testing, the whole logic of a method under test is eliminated.
All statements in a `void` method are removed. In other case, the body is replaced by a single return statement. 
This approach generates fewer mutants. Code from the authors can be found in [their GitHub repository](https://github.com/cqse/test-analyzer).

The goal of Descartes is to bring an effective implementation of this kind of mutation operator into the world of PIT and check its performance in real world projects.

### Mutation operators
The goal of *extreme mutation operators* is to replace the body of a method by one simple return instruction
or just remove all instructions if is possible. Descartes supports the following mutation operators:

#### `void` mutation operator
This operator accepts a `void` method and removes all the instructions on its body. For example, with the following class as input:

```Java
class A {

  int field = 3;

  public void Method(int inc) {
    field += 3;
  }

}
```
the mutation operator will generate:

```Java
class A {

  int field = 3;

  public void Method(int inc) { }

}
```

#### `null` mutation operator
This operator accepts a method with a reference return type and replaces all instructions
with `return null`. For example, using the following class as input:
```Java
class A {
    public A clone() {
        return new A();
    }
}
```
this operator will generate:

```Java
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

```Java
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

```Java
class A {
  public int[] getRange(int count) {
    return new int[0];
  }
}
```

#### Constant mutation operator
This operator accepts any method with primitive or `String` return type. It replaces the method body
with a single instruction returning a defined constant.
For example, if the integer constant `3` is specified, then for the following class:

```Java
class A {
    int field;

    public int getAbsField() {
        if(field >= 0)
            return field;
        return -field;
    }
}
```
this operator will generate:

```Java
class A {
    int field;

    public int getAbsField() {
        return 3;
    }
}
```

#### `new` mutation operator

*New in version 1.2.6*

This operator accepts any method whose return type has a constructor with no parameters and belongs to a `java` package.
It replaces the code of the method by a single instruction returning a new instance.

For example:

```Java
class A {
    int field;
    
    public ArrayList range(int end) {
        ArrayList l = new ArrayList();
        for(int i = 0; i < size; i++) {
            A a = new A();
            a.field = i;
            l.add(a);
        }
        return l;
    }
}  
```
 
is transformed to:

```Java
class A {
    int field;
    
    public List range(int end) {
        return new ArrayList();
    }
}  
```

This operator handles the following special cases:

| Return Type  | Replacement  |
|--------------|--------------|
| `Collection` | `ArrayList`  |
| `Iterable`   | `ArrayList`  |
| `List`       | `ArrayList`  |
| `Queue`      | `LinkedList` |
| `Set`        | `HashSet`    |
| `Map`        | `HashMap`    |

This means that if a method returns an instance of `Collection` the code of the mutated method will be
`return new ArrayList();`.

This operator is not enabled by default.

#### `optional` mutation operator

*New in version 1.2.6*

This operator accepts any method whose return type is `java.util.Optional`.
It replaces the code of the method by a single instruction returning an *empty* instance.

For example:

```Java
class A {
    int field;
    
    public Optional<Integer> getOptional() {
        return Optional.of(field);
    }
}  
```
 
is transformed to:

```Java
class A {
    int field;
    
   public Optional<Integer> getOptional() {
           return Optional.empty();
   }
}  
```

This operator is not enabled by default.

#### `argument` mutation operator

*New in version 1.3*

This operator replaces the body of a method by returning the value of the first parameter that has the same type as the return type of the method.

For example:

```Java
class A {
    public int m(int x, int y) {
        return x + 2 * y;
    }
}
```

is transformed to:

```Java
class A {
    public int m(int x) {
        return x;
    }
}
```

This operator is not enabled by default.

#### `this` mutation operator
*New in version 1.3*

Replaces the body of a method by `return this;` if applicable. The goal of this operator is to perform better transformations targeting fluent APIs.

For example:

```Java
class A {

    int value = 0;
    public A addOne() {
        value += 1;
        return this;
    }
}
```

is transformed to:

```Java
class A {

    int value = 0;
    public A addOne() {
        return this;
    }
}
```

This operator is not enabled by default.

### Stop Methods

Descartes avoids some methods that are generally not interesting and may introduce false positives such as simple getters, simple setters, empty void methods or methods returning constant values, delegation patterns as well as deprecated and compiler generated methods. Those methods are automatically detected by inspecting their code.
A complete list of examples can be found [here](src/test/java/eu/stamp_project/test/input/StopMethods.java).
The exclusion of stop methods can be configured. For more details see section: ["Running Descartes on your project"](#running-descartes-on-your-project).

### Do not use `null` in methods annotated with `@NotNull`

*New in version 1.2.6*

Descartes will avoid using the `null` operators in methods annotated with `@NotNull`. This increases its compatibility with Kotlin sources. This feature can be configured. See ["Running Descartes on your project"](#running-descartes-on-your-project) for more details.

### Skip methods using `@DoNotMutate`

*New in version 1.3*

Descartes skips all method that are annotated with *any* annotation whose name is `DoNotMutate`. 
For example, in the following fragment of code, the method `m` will not be mutated.

```Java
class A {
    @DoNotMutate
    public void m() {...}
}
```

All methods in a class annotated with `@DoNotMutate` will be avoided as well. For example, in the following fragment of code, the method `m` will not be mutated:

```Java
@DoNotMutate
class A {
    public void m() {...}
}
```

The `DoNotMutate` annotation may specify which operators should be considered. For example:

```Java
class A {
    @DoNotMutate(operators = "false")
    public boolean m() { return true; }
}
```

will instruct Descartes not to use the `false` mutation operator to mutate `m`.

When specifying operators, a method annotation takes precedence over class annotations. That, is the `@DoNotMutate` of a method overrides the same annotation in the class For example:

```Java
@DoNotMutate(operators = "true")
class A {

    public boolean n() { return false; }

    @DoNotMutate(operators = "false")
    public boolean m() { return true; }
}
```

will not mutate method `n` with `true`, as instructed in the class annotation. On the other hand, `m` will be mutated by `true` but not by `false`. 

Descartes includes a definition of `DoNotMutate`. However, when the tool inspects the annotations of a class or method  it matches only the simple name of the annotation class and ignores the package. So, any `DoNotMutate` annotation will be considered. In this way a project does not need to add Descartes as a dependency, it can declare its own `DoNotMutate` and use it.

This feature is also configurable. See ["Running Descartes on your project"](#running-descartes-on-your-project) for more details.


## Descartes Output

PIT reporting extensions work with Descartes and include `XML`, `CSV` and `HTML` formats. The `HTML` format is rather convenient since it also shows the line coverage.
Descartes also provides three new reporting extensions:
  - a general reporting extension supporting `JSON` files. It works also with **Gregor**, the default mutation engine for PIT. To use just set `JSON` as report format for PIT.
  - a reporting extension designed for Descartes that generates a JSON file with information about pseudo and partially tested methods. To use just set `METHOD` as report format for PIT.
  - Descartes can generate a human readable report containing only the list of methods with testing issues by using the `ISSUES` format.
 
Examples of these reporting extensions for [Apache Commons-CLI](https://github.com/apache/commons-cli/tree/b9ccc94008c78a59695f0c77ebe4ecf284370956) can be checked [here](docs/examples/commons-cli).

For more details on how to use and configure these reporting extensions please check section: ["Running Descartes on your project"](#running-descartes-on-your-project). For a more detailed description of the formats see [Output Formats](./docs/output-format.md).

## Running Descartes on your project

Stable releases of Descartes are available from Maven Central.
Descartes is a plugin for PIT, so they have to be used together.
PIT integrates with majors test and build tools such as [Maven](https://maven.apache.org),
[Ant](http://apache.ant.org) and [Gradle](https://gradle.org).


### Using Maven

The minimum configuration to use Descartes in a Maven project is the following:

```xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.6.6</version>
  <configuration>
    <mutationEngine>descartes</mutationEngine>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>eu.stamp-project</groupId>
      <artifactId>descartes</artifactId>
      <version>1.3.1</version>
    </dependency>
  </dependencies>
</plugin>
```


This actually configures PIT for the project, adds Descartes as a dependency of PIT and sets Descartes as the mutation engine to use.
Later, one need to run the mutation coverage goal provided by the PIT Maven plugin as follows:

```
cd my-project-under-test
mvn clean package # ensures clean state
mvn org.pitest:pitest-maven:mutationCoverage
```
##### Specifying operators

The operators must be specified in the `pom.xml` file. Each operator identifier should be added to the `mutators` element inside the `configuration` element. `void` and `null` operators are identified by `void` and `null` respectively.
The values for the constant mutation operator can be specified using the regular literal notation used in a Java program. For example `true`, `1`, `2L`, `3.0f`, `4.0`, `'a'`, `"literal"`, represent `boolean`, `int`, `long`, `float`, `double`, `char`, `string` constants.
Negative values and binary, octal and hexadecimal bases for integer constants are supported as stated by the [language specification](https://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10).
In order to specify a `byte` or `short` value, a cast-like notation can be used: `(short) -1`, `(byte)0x1A`.

The following configuration:
```xml
<mutators>
    <mutator>void</mutator>
    <mutator>4</mutator>
    <mutator>"some string"</mutator>
    <mutator>false</mutator>
</mutators>
```
will instruct the tool to use the `void` operator. The constant operator will replace the body of every method returning `int` with `return 4;` and will use `"some string"` and `false` for every `string` and `boolean` method.

By default, Descartes uses the following operators:

```xml
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

All the goals defined by the `pitest-maven` plugin work with Descartes. For more information check the [Maven plugin's web page](http://pitest.org/quickstart/maven/).

##### Configuring stop methods

To configure the stop methods under consideration Descartes provides a `STOP_METHODS` [feature](http://pitest.org/quickstart/advanced/#mutation-interceptor).
This feature is enabled by default. The parameter `exclude` can be used to prevent certain methods to be treated as stop methods and bring them back to the analysis. This parameter can take any of the following values:


|`exclude`        | Method description                                                                     | Example                                                                                          |
|-----------------|----------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| `empty`         | `void` methods with no instruction.                                                    | `public void m() {}`                                                                             |
| `enum`          | Methods generated by the compiler to support enum types (`values` and `valueOf`).      |                                                                                                  |
| `to_string`     | `toString` methods.                                                                    |                                                                                                  |
| `hash_code`     | `hashCode` methods.                                                                    |                                                                                                  |
| `deprecated`    | Methods annotated with `@Deprecated` or belonging to a class with the same annotation. | `@Deprecated public void m() {...}`                                                              |
| `synthetic`     | Methods generated by the compiler.                                                     |                                                                                                  |
| `getter`        | Simple getters.                                                                        | `public int getAge() { return this.age; }`                                                       |
| `setter`        | Simple setters. Includes also fluent simple setters.                                   | `public void setX(int x) { this.x = x; }` and `public A setX(int x){ this.x = x; return this; }` |
| `constant`      | Methods returning a literal constant.                                                  | `public double getPI() { return 3.14; }`                                                         |
| `delegate`      | Methods implementing simple delegation.                                                | `public int sum(int[] a, int i, int j) {return this.adder(a, i, j); }`                           |
| `clinit`        | Static class initializers.                                                             |                                                                                                  |
| `return_this`   | Methods that only return `this`.                                                       | `public A m() { return this; }`                                                                  |
| `return_param`  | Methods that only return the value of a real parameter                                 | `public int m(int x, int y) { return y; }`                                                       |
| `kotlin_setter` | Setters generated for data classes in Kotlin (*New in version 2.1.6*)                  |                                                                                                  |

So, for example, if we don't want to exclude deprecated methods and mutate them the following snippet should be added under the `configuration` element:

```xml
<features>
  <feature>
  <!-- This will allow descartes to mutate deprecated methods -->
    +STOP_METHODS(except[deprecated])
  </feature>
</features>
```

More than one group can be excluded at the same time:

```xml
<features>
  <feature>
  <!-- This will allow descartes to mutate toString and enum generated methods -->
    +STOP_METHODS(except[to_string] except[enum])
  </feature>
</features>
```

The feature can be completely disabled:

```xml
<features>
  <feature>
  <!--No method is considered as a stop method and therefore all of them will be mutated -->
    -STOP_METHODS()
  </feature>
</features>
```

##### Configuring reports

As said before, there are several reporting options provided by Descartes:
- `JSON` for a general mutation testing report using that file format. It can be used with **Gregor**.
- `METHODS` that produces a `methods.json` file with the list of all methods analyzed and categorized according to the mutation testing result.
- `ISSUES` a human readable report containing only the methods with testing issues.

They can be configured and combined as regular PIT report formats:

```xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.6.6</version>
  <configuration>
    <outputFormats>
      <value>JSON</value>
      <value>METHODS</value>
      <value>ISSUES</value>
    </outputFormats>
    <mutationEngine>descartes</mutationEngine>
  </configuration>
    <dependencies>
    <dependency>
      <groupId>eu.stamp-project</groupId>
      <artifactId>descartes</artifactId>
      <version>1.3.1</version>
    </dependency>
  </dependencies>
</plugin>
```
#### Other filters

From version *1.2.6* Descartes includes `AVOID_NULL` as a feature preventing the `null` operator to mutate a method marked with `@NotNull`.
The feature is active by default.

It can be removed by adding the following configuration:

```xml
<features>
  <feature>
    -AVOID_NULL()
  </feature>
</features>
```

From version *1.3* Descartes includes `DO_NOT_MUTATE` as a feature preventing the mutation of method and classes annotated with `@DoNotMutate`. 
The feature is active by default.

It can be removed by adding the following configuration:

```xml
<features>
  <feature>
    -DO_NOT_MUTATE()
  </feature>
</features>
```

From version *1.3* Descartes includes `SKIP_SHORT` as a feature to skip methods shorter than a given number of lines.
The feature is not active by default. To activate add the following configuration:

```xml
<features>
  <feature>
    +SKIP_SHORT()
  </feature>
</features>
```

The number of lines can be configured as follows:

```
+SKIP_SHORT(lines[3])
```

The line threshold is equal to 5 by default.

### Using Gradle
Follow the [instructions](http://gradle-pitest-plugin.solidsoft.info/) to set up PIT for a project that uses [Gradle](https://gradle.org/).
Add Descartes as a dependency of the `pitest` task

```
pitest 'eu.stamp-project:descartes:1.3.1'
```

then specify `descartes` in the `mutationEngine` option inside the plugin configuration.

An example of the final configuration could be:
```
plugins {
    id 'java'
    id 'info.solidsoft.pitest' version '1.5.1'
}

// Other configurations

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation group: 'junit', name: 'junit', version: '4.13.1'
    pitest 'eu.stamp-project:descartes:1.3.1'
}

pitest {
  mutationEngine = "descartes"
  pitestVersion = "1.6.6"
}
```
The `pitestVersion` property has to be specified to avoid version issues with the default version shipped with the Gradle plugin.
At last, run the `pitest` task for the project under test:

```
gradle pitest
```

#### Specifying operators

To specify a custom selection of mutation operators, use the `operators` property in the `pitest` block as follows:

```
mutators = [ '1.2', 'true', 'optional', '"a"' ]
```

This mutates methods returning `double` values with `1.2`, boolean methods with `true`, methods returning `Optional` and methods returning `String`.

See the Maven section for more details on the operator identifiers.

#### Configuring stop methods

As explained in the Maven section, Descartes skips some methods that are usually of no interest like simple getters and setters. However, these methods can be reincorporated to the analysis using the `STOP_METHODS` feature. The following removes all stop methods but allows mutating simple setters:

```
features = ['+STOP_METHODS(except[setter])']
```

To allow all stop methods do:

```
features = ['-STOP_METHODS()']
```

#### Configuring reports

Descartes custom output formats can be configured as follows:

```
outputFormats = ['ISSUES', 'METHODS', 'JSON']
```

#### Other features

Descartes includes `AVOID_NULL` and `DO_NOT_MUTATE` features as explained previously. These are active by default. To disable this features it is enough to add the following configuration to the `pitest` block:

```
features = ['-AVOID_NULL()']
```

```
features = ['-DO_NOT_MUTATE()']
```

### Running from the command line
Descartes can be used when invoking PIT from the command line. To do this, follow [the instructions](http://pitest.org/quickstart/commandline/) to run PIT, include Descartes in the classpath specification and add `--mutationEngine=descartes`.

### Installing and building from source

In a terminal, clone the repository:
```
git clone https://github.com/STAMP-project/pitest-descartes.git
```
switch to the cloned folder:
```
cd  pitest-descartes
```
install Descartes using the regular [Apache Maven](https://maven.apache.org) commands:
```
mvn install
```
After installing the package, PIT should be able to find the Descartes mutation engine.


## More...

### Performance

You can check [here](docs/performance-comparison.md) a comparison between Descartes and Gregor, the default mutation engine for PITest, considering the number of mutants both engines created for the same projects, and the total execution time.

### External Links

Articles mentioning Descartes:

* [A Comprehensive Study of Pseudo-tested Methods](https://hal.inria.fr/hal-01867423/file/main.pdf) (Empirical Software Engineering, [doi:10.1007/s10664-018-9653-2](https://doi.org/10.1007/s10664-018-9653-2))
* [Descartes: a PITest engine to detect pseudo-tested methods - Tool Demonstration](https://hal.inria.fr/hal-01870976/file/main.pdf) (Proceedings of ASE, [doi:10.1145/3238147.3240474](https://doi.org/10.1145/3238147.3240474))
* [Mutation testing with PIT and Descartes](http://massol.myxwiki.org/xwiki/bin/view/Blog/MutationTestingDescartes)
* [Controlling Test Quality](http://massol.myxwiki.org/xwiki/bin/view/Blog/ControllingTestQuality)

### License
Descartes is published under LGPL-3.0 (see [LICENSE.md](LICENSE) for further details).

### Contributing

Issues, pull requests and other contributions are welcome.

### Funding

Until December 2019 Descartes was partially funded by research project STAMP (European Commission - H2020)
![STAMP - European Commission - H2020](docs/logo_readme_md.png)

