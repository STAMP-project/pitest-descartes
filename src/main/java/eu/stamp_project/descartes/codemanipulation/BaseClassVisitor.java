package eu.stamp_project.descartes.codemanipulation;

import static org.pitest.bytecode.ASMVersion.ASM_VERSION;

import org.pitest.reloc.asm.ClassVisitor;

public class BaseClassVisitor extends ClassVisitor {

  public BaseClassVisitor() {
    super(ASM_VERSION);
  }

  public BaseClassVisitor(final ClassVisitor classVisitor) {
    super(ASM_VERSION, classVisitor);
  }

  private TypeInfo classInfo;

  public TypeInfo getClassInfo() {
    return classInfo;
  }

  public MethodInfo createMethodInfo(
      final int access,
      final String name,
      final String desc,
      final String signature,
      final String[] exceptions) {
    return new MethodInfo(classInfo, access, name, desc, signature, exceptions);
  }

  @Override
  public void visit(
      int version,
      int access,
      String name,
      String signature,
      String superName,
      String[] interfaces) {
    classInfo = new TypeInfo(version, access, name, signature, superName, interfaces);
    super.visit(version, access, name, signature, superName, interfaces);
  }
}
