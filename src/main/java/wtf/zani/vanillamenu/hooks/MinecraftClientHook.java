package wtf.zani.vanillamenu.hooks;

import club.maxstats.weave.loader.api.Hook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import wtf.zani.vanillamenu.VanillaMenu;
import wtf.zani.vanillamenu.util.ClassUtil;

import java.io.IOException;

public class MinecraftClientHook extends Hook {
    public static boolean isFirst = true;

    public MinecraftClientHook() {
        super("net/minecraft/client/Minecraft");
    }

    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        final MethodNode displayGuiScreen = classNode.methods
                .stream()
                .filter(methodNode -> methodNode.name.equals("displayGuiScreen"))
                .findFirst()
                .orElseThrow();

        displayGuiScreen.instructions.clear();

        if (displayGuiScreen.localVariables != null) displayGuiScreen.localVariables.clear();
        if (displayGuiScreen.tryCatchBlocks != null) displayGuiScreen.tryCatchBlocks.clear();

        try {
            // completely overwrite the patches lunar does to displayGuiScreen with our own modified code
            final ClassNode hookClassNode = ClassUtil.openClass(this.getClass().getName());
            final MethodNode hookDisplayGuiScreen = hookClassNode.methods
                    .stream()
                    .filter(methodNode -> methodNode.name.equals("displayGuiScreen"))
                    .findFirst()
                    .orElseThrow();

            displayGuiScreen.instructions.add(hookDisplayGuiScreen.instructions);

            if (displayGuiScreen.localVariables != null)
                displayGuiScreen.localVariables.addAll(hookDisplayGuiScreen.localVariables);
            if (displayGuiScreen.tryCatchBlocks != null)
                displayGuiScreen.tryCatchBlocks.addAll(hookDisplayGuiScreen.tryCatchBlocks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public void displayGuiScreen(GuiScreen screen) {
        final Minecraft mc = Minecraft.getMinecraft();

        if (mc.currentScreen == null && isFirst) {
            if (!screen.getClass().getName().equals(GuiMainMenu.class.getName())) {
                VanillaMenu.logger.info("Replaced Lunar's main menu!");

                isFirst = false;

                screen = new GuiMainMenu();
            } else {
                return;
            }
        }

        if (mc.currentScreen != null) {
            mc.currentScreen.onGuiClosed();
        }

        if (screen == null && mc.theWorld == null) {
            screen = new GuiMainMenu();
        } else if (screen == null && mc.thePlayer.getHealth() <= 0.0F) {
            screen = new GuiGameOver();
        }

        if (screen instanceof GuiMainMenu) {
            mc.gameSettings.showDebugInfo = false;
            mc.ingameGUI.getChatGUI().clearChatMessages();
        }

        mc.currentScreen = screen;

        if (screen != null) {
            ScaledResolution scaledRes = new ScaledResolution(mc);

            int scaledWidth = scaledRes.getScaledWidth();
            int scaledHeight = scaledRes.getScaledHeight();

            screen.setWorldAndResolution(mc, scaledWidth, scaledHeight);

            mc.setIngameNotInFocus();
            mc.skipRenderWorld = false;
        } else {
            mc.getSoundHandler().resumeSounds();
            mc.setIngameFocus();
        }
    }
}
