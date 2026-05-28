package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.LivingEntityRenderPreEvent;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntityRenderer.class})
public abstract class LivingEntityRendererMixin<S extends LivingEntityRenderState> {
   @Inject(
      method = {"render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onRender(
      S livingEntityRenderState,
      MatrixStack matrixStack,
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      CameraRenderState cameraRenderState,
      CallbackInfo ci
   ) {
      try {
         LivingEntity entity = (LivingEntity)livingEntityRenderState.entityType.getBaseClass().newInstance();
         entity.setPos(livingEntityRenderState.x, livingEntityRenderState.y, livingEntityRenderState.z);
         entity.setBodyYaw(livingEntityRenderState.bodyYaw);
         entity.setPitch(livingEntityRenderState.pitch);
         LivingEntityRenderPreEvent event = new LivingEntityRenderPreEvent(entity);
         Shimmer.getInstance().getEventBus().post(event);
         if (event.isCancelled()) {
            ci.cancel();
         }
      } catch (Exception var8) {
      }
   }
}
