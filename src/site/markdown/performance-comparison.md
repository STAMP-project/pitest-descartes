# Comparison with Gregor

*Gregor* is the default mutation testing engine of PITest. We have compared Gregor and Descartes over a set of open source projects available from GitHub. We considered the execution time to evaluate a test suite, the number of mutants created by both engines and the results they reported.

In all cases Descartes created fewer mutants and employed much less time to complete the analysis.

Nevertheless, the results given by Descartes are coarse-grained compared to Gregor but more actionable and easier to understand and fix. It is, in general, not a replacement, but a tool to discover the worst tested methods in a project. Also since it takes less time, Descartes can be used more frequently. 

The table below summarizes the comparison:

|                                                                         | Descartes |         | Gregor   |         |
|-------------------------------------------------------------------------|-----------|---------|----------|---------|
| Project                                                                 | Time      | Mutants | Time     | Mutants |
| [authzforce](https://github.com/authzforce/core.git)                    | 0:08:00   | 626     | 1:23:50  | 7296    |
| [aws-sdk-java](https://github.com/aws/aws-sdk-java)                     | 1:32:23   | 161758  | 6:11:22  | 2141689 |
| [commons-cli](https://github.com/apache/commons-cli)                    | 0:00:13   | 271     | 0:01:26  | 2560    |
| [commons-codec](https://github.com/apache/commons-codec)                | 0:02:02   | 979     | 0:07:57  | 9233    |
| [commons-collections](https://github.com/apache/commons-collections)    | 0:01:41   | 3558    | 0:05:41  | 20394   |
| [commons-io](https://github.com/apache/commons-io)                      | 0:02:16   | 1164    | 0:12:48  | 8809    |
| [commons-lang](https://github.com/apache/commons-lang)                  | 0:02:07   | 3872    | 0:21:02  | 30361   |
| [flink-core](https://github.com/apache/flink/tree/master/flink-core)    | 0:14:04   | 4935    | 2:29:45  | 43619   |
| [gson](https://github.com/google/gson)                                  | 0:01:08   | 848     | 0:05:34  | 7353    |
| [imagej-common](https://github.com/imagej/imagej-common)                | 0:08:07   | 1947    | 0:29:09  | 15592   |
| [jaxen](https://github.com/jaxen-xpath/jaxen)                           | 0:01:31   | 1252    | 0:24:40  | 12210   |
| [jfreechart](https://github.com/jfree/jfreechart)                       | 0:05:48   | 7210    | 0:41:28  | 89592   |
| [jgit](https://github.com/eclipse/jgit)                                 | 1:30:08   | 7152    | 16:02:03 | 78316   |
| [joda-time](https://github.com/JodaOrg/joda-time)                       | 0:03:39   | 4525    | 0:16:32  | 31233   |
| [jopt-simple](https://github.com/jopt-simple/jopt-simple)               | 0:00:37   | 412     | 0:01:36  | 2271    |
| [jsoup](https://github.com/jhy/jsoup)                                   | 0:02:43   | 1566    | 0:12:49  | 14054   |
| [sat4j-core](https://github.com/apache/pdfbox)                          | 0:53:09   | 2304    | 10:55:50 | 17163   |
| [pdfbox](https://gitlab.ow2.org/sat4j/sat4j/tree/master/org.sat4j.core) | 0:44:07   | 7559    | 6:20:25  | 79763   |
| [scifio](https://github.com/scifio/scifio)                              | 0:24:14   | 3627    | 3:12:11  | 62768   |
| [spoon](https://github.com/INRIA/spoon)                                 | 2:24:55   | 4713    | 56:47:57 | 43916   |
| [urbanairship](https://github.com/urbanairship/java-library)            | 0:07:25   | 3082    | 0:11:31  | 17345   |
| [xwiki-rendering](https://github.com/xwiki/xwiki-rendering)             | 0:10:56   | 5534    | 2:07:19  | 112605  |

For a full comparison including how the results from both mutations engine correlate check the [experiments repository](https://github.com/STAMP-project/descartes-experiments) This repository contains a set of IPython notebooks including:
- a [full comparison on a set of real open-source projects](https://github.com/STAMP-project/descartes-experiments/blob/master/scripts/project_stats.ipynb)
- [how the mutation scores computed with both engines are correlated](https://github.com/STAMP-project/descartes-experiments/blob/master/scripts/score_comparison.ipynb)
- [the presence of pseudo-tested methods](https://github.com/STAMP-project/descartes-experiments/blob/master/scripts/pseudo_tested_methods.ipynb) in those projects and
- a [statistical proof](https://github.com/STAMP-project/descartes-experiments/blob/master/scripts/hypergeometric_test.ipynb) that these are the worst tested methods on each project.
