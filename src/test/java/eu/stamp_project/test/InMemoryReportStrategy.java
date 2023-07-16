package eu.stamp_project.test;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import org.pitest.util.ResultOutputStrategy;

public class InMemoryReportStrategy implements ResultOutputStrategy {
  Map<String, StringWriter> writers = new HashMap<>();

  @Override
  public Writer createWriterForFile(String sourceFile) {
    StringWriter writer = writers.get(sourceFile);
    // Check the createWriterForFile is not called several times for the same file
    // thus not using computeIfAbsent
    if (writer != null) {
      throw new InvalidParameterException("Writer for " + sourceFile + " was already created");
    }
    writer = new StringWriter();
    writers.put(sourceFile, writer);
    return writer;
  }

  public StringWriter getWriterCreatedForFile(String sourceFile) {
    StringWriter writer = writers.get(sourceFile);
    if (writer == null) {
      throw new InvalidParameterException("No writer has been created for source file " + sourceFile);
    }
    return writer;
  }

  public String getContentForFile(String sourceFile) {
    return getWriterCreatedForFile(sourceFile).toString();
  }

  public boolean hasFile(String sourceFile) {
    return writers.get(sourceFile) != null;
  }

  public boolean hasFile(Path path) {
    return hasFile(path.toString());
  }

}
