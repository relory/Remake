package sh.body.test.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import sh.body.remake.transformer.ClassTransformer;

public class MainTransformer extends ClassTransformer {
    public MainTransformer() {
        super("sh/body/test/Main");
    }

    @Override
    public void process(final ClassNode classNode) {
        System.out.println("[Transformer] Found class: " + classNode.name);
        for (final MethodNode methodNode : classNode.methods) {
            if (!methodNode.name.equals("test")) {
                continue;
            }

            for (final AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                if (abstractInsnNode.getOpcode() != Opcodes.LDC) {
                    continue;
                }

                final LdcInsnNode ldc = (LdcInsnNode) abstractInsnNode;
                ldc.cst = "Hooked by remake <3";
            }
        }
    }
}
