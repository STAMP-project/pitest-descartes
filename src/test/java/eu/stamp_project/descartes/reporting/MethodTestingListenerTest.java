package eu.stamp_project.descartes.reporting;


public class MethodTestingListenerTest extends JsonListenerTest<MethodTestingListener.Factory> {
  MethodTestingListenerTest() {
    super(MethodTestingListener.Factory.class);
  }

  @Override
  String getFileName() {
    return "methods";
  }
}


