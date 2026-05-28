package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.BlockBreakingEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public abstract class MinecraftClientMixin {
   @Inject(
      method = {"<init>"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V",
         shift = Shift.BEFORE
      )}
   )
   private void init(RunArgs args, CallbackInfo ci) {
      Shimmer.getInstance().postInitialize();
   }

   @Inject(
      cancellable = true,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/hit/BlockHitResult;getSide()Lnet/minecraft/util/math/Direction;"
      )},
      method = {"handleBlockBreaking"}
   )
   private void HandleBlockBreaking(CallbackInfo ci) {
      BlockBreakingEvent event = new BlockBreakingEvent();
      Shimmer.getInstance().getEventBus().post(event);
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   public void postApplyModule(RunArgs args, CallbackInfo ci) {
      Shimmer.getInstance()
         .getModuleManager()
         .enabledModules
         .sort((o, o1) -> MinecraftClient.getInstance().textRenderer.getWidth(o1) - MinecraftClient.getInstance().textRenderer.getWidth(o));
   }
}
