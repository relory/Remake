package sh.body.test;

import sh.body.remake.Remake;
import sh.body.test.transformers.MainTransformer;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void test(){
        System.out.println("Hello, World!");
    }

    public static void main(final String[] args) throws FileNotFoundException {
        initialize();
        test();
        transform();
        test();
    }

    private static void initialize() throws FileNotFoundException {
        //library Path (Shared Object on Linux, dylib on Darwin, and dll on Windows)
        final String path = System.getProperty("user.home") + "/.remake/engine." + (System.getProperty("os.name").toLowerCase().contains("win") ? "dll" : "so");
        Remake.getInstance().initialize(new File(path));
    }

    private static void transform() {
        Remake.getInstance().getTransformers().add(new MainTransformer());
        Remake.getInstance().remake(Main.class);
    }
}
