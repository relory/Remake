package sh.body.test;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import sh.body.remake.Remake;
import sh.body.remake.transformer.ClassTransformer;

import java.util.Arrays;

public class Main {

    public static void test(){
        System.out.println("Hello, World!");
    }

    public static void main(String[] args){
        Remake.getInstance().initialize("C:\\Users\\ilyas\\OneDrive\\Bureau\\StellarTweaks\\Remake\\Engine\\x64\\Release\\Engine.dll");
        test();
        Remake.getInstance().getTransformers().add(new ClassTransformer("sh/body/test/Main") {

            @Override
            public void process(ClassNode classNode) {
                System.out.println("[Transformer] Found class: " + classNode.name);
                classNode.methods.stream()
                        .filter(methodNode -> methodNode.name.equals("test"))
                        .forEach(methodNode -> {
                    methodNode.instructions.forEach((insn) -> {
                        if(insn.getOpcode() == Opcodes.LDC) {
                            LdcInsnNode ldc = (LdcInsnNode) insn;
                            System.out.println("Changed ldc: " + ldc.cst);
                            ldc.cst = "Hooked by remake <3";

                        }
                    });
                });
            }
        });

        Remake.getInstance().remake(Main.class);
        test();

    }

}
