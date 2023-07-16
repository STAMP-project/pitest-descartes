package eu.stamp_project.descartes.codemanipulation;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class LineCounter {

  private int firstLine;
  private int lastLine;

  public LineCounter() {
    reset();
  }

  public void reset() {
    firstLine = Integer.MAX_VALUE;
    lastLine = Integer.MIN_VALUE;
  }

  public void registerLine(int line) {
    firstLine = Math.min(firstLine, line);
    lastLine = Math.max(lastLine, line);
  }

  public boolean empty() {
    return firstLine > lastLine;
  }

  public int getCount() {
    if (empty()) {
      return 0;
    }
    return lastLine - firstLine + 1;
  }

  public int getFirstLine() {
    ensureNotEmpty();
    return firstLine;
  }

  public int getLastLine() {
    ensureNotEmpty();
    return lastLine;
  }

  public Collection<Integer> getShiftedRange() {
    if (empty()) {
      return Collections.emptyList();
    }
    // NOTE: MutationDetails has the following two methods:
    //  getFirstIndex() { return this.id.getFirstIndex(); }
    // which returns the first index included in the identifier
    //  getInstructionIndex() { return getFirstIndex() - 1; }
    // which is used mostly for filters
    // Returning 0 as the first index results in an index out of bound exception in the filters
    // that's why I return 1 here. In any case it is really hard to create a method covered in the
    // first bytecode instruction (0) and not the second one (1).
    // Here the first index is the one that really counts as all tests will execute it and this
    // will determine the coverage.
    return IntStream.rangeClosed(1, lastLine - firstLine + 1).boxed().collect(Collectors.toList());
  }

  private void ensureNotEmpty() {
    if (empty()) {
      throw new NoSuchElementException("Attempt to grab a line from an empty interval");
    }
  }
}
