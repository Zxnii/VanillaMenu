package wtf.zani.vanillamenu.hooks;

import net.weavemc.loader.api.Hook;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import wtf.zani.vanillamenu.Util;

import java.util.Arrays;

@SuppressWarnings("unused")
public class GuiScreenHook extends Hook {
    public GuiScreenHook() {
        super("net/minecraft/client/gui/GuiScreen");
    }

    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        final MethodNode drawScreen = Util.findMethod(classNode, methodNode -> methodNode.name.equals("drawScreen"));
        final InsnList filteredInstructions = new InsnList();

        Arrays.stream(drawScreen.instructions.toArray())
                .filter(instruction -> {
                    if (instruction instanceof final MethodInsnNode methodCall) {
                        return !methodCall.name.endsWith("$impl$renderLunarClientBrand");
                    }

                    return true;
                })
                .forEach(filteredInstructions::add);

        drawScreen.instructions = filteredInstructions;

        assemblerConfig.computeFrames();
    }
}
