package eu.stamp_project.descartes.codemanipulation;

import org.pitest.reloc.asm.Type;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeInfo extends ElementInfo {

    public static final int UNKNOWN_VERSION = -1;

    private final int version;

    private final Type type;
    private final Type superType;
    private final Set<Type> interfaces;

    public TypeInfo(Class<?> type) {
        super(type.getModifiers(), Type.getType(type).getInternalName(), type.toGenericString());
        version = UNKNOWN_VERSION;
        this.type = Type.getType(type);
        superType = Type.getType(type.getSuperclass());
        interfaces = Stream.of(type.getInterfaces()).map(Type::getType).collect(Collectors.toSet());
    }

    public TypeInfo(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super(access, name, signature);
        this.version = version;
        type = Type.getObjectType(name);
        superType = Type.getObjectType(superName);
        this.interfaces = toTypeSet(interfaces);
    }

    public int getVersion() { return version; }

    public Type getSuperType() {  return superType; }

    public Set<Type> getInterfaces() { return interfaces; }

    public boolean canBeAssignedTo(Type other) {
        return type.equals(other)
                || superType.equals(other)
                || interfaces.contains(other)
                || other.equals(Type.getType(Object.class));
    }

}
