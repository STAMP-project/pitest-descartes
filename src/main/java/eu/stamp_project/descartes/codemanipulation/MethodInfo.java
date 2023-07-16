package eu.stamp_project.descartes.codemanipulation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;

public class MethodInfo extends ElementInfo {

  private final Type returnType;
  private final List<Type> argumentTypes;
  private final Set<Type> exceptions;
  private final TypeInfo owner;
  private final String descriptor;

  public MethodInfo(Method method) {
    super(
        method.getModifiers(),
        method.getName(),
        method.toGenericString());
    Type methodType = Type.getType(method);
    owner = new TypeInfo(method.getDeclaringClass());
    descriptor = methodType.getDescriptor();
    returnType = methodType.getReturnType();
    argumentTypes = List.of(methodType.getArgumentTypes());
    exceptions =
        Stream.of(method.getExceptionTypes()).map(Type::getType).collect(Collectors.toSet());
  }

  public MethodInfo(
      TypeInfo owner, int access, String name, String desc, String signature, String[] exceptions) {
    super(access, name, signature);
    this.owner = owner;
    descriptor = desc;
    returnType = Type.getReturnType(desc);
    argumentTypes = List.of(Type.getArgumentTypes(desc));
    this.exceptions = toTypeSet(exceptions);
  }

  public Type getReturnType() {
    return returnType;
  }

  public List<Type> getArgumentTypes() {
    return argumentTypes;
  }

  public Set<Type> getExceptions() {
    return exceptions;
  }

  public TypeInfo getOwner() {
    return owner;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public boolean isConstructor() {
    return "<init>".equals(name);
  }

  public boolean hasNoCode() {
    return hasFlag(Opcodes.ACC_NATIVE | Opcodes.ACC_ABSTRACT);
  }
}
