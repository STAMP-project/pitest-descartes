package eu.stamp_project.descartes.reporting.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.pitest.mutationtest.ClassMutationResults;

public class ClassReport {
  private final String className;

  private final Collection<MethodReport> methods;

  public ClassReport(ClassMutationResults classMutationResults) {
    this(
        classMutationResults.getMutatedClass().asJavaName(),
        MethodReport.getMethodReports(classMutationResults).collect(Collectors.toList())
    );
  }

  public ClassReport(final String className, final Collection<MethodReport> methods) {
    this.className = className;
    this.methods = new ArrayList<>(methods);
  }

  public String getClassName() {
    return className;
  }

  public int getNumberOfIssues() {
    // The number of methods in one class is limited to 65535
    return (int) methods.stream().filter(MethodReport::hasIssues).count();
  }

  public boolean hasIssues() {
    return getNumberOfIssues() > 0;
  }

  public Collection<MethodReport> getMethods() {
    return Collections.unmodifiableCollection(methods);
  }

  public Collection<MethodReport> getMethodsWithIssues() {
    return methods.stream().filter(MethodReport::hasIssues).collect(Collectors.toList());
  }

}
