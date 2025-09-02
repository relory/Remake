package sh.body.remake.transformer;

import org.objectweb.asm.tree.ClassNode;

public abstract class ClassTransformer {
    private final String className;

    public ClassTransformer(final String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public abstract void process(final ClassNode classNode);
}
