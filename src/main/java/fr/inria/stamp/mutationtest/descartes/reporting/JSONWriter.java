package fr.inria.stamp.mutationtest.descartes.reporting;


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;
import java.util.EnumSet;

enum WriterState {
    Initial, EmptyObject, Object, EmptyList, List, Value, Final
}


public class JSONWriter {
    Writer out;
    Stack<WriterState> memory;
    WriterState state;

    public JSONWriter(Writer out) {
        this.out = out;
        state = WriterState.Initial;
        memory = new Stack<WriterState>();
    }

    public void beginObject() throws IOException {
        beginStructure();
        state = WriterState.EmptyObject;
        out.write('{');
    }

    public void beginObjectAttribute(String key) throws IOException {
        beginAttribute(key); beginObject();
    }

    public void endObject() throws IOException {
        if(state != WriterState.EmptyObject && state != WriterState.Object)
            throw new IllegalStateException("Object not started");
        out.write('}');
        endStructure();
    }

    private void beginStructure() throws IOException {
        switch(state) {
            case EmptyObject: //Illegal states
            case Object:
            case Final:
                throw new IllegalStateException("Cant' start an object in the current state");
            case Initial: //Initial object or list
                memory.push(WriterState.Final);
                break;
            case List: //The new object or list is placed inside another list which already has some elements
                out.write(',');
            case EmptyList: //The new object or list is placed inside another list which has no other elements
                memory.push(WriterState.List);
                break;
            default: // The object or list is placed as value of an attribute
                memory.push(state);
        }
        //This is one case in which I guess the switch is better than other alternatives
        //Here it is sort of replacing a follow lookup in a LL(1) parser
        //The entire code of beginList and beginObject could be placed here adding some parameters
        //but I think this won't help readability
    }

    private void endStructure() {
        state = memory.pop();
        if(state == WriterState.Value)
            state = memory.pop();
    }

    public void beginList() throws IOException {
        beginStructure();
        state = WriterState.EmptyList;
        out.write('[');
    }

    public void beginListAttribute(String key) throws IOException {
        beginAttribute(key); beginList();
    }

    public void endList() throws IOException {
        if(state != WriterState.EmptyList && state != WriterState.List)
            throw new IllegalStateException("List not started");
        out.write(']');
        endStructure();
    }

    public void beginAttribute(String key) throws IOException {
        if(state != WriterState.Object && state != WriterState.EmptyObject)
            throw new IllegalStateException("Attributes can only be added to objects");
        if(state == WriterState.Object)
            out.write(',');
        memory.push(WriterState.Object);
        state = WriterState.Value;
        out.write(String.format("\"%1$s\":", escapeString(key)));
    }

    public void writeAttribute(String key, int value) throws IOException {
        beginAttribute(key); write(value);
    }

    public void writeAttribute(String key, long value) throws IOException {
        beginAttribute(key); write(value);
    }

    public void writeAttribute(String key, boolean value) throws IOException {
        beginAttribute(key); write(value);
    }

    public void writeAttribute(String key, String value) throws IOException {
        beginAttribute(key); write(value);
    }

    private void writeValue(String value) throws IOException {
        if(state != WriterState.List && state != WriterState.EmptyList && state != WriterState.Value)
            throw new IllegalStateException("Can't place a value in current context");
        if(state == WriterState.List)
            out.write(',');
        if(state == WriterState.EmptyList)
            state = WriterState.List;
        out.write(value);
        if(state == WriterState.Value)
            state = memory.pop();
    }

    public void write(String value) throws IOException {
        writeValue(String.format("\"%1$s\"", escapeString(value)));
    }

    public void write(int value) throws IOException {
        writeValue(String.valueOf(value));
    }

    public void write(long value) throws IOException {
        writeValue(String.valueOf(value));
    }

    public void write(boolean value) throws IOException {
        writeValue(String.valueOf(value).toLowerCase());
    }

    public void close() throws IOException {
        if(state != WriterState.Final)
            throw new IllegalStateException("JSON structure isn't finished");
        out.close();
    }

    public static String escapeString(String value) {
        StringWriter result = new StringWriter(value.length());
        for(int i=0; i< value.length(); i++) {
            char c = value.charAt(i);
            String toWrite = null;
            if(c < 128) {
                toWrite = ESCAPE_SEQUENCES[c];
            }
            else if(c == '\u2028') {
                toWrite = "\\u2028";
            }
            else if(c == '\u2029') {
                toWrite = "\\u2028";
            }
            if(toWrite == null)
                result.write(c);
            else
                result.write(toWrite);
        }
        return result.getBuffer().toString();
    }

    private final static String[] ESCAPE_SEQUENCES;
    static {
        //Adapted from GSON Library
        //https://github.com/google/gson/blob/master/gson/src/main/java/com/google/gson/stream/JsonWriter.java

        ESCAPE_SEQUENCES = new String[128];
        for(int i=0; i <= 31; i++)
            ESCAPE_SEQUENCES[i] = String.format("\\u%04x", i);

        ESCAPE_SEQUENCES['"'] = "\\\"";
        ESCAPE_SEQUENCES['\\'] = "\\\\";
        ESCAPE_SEQUENCES['\t'] = "\\t";
        ESCAPE_SEQUENCES['\b'] = "\\b";
        ESCAPE_SEQUENCES['\n'] = "\\n";
        ESCAPE_SEQUENCES['\r'] = "\\r";
        ESCAPE_SEQUENCES['\f'] = "\\f";

    }

}
