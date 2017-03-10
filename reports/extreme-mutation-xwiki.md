# Mutation testing applied to XWiki projects
We applied a mutation testing to the XWiki code to evaluate an specific type of mutation inspired by [this article](http://dl.acm.org/citation.cfm?doid=2896941.2896944). This mutation consists in removing the body of `void` methods. If such modified method is not detected by the test suite it is considered as **pseudo-tested**. For this matter, we extended [PIT](http://pitest.org) with a new mutation engine that performs only this kind of mutation. The effective implementation can be found [here](https://github.com/STAMP-project/pitest-descartes).

The nature of this mutation is simple. Given a `void` method all instructions from its body are removed. For example, given the following class:

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

After that, all tests covering the changed code are executed.

The experiment was performed over the `xwiki-commons` and `xwiki-rendering` projects. They were considered as independent projects. The build of the `xwiki-platform` project failed on the testing stage and, as `xwiki-enterprise` depends on it, we didn't tested both until we manage to properly build them.

Only the mutation described above was applied.

## Obtained results
A mutation, in this case a `void` method whose code has been removed, is classified according to the outcome of the test suite when the involved unit tests are rerun. A mutation is said *NON-COVERED* if the code is not covered in any test case. A covered mutation is said *KILLED* if at least one test fails when the mutation is applied, otherwise it *SURVIVED*. Some mutations are *TIMED-OUT* if are covered and, at least one related test didn't run in the expected time. The coverage criterion is implemented by PIT. A method is **pseudo-tested** if the corresponding mutation *SURVIVED*. On the other hand, *KILLED* and *TIMED-OUT* mutations are considered as **detected**.

### `xwiki-commons`
A total of **1195** `void` methods were found. Near  46% of them were not covered by the test suite. **84** mutation *SURVIVED* which represents around **13%** of methods which are covered by the test suite and **7%** of the total amount. Number of mutants by category are shown below.

Category      | Count  
--------------|-------
*NON-COVERED* | 552   
*KILLED*      | 558   
*TIMED-OUT*   | 1     
*SURVIVED*    | 84  
**Total**     | 1195  

As a concrete example, the [`initialize`](https://github.com/xwiki/xwiki-commons/blob/e149f94a9a257b52d606f962a7517fc36b089628/xwiki-commons-core/xwiki-commons-extension/xwiki-commons-extension-api/src/main/java/org/xwiki/extension/job/history/internal/DefaultExtensionJobHistory.java#L110-L119) method of the `org.xwiki.extension.job.history.internal.DefaultExtensionJobHistory` that is covered by the [`DefaultExtensionJobHistoryTest.addGetRecords`](https://github.com/xwiki/xwiki-commons/blob/e149f94a9a257b52d606f962a7517fc36b089628/xwiki-commons-core/xwiki-commons-extension/xwiki-commons-extension-api/src/test/java/org/xwiki/extension/job/history/internal/DefaultExtensionJobHistoryTest.java#L57-L92) test was considered **pseudo-tested**.

In some cases, a mutant that *SURVIVED* is related to a fallback, sanity check or cleanup code. An example could be the [`repairExtension`](https://github.com/xwiki/xwiki-commons/blob/e149f94a9a257b52d606f962a7517fc36b089628/xwiki-commons-core/xwiki-commons-extension/xwiki-commons-extension-api/src/main/java/org/xwiki/extension/job/internal/AbstractExtensionJob.java#L227-L238) method of the `org.xwiki.extension.job.internal.AbstractExtensionJob` class covered by [`InstallJobTest`](https://github.com/xwiki/xwiki-commons/blob/8c6be13dbd2a517d5601519a6671bb4273d56914/xwiki-commons-core/xwiki-commons-extension/xwiki-commons-extension-api/src/test/java/org/xwiki/extension/job/internal/InstallJobTest.java). The method is called in a chain of delegations containing some `try...catch...finally` blocks. Also this could indicate that in this particular scenario the repair wasn't actually needed.

Other cases consisted of methods that call library code. For example, the `sort` method of `org.xwiki.extension.repository.internal.RepositoryUtils` delegates the call to `java.util.Collection.sort`. This particular method seems to be covered by `DefaultLocalExtensionRepositoryTest.testSearch` and there is no check concerning the order of the returned elements. This could mean that, in fact, sorting is not essential or that a new test should be added to verify the order of the returned collection.

Most mutants that *SURVIVED* are inside classes included in packages named `internal` or were inside a package named `internal`. Considerable numbers come from abstract classes (i.e. `AbstractRequest`, `AbstractMavenExtension`) or classes whose names begin with `Default` (i.e. `DefaultComponentDependency`, `DefaultJobManager`). Some **pseudo-tested** methods are inside logging classes such as `AllLogRule` and `LogEvent`. Many methods named `initialize`, `update`, `close` or `dispose` and setter methods were also marked as **pseudo-tested**. Nevertheless proportions of elements described above remained more or less the same for *NON-COVERED* and **detected** mutants, which could mean that there is no strong feature able to characterize the set of non-detected mutants.

### `xwiki-rendering`
In this case **2694** `void` methods were found and 69% of them were *NON-COVERED*. 141 mutants *SURVIVED* for a **17%** of test suite covered methods and **5%** of the total amount. Number of mutants by category are shown below.

Category      | Count  
--------------|-------
*NON-COVERED* | 1854   
*KILLED*      | 588   
*TIMED-OUT*   | 111     
*SURVIVED*    | 141  
**Total**     | 2694  

Although there are some abstract classes and classes from internal packages involved, they appear in much smaller numbers than what was obtained for the `xwiki-commons` project. Some scenarios come from methods generated by `jacc` and methods whose names starts with `begin` or `end` (i.e. `beginSection`, `endParagraph`, `beginDocument`), `initialize` methods and many setter methods. As for **detected** mutations they contain methods that, by their names, seem to deal with document elements, for example `onComment`, `handleImage`, `sendCharacters`. However, we couldn't find any strong feature for either category.
