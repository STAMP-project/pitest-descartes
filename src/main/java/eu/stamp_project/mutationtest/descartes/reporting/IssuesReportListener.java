package eu.stamp_project.mutationtest.descartes.reporting;

import eu.stamp_project.mutationtest.descartes.reporting.models.MethodRecord;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.util.Unchecked;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class IssuesReportListener implements MutationResultListener {

    private ListenerArguments arguments;

    private VelocityEngine engine;
    private Template methodResportTemplate;
    private Template indexTemplate;

    private List<ClassIssues> findings;


    public IssuesReportListener(final ListenerArguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public void runStart() {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();

        methodResportTemplate = engine.getTemplate("templates/method-report.vm", "UTF-8");
        indexTemplate = engine.getTemplate("templates/index-report.vm", "UTF-8");

        findings = new ArrayList<>();
    }

    private Writer createFile(String path) {
        String filePath = Paths.get("issues", path).toString();
        return arguments.getOutputStrategy().createWriterForFile(filePath);
    }

    @Override
    public void handleMutationResult(ClassMutationResults classMutationResults) {

        MethodRecord[] issues = MethodRecord.getRecords(classMutationResults)
                .filter(MethodRecord::hasIssues)
                .toArray(MethodRecord[]::new);

        if (issues.length == 0) {
            return;
        }

        String className = classMutationResults.getMutatedClass().asJavaName();
        VelocityContext context = new VelocityContext();
        context.put("className", className);
        context.put("issues", issues);

        try (Writer writer = createFile(className + ".html")) {
            methodResportTemplate.merge(context, writer);
        } catch (IOException exc) {
            throw Unchecked.translateCheckedException("Could not write file for class: " + className, exc);
        }

        findings.add(new ClassIssues(className, issues.length));
    }

    private String getStringTime() {
        //TODO: Is there a way to have this code in the template?

        Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - arguments.getStartTime());
        String[] units = {"day", "hour", "minute", "second"};
        long[] parts = { elapsed.toDays(), elapsed.toHours() % 24, elapsed.toMinutes() % 60, elapsed.getSeconds() % 60};
        StringBuilder result = new StringBuilder();
        for(int i=0; i < parts.length; i++) {
            if(parts[i] == 0) continue;
            result.append(parts[i]).append(" ").append(units[i]);
            if(parts[i] > 1)
                result.append("s");
            result.append(" ");
        }
        if(result.length() == 0)
            return "less than one second";
        return result.substring(0, result.length() - 1);
    }

    @Override
    public void runEnd() {

        VelocityContext context = new VelocityContext();
        context.put("duration", getStringTime());
        context.put("total", findings.stream().mapToInt(ClassIssues::getIssues).sum()); // Is there a way to put this code into the template??
        context.put("findings", findings);
        context.put("operators", arguments.getEngine().getMutatorNames());

        try (Writer writer = createFile("index.html")) {
            indexTemplate.merge(context, writer);
        }
        catch (IOException exc) {
           throw Unchecked.translateCheckedException("Could not write report index file", exc);
        }

    }

    public static class ClassIssues {
        private String className;
        private int issues;

        public ClassIssues(String className, int issues) {
            this.className = className;
            this.issues = issues;
        }

        public String getClassName() {
            return className;
        }

        public int getIssues() {
            return issues;
        }
    }

}
