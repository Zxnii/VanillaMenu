package wtf.zani.vanillamenu.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import wtf.zani.vanillamenu.VanillaMenu;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class ClassUtil {
    public static ClassNode openClass(String className) throws IOException {
        final InputStream classStream = Objects.requireNonNull(
                ClassUtil.class.getClassLoader()
                        .getResourceAsStream(className.replace('.', '/') + ".class"));

        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(classStream.readAllBytes());

        classReader.accept(classNode, 0);
        classStream.close();

        return classNode;
    }

    // unused but helpful for debugging
    public static void dumpClass(ClassNode classNode) {
        try {
            final ClassWriter classWriter = new ClassWriter(0);

            classNode.accept(classWriter);

            final byte[] classBytes = classWriter.toByteArray();

            final OutputStream outputFile = new FileOutputStream(classNode.name.replace('/', '.'));

            outputFile.write(classBytes);
            outputFile.close();
        } catch (IOException exception) {
            VanillaMenu.logger.warn("Failed to dump class", exception);
        }
    }
}
