package wtf.zani.vanillamenu.hooks.delegations;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.*;
import wtf.zani.vanillamenu.Util;
import wtf.zani.vanillamenu.accessors.lunar.LoadingScreenRendererAccessor;
import wtf.zani.vanillamenu.render.LoadingScreenRenderer;

import java.util.Comparator;

import static org.objectweb.asm.Opcodes.*;

public class LoadingScreenRendererHook extends DelegatedHook {
    private static LoadingScreenRendererHook instance;
    public ClassNode node;
    // just in case lunar has another class that uses logo/logo-128x117.png
    private boolean foundRenderer = false;

    public LoadingScreenRendererHook() {
        instance = this;
    }

    public static LoadingScreenRendererHook getInstance() {
        return instance;
    }

    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        if (this.foundRenderer) return;

        this.node = classNode;
        this.foundRenderer = true;

        final MethodNode drawMethod = classNode.methods.stream().filter(methodNode ->
                        methodNode.desc.endsWith("V")
                                && (methodNode.access & ACC_PUBLIC) > 0
                                && (methodNode.access & ACC_STATIC) == 0)
                .max(Comparator.comparingInt(a -> a.instructions.size()))
                .orElseThrow();

        final String rendererName = Util.internalName(LoadingScreenRenderer.class);

        drawMethod.instructions = Util.asm(
                new VarInsnNode(ALOAD, 0),
                new MethodInsnNode(INVOKESTATIC, Util.internalName(LoadingScreenRendererAccessor.class), "create", "(Ljava/lang/Object;)V"),
                new MethodInsnNode(INVOKESTATIC, rendererName, "getInstance", "()L" + rendererName + ";"),
                new MethodInsnNode(INVOKEVIRTUAL, rendererName, "draw", "()V"),
                new InsnNode(RETURN)
        );

        assemblerConfig.computeFrames();
    }
}
