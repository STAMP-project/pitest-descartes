package eu.stamp_project.descartes.reporting.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.TraceSignatureVisitor;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MutationDetails;

public class MethodDetails {

  private final Location location;
  private final String fileName;
  private final int lineNumber;

  public MethodDetails(Location location, String fileName, int lineNumber) {
    this.location = location;
    this.fileName = fileName;
    this.lineNumber = lineNumber;
  }

  public MethodDetails(MutationDetails mutationDetails) {
    this(
        mutationDetails.getId().getLocation(),
        mutationDetails.getFilename(),
        mutationDetails.getLineNumber()
    );
  }

  public MethodDetails(MutationResult mutationResult) {
    this(mutationResult.getDetails());
  }

  @JsonGetter("name")
  public String getMethodName() {
    return location.getMethodName();
  }

  @JsonGetter("desc")
  public String getMethodDesc() {
    return location.getMethodDesc();
  }

  @JsonGetter("class")
  public String getClassName() {
    return location.getClassName().getNameWithoutPackage().asInternalName();
  }

  @JsonGetter("package")
  public String getPackageName() {
    return location.getClassName().getPackage().asInternalName();
  }

  @JsonGetter("file")
  public String getFileName() {
    return fileName;
  }

  @JsonGetter("line")
  public int getLineNumber() {
    return lineNumber;
  }

  @JsonIgnore
  public String getDeclaration() {
    TraceSignatureVisitor visitor = new TraceSignatureVisitor(0);
    new SignatureReader(getMethodDesc()).accept(visitor);
    return getMethodName() + visitor.getDeclaration();
  }

  @JsonIgnore
  public String getReturnType() {
    TraceSignatureVisitor visitor = new TraceSignatureVisitor(0);
    new SignatureReader(getMethodDesc()).accept(visitor);
    return visitor.getReturnType();
  }

  @JsonIgnore
  public boolean isVoid() {
    return getMethodDesc().endsWith(")V");
  }

  public static String methodKey(MutationDetails mutationDetails) {
    String className = mutationDetails.getClassName().asJavaName();
    String methodName = mutationDetails.getMethod();
    String methodDescription = mutationDetails.getId().getLocation().getMethodDesc();
    return className + "." + methodName + methodDescription;
  }

  public static String methodKey(MutationResult mutation) {
    return methodKey(mutation.getDetails());
  }

}
