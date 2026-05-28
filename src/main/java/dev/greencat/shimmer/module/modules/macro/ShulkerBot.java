package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.KeyBindAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.util.player.PlayerUtil;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;

public class ShulkerBot extends Module {
   private static long lastClick = 0L;
   public final BooleanSetting right = new BooleanSetting("Right Click", "", false);

   public ShulkerBot() {
      super("ShulkerBot", "Auto hit shulker's bullet", -1, Module.Category.MACRO);
      this.needDisable = true;
      this.addSettings(new Setting[]{this.right});
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         boolean hasBullet = false;
         ShulkerBulletEntity bullet = null;

         for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
            if (entity instanceof ShulkerBulletEntity && entity.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos()) <= 5.0) {
               boolean available = true;

               for (Entity entity1 : MinecraftClient.getInstance().world.getEntities()) {
                  if (entity1 instanceof ShulkerEntity && (double)entity.distanceTo(entity1) <= 1.3) {
                     available = false;
                     break;
                  }
               }

               if (available) {
                  hasBullet = true;
                  bullet = (ShulkerBulletEntity)entity;
                  break;
               }
            }
         }

         if (hasBullet) {
            MinecraftClient.getInstance().player.setYaw((float)RotationUtil.getYaw(bullet.getBoundingBox().getCenter()));
            MinecraftClient.getInstance().player.setPitch((float)RotationUtil.getPitch(bullet.getBoundingBox().getCenter()));
            if (System.currentTimeMillis() - lastClick >= 100L) {
               if (!this.right.isEnabled()) {
                  KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.attackKey).getBoundKey());
               } else {
                  PlayerUtil.useItem();
               }

               lastClick = System.currentTimeMillis();
            }
         }
      }
   }
}
