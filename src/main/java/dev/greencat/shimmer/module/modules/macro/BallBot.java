package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.ActionBarRenderEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class BallBot extends Module {
   private static final BooleanSetting autoDisable = new BooleanSetting("Auto Disable", "Disable this function automatically when reach 40 bounces", true);
   public static float variableYaw = 0.0F;
   private static final float fixedPitch = -90.0F;

   public BallBot() {
      super("BallBot", "Auto do benchball", -1, Module.Category.MACRO, true);
      this.addSettings(new Setting[]{autoDisable});
      this.needDisable = true;
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         Entity target = null;

         for (Entity it : MinecraftClient.getInstance().world.getEntities()) {
            if (it instanceof ArmorStandEntity) {
               ArmorStandEntity armorStand = (ArmorStandEntity)it;
               if (MinecraftClient.getInstance().world.getBlockState(it.getBlockPos()).isAir()
                  && !armorStand.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                  String origin = armorStand.getEquippedStack(EquipmentSlot.HEAD).getName().toString();
                  String id = origin.split("\\[")[1].split("]")[0];
                  if (id.length() == 16) {
                     target = armorStand;
                     break;
                  }
               }
            }
         }

         if (target != null) {
            MinecraftClient.getInstance().player.setPitch(-90.0F);
            MinecraftClient.getInstance().player.setYaw((float)RotationUtil.getYaw(target));
            MinecraftClient.getInstance().options.forwardKey.setPressed(true);
         } else {
            MinecraftClient.getInstance().options.forwardKey.setPressed(false);
            MinecraftClient.getInstance()
               .player
               .getMovement()
               .subtract(MinecraftClient.getInstance().player.getMovement().x, 0.0, MinecraftClient.getInstance().player.getMovement().z);
         }

         ServerRotation.useServerRotation = true;
         ServerRotation.serverYaw = variableYaw;
         ServerRotation.serverPitch = -90.0F;
      }
   }

   @ShimmerSubscribe
   public void onMessage(ActionBarRenderEvent event) {
      if (event.text != null && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null && autoDisable.isEnabled()) {
         StringBuilder finalText = new StringBuilder();
         boolean shouldSkipNext = false;

         for (int i = 0; i < event.text.getString().length(); i++) {
            if (event.text.getString().substring(i, i + 1).contains("§")) {
               shouldSkipNext = true;
            } else if (shouldSkipNext) {
               shouldSkipNext = false;
            } else {
               finalText.append(event.text.getString().substring(i, i + 1));
            }
         }

         if (finalText.toString().contains("Bounces: 40")) {
            MinecraftClient.getInstance()
               .world
               .playSound(
                  MinecraftClient.getInstance().player,
                  MinecraftClient.getInstance().player.getBlockPos().up(),
                  SoundEvents.ENTITY_PLAYER_LEVELUP,
                  SoundCategory.PLAYERS,
                  1.0F,
                  1.0F
               );
            this.setEnabled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      ServerRotation.useServerRotation = false;
      MinecraftClient.getInstance().options.jumpKey.setPressed(false);
      MinecraftClient.getInstance().options.forwardKey.setPressed(false);
   }

   @Override
   public void onEnable() {
      if (MinecraftClient.getInstance().player != null) {
         super.onEnable();
         ServerRotation.useServerRotation = true;
         variableYaw = MinecraftClient.getInstance().player.getYaw();
      }
   }
}
