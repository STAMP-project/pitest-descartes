## Descartes for dummies

### Prerequisites

Install and set-up these programs:

1. [Java 8](https://www.java.com/en/download/help/download_options.xml)
2. [GIT](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
3. [Maven](https://maven.apache.org/install.html)

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
#### Clone and Compile Maven Project (DHELL)

1. From the root folder, clone the project
```
git clone https://github.com/STAMP-project/dhell.git
```
2. Access to the project directory
```
cd dhell
```
3.  Compile application and tests, run tests  and create the artifact:
```
mvn clean package
```

#### Execute Descartes

1. Descartes is a plugin for PIT so they have to be used together. To configure PIT to use Descartes modify the pom.xml of the project adding this configuration (or copy [this file](dhell-pom.xml) to dhell folder and rename it to pom.xml)

```xml
<project>
  ...
  <build>
    ...
    <plugins
      ...
      <plugin>
        <groupId>org.pitest</groupId>
        <artifactId>pitest-maven</artifactId>
        <version>1.2.0</version>  
        <configuration>
          <mutationEngine>descartes</mutationEngine>
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
            <mutator>(short)1</mutator>
            <mutator>(short)2</mutator>
            <mutator>0L</mutator>
            <mutator>1L</mutator>
            <mutator>0.0</mutator>
            <mutator>1.0</mutator>
            <mutator>0.0f</mutator>
            <mutator>'\40'</mutator>
            <mutator>'A'</mutator>
            <mutator>""</mutator>
            <mutator>"A"</mutator>
          </mutators>
        </configuration>
        <dependencies>
          <dependency>
            <groupId>fr.inria.stamp</groupId>
            <artifactId>descartes</artifactId>
            <version>0.2-SNAPSHOT</version>
          </dependency>
        </dependencies>
      </plugin>
      ...
    </plugins>
    ...    
  </build>
  ...
</project>
```

2. Execute PIT using Descartesas mutationEngine
```
cd dhell
mvn test org.pitest:pitest-maven:mutationCoverage
```
3. Check the report
PIT will output an html report to target/pit-reports/YYYYMMDDHHMI folder.

### Conclusion
We hope this quick overview has increased your interest in Descardes usage. Note that this is a very truncated quick-start guide. Now you are ready for more comprehensive details concerning the actions you have just performed. Check out the Descardes Readme Guide.
