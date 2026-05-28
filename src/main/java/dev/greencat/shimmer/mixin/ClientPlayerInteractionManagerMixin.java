package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.module.modules.macro.DojoHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerInteractionManager.class})
public class ClientPlayerInteractionManagerMixin {
   @Inject(
      method = {"attackEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void dojoHelperMixin(PlayerEntity player, Entity target, CallbackInfo ci) {
      if (Shimmer.getInstance().getModuleManager().isModuleEnabled("DojoHelper")
         && DojoHelper.isInForce()
         && target instanceof LivingEntity livingEntity
         && livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.LEATHER_HELMET) {
         ci.cancel();
      }

      if (Shimmer.getInstance().getModuleManager().isModuleEnabled("DojoHelper") && DojoHelper.isInDiscipline()) {
         if (target instanceof LivingEntity livingEntity
            && livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.LEATHER_HELMET
            && player.getMainHandStack().getItem() != Items.WOODEN_SWORD) {
            KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[0]).getBoundKey());
            ci.cancel();
         }

         if (target instanceof LivingEntity livingEntity
            && livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.IRON_HELMET
            && player.getMainHandStack().getItem() != Items.IRON_SWORD) {
            KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[1]).getBoundKey());
            ci.cancel();
         }

         if (target instanceof LivingEntity livingEntity
            && livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.GOLDEN_HELMET
            && player.getMainHandStack().getItem() != Items.GOLDEN_SWORD) {
            KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[2]).getBoundKey());
            ci.cancel();
         }

         if (target instanceof LivingEntity livingEntity
            && livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.DIAMOND_HELMET
            && player.getMainHandStack().getItem() != Items.DIAMOND_SWORD) {
            KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[3]).getBoundKey());
            ci.cancel();
         }
      }
   }
}
