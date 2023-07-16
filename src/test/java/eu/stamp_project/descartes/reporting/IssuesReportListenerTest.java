package eu.stamp_project.descartes.reporting;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.stamp_project.descartes.reporting.models.ClassReport;
import eu.stamp_project.test.InMemoryReportStrategy;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.MutationResultListener;

class IssuesReportListenerTest extends ListenerTest<IssuesReportListener.Factory>{

  IssuesReportListenerTest() {
    super(IssuesReportListener.Factory.class);
  }

  @ParameterizedTest
  @MethodSource("provideClassMutationResults")
  void testReportGeneratesReportFiles(Collection<ClassMutationResults> results)
      throws InvocationTargetException, NoSuchMethodException, InstantiationException,
      IllegalAccessException {
    InMemoryReportStrategy reportStrategy = new InMemoryReportStrategy();
    MutationResultListener listener = getListener(reportStrategy);
    handleMutationResults(listener, results);
    assertTrue(reportStrategy.hasFile(Paths.get("issues", "index.html")),
        "Final report does not contain index file"
    );
    for (ClassMutationResults classMutationResults : results) {
      ClassReport classReport = new ClassReport(classMutationResults);
      Path expectedClassReportPath = Paths.get("issues/", classReport.getClassName() + ".html");
      if (classReport.hasIssues()) {
        assertTrue(reportStrategy.hasFile(expectedClassReportPath),
            "No report was generated for class with testing issues: " + classReport.getClassName()
        );
      }
      else {
        assertFalse(reportStrategy.hasFile(expectedClassReportPath),
            "A report was generated for a class with no issues: " + classReport.getClassName());
      }
    }



  }

}
