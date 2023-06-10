package wtf.zani.vanillamenu.mixin;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Mixin(FontRenderer.class)
public abstract class FontRendererMixin {
    private Method originalRenderMethod = null;
    private Method originalGetWidthMethod = null;

    @Redirect(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I"
    ), method = "drawString(Ljava/lang/String;FFIZ)I")
    public int redirectRenderString(FontRenderer instance, String string, float x, float y, int color, boolean shadow) throws InvocationTargetException, IllegalAccessException {
        if (this.originalRenderMethod == null) {
            this.originalRenderMethod = Arrays.stream(FontRenderer.class.getMethods())
                    .filter(method -> method.getName().equals("original$renderString"))
                    .findFirst()
                    .orElseThrow();
        }

        return (int) this.originalRenderMethod.invoke(instance, new Object[]{string, x, y, color, shadow});
    }

    @Inject(at = @At("HEAD"), method = "getStringWidth", cancellable = true)
    public void getStringWidth(String string, CallbackInfoReturnable<Integer> cir) throws InvocationTargetException, IllegalAccessException {
        if (this.originalGetWidthMethod == null) {
            this.originalGetWidthMethod = Arrays.stream(FontRenderer.class.getMethods())
                    .filter(method -> method.getName().equals("original$getStringWidth"))
                    .findFirst()
                    .orElseThrow();
        }

        cir.setReturnValue((Integer) this.originalGetWidthMethod.invoke(this, new Object[]{string}));
        cir.cancel();
    }
}
