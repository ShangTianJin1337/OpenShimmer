package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.MoveEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.event.fabric.PostMotionEvent;
import dev.greencat.shimmer.event.fabric.PreMotionEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public class ClientPlayerEntityMixin {
   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void tick(CallbackInfo ci) {
      if (Shimmer.mc.player != null && Shimmer.mc.world != null) {
         TickEvent event = new TickEvent();
         Shimmer.getInstance().getEventBus().post(event);
      }
   }

   @Inject(
      method = {"tickMovement"},
      at = {@At("HEAD")}
   )
   private void tickMovement(CallbackInfo ci) {
      if (Shimmer.mc.player != null && Shimmer.mc.world != null) {
         MoveEvent event = new MoveEvent();
         Shimmer.getInstance().getEventBus().post(event);
         if (event.isCancelled()) {
            ci.cancel();
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"sendMovementPackets()V"}
   )
   private void onSendMovementPacketsHEAD(CallbackInfo ci) {
      ((PreMotionEvent)PreMotionEvent.EVENT.invoker()).call();
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"sendMovementPackets()V"}
   )
   private void onSendMovementPacketsTAIL(CallbackInfo ci) {
      ((PostMotionEvent)PostMotionEvent.EVENT.invoker()).call();
   }
}
