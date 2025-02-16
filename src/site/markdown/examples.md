# Examples

Descartes has been applied to well-maintained and popular open source projects, and it has found relevant testing issues.
This page shows concrete examples of poorly tested methods discovered on those projects with the help of Descartes.
More details can be found [here](https://github.com/STAMP-project/descartes-experiments).

## Example #1 : A test case with no assertions

The code block below shows an excerpt of a test case. This test case is the only one verifying the functionality of the `isEncodeEqual` boolean method, which is invoked for every input pair in the `data` array.

```java
public void TestIsEncodeEquals() {
  final String[][] data = {
    {"Meyer", "Mayr"},
    ...
    {"Miyagi", "Miyako"}
    };
  for (final String[] element : data) {
    final boolean encodeEqual = getStringEncoder().isEncodeEqual(element[1], element[0]);
  }
}
```

This test case is missing an assertion. Descartes classifies `isEncodeEqual` as **pseudo-tested**. The return value of the method can be changed to any value and the test case will not fail.

## Example #2 : A weak assertion

The following code extract shows a test case that verifies the `void` `write` method of the `TeeOutputStream` class.
This method is supposed to send the given input data to two underlying output streams: `baos1` and `baos2`.
The assertion verifies that both output streams have the same content. However, this assertion is not enough to verify the `write` method.

```java
public void testTee() { 
  ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
  ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
  TeeOutputStream tos = new TeeOutputStream(baos1, baos2); 
  ... 
  tos.write(array); 
  assertByteArrayEquals(baos1.toByteArray(), baos2.toByteArray());
}
```

When Descartes removes the code of the `write` method, nothing is written to the underlying streams and the test still passes as the two streams have the same content: an empty buffer.
Descartes then classifies the method as **pseudo-tested**.

To fix the code, one must check that both outputs have the expected value, that is, the content of both must be equal to `array`:

```java
assertByteArrayEquals(array, baos1.toByteArray());
assertByteArrayEquals(array, baos2.toByteArray());
```

## Example #3 : Incorrect exception verification

The following code block shows the fragment of a `SingletonListIterator` class and a test case verifying the `add` method from the `Iterator` interface. The method is not supported by the class, and it throws the corresponding exception. The test case was written with that behavior in mind.

```java
class SingletonListIterator implements Iterator<Node> {
  ...
  void add() {
    throw new UnsupportedOperationException();
  }
  ...
}

class SingletonListIteratorTest {
  ...

  @Test
  void testAdd() {
    SingletonListIterator it = ...
    ...
    try {
      it.add(value);
    } 
    catch (Exception ex) {
      // empty
    }
    ...
  }
}
```

Descartes classifies the `add` method as **pseudo-tested**. When Descartes removes the code of `add`, no exception is thrown and yet the test case still passes and there is no other test case verifying this method.

Test cases verifying exceptions should include a `fail` invocation right after the code that is expected to throw the exception or use a dedicates assertion.

## Example # 4 : Not testing the error case

Testing only the *happy path* is a well known test smell. Our test suites should include test cases where the behavior of our code is verified against problematic inputs.

The code below shows the `checkNumberOfArgs` method and how it is used. the test suite of the project that includes this code has several test cases executing this method.

```java
class AnyOfAny {
  protected void checkNumberOfArgs(int numInputs) {
    if (numInputs < 2) {
      throw new IllegalArgumentException();
    }
  }
  
  public void evaluate(String[] args) {
    checkNumberOfArgs(args.length);
    ...
  }
}
```

When Descartes removes the code of `checkNumberOfArgs` no test case fails and the method is classified as **pseudo-tested**. This means that no test case is actually checking the behavior of the code when `numInputs` is lower than 2.

## Example # 5 : A misplaced assertion

The code below shows the `SdkTLSSocketFactory` class and an example of a test case verifying its implementation. The test case uses a custom-made mock that checks the values being passed to `setEnabledProtocols`.

```java

class SdkTLSSocketFactory {
  protected void prepareSocket(SSLSocket socket) {
    ...
    socket.setEnabledProtocols(protocols);
    ...
  }
}

@Test
void typical() {
  SdkTLSSocketFactory f = ...
  f.prepareSocket(new TestSSLSocket() {
    ...

    @Override
    public void setEnabledProtocols(String[] protocols) {
      assertTrue(Arrays.equals(protocols, expected));
    }
    ...
  });
}
```
 When Descartes removes the code of `prepareSocket`, the test case does not fail and the method is classified as **pseudo-tested**. When the code  of the method is removed the assertion is never invoked. Although the transformation performed by Descartes is rather extreme, and it is not likely a bug introduced during development, there could be an error that can prevent the invocation of `setEnabledProtocols` under certain input or program state and no test case would detect the issue.
 
To improve the test, one should check that `setEnabledProtocols` is actually invoked and that it is invoked with the right arguments. This is easy to achieve with a mocking library as [Mockito](https://site.mockito.org/).