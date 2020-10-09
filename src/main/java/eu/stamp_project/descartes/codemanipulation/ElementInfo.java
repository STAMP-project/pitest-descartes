package eu.stamp_project.descartes.codemanipulation;

import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.Type;

import java.util.Set;
import java.util.stream.Stream;

public abstract class ElementInfo {

    protected int access;
    protected String name;
    protected String signature;

    public ElementInfo(int access, String name, String signature) {
        this.access = access;
        this.name = name; //TODO: validate
        this.signature = signature;
    }

    public String getName() { return name; }

    public String getSignature() { return signature; }

    public boolean hasFlag(int flag) { return (access & flag) != 0; }

    protected Type[] toTypes(String[] typeNames) {
        if(typeNames == null || typeNames.length == 0) return new Type[0];
        return Stream.of(typeNames).map(Type::getObjectType).toArray(Type[]::new);
    }

    protected Set<Type> toTypeSet(String[] typeNames) { return Set.of(toTypes(typeNames)); }

    public boolean isStatic() { return hasFlag(Opcodes.ACC_STATIC); }

    public boolean isAbstract() { return hasFlag(Opcodes.ACC_ABSTRACT); }
}
