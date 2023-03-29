package wtf.zani.vanillamenu.hooks;

import club.maxstats.weave.loader.api.Hook;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;

@SuppressWarnings("unused")
public class VanillaMenuHook extends Hook {
    public VanillaMenuHook() {
        super("net/minecraft/client/Minecraft");
    }

    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        final MethodNode displayGuiScreen = classNode.methods
                .stream()
                .filter(methodNode -> methodNode.name.equals("displayGuiScreen"))
                .findFirst()
                .orElseThrow();
        final InsnList filteredInstructions = new InsnList();

        Arrays.stream(displayGuiScreen.instructions.toArray())
                .filter(instruction -> {
                    if (instruction instanceof final MethodInsnNode methodCall) {
                        return !methodCall.name.endsWith("$impl$displayGuiScreen");
                    }

                    return true;
                })
                .forEach(filteredInstructions::add);

        assemblerConfig.computeFrames();
    }
}
