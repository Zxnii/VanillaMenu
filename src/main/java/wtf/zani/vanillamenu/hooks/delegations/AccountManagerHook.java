package wtf.zani.vanillamenu.hooks.delegations;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.*;
import wtf.zani.vanillamenu.Util;
import wtf.zani.vanillamenu.accessors.lunar.AccountManagerAccessor;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class AccountManagerHook extends DelegatedHook {
    private static AccountManagerHook instance;

    public ClassNode node;

    public AccountManagerHook() {
        instance = this;
    }

    public static AccountManagerHook getInstance() {
        return instance;
    }

    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        this.node = classNode;

        final MethodNode constructor = Util.findMethod(classNode, methodNode -> methodNode.name.equals("<init>"));
        final AbstractInsnNode superCall = Arrays.stream(constructor.instructions.toArray())
                .filter(insnNode ->
                        insnNode instanceof final MethodInsnNode call
                                && call.owner.equals(classNode.superName)
                                && call.name.equals("<init>"))
                .findFirst()
                .orElseThrow();

        constructor.instructions.insert(superCall, Util.asm(
                new VarInsnNode(ALOAD, 0),
                new MethodInsnNode(INVOKESTATIC, Util.internalName(AccountManagerAccessor.class), "create", "(Ljava/lang/Object;)V")
        ));

        assemblerConfig.computeFrames();
    }
}
