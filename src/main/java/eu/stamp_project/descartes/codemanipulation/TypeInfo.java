package eu.stamp_project.descartes.codemanipulation;

import org.pitest.reloc.asm.Type;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeInfo extends ElementInfo {

    public static final int UNKNOWN_VERSION = -1;

    private final int version;

    private final Type superType;
    private final Set<Type> interfaces;

    public TypeInfo(Class<?> type) {
        //TODO: generic string == signature?
        super(type.getModifiers(), Type.getType(type).getDescriptor(), type.toGenericString());
        version = UNKNOWN_VERSION;
        superType = Type.getType(type.getSuperclass());
        interfaces = Stream.of(type.getInterfaces()).map(Type::getType).collect(Collectors.toSet());
    }

    public TypeInfo(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super(access, name, signature);

        this.version = version;
        //TODO: Validate
        superType = Type.getObjectType(superName);
        this.interfaces = toTypeSet(interfaces);
    }

    public int getVersion() { return version; }

    public Type getSuperType() {  return superType; }

    public Set<Type> getInterfaces() { return interfaces; }

}
