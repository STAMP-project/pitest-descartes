package eu.stamp_project.mutationtest.descartes.bodyanalysis;

public class LineCounter {

    private boolean started = false; //Flag :(

    private int firstLine;
    private int currentLine;

    public LineCounter() {
        reset();
    }

    public void reset() {
        started = false;
    }

    public void registerLine(int line) {
        if(!started) {
            firstLine = line;
            started = true;
        }
        currentLine = line;
    }

    public int getCount() {
        return currentLine - firstLine + 1; //??
    }

    public int getFirstLine() {
        return firstLine;
    }

    public int getLine() {
        return currentLine;
    }
}
