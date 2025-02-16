# How does Descartes work?

Descartes finds the worst tested methods in a project by using *mutation testing*, a well known technique to assess the quality of a test suite by introducing artificial faults and then checking if the tests are able to detect them. In particular the tool uses *extreme mutation testing* that works at the method level.

## Mutation testing
Mutation testing verifies if your test suite can detect bugs.
The technique works by introducing small changes or faults into the original program. These modified versions are called **mutants**.
A good test suite should be able to detect or *kill* a mutant. That is, at least one test case should fail when the test suite is executed with the mutant instead of the original program. 
[Read more](https://en.wikipedia.org/wiki/Mutation_testing).
Traditional mutation testing works at the instruction level, e.g., replacing ">" by "<=", so the number of generated mutants is huge, and it takes more time to check the entire test suite.
That's why the authors of [Will my tests tell me if I break this code?](http://dl.acm.org/citation.cfm?doid=2896941.2896944) proposed an *Extreme Mutation* strategy, which works at the method level.

## Extreme Mutation Testing
In Extreme Mutation testing, instead of changing one instruction at a time, the whole logic of a method under test is eliminated.
All statements in a `void` method are removed. If he method is not `void`, the body is replaced by a single return statement producing a constant value. 
This approach generates fewer mutants. Code from the authors can be found in [their GitHub repository](https://github.com/cqse/test-analyzer).

The goal of Descartes is to bring an effective implementation of this kind of mutation operator into the world of [PIT](https://pitest.org) and check its performance in real world projects.

## Method classification
Extreme mutation classify methods according to the outcome of the extreme mutants. If no extreme mutant created for a method are detected by the test suite it is then considered as **pseudo-tested**. These are the worst tested methods in the project. If there are mixed results, that is, for the same method some extreme mutants are detected and others are not, then the method is classified as **partially-tested**. These are not well tested either. Otherwise, if all mutants are detected the method is considered as **tested**.

Descartes finds and reports all **pseudo-tested** and **partially-tested** methods in a given project.

## Mutation operators
The *mutation operators* are models of the artificial faults used to create mutants. In particular, *extreme mutation operators* replace the body of a method by one simple return instruction or just remove all instructions if possible. The set of mutation operators that Descartes uses can be configured. Further details on how to configure are given in ["Running Descartes on your project"](./running-on-your-project.html). You can find frequent configuration examples [here](./frequent-configurations.html).

Below you can find the full list of extreme mutation operators used by Descartes.

## `void` mutation operator
This operator accepts a `void` method and removes all the instructions on its body. For example, with the following class as input:

```java
class A {

  int field = 3;

  public void Method(int inc) {
    field += 3;
  }

}
```
the mutation operator will generate:

```java
class A {

  int field = 3;

  public void Method(int inc) { }

}
```

## `null` mutation operator
This operator accepts a method with a reference return type and replaces all instructions
with `return null`. For example, using the following class as input:
```java
class A {
    public B create() {
        return new B();
    }
}
```
this operator will generate:

```java
class A {
    public B create() {
        return null;
    }
}
```
## `empty` mutation operator
This is a special operator which targets methods that return arrays. It replaces the entire
body with a `return` statement that produces an empty array of the corresponding type.
For example, the following class:

```java
class A {
  public int[] getRange(int count) {
    int[] result = new int[count];
    for(int i=0; i < count; i++) {
      result[i] = i;
    }
    return result;
  }
}
```
will become:

```java
class A {
  public int[] getRange(int count) {
    return new int[0];
  }
}
```

## Constant mutation operator
This operator accepts any method with a primitive or `String` return type. It replaces the method body
with a single instruction returning a defined constant.
For example, if the integer constant `3` is specified, then for the following class:

```java
class A {
    int field;

    public int getAbsField() {
        if(field >= 0)
            return field;
        return -field;
    }
}
```
this operator will generate:

```java
class A {
    int field;

    public int getAbsField() {
        return 3;
    }
}
```

## `new` mutation operator
*New in version 1.2.6*

This operator accepts any method whose return type has a constructor with no parameters and belongs to a `java` package.
It replaces the code of the method by a single instruction returning a new instance.

For example:

```java
class A {
    int field;
    
    public ArrayList range(int end) {
        ArrayList l = new ArrayList();
        for(int i = 0; i < size; i++) {
            A a = new A();
            a.field = i;
            l.add(a);
        }
        return l;
    }
}  
```
 
is transformed to:

```java
class A {
    int field;
    
    public List range(int end) {
        return new ArrayList();
    }
}  
```

This operator handles the following special cases:

| Return Type  | Replacement  |
|--------------|--------------|
| `Collection` | `ArrayList`  |
| `Iterable`   | `ArrayList`  |
| `List`       | `ArrayList`  |
| `Queue`      | `LinkedList` |
| `Set`        | `HashSet`    |
| `Map`        | `HashMap`    |

This means that if a method returns an instance of `Collection` the code of the mutated method will be
`return new ArrayList();`.

This operator is not enabled by default.

## `optional` mutation operator
*New in version 1.2.6*

This operator accepts any method whose return type is `java.util.Optional`.
It replaces the code of the method by a single instruction returning an *empty* instance.

For example:

```java
class A {
    int field;
    
    public Optional<Integer> getOptional() {
        return Optional.of(field);
    }
}  
```
 
is transformed to:

```java
class A {
    int field;
    
   public Optional<Integer> getOptional() {
           return Optional.empty();
   }
}  
```

This operator is not enabled by default.

## `argument` mutation operator
*New in version 1.3*

This operator replaces the body of a method by returning the value of the first parameter that has the same type as the return type of the method.

For example:

```java
class A {
    public int m(int x, int y) {
        return x + 2 * y;
    }
}
```

is transformed to:

```java
class A {
    public int m(int x) {
        return x;
    }
}
```

This operator is not enabled by default.

## `this` mutation operator
*New in version 1.3*

Replaces the body of a method by `return this;` if applicable. The goal of this operator is to perform better transformations targeting fluent APIs.

For example:

```java
class A {

    int value = 0;
    public A addOne() {
        value += 1;
        return this;
    }
}
```

is transformed to:

```java
class A {

    int value = 0;
    public A addOne() {
        return this;
    }
}
```

This operator is not enabled by default.

## Stop Methods

Descartes avoids some methods that are generally not interesting and may introduce false positives such as simple getters, simple setters, empty void methods or methods returning constant values, delegation patterns as well as deprecated and compiler generated methods. Those methods are automatically detected by inspecting their code.
A complete list of examples can be found [here](https://github.com/STAMP-project/pitest-descartes/blob/master/src/test/java/eu/stamp_project/test/input/StopMethods.java).
The exclusion of stop methods can be configured. For more details see: ["Running Descartes on your project"](./running-on-your-project.html).

## Do not use `null` in methods annotated with `@NotNull`
*New in version 1.2.6*

Descartes will avoid using the `null` operator in methods annotated with `@NotNull`. This increases its compatibility with Kotlin sources. This feature can be configured. See ["Running Descartes on your project"](./running-on-your-project.html) for more details.

## Skip methods using `@DoNotMutate`
*New in version 1.3*

Descartes skips all method that are annotated with *any* annotation whose name is `DoNotMutate`. 
For example, in the following fragment of code, the method `m` will not be mutated.

```java
class A {
    @DoNotMutate
    public void m() {/* ... */}
}
```

All methods in a class annotated with `@DoNotMutate` will be avoided as well. For example, in the following fragment of code, the method `m` will not be mutated:

```java
@DoNotMutate
class A {
    public void m() {/* ... */}
}
```

The `DoNotMutate` annotation may specify which operators should be considered. For example:

```java
class A {
    @DoNotMutate(operators = "false")
    public boolean m() { return true; }
}
```

will instruct Descartes not to use the `false` mutation operator to mutate `m`.

When specifying operators, a method annotation takes precedence over class annotations. That, is the `@DoNotMutate` of a method overrides the same annotation in the class For example:

```java
@DoNotMutate(operators = "true")
class A {

    public boolean n() { return false; }

    @DoNotMutate(operators = "false")
    public boolean m() { return true; }
}
```

will not mutate method `n` with `true`, as instructed in the class annotation. On the other hand, `m` will be mutated by `true` but not by `false`. 

Descartes includes a definition of `DoNotMutate`. However, when the tool inspects the annotations of a class or method it matches only the simple name of the annotation class and ignores the package. So, any `DoNotMutate` annotation will be considered. In this way a project does not need to add Descartes as a dependency, it can declare its own `DoNotMutate` and use it.

This feature is also configurable. See ["Running Descartes on your project"](./running-on-your-project.html) for more details.
