package eu.stamp_project.mutationtest.descartes.bodyanalysis;

import java.util.NoSuchElementException;

import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.commons.Method;

public class LineCounter {

    private final ClassName className;
    private final Method method;
    private int firstLine;
    private int lastLine;

    public LineCounter(ClassName className, Method method) {
        this.className = className;
		this.method = method;
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
        ensureNotEmpty();
        return firstLine;
    }

    public int getLastLine() {
        ensureNotEmpty();
        return lastLine;
    }

    private void ensureNotEmpty() {
      if (empty()) {
            throw new NoSuchElementException("Attempt to grab a line from an empty interval in: " + className.asJavaName() + " at " + method.getName() + " with " + method.getDescriptor());
      }
    }
}
