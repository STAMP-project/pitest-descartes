package eu.stamp_project.descartes.reporting;

import java.io.Writer;
import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;

public abstract class BaseMutationResultListener implements MutationResultListener {
  public BaseMutationResultListener(final ListenerArguments arguments) {
    this.arguments = Objects.requireNonNull(arguments);
  }

  private final ListenerArguments arguments;

  public Writer createWriterFor(String sourceFile) {
    return arguments.getOutputStrategy().createWriterForFile(sourceFile);
  }

  public long getElapsedMilliseconds() {
    return System.currentTimeMillis() - arguments.getStartTime();
  }

  public Duration getElapsedTime() {
    return Duration.ofMillis(getElapsedMilliseconds());
  }

  public Collection<String> getMutatorNames() {
    return arguments.getEngine().getMutatorNames();
  }

}
