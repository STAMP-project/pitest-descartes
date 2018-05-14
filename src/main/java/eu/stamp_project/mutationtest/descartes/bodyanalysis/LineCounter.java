package eu.stamp_project.mutationtest.descartes.bodyanalysis;

import java.util.NoSuchElementException;

public class LineCounter {

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

    public boolean empty() { return firstLine > lastLine; }

    public int getCount() {
        if (empty())
            return 0;
        return lastLine - firstLine + 1;
    }

    public int getFirstLine() {
        if(empty())
            throw new NoSuchElementException("Attempt to grab a line from an empty interval.");
        return firstLine;
    }

    public int getLastLine() {
        if (empty())
            throw new NoSuchElementException("Attempt to grab a line from an empty interval.");
        return lastLine;

    }
}
