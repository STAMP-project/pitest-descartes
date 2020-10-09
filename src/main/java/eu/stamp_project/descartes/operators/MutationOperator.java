package eu.stamp_project.descartes.operators;

import eu.stamp_project.descartes.annotations.Operator;
import eu.stamp_project.descartes.codemanipulation.MethodInfo;
import eu.stamp_project.descartes.operators.parsing.LiteralParser;
import org.pitest.reloc.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class MutationOperator {

    public abstract boolean canMutate(MethodInfo method);

    public abstract void generateCode(MethodInfo method, MethodVisitor generator);

    public String getIdentifier() {
        return getClass().getAnnotation(Operator.class).identifier();
    }

    public String getDescription() {
        return getClass().getAnnotation(Operator.class).description();
    }

    public static MutationOperator fromID(String id) {
        id = id.trim();
        if(ID_2_CLASS.containsKey(id)) {
            Class<?> operatorClass = ID_2_CLASS.get(id);
            try {
                return (MutationOperator) operatorClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exc) {
                throw new AssertionError("Could not invoke default constructor for class " + operatorClass.getName(), exc);
            }
        }
        LiteralParser parser = new LiteralParser();
        LiteralParser.Result result = parser.parse(id);
        if(result.hasError()) {
            throw new InvalidOperatorException("Provided identifier \"" + id + "\" is not a valid mutation operator identidier.");
        }
        return new ConstantMutationOperator(id, result.getValue());
    }

    private final static Class<?>[] PARAMETERLESS_OPERATORS =  {
            EmptyArrayMutationOperator.class,
            NewInstanceMutationOperator.class,
            NullMutationOperator.class,
            OptionalMutationOperator.class,
            VoidMutationOperator.class
    };

    private final static Map<String, Class<?>> ID_2_CLASS;
    static {
        ID_2_CLASS = new HashMap<>(PARAMETERLESS_OPERATORS.length);
        Arrays.stream(PARAMETERLESS_OPERATORS).forEach(opc ->
                ID_2_CLASS.put(opc.getAnnotation(Operator.class).identifier(), opc));
    }
}
