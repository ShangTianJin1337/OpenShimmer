package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public class MixinCamera {
   @ModifyVariable(
      method = {"clipToSpace"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private float modifyClipToSpace(float d) {
      return Shimmer.getInstance().getModuleManager().isModuleEnabled("Camera")
         ? (float)dev.greencat.shimmer.module.modules.player.Camera.Distance.getValue()
         : 4.0F;
   }

   @Inject(
      method = {"clipToSpace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onClipToSpace(float desiredCameraDistance, CallbackInfoReturnable<Float> info) {
      if (dev.greencat.shimmer.module.modules.player.Camera.CameraClip.isEnabled() && Shimmer.getInstance().getModuleManager().isModuleEnabled("Camera")) {
         info.setReturnValue(desiredCameraDistance);
      }
   }
}
