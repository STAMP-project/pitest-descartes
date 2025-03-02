# Descartes: A Mutation Engine for PIT

![build-on-push](https://github.com/STAMP-project/pitest-descartes/workflows/build-on-push/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.stamp-project/descartes/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/eu.stamp-project/descartes)

*I mutate therefore I am*

This document briefly explains what if Descartes and the minimum steps to use it or start using its repository. For a full documentation on how to use Descartes in other scenarios and how to configure it go to the [project's website](https://pites-descartes.github.io).

## What is Descartes?

Descartes evaluates the capability of your test suite to detect bugs using extreme mutation testing. It is able to find the worst tested methods in a project.

Descartes is developed as a mutation engine plugin for [PIT](http://pitest.org) which implements extreme mutation operators as proposed in the paper [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944). It also works alongside other PIT extensions to provide more meaningful and actionable results improve results.

## Quick start with Maven

To use Descartes in a Maven project, add the following plugin configuration to your `pom.xml`:

```xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.17.2</version>
  <configuration>
    <mutationEngine>descartes</mutationEngine>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>eu.stamp-project</groupId>
      <artifactId>descartes</artifactId>
      <version>LATEST</version>
    </dependency>
  </dependencies>
</plugin>
```

Then, execute the regular mutation coverage goal in the root folder of the project under test:

```
cd my-project-under-test
mvn clean package org.pitest:pitest-maven:mutationCoverage
```

All options from PIT can be used. For more details check [the PIT documentation](https://pitest.org/quickstart/maven/).

If you use a multi-module project take a look at [PitMP](https://github.com/STAMP-project/pitmp-maven-plugin).

## Quick start with the repository


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

## Referencing Descartes

If you use Descartes on your research projects you may cite is as:

```bibtex
@inproceedings{vera_descartes_2018,
  author    = {Vera-Pérez, Oscar L. and Monperrus, Martin and Baudry, Benoit},
  url       = {https://dl.acm.org/citation.cfm?doid=3238147.3240474},
  booktitle = {Proceedings of the 2018 33rd ACM/IEEE International Conference on Automated Software Engineering (ASE ’18)},
  date      = {2018},
  month     = {09},
  doi       = {10.1145/3238147.3240474},
  pages     = {908--911},
  title     = {Descartes: A {PITest} Engine to Detect Pseudo-Tested Methods},
  urldate   = {2019-08-06},
  doi       = {10.1145/3238147.3240474},
  arxiv     = {https://arxiv.org/abs/1811.03045},
  hal       = {https://hal.inria.fr/hal-01870976v1}
}
```

## License
Descartes is published under [LGPL-3.0](https://www.gnu.org/licenses/lgpl-3.0.html#license-text).

## Contributing

Issues, pull requests and other contributions are welcome. Check our [GitHub repository](https://github.com/STAMP-Project/pitest-descartes)

## Funding

Until December 2019 Descartes was partially funded by the [STAMP research project (European Commission - H2020)](https://stamp.ow2.org/).

![STAMP - European Commission - H2020](src/site/resources/images/logo.png)

