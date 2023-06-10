package wtf.zani.vanillamenu.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import wtf.zani.vanillamenu.VanillaMenu;
import wtf.zani.vanillamenu.accessors.Accessor;
import wtf.zani.vanillamenu.accessors.lunar.LoadStageAccessor;
import wtf.zani.vanillamenu.accessors.lunar.LoadingScreenRendererAccessor;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

public class LoadingScreenRenderer {
    private static final String[] spinnerAnimation = new String[]{
            " |",
            "/",
            "-",
            "\\"
    };

    private static LoadingScreenRenderer instance;

    private final ResourceLocation mojangLogo;

    private final ScaledResolution scaledResolution;
    private final int scaleFactor;

    private final TextureManager textureManager;
    private final FontRenderer fontRenderer;
    private final Framebuffer framebuffer;
    private final Minecraft minecraft;

    private int frame = 0;

    public LoadingScreenRenderer(TextureManager textureManager) {
        instance = this;

        this.minecraft = Minecraft.getMinecraft();
        this.fontRenderer = this.minecraft.fontRendererObj;

        this.scaledResolution = new ScaledResolution(this.minecraft);
        this.scaleFactor = this.scaledResolution.getScaleFactor();

        this.textureManager = textureManager;
        this.framebuffer = new Framebuffer(
                this.scaledResolution.getScaledWidth() * this.scaleFactor,
                this.scaledResolution.getScaledHeight() * this.scaleFactor, true);

        final ResourceLocation mojangLogoLocation = new ResourceLocation("textures/gui/title/mojang.png");

        ResourceLocation mojangLogo = null;

        try (final InputStream logoInputStream =
                     this.minecraft.getResourcePackRepository().rprDefaultResourcePack.getInputStream(mojangLogoLocation)) {
            mojangLogo = textureManager.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(logoInputStream)));
        } catch (IOException exception) {
            VanillaMenu.logger.error("Unable to load logo: " + mojangLogoLocation, exception);
        }

        this.mojangLogo = mojangLogo;
    }

    @SuppressWarnings("unused")
    public static LoadingScreenRenderer getInstance() {
        return instance;
    }

    public void draw() {
        final int imageWidth = 256;
        final int imageHeight = 256;

        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        final int displayWidth = this.minecraft.displayWidth;
        final int displayHeight = this.minecraft.displayHeight;

        this.framebuffer.bindFramebuffer(false);

        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        this.textureManager.bindTexture(this.mojangLogo);

        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer
                .pos(0.0D, displayHeight, 0.0D)
                .tex(0.0D, 0.0D)
                .color(255, 255, 255, 255)
                .endVertex();
        worldRenderer
                .pos(displayWidth, displayHeight, 0.0D)
                .tex(0.0D, 0.0D)
                .color(255, 255, 255, 255)
                .endVertex();
        worldRenderer
                .pos(displayWidth, 0.0D, 0.0D)
                .tex(0.0D, 0.0D)
                .color(255, 255, 255, 255)
                .endVertex();
        worldRenderer
                .pos(0.0D, 0.0D, 0.0D)
                .tex(0.0D, 0.0D)
                .color(255, 255, 255, 255)
                .endVertex();
        tessellator.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.minecraft.draw(
                (this.scaledResolution.getScaledWidth() - imageWidth) / 2,
                (scaledResolution.getScaledHeight() - imageHeight) / 2,
                0,
                0,
                imageWidth,
                imageHeight,
                255, 255, 255, 255);

        GlStateManager.disableLighting();
        GlStateManager.disableFog();

        this.framebuffer.unbindFramebuffer();
        this.framebuffer.framebufferRender(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor);

        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, 0.1F);

        final LoadingScreenRendererAccessor accessor = Accessor.loadingScreenRendererAccessor;

        String currentStatus = "Starting Minecraft";

        if (accessor != null) {
            final LoadStageAccessor loadStage = accessor.getLoadStage();
            final String status = accessor.getStatus();

            if (loadStage != null) currentStatus = loadStage.getCategory();
            else currentStatus = status != null ? status : "Starting Minecraft";
        }

        final int stringWidth = this.fontRenderer.getStringWidth(currentStatus);
        final float textX = (float) (displayWidth / this.scaleFactor) / 2 - (float) stringWidth / 2;
        final float textY = (float) (displayHeight / this.scaleFactor) - 20;

        this.fontRenderer.drawString(currentStatus,
                textX,
                textY,
                0,
                false);
        this.fontRenderer.drawString(spinnerAnimation[(frame / 10) % spinnerAnimation.length],
                textX + stringWidth + 10,
                textY,
                0,
                false);

        this.minecraft.updateDisplay();

        this.frame++;
    }
}
