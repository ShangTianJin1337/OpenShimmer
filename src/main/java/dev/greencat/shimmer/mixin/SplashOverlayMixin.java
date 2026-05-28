package dev.greencat.shimmer.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.awt.Color;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SplashOverlay.class})
public class SplashOverlayMixin {
   @Shadow
   @Final
   private boolean reloading;
   @Shadow
   private long reloadStartTime;
   @Shadow
   private long reloadCompleteTime;
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   @Final
   private ResourceReload reload;
   @Shadow
   private float progress;
   @Shadow
   @Final
   private Consumer<Optional<Throwable>> exceptionHandler;

   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      int width = context.getScaledWindowWidth();
      int height = context.getScaledWindowHeight();
      long l = Util.getMeasuringTimeMs();
      if (this.reloading && this.reloadStartTime == -1L) {
         this.reloadStartTime = l;
      }

      float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0F : -1.0F;
      float g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0F : -1.0F;
      if (f >= 1.0F) {
         if (this.client.currentScreen != null) {
            this.client.currentScreen.render(context, 0, 0, delta);
         }
      } else if (this.reloading) {
         if (this.client.currentScreen != null && g < 1.0F) {
            this.client.currentScreen.render(context, mouseX, mouseY, delta);
         }
      } else {
         GlStateManager._clear(16384);
      }

      GlStateManager._disableDepthTest();
      GlStateManager._depthMask(false);
      GlStateManager._enableBlend();
      context.fill(0, 0, width, height, Color.BLACK.getRGB());
      Identifier LOGO = Identifier.of("shimmer", "logo_splash.png");
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, LOGO, width / 2 - 100, height / 4 + 32, 0.0F, 0.0F, 200, 64, 200, 64);
      Identifier LOADING = Identifier.of("shimmer", "loading/loading_" + (System.currentTimeMillis() - this.reloadStartTime) / 30L % 60L + ".png");
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, LOADING, width / 2 - 20, height / 5 * 4 - 20, 0.0F, 0.0F, 40, 40, 40, 40);
      GlStateManager._disableBlend();
      GlStateManager._depthMask(true);
      GlStateManager._enableDepthTest();
      float t = this.reload.getProgress();
      this.progress = MathHelper.clamp(this.progress * 0.95F + t * 0.050000012F, 0.0F, 1.0F);
      if (f >= 2.0F) {
         this.client.setOverlay((Overlay)null);
      }

      if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0F)) {
         try {
            this.reload.throwException();
            this.exceptionHandler.accept(Optional.empty());
         } catch (Throwable var16) {
            this.exceptionHandler.accept(Optional.of(var16));
         }

         this.reloadCompleteTime = Util.getMeasuringTimeMs();
         if (this.client.currentScreen != null) {
            this.client.currentScreen.init(this.client, context.getScaledWindowWidth(), context.getScaledWindowHeight());
         }
      }

      ci.cancel();
   }
}
