package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.BackgroundDrawnEvent;
import dev.greencat.shimmer.event.events.OnScreenOpenEvent;
import dev.greencat.shimmer.module.modules.render.OneGui;
import dev.greencat.shimmer.util.render.BackgroundEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Screen.class})
public class ScreenMixin {
   @Inject(
      method = {"init(Lnet/minecraft/client/MinecraftClient;II)V"},
      at = {@At("RETURN")}
   )
   public void onInit(MinecraftClient client, int width, int height, CallbackInfo ci) {
      OnScreenOpenEvent event = new OnScreenOpenEvent(MinecraftClient.getInstance().currentScreen);
      Shimmer.getInstance().getEventBus().post(event);
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   public void onBackgroundDrown(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      BackgroundEffect.onRender(context, mouseX, mouseY, delta);
      BackgroundDrawnEvent event = new BackgroundDrawnEvent(MinecraftClient.getInstance().currentScreen);
      Shimmer.getInstance().getEventBus().post(event);
   }

   @Inject(
      method = {"renderInGameBackground"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderInGameBackground(DrawContext context, CallbackInfo ci) {
      if (OneGui.backgroundEffect.isEnabled()) {
         BackgroundEffect.renderBackground = true;
         ci.cancel();
      }
   }

   @Inject(
      method = {"applyBlur"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void applyBlur(CallbackInfo ci) {
      ci.cancel();
   }
}
