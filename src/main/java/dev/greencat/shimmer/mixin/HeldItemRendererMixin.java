package dev.greencat.shimmer.mixin;

import com.google.common.base.MoreObjects;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.ArmRenderEvent;
import dev.greencat.shimmer.event.events.HeldItemRendererEvent;
import dev.greencat.shimmer.module.modules.render.Animation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HeldItemRenderer.class})
public abstract class HeldItemRendererMixin {
   @Shadow
   private float equipProgressMainHand;
   @Shadow
   private float equipProgressOffHand;
   @Shadow
   private ItemStack mainHand;
   @Shadow
   private ItemStack offHand;

   @ModifyVariable(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = @At(
         value = "STORE",
         ordinal = 0
      ),
      index = 6
   )
   private float modifySwing(float swingProgress) {
      Hand hand = (Hand)MoreObjects.firstNonNull(MinecraftClient.getInstance().player.preferredHand, Hand.MAIN_HAND);
      if (Shimmer.getInstance().getModuleManager().getModule("Animation") != null
         && Shimmer.getInstance().getModuleManager().getModule("Animation").isEnabled()) {
         if (hand == Hand.OFF_HAND && !MinecraftClient.getInstance().player.getOffHandStack().isEmpty()) {
            return (float)((double)swingProgress + Animation.offHandSwingProgress.getValue());
         }

         if (hand == Hand.MAIN_HAND && !MinecraftClient.getInstance().player.getMainHandStack().isEmpty()) {
            return (float)((double)swingProgress + Animation.mainHandSwingProgress.getValue());
         }
      }

      return swingProgress;
   }

   @ModifyArg(
      method = {"updateHeldItems"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F",
         ordinal = 2
      ),
      index = 0
   )
   private float modifyEquipProgressMainhand(float value) {
      if (Shimmer.getInstance().getModuleManager().getModule("Animation") != null
         && Shimmer.getInstance().getModuleManager().getModule("Animation").isEnabled()) {
         float f = MinecraftClient.getInstance().player.getAttackCooldownProgress(1.0F);
         float modified = Animation.oldAnimation.isEnabled() ? 1.0F : f * f * f;
         return (this.mainHand == MinecraftClient.getInstance().player.getMainHandStack() ? modified : 0.0F) - this.equipProgressMainHand;
      } else {
         return value;
      }
   }

   @ModifyArg(
      method = {"updateHeldItems"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/MathHelper;clamp(FFF)F",
         ordinal = 3
      ),
      index = 0
   )
   private float modifyEquipProgressOffhand(float value) {
      return Shimmer.getInstance().getModuleManager().getModule("Animation") != null
            && Shimmer.getInstance().getModuleManager().getModule("Animation").isEnabled()
         ? (float)(this.offHand == MinecraftClient.getInstance().player.getOffHandStack() ? 1 : 0) - this.equipProgressOffHand
         : value;
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
         shift = Shift.BEFORE
      )}
   )
   private void onRenderItem(
      AbstractClientPlayerEntity player,
      float tickProgress,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      MatrixStack matrices,
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      int light,
      CallbackInfo ci
   ) {
      Shimmer.getInstance().getEventBus().post(new HeldItemRendererEvent(hand, matrices));
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;IFFLnet/minecraft/util/Arm;)V"
      )}
   )
   private void onRenderArm(
      AbstractClientPlayerEntity player,
      float tickProgress,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      MatrixStack matrices,
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      int light,
      CallbackInfo ci
   ) {
      Shimmer.getInstance().getEventBus().post(new ArmRenderEvent(hand, matrices));
   }

   @Inject(
      method = {"applyEatOrDrinkTransformation"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/lang/Math;pow(DD)D",
         shift = Shift.BEFORE
      )},
      cancellable = true
   )
   private void cancelTransformations(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, PlayerEntity player, CallbackInfo ci) {
      if (Shimmer.getInstance().getModuleManager().getModule("Animation") != null
         && Shimmer.getInstance().getModuleManager().getModule("Animation").isEnabled()
         && Animation.noFoodAnimation.isEnabled()) {
         ci.cancel();
      }
   }
}
