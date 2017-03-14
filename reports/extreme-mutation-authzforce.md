# Mutation testing applied to Authzforce Core project
We applied [mutation testing](https://en.wikipedia.org/wiki/Mutation_testing) to evaluate an specific type of mutation inspired by [this article](http://dl.acm.org/citation.cfm?doid=2896941.2896944). This mutation consists in removing the body of `void` methods. If such modified method is not detected by the test suite it is considered as **pseudo-tested**. For this matter, we extended [PIT](http://pitest.org) with a new mutation engine that performs only this kind of mutation. The effective implementation can be found [here](https://github.com/STAMP-project/pitest-descartes).

The nature of this mutation is simple. Given a `void` method all instructions from its body are removed. For example, with the following class as input:

``` java
class A {

  int field = 3;

  public void Method(int inc) {
    field += 3;
  }

}
```
the generated mutant would be:

``` java
class A {

  int field = 3;

  public void Method(int inc) { }

}
```

After that, all tests covering the changed code are executed.

The experiment was performed over the [authzforce/core](https://github.com/authzforce/core) repository. Only the mutation described above was applied.

The following test classes had to be removed from the experiment:
* [NonRegression](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/test/java/org/ow2/authzforce/core/pdp/impl/test/NonRegression.java)
* [ConformanceV3Others](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/test/java/org/ow2/authzforce/core/pdp/impl/test/conformance/ConformanceV3Others.java)
* [CustomPdpTest](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/test/java/org/ow2/authzforce/core/pdp/impl/test/custom/CustomPdpTest.java)
* [EmbeddedPdpBasedAuthzInterceptorTest](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/test/java/org/ow2/authzforce/core/pdp/impl/test/pep/cxf/EmbeddedPdpBasedAuthzInterceptorTest.java)

The tool could not properly run those tests, even when they can be run manually. This is probably an issue on our side.
Also, empty `void` methods were ignored.

## Results
A mutation consists in transforming the program at one single point to produce a mutant. We generated **60** mutants for this case study. Then, we ran the test suite on each mutant and we classified each mutant as follows: a mutant is said *NON-COVERED* if the code is not covered in any test case (coverage is evaluated by PIT); a covered mutant is said *KILLED* if at least one test fails when the test suite runs on the mutant; otherwise the mutant is classified as *SURVIVED*. A method is said **pseudo-tested** if the corresponding mutant *SURVIVED*. On the other hand, *KILLED* mutants are considered as **detected**. The number of mutants by category is shown below.  

The whole analysis process (mutant generation + test case execution) took less than 3 minutes.

Out of the **60**  mutants, **45%** are not covered by the test suite. **14** mutants *SURVIVED* which represents around **42%** of methods which are covered by the test suite and around **23%** of all `void` methods.

Category      | Count  
--------------|-------
*NON-COVERED* | 27   
*SURVIVED*    | 14  
*KILLED*      | 19       
**Total**     | 60  

The following table contains all **pseudo-tested** methods found. Method names are links the corresponding code in the github repository.

 Method  | Location | Package
-------- | -------- | -------
 [close](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/BasePdpEngine.java#L980-L988)                                                     |	BasePdpEngine                                                             |	org.ow2.authzforce.core.pdp.impl
 [close](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/CloseableAttributeProvider.java#L67-L70)                                          |	CloseableAttributeProvider.ModuleAdapter                                  |	org.ow2.authzforce.core.pdp.impl
 [close](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/CloseableAttributeProvider.java#L89-L112)                                         |	CloseableAttributeProvider                                                |	org.ow2.authzforce.core.pdp.impl
 [close](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/CloseableAttributeProvider.java#L217-L222)                                        |	CloseableAttributeProvider                                                |	org.ow2.authzforce.core.pdp.impl
 [putOther](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/IndividualDecisionRequestContext.java#L285-L289)                               |	IndividualDecisionRequestContext                                          |	org.ow2.authzforce.core.pdp.impl
 [process](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/ModularAttributeProvider.java#L57-L75)                                          |	ModularAttributeProvider.ISSUED_TO_NON_ISSUED_ATTRIBUTE_COPY_ENABLED_MODE |	org.ow2.authzforce.core.pdp.impl
 [addFirstEmptyRuleWithOverriddenEffect](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/combining/DPOverridesCombiningAlg.java#L779-L793) |	DPOverridesCombiningAlg.OverridingEffectFirstRuleCollector                |	org.ow2.authzforce.core.pdp.impl.combining
 [close](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/expression/ExpressionFactoryImpl.java#L644-L651)                                  |	ExpressionFactoryImpl                                                     |	org.ow2.authzforce.core.pdp.impl.expression
 [checkNumberOfArgs](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/func/StandardHigherOrderBagFunctions.java#L85-L93)                    |	StandardHigherOrderBagFunctions.BooleanHigherOrderTwoBagFunction          |	org.ow2.authzforce.core.pdp.impl.func
 [checkNumberOfArgs](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/func/StandardHigherOrderBagFunctions.java#L303-L310)                  |	StandardHigherOrderBagFunctions.OneBagOnlyHigherOrderFunction             |	org.ow2.authzforce.core.pdp.impl.func
 [checkNumberOfArgs](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/func/StandardHigherOrderBagFunctions.java#L542-L550)                  |	StandardHigherOrderBagFunctions.AnyOfAny                                  |	org.ow2.authzforce.core.pdp.impl.func
 [setSameDate](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/func/TimeRangeComparisonFunction.java#L77-L81)                              |	TimeRangeComparisonFunction.Call                                          |	org.ow2.authzforce.core.pdp.impl.func
 [setResult](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/policy/PolicyEvaluators.java#L438-L459)                                       |	BaseTopLevelPolicyElementEvaluator.EvalResults                            |	org.ow2.authzforce.core.pdp.impl.policy
 [close](https://github.com/authzforce/core/blob/1f5950290744ef037f4aa4962469368ec68188d0/src/main/java/org/ow2/authzforce/core/pdp/impl/policy/RootPolicyEvaluators.java#L361-L365)                                       |	RootPolicyEvaluators.StaticView                                           |	org.ow2.authzforce.core.pdp.impl.policy

Here are some interesting insights that appear in this table:
- the amount of methods named `close` is striking. Only 4 more `void` `close` methods were found in the project. They were empty and one of them was not covered by the test suit.
This could indicate that tests don't include or induce scenarios where the state of those objects is verified after the invocation of those methods.
- there are 3 methods named `checkNumberOfArgs` probably indicating that the test cases don't trigger a failure of the conditions checked by those methods.

Another interesting fact to notice is that out of the **27** non covered mutants, **13** were setter methods.
