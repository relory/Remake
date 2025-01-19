package sh.body.remake;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import sh.body.remake.transformer.ClassTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Remake {

    private static Remake instance;
    private final List<ClassTransformer> transformers = new ArrayList<>();
    private boolean initialized = false;

    private final List<String> transformingClasses = new ArrayList<>();

    public static Remake getInstance(){
        if (instance == null)
            instance = new Remake();

        return instance;
    }

    public List<ClassTransformer> getTransformers() {
        return transformers;
    }

    public void initialize(String libraryPath) {
        if(new File(libraryPath).isAbsolute())
            System.load(libraryPath);
        else
            System.load(System.getProperty("user.dir") + "\\" + libraryPath);
        
        _init();
        initialized = true;
    }

    public void remake(Class<?> klass) {
        if (!initialized)
            throw new RuntimeException("Cannot use Remake without it being initialized.");
        _remake(klass);
    }

    public void remake(String klass) throws ClassNotFoundException {
        _remake(Class.forName(klass));
    }

    private native void _init();
    private native void _remake(Class<?> klass);

    private byte[] _transform(String className, byte[] classData) {
        if(transformingClasses.contains(className))
            return classData;

        transformingClasses.add(className);

        ClassReader classReader = new ClassReader(classData);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        Set<ClassTransformer> matchingTransformers = transformers.stream().filter(transformer -> transformer.getClassName().replaceAll("\\.", "/").equals(className)).collect(Collectors.toSet());

        matchingTransformers.forEach(transformer -> {
            try{
                transformer.process(classNode);
            } catch(Exception e){
                throw new RuntimeException("Could not process class: " + className + " ; failed on transformer: " + transformer.getClass().getName());
            }
        });

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        transformingClasses.remove(className);
        return classWriter.toByteArray();
    }

}

