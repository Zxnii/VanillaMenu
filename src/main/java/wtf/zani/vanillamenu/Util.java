package wtf.zani.vanillamenu;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;

public class Util {
    public static MethodNode findMethod(ClassNode classNode, Predicate<MethodNode> predicate) {
        return classNode.methods
                .stream()
                .filter(predicate)
                .findFirst()
                .orElseThrow();
    }

    public static InsnList asm(AbstractInsnNode... nodes) {
        final InsnList list = new InsnList();

        Arrays.stream(nodes).forEach(list::add);

        return list;
    }

    public static void dumpClass(ClassNode node) {
        try (final FileOutputStream outputStream = new FileOutputStream(node.name.replace('/', '.') + ".class")) {
            final ClassWriter classWriter = new ClassWriter(0);

            node.accept(classWriter);

            outputStream.write(classWriter.toByteArray());
        } catch (IOException ignored) {

        }
    }

    public static String internalName(Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }
}
