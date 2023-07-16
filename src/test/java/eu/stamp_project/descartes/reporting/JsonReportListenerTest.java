package eu.stamp_project.descartes.reporting;

public class JsonReportListenerTest extends JsonListenerTest<JsonReportListener.Factory> {
  JsonReportListenerTest() {
    super(JsonReportListener.Factory.class);
  }

  @Override
  String getFileName() {
    return "mutations";
  }
}
