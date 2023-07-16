package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.descartes.operators.parsing.LiteralParser;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.pitest.reloc.asm.commons.GeneratorAdapter;

public abstract class MutationOperator {

  public abstract boolean canMutate(MethodInfo method);

  public void writeMutant(MethodInfo method, GeneratorAdapter code) {
    if (!canMutate(method)) {
      throw new IllegalArgumentException(
          "Operator " + getIdentifier() + " can not mutate method " + method.getDescriptor());
    }
    generateCode(method, code);
  }

  protected abstract void generateCode(MethodInfo method, GeneratorAdapter generator);

  public String getIdentifier() {
    return getClass().getAnnotation(Operator.class).identifier();
  }

  public String getDescription() {
    return getClass().getAnnotation(Operator.class).description();
  }

  public static MutationOperator fromIdentifier(final String id) {
    final String trimmedId = id.trim();
    if (ID_2_CLASS.containsKey(trimmedId)) {
      Class<?> operatorClass = ID_2_CLASS.get(trimmedId);
      try {
        return (MutationOperator) operatorClass.getConstructor().newInstance();
      } catch (InstantiationException
          | IllegalAccessException
          | InvocationTargetException
          | NoSuchMethodException exc) {
        throw new AssertionError(
            "Could not invoke default constructor for class " + operatorClass.getName(), exc);
      }
    }
    LiteralParser parser = new LiteralParser();
    LiteralParser.Result result = parser.parse(trimmedId);
    if (result.hasError()) {
      throw new InvalidOperatorException(
          "Provided identifier \"" + trimmedId + "\" is not a valid mutation operator identidier.");
    }
    return new ConstantMutationOperator(trimmedId, result.getValue());
  }

  public static List<MutationOperator> fromIdentifiers(final Collection<String> identifiers) {
    return identifiers.stream().map(MutationOperator::fromIdentifier).collect(Collectors.toList());
  }

  private static final Class<?>[] PARAMETERLESS_OPERATORS = {
    EmptyArrayMutationOperator.class,
    NewInstanceMutationOperator.class,
    NullMutationOperator.class,
    OptionalMutationOperator.class,
    VoidMutationOperator.class,
    ArgumentPropagationOperator.class,
    ThisMutationOperator.class
  };

  private static final Map<String, Class<?>> ID_2_CLASS;

  static {
    ID_2_CLASS = new HashMap<>(PARAMETERLESS_OPERATORS.length);
    Arrays.stream(PARAMETERLESS_OPERATORS)
        .forEach(opc -> ID_2_CLASS.put(opc.getAnnotation(Operator.class).identifier(), opc));
  }
}
