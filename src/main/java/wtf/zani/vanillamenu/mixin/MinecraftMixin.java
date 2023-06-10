package wtf.zani.vanillamenu.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.zani.vanillamenu.VanillaMenu;
import wtf.zani.vanillamenu.render.LoadingScreenRenderer;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public WorldClient theWorld;

    @Shadow
    private TextureManager renderEngine;

    private boolean isFirst = true;

    @ModifyVariable(at = @At("HEAD"), method = "displayGuiScreen", argsOnly = true)
    public GuiScreen modifyScreen(GuiScreen screen) {
        if (this.theWorld == null && screen != null && !screen.getClass().getName().startsWith("net.minecraft") && isFirst) {
            isFirst = false;

            VanillaMenu.logger.info("Replaced Lunar's main menu!");

            return new GuiMainMenu();
        }

        return screen;
    }

    @Inject(at = @At("HEAD"), method = "drawSplashScreen", cancellable = true)
    public void drawSplashScreen(TextureManager textureManager, CallbackInfo ci) {
        new LoadingScreenRenderer(textureManager).draw();

        // screw you lunar
        ci.cancel();
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/IReloadableResourceManager;registerReloadListener(Lnet/minecraft/client/resources/IResourceManagerReloadListener;)V",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            ), method = "startGame")
    public void setupSplashScreen(CallbackInfo ci) throws LWJGLException {
        this.drawSplashScreen(this.renderEngine);
    }

    @Inject(at = @At("TAIL"), method = "createDisplay")
    public void setTitle(CallbackInfo ci) {
        Display.setTitle("Minecraft 1.8.9");
    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;drawSplashScreen(Lnet/minecraft/client/renderer/texture/TextureManager;)V"
            ), method = "startGame")
    public void deferSplashScreen(Minecraft instance, TextureManager textureManager) {
    }

    @Shadow
    protected abstract void drawSplashScreen(TextureManager textureManager) throws LWJGLException;
}
