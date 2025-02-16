# Output formats provided by Descartes

PIT reporting extensions work with Descartes and include `XML`, `CSV` and `HTML` formats. The `HTML` format is rather convenient since it also shows the line coverage. Descartes also provides three new reporting extensions `JSON`, `METHOD` and `ISSUES`. This page explains the content included on each report format and provides links to actual examples. To understand how the report formats can be configured check  [how to run Descartes on you project](./running-on-your-project.html) and the [frequently used configurations](./frequent-configurations.html).

## `JSON` report format

The `JSON` report format is a general reporting extension supporting `JSON` files. It also works with **Gregor**, the default mutation engine for PIT. Set `JSON` as report format for PIT to use it. The structure of the report files is very similar to that of PIT's `XML` report format.
An example of a file generate with this format for an actual project can be checked [here](./examples/commons-cli/methods.json).
A [JSON schema](https://json-schema.org/) definition for this report format can be downloaded [here](https://raw.githubusercontent.com/STAMP-project/pitest-descartes/master/src/main/resources/schemas/mutations.report.schema.json).

## `METHOD` report format

This reporting extension is specific to Descartes. It generates a JSON file with information about pseudo and partially tested methods. Set `METHOD` as report format for PIT to use it. This format produces a `JSON` file with the following structure:

```json
{
    "methods": [
    ...

    {
      "name": "getOptionValue",
      "description": "(Ljava/lang/String;)Ljava/lang/String;",
      "class": "CommandLine",
      "package": "org/apache/commons/cli",
      "file-name": "CommandLine.java",
      "line-number": 202,
      "classification": "tested",
      "detected": ["\"A\"", "null", "\"\""],
      "not-detected": [],
      "tests": [...],
      "mutations": [
        ...
        {
          "status": "KILLED",
          "mutator": "\"A\"",
          "tests-run": 1,
          "detected-by": "org.apache.commons.cli.bug.BugsTest.test31148(org.apache.commons.cli.bug.BugsTest)",
          "tests": [...]
        ...
      ]
    }
    ...

  ],
  "analysis": {
    "time": 15918,
    "mutators": [
      "void",
      "null",
      "empty",
      "true",
      "false",
      "0",
      "1",
      "(short)0",
      "(short)1",
      "(byte)0",
      "(byte)1",
      "0L",
      "1L",
      "0.0",
      "1.0",
      "0.0f",
      "1.0f",
      "'\\40'",
      "'A'",
      "\"\"",
      "\"A\""
    ]
}
```

The `methods` list contains an entry for every method that was part of the analysis. For each method there is information about its metadata, its testing classification (*tested*, *partially-tested*, *pseudo-tested*, *not-covered*), which mutations were detected and which were not, all tests covering the method and there is also a list of entries for every mutation performed with more information.

A method is said to be *not-covered* if it is not executed by the test suite. It is said to be *pseudo-tested* if it is covered by the test suite, yet no extreme mutation applied to the method was detected by any test case. A method is said to be *partially-tested* if it has mixed results: the test suite detects only some mutations. Finally, a method is classified as *tested* if the test suite detects all extreme transformations.
An example of a file generated with this format for an actual project can be checked [here](./examples/commons-cli/methods.json).

A [JSON schema](https://json-schema.org/) definition for this report format can be downloaded [here](https://raw.githubusercontent.com/STAMP-project/pitest-descartes/master/src/main/resources/schemas/methods.report.schema.json).

## `ISSUES` report format

This report format generates a set of human-readable HTML files containing information about methods with testing issues. For each method it includes the mutation that were applied, if they were detected and the test cases executing the method. It also includes small hints on how to improve the testing of each method. This format is more suitable when you are trying to manually improve the test suite or to include in code reviews.
An example of the files generated for an actual project can be checked [here](./examples/commons-cli/issues/index.html).
