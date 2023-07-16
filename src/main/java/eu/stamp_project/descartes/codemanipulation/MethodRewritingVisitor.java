package eu.stamp_project.descartes.codemanipulation;


import org.pitest.reloc.asm.MethodVisitor;

public abstract class MethodRewritingVisitor extends BaseMethodVisitor {

  public MethodRewritingVisitor(MethodVisitor mv) {
    super(mv);
  }

  @Override
  public void visitCode() {
    generateCode();
    mv.visitMaxs(0, 0);
  }

  protected abstract void generateCode();
}
