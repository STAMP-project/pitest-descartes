package eu.stamp_project.mutationtest.descartes.operators;

import org.pitest.reloc.asm.ClassWriter;

public class DynamicClassLoader extends ClassLoader {


    public Class<?> defineClass(String name, ClassWriter writer) {
        byte[] byteCode = writer.toByteArray();
        return defineClass(name, byteCode, 0, byteCode.length);
    }
}
