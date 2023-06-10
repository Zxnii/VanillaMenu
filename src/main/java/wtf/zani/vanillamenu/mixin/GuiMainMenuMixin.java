package wtf.zani.vanillamenu.mixin;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.zani.vanillamenu.screens.GuiAccountSwitcher;

@Mixin(GuiMainMenu.class)
public abstract class GuiMainMenuMixin extends GuiScreen {
    @Shadow
    private GuiButton realmsButton;

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setConnectedToRealms(Z)V"))
    public void postInitGui(CallbackInfo ci) {
        this.buttonList.add(new GuiButton(
                100,
                this.realmsButton.xPosition,
                this.realmsButton.yPosition,
                this.realmsButton.getButtonWidth(),
                20,
                "Accounts"
        ));
        this.buttonList.remove(this.realmsButton);
    }

    @Inject(method = "actionPerformed", cancellable = true, at = @At("HEAD"))
    public void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 100) {
            this.mc.displayGuiScreen(new GuiAccountSwitcher());

            ci.cancel();
        }
    }
}
