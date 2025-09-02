package sh.body.remake;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import sh.body.remake.transformer.ClassTransformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Remake {
    private static Remake instance;
    private boolean initialized = false;

    private final List<ClassTransformer> transformers = new ArrayList<>();
    private final List<String> transformingClasses = new ArrayList<>();

    private native void nInit();
    private native void nRemake(final Class<?> klass);

    public static Remake getInstance() {
        if (instance == null) {
            instance = new Remake();
        }

        return instance;
    }

    public List<ClassTransformer> getTransformers() {
        return transformers;
    }

    public void initialize(final File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("[Remake] Unable to Find File: " + file.getAbsolutePath());
        }

        System.load(file.getAbsolutePath());
        this.nInit();
        this.initialized = true;
    }

    public void remake(final Class<?> clazz) {
        if (!this.initialized) {
            throw new RuntimeException("[Remake] Remake must be Initialized before using it.");
        }

        this.nRemake(clazz);
    }

    public void remake(final String clazz) throws ClassNotFoundException {
        this.nRemake(Class.forName(clazz));
    }

    private byte[] transform(final String className, final byte[] classData) {
        if (this.transformingClasses.contains(className)) {
            return classData;
        }

        this.transformingClasses.add(className);

        final ClassReader classReader = new ClassReader(classData);
        final ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        final Set<ClassTransformer> matchingTransformers = transformers.stream().filter(transformer -> transformer.getClassName().replaceAll("\\.", "/").equals(className)).collect(Collectors.toSet());
        for (final ClassTransformer classTransformer : matchingTransformers) {
            try {
                classTransformer.process(classNode);
            } catch (final Exception exception) {
                throw new RuntimeException("[Remake] Unable to Process Class: " + className + ".");
            }
        }

        final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        transformingClasses.remove(className);

        return classWriter.toByteArray();
    }
}

