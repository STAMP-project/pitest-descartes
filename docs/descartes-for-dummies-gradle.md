## Descartes for dummies

### Prerequisites

Install and set-up these programs:

1. [Java 8](https://www.java.com/en/download/help/download_options.xml)
2. [GIT](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
3. [Maven](https://maven.apache.org/install.html)
4. [Gradle](https://docs.gradle.org/current/userguide/installation.html)

### Descartes Set-Up
#### Clone Descartes and install it in local maven repository

1. From the root folder, clone the project:
```
git clone https://github.com/STAMP-project/pitest-descartes.git
```
2. Access to the descartes project directory
```
cd  pitest-descartes
```
3. Create descartes jar and install in local maven repository
```
mvn install
```
### Execute an analysis with Descartes
#### Clone and Compile Gradle Project (DHEG)

1. From the root folder, clone the project
```
git clone https://github.com/STAMP-project/dheg.git
```
2. Access to the project directory
```
cd dheg
```
3. Build the project
```
gradle build
```

#### Execute Descartes

1. Descartes is a plugin for PIT so they have to be used together. To configure PIT to use Descartes modify build.gradle file of the project adding this configurazion (or copy [this file](dheg-build.gradle) to dheg folder and rename it to build.gradle)

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org.m2/"
    }
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
  targetClasses = ['myWorld*']
  mutationEngine = "descartes"
}
```

2. Execute PIT using Descartesas mutationEngine
```
cd dheg
gradle pitest
```
3. Check the report
PIT will output an html report to build/reports/pitest/YYYYMMDDHHMI folder.

### Conclusion
We hope this quick overview has increased your interest in Descardes usage. Note that this is a very truncated quick-start guide. Now you are ready for more comprehensive details concerning the actions you have just performed. Check out the Descardes Readme Guide.
