package wtf.zani.vanillamenu.hooks;

import net.weavemc.loader.api.Hook;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.*;
import wtf.zani.vanillamenu.Util;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

@SuppressWarnings("unused")
public class MinecraftHook extends Hook {
    public MinecraftHook() {
        super("net/minecraft/client/Minecraft");
    }

    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        final MethodNode setDisplayTitle = Util.findMethod(classNode, methodNode -> methodNode.name.endsWith("setDisplayTitle"));
        final MethodNode displayGuiScreen = Util.findMethod(classNode, methodNode -> methodNode.name.equals("displayGuiScreen"));

        final InsnList filteredInstructions = new InsnList();

        Arrays.stream(displayGuiScreen.instructions.toArray())
                .filter(instruction -> {
                    if (instruction instanceof final MethodInsnNode methodCall) {
                        return !methodCall.name.endsWith("$impl$displayGuiScreen");
                    }

                    return true;
                })
                .forEach(filteredInstructions::add);

        displayGuiScreen.instructions = filteredInstructions;

        setDisplayTitle.instructions = Util.asm(
                new LdcInsnNode("Minecraft 1.8.9"),
                new MethodInsnNode(INVOKESTATIC, "org/lwjgl/opengl/Display", "setTitle", "(Ljava/lang/String;)V"),
                new InsnNode(RETURN)
        );

        assemblerConfig.computeFrames();
    }
}
