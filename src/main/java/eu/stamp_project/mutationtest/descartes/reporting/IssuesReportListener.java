package eu.stamp_project.mutationtest.descartes.reporting;

import eu.stamp_project.mutationtest.descartes.reporting.models.MethodClassification;
import eu.stamp_project.mutationtest.descartes.reporting.models.MethodRecord;
import j2html.tags.ContainerTag;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;
import org.pitest.util.Unchecked;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class IssuesReportListener implements MutationResultListener {

    private ListenerArguments arguments;
    private long totalIssues = 0;

    ContainerTag classList;

    public IssuesReportListener(final ListenerArguments arguments) {
        this.arguments = arguments;
    }


    @Override
    public void runStart() {
        classList = ul();
        totalIssues = 0;
        transferCSS();
    }

    private Writer createFile(String path) {
        String filePath = Paths.get("issues", path).toString();
        return arguments.getOutputStrategy().createWriterForFile(filePath);
    }

    private void transferCSS() {
        try (Writer writer = createFile("style.css")) {
            InputStream originalCss = getClass().getClassLoader().getResourceAsStream("files/style.css");
            InputStreamReader reader = new InputStreamReader(originalCss);

            char[] buffer = new char[originalCss.available()];
            int read;
            while((read = reader.read(buffer)) >= 0) {
                writer.write(buffer, 0, read);
            }
            reader.close();
            writer.close();
        }
        catch (IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }

    public void handleMethod(MethodRecord method) {
        try(Writer writer = createFile(getFilePath(method))) {
            String declaration = method.declaration();
            ContainerTag backLink = a("[Back]").withHref("../index.html");

            ContainerTag document = body(
                    h1(declaration),
                    backLink,
                    dl(
                            dt("Class"), dd(method.className()),
                            dt("Package"), dd(method.packageName())

                    ),
                    p(text("This method is "), strong(method.getClassification().toString())));

            document.with(h2("Transformations"));

            if(method.getClassification() == MethodClassification.PARTIALLY_TESTED)
                document.with(
                        p(text("It seems that this method has been tested to return only the following value(s): "),
                        text(  method.getUndetectedMutations().stream().map(
                                mutation -> {
                                    String mutator = mutation.getDetails().getMutator();
                                    if(mutator.equals("empty")) //TODO: Improve hard coded array verification
                                        return "an empty array";
                                    return mutator;
                                }).collect(Collectors.joining(", "))),
                        text(".")));

            if(method.isVoid()) //Void method with issues is pseudo-tested
                document.with(p("To body of this method was removed but the test suite was not able to detect the transformation."));
            else {
                document.with(
                        p("The following transformations were applied but they were not detected by the test suite:"),
                        ul( each(method.getUndetectedMutations(),  mutation ->
                                li(mutation.getDetails().getDescription())))
                );
            }

            if(method.getClassification() == MethodClassification.PARTIALLY_TESTED) {
                document.with(p("The following transformations were detected by the test suite when applied."),
                        ul(each(method.getDetectedMutations(), mutation ->
                                li(mutation.getDetails().getDescription()))));
            }
            document.with(
                    h2("Tests"),
                    p("The method is covered by the following test cases:"),
                    ul( each(method.getTests(), test -> li(test.getName()))))
            .with(backLink);

            html(head(title(declaration)), link().withHref("../style.css").withRel("stylesheet")).with(document).render(writer);

        }
        catch (IOException exc) {
            throw Unchecked.translateCheckedException(exc);
        }
    }

    @Override
    public void handleMutationResult(ClassMutationResults classMutationResults) {

        ContainerTag methodList = ul();

        long issues = MethodRecord.getRecords(classMutationResults)
                .filter(MethodRecord::hasIssues)
                .peek( method -> {
                    handleMethod(method);
                    methodList.with(
                            li(
                                    a(method.declaration()).withHref(getFilePath(method)),
                                    text(" "),
                                    b(method.getClassification().toString())));
                })
                .count();
        totalIssues += issues;
        if(issues == 0) return; //Write only classes wiht issues

        classList.with(
                li(text(classMutationResults.getMutatedClass().asJavaName()),
                        text(" ("),
                        strong("Issues: "),
                        text(Long.toString(issues) + ")"))
                        .with(methodList));
    }

    private String getFilePath(MethodRecord method) {
        return Paths.get(method.getLocation().getClassName().asJavaName(), method.declaration() + ".html").toString();
    }

    private String getStringTime() {

        Duration ellapsed = Duration.ofMillis(System.currentTimeMillis() - arguments.getStartTime());
        String[] units = {"day", "hour", "minute", "second"};
        long[] parts = { ellapsed.toDays(), ellapsed.toHours() % 24, ellapsed.toMinutes() % 60, ellapsed.getSeconds() % 60};
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

        try (Writer writer = createFile("index.html")){

            html(
                    head(title("Testing issues report"), link().withHref("style.css").withRel("stylesheet")),
                    body(
                            h1("Issues Report"),
                            dl(
                                    dt("Duration"), dd(getStringTime()),
                                    dt("Issues"), dd(Long.toString(totalIssues))
                            ),
                            h2("Classes"), classList))
                    .render(writer);

        }
        catch (IOException exc) {
           throw Unchecked.translateCheckedException(exc);
        }

    }

}
