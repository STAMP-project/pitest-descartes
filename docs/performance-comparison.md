# Performance

We have compared the performance of Descartes and Gregor, which is the
default mutation engine for PITest. The table below shows the execution
time and number of mutants created by both engines in a set of
open source projects. In all cases Descartes created much less mutants
and employed much less time.

Nevertheless, the results given by Descartes are coarse-grained compared
to Gregor. It is, in general, not a replacement, but a tool to discover
the worst tested methods in a project.


|                                                                         | Descartes |         | Gregor   |         |
|-------------------------------------------------------------------------|-----------|---------|----------|---------|
| Project                                                                 | Time      | Mutants | Time     | Mutants |
| [authzforce](https://github.com/authzforce/core.git)                    | 0:08:00   |    626  |  1:23:50 |    7296 |
| [aws-sdk-java](https://github.com/aws/aws-sdk-java)                     | 1:32:23   | 161758  |  6:11:22 | 2141689 |
| [commons-cli](https://github.com/apache/commons-cli)                    | 0:00:13   |    271  |  0:01:26 |    2560 |
| [commons-codec](https://github.com/apache/commons-codec)                | 0:02:02   |    979  |  0:07:57 |    9233 |
| [commons-collections](https://github.com/apache/commons-collections)     | 0:01:41   |   3558  |  0:05:41 |   20394 |
| [commons-io](https://github.com/apache/commons-io)                      | 0:02:16   |   1164  |  0:12:48 |    8809 |
| [commons-lang](https://github.com/apache/commons-lang)                  | 0:02:07   |   3872  |  0:21:02 |   30361 |
| [flink-core](https://github.com/apache/flink/tree/master/flink-core)    | 0:14:04   |   4935  |  2:29:45 |   43619 |
| [gson](https://github.com/google/gson)                                  | 0:01:08   |    848  |  0:05:34 |    7353 |
| [imagej-common](https://github.com/imagej/imagej-common)                | 0:08:07   |   1947  |  0:29:09 |   15592 |
| [jaxen](https://github.com/jaxen-xpath/jaxen)                           | 0:01:31   |   1252  |  0:24:40 |   12210 |
| [jfreechart](https://github.com/jfree/jfreechart)                       | 0:05:48   |   7210  |  0:41:28 |   89592 |
| [jgit](https://github.com/eclipse/jgit)                                 | 1:30:08   |   7152  | 16:02:03 |   78316 |
| [joda-time](https://github.com/JodaOrg/joda-time)                       | 0:03:39   |   4525  |  0:16:32 |   31233 |
| [jopt-simple](https://github.com/jopt-simple/jopt-simple)               | 0:00:37   |    412  |  0:01:36 |    2271 |
| [jsoup](https://github.com/jhy/jsoup)                                   | 0:02:43   |   1566  |  0:12:49 |   14054 |
| [sat4j-core](https://github.com/apache/pdfbox)                          | 0:53:09   |   2304  | 10:55:50 |   17163 |
| [pdfbox](https://gitlab.ow2.org/sat4j/sat4j/tree/master/org.sat4j.core) | 0:44:07   |   7559  |  6:20:25 |   79763 |
| [scifio](https://github.com/scifio/scifio)                              | 0:24:14   |   3627  |  3:12:11 |   62768 |
| [spoon](https://github.com/INRIA/spoon)                                 | 2:24:55   |   4713  | 56:47:57 |   43916 |
| [urbanairship](https://github.com/urbanairship/java-library)            | 0:07:25   |   3082  |  0:11:31 |   17345 |
| [xwiki-rendering](https://github.com/xwiki/xwiki-rendering)             | 0:10:56   |   5534  |  2:07:19 |  112605 |

