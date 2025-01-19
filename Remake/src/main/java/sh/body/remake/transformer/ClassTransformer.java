package sh.body.remake.transformer;

import org.objectweb.asm.tree.ClassNode;

public abstract class ClassTransformer {
    private final String className;

    public ClassTransformer(String className){
        this.className = className;
    }

    public String getClassName(){
        return className;
    }

    public abstract void process(ClassNode classNode);
}
