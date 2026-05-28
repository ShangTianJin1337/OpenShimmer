package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.RenderInGameHudEvent;
import dev.greencat.shimmer.module.modules.render.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public class InGameHudMixin {
   @Inject(
      method = {"render"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      RenderInGameHudEvent event = new RenderInGameHudEvent(context);
      Shimmer.getInstance().getEventBus().post(event);
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"},
      at = {@At("HEAD")}
   )
   public void renderSidebar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      if (Shimmer.getInstance() != null && Shimmer.getInstance().getModuleManager() != null) {
         boolean isHUDEnabled = Shimmer.getInstance().getModuleManager().isModuleEnabled("HUD");
         if (isHUDEnabled
            && ((Hud)Shimmer.getInstance().getModuleManager().getModule("HUD")).getArrayListHeight()
               >= MinecraftClient.getInstance().getWindow().getScaledHeight() / 3) {
            context.getMatrices()
               .translate(
                  0.0F,
                  (float)(((Hud)Shimmer.getInstance().getModuleManager().getModule("HUD")).getArrayListHeight() + 3)
                     - (float)MinecraftClient.getInstance().getWindow().getScaledHeight() / 4.0F
               );
         }
      }
   }

   @Inject(
      method = {"renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"},
      at = {@At("RETURN")}
   )
   public void renderSidebar2(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
      if (Shimmer.getInstance() != null && Shimmer.getInstance().getModuleManager() != null) {
         boolean isHUDEnabled = Shimmer.getInstance().getModuleManager().isModuleEnabled("HUD");
         if (isHUDEnabled
            && ((Hud)Shimmer.getInstance().getModuleManager().getModule("HUD")).getArrayListHeight()
               >= MinecraftClient.getInstance().getWindow().getScaledHeight() / 3) {
            context.getMatrices()
               .translate(
                  0.0F,
                  -(
                     (float)(((Hud)Shimmer.getInstance().getModuleManager().getModule("HUD")).getArrayListHeight() + 3)
                        - (float)MinecraftClient.getInstance().getWindow().getScaledHeight() / 4.0F
                  )
               );
         }
      }
   }
}
