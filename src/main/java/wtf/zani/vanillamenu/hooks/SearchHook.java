package wtf.zani.vanillamenu.hooks;

import net.weavemc.loader.api.Hook;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import wtf.zani.vanillamenu.hooks.delegations.AccountManagerHook;
import wtf.zani.vanillamenu.hooks.delegations.LoadingScreenRendererHook;

import java.util.Arrays;

@SuppressWarnings("unused")
public class SearchHook extends Hook {
    private final AccountManagerHook accountManagerHook = new AccountManagerHook();
    private final LoadingScreenRendererHook loadingScreenRendererHook = new LoadingScreenRendererHook();

    public SearchHook() {
        super("*");
    }

    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        if (!classNode.name.startsWith("com/moonsworth/lunar")) return;

        classNode.methods.stream()
                .flatMap(methodNode -> Arrays.stream(methodNode.instructions.toArray())
                        .filter(instruction -> instruction instanceof LdcInsnNode))
                .forEach(instruction -> {
                    final LdcInsnNode ldc = (LdcInsnNode) instruction;

                    if (ldc.cst instanceof final String casted) {
                        switch (casted) {
                            case "launcher_accounts.json" ->
                                    this.accountManagerHook.transform(classNode, assemblerConfig);
                            case "logo/logo-128x117.png" ->
                                    this.loadingScreenRendererHook.transform(classNode, assemblerConfig);
                        }
                    }
                });
    }
}
