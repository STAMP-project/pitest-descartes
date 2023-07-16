package eu.stamp_project.descartes.reporting;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Stack;


public class JsonWriter {

  enum WriterState {
    INITIAL,
    EMPTY_OBJECT,
    OBJECT,
    EMPTY_LIST,
    LIST,
    VALUE,
    FINAL
  }

  Writer out;
  Stack<WriterState> memory;
  WriterState state;

  public JsonWriter(Writer out) {
    this.out = out;
    state = WriterState.INITIAL;
    memory = new Stack<>();
  }

  public void beginObject() throws IOException {
    beginStructure();
    state = WriterState.EMPTY_OBJECT;
    out.write('{');
  }

  public void beginObjectAttribute(String key) throws IOException {
    beginAttribute(key);
    beginObject();
  }

  public void endObject() throws IOException {
    if (state != WriterState.EMPTY_OBJECT && state != WriterState.OBJECT) {
      throw new IllegalStateException("Object not started");
    }
    out.write('}');
    endStructure();
  }

  @SuppressWarnings("checkstyle:fallthrough")
  private void beginStructure() throws IOException {
    switch (state) {
      case EMPTY_OBJECT: // Illegal states
      case OBJECT:
      case FINAL:
        throw new IllegalStateException("Cant' start an object in the current state");
      case INITIAL: // Initial object or list
        memory.push(WriterState.FINAL);
        break;
      case LIST:
        // The new object or list is placed inside another list
        // which already has some elements
        out.write(',');
      case EMPTY_LIST:
        // The new object or list is placed inside another list
        // which has no other elements
        memory.push(WriterState.LIST);
        break;
      default: // The object or list is placed as value of an attribute
        memory.push(state);
    }
    // This is one case in which I guess the switch is better than other alternatives
    // Here it is sort of replacing a follow lookup in a LL(1) parser
    // The entire code of beginList and beginObject could be placed here adding some parameters
    // ,but I think this won't help readability
  }

  private void endStructure() {
    state = memory.pop();
    if (state == WriterState.VALUE) {
      state = memory.pop();
    }
  }

  public void beginList() throws IOException {
    beginStructure();
    state = WriterState.EMPTY_LIST;
    out.write('[');
  }

  public void beginListAttribute(String key) throws IOException {
    beginAttribute(key);
    beginList();
  }

  public void endList() throws IOException {
    if (state != WriterState.EMPTY_LIST && state != WriterState.LIST) {
      throw new IllegalStateException("List not started");
    }
    out.write(']');
    endStructure();
  }

  public void writeStringList(Collection<String> values) throws IOException {
    beginList();
    for (String value : values) {
      write(value);
    }
    endList();
  }

  public void writeStringListAttribute(String key, Collection<String> values) throws IOException {
    beginAttribute(key);
    writeStringList(values);
  }

  public void beginAttribute(String key) throws IOException {
    if (state != WriterState.OBJECT && state != WriterState.EMPTY_OBJECT) {
      throw new IllegalStateException("Attributes can only be added to objects");
    }
    if (state == WriterState.OBJECT) {
      out.write(',');
    }
    memory.push(WriterState.OBJECT);
    state = WriterState.VALUE;
    out.write(String.format("\"%1$s\":", escapeString(key)));
  }

  public void writeAttribute(String key, int value) throws IOException {
    beginAttribute(key);
    write(value);
  }

  public void writeAttribute(String key, long value) throws IOException {
    beginAttribute(key);
    write(value);
  }

  public void writeAttribute(String key, boolean value) throws IOException {
    beginAttribute(key);
    write(value);
  }

  public void writeAttribute(String key, String value) throws IOException {
    beginAttribute(key);
    write(value);
  }

  private void writeValue(String value) throws IOException {
    if (state != WriterState.LIST
        && state != WriterState.EMPTY_LIST
        && state != WriterState.VALUE) {
      throw new IllegalStateException("Can't place a value in current context");
    }
    if (state == WriterState.LIST) {
      out.write(',');
    }
    if (state == WriterState.EMPTY_LIST) {
      state = WriterState.LIST;
    }
    out.write(value);
    if (state == WriterState.VALUE) {
      state = memory.pop();
    }
  }

  public void write(String value) throws IOException {
    if (value == null) {
      writeValue("null");
    } else {
      writeValue(String.format("\"%1$s\"", escapeString(value)));
    }
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
    if (state != WriterState.FINAL) {
      throw new IllegalStateException("JSON structure isn't finished");
    }
    out.close();
  }

  public static String escapeString(String value) {
    StringWriter result = new StringWriter(value.length());
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      String toWrite = null;
      if (c < 128) {
        toWrite = ESCAPE_SEQUENCES[c];
      } else if (c == '\u2028') {
        toWrite = "\\u2028";
      } else if (c == '\u2029') {
        toWrite = "\\u2028";
      }
      if (toWrite == null) {
        result.write(c);
      } else {
        result.write(toWrite);
      }
    }
    return result.getBuffer().toString();
  }

  private static final String[] ESCAPE_SEQUENCES;

  static {
    // Adapted from GSON Library
    // https://github.com/google/gson/blob/master/gson/src/main/java/com/google/gson/stream/JsonWriter.java

    ESCAPE_SEQUENCES = new String[128];
    for (int i = 0; i <= 31; i++) {
      ESCAPE_SEQUENCES[i] = String.format("\\u%04x", i);
    }

    ESCAPE_SEQUENCES['"'] = "\\\"";
    ESCAPE_SEQUENCES['\\'] = "\\\\";
    ESCAPE_SEQUENCES['\t'] = "\\t";
    ESCAPE_SEQUENCES['\b'] = "\\b";
    ESCAPE_SEQUENCES['\n'] = "\\n";
    ESCAPE_SEQUENCES['\r'] = "\\r";
    ESCAPE_SEQUENCES['\f'] = "\\f";
  }
}
