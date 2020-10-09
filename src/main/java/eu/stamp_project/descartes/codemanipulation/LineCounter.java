package eu.stamp_project.descartes.codemanipulation;

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineCounter {

    private int firstLine;
    private int lastLine;

    public LineCounter() { reset(); }

    public void reset() {
        firstLine = Integer.MAX_VALUE;
        lastLine = Integer.MIN_VALUE;
    }

    public void registerLine(int line) {
        firstLine = Math.min(firstLine, line);
        lastLine = Math.max(lastLine, line);
    }

    public boolean empty() { return firstLine > lastLine; }

    public int getCount() {
        if (empty())
            return 0;
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
        if(empty())
            return Collections.emptyList();
        return IntStream.rangeClosed(1, lastLine - firstLine + 1).boxed().collect(Collectors.toList());
    }

    private void ensureNotEmpty() {
      if (empty())
            throw new NoSuchElementException("Attempt to grab a line from an empty interval");
    }
}
