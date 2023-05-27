package wtf.zani.vanillamenu.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import wtf.zani.vanillamenu.VanillaMenu;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public WorldClient theWorld;

    private boolean isFirst = true;

    @ModifyVariable(at = @At(value = "HEAD"), method = "displayGuiScreen", argsOnly = true)
    public GuiScreen modifyScreen(GuiScreen screen) {
        if (this.theWorld == null && screen != null && !screen.getClass().getName().startsWith("net.minecraft") && isFirst) {
            isFirst = false;

            VanillaMenu.logger.info("Replaced Lunar's main menu!");

            return new GuiMainMenu();
        }

        return screen;
    }
}
