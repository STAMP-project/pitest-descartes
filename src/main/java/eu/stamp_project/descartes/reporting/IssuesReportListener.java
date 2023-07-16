package eu.stamp_project.descartes.reporting;

import eu.stamp_project.descartes.reporting.models.ClassReport;
import eu.stamp_project.descartes.reporting.models.ProjectReport;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.MutationResultListenerFactory;
import org.pitest.util.Unchecked;


public class IssuesReportListener extends BaseMutationResultListener {

  private List<ClassReport> findings;
  private Template classTemplate;
  private Template indexTemplate;

  public IssuesReportListener(final ListenerArguments arguments) {
    super(arguments);
  }

  @Override
  public void runStart() {
    findings = new ArrayList<>();
    try {
      Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
      freemarkerConfig.setClassLoaderForTemplateLoading(
          IssuesReportListener.class.getClassLoader(),
          "templates"
      );
      indexTemplate = freemarkerConfig.getTemplate("html-report-index.ftlh");
      classTemplate = freemarkerConfig.getTemplate("html-report-class.ftlh");
    } catch (IOException exc) {
      throw Unchecked.translateCheckedException(exc);
    }
  }

  private Writer createFile(String path) {
    String filePath = Paths.get("issues", path).toString();
    return createWriterFor(filePath);
  }

  @Override
  public void handleMutationResult(ClassMutationResults classMutationResults) {
    ClassReport classReport = new ClassReport(classMutationResults);
    if (!classReport.hasIssues()) {
      return;
    }
    findings.add(classReport);
    try (Writer writer = createFile(classReport.getClassName() + ".html")) {
      classTemplate.process(classReport, writer);
    } catch (IOException | TemplateException exc) {
      throw Unchecked.translateCheckedException(
          "Could not write file for class: " + classReport.getClassName(), exc);
    }
  }

  @Override
  public void runEnd() {
    try (Writer writer = createFile("index.html")) {
      indexTemplate.process(new ProjectReport(
          getElapsedTime(),
          findings,
          getMutatorNames()
      ), writer);
    } catch (IOException | TemplateException exc) {
      throw Unchecked.translateCheckedException("Could not write report index file", exc);
    }
  }

  public static class Factory implements MutationResultListenerFactory {
    @Override
    public MutationResultListener getListener(
        Properties properties, ListenerArguments listenerArguments) {
      return new IssuesReportListener(listenerArguments);
    }

    @Override
    public String name() {
      return "ISSUES";
    }

    @Override
    public String description() {
      return "Pseudo and partially-tested methods HTML report";
    }
  }
}
