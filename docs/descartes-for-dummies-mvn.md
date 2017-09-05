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
mvn package
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

1. Descartes is a plugin for PIT so they have to be used together. To configure PIT to use Descartes modify the pom.xml of the project adding this configurazion.

From the root folder copy the configuration to the file dhell/dspot.properties
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
          <targetClasses>
            <param>myWorld.*</param>
          </targetClasses>
          <targetTests>
            <param>myWorld.*</param>
          </targetTests>
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
        ...
      </plugin>
      ...
    </plugins>
    ...    
  </build>
  ...
</project>
```

2. Execute Descartes
```
cd dspot
java -jar target/dspot-1.0.0-jar-with-dependencies.jar  -p ../dhell/dspot.properties -i 1 -t myWorld.HelloAppTest
```
The execution uses these parameters:
* p: [mandatory] specify the path to the configuration file.
* i: specify the number of amplification iteration.
* t: fully qualified names of test classes to be amplified.

3. Check the Output
```
======= REPORT =======
PitMutantScoreSelector: 
The original test suite kill 106 mutants
The amplification results with 33 new tests
it kill 18 more mutants
```

4. Check the amplified test
Open dspot/dspot-out/myWorld/AmplHelloAppTest.java

### Conclusion
We hope this quick overview has increased your interest in DSpot usage. Note that this is a very truncated quick-start guide. Now you are ready for more comprehensive details concerning the actions you have just performed. Check out the DSpot Readme Guide.
