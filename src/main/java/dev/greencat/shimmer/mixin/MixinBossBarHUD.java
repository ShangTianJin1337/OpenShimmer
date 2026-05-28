package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BossBarHud.class})
public class MixinBossBarHUD {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   public void modifyYaxis(DrawContext context, CallbackInfo ci) {
      if (Shimmer.getInstance() != null && Shimmer.getInstance().getModuleManager() != null) {
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   public void modifyYaxis2(DrawContext context, CallbackInfo ci) {
      if (Shimmer.getInstance() != null && Shimmer.getInstance().getModuleManager() != null) {
      }
   }
}
