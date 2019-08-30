# Output Format

Descartes includes its own output formats which are focused on *pseudo*
and *partially-tested* methods.
These formats can used by specifying `METHODS` and/or `ISSUES` in the
PITest `outputFormats` parameter.

`METHODS` produces a `JSON` file with the following structure:

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

The `methods` list contains an entry for every methods that was part of
the analysis. For each method there is information about its metadata,
its testing classification (tested, partially-tested, pseudo-tested,
not-covered), which mutations were detected and which not, all tests
covering the method and there is also a list of entries for every
mutation performed with more information.

Examples of files with this format can be checked [here](examples/commons-cli/methods.json)
and also [here](examples/jsoup.json). These were obtained from actual
open-source projects.

The `ISSUES` output format contains this same information but it is
rendered as an `HTML` document with human-readable descriptions. An example can be checked [here](examples/commons-cli/issues/).
