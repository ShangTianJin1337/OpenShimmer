package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.rotation.Rotation;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.item.Items;

public class LockBat extends Module {
   public LockBat() {
      super("LockBat", "Auto right click when you need to do a reel", -1, Module.Category.MACRO);
   }

   @ShimmerSubscribe
   public void onClientTick(TickEvent event) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         BatEntity[] target = new BatEntity[]{null};
         double[] distance = new double[]{999999.0};
         MinecraftClient.getInstance()
            .world
            .getEntities()
            .forEach(
               entity -> {
                  if (MinecraftClient.getInstance().player.getMainHandStack().getItem() == Items.LEAD
                     && entity instanceof BatEntity
                     && !((BatEntity)entity).isLeashed()
                     && MinecraftClient.getInstance().player.getEntityPos().distanceTo(entity.getEntityPos()) <= distance[0]) {
                     target[0] = (BatEntity)entity;
                     distance[0] = MinecraftClient.getInstance().player.getEntityPos().distanceTo(entity.getEntityPos());
                  }
               }
            );
         if (target[0] != null) {
            Rotation rotation = RotationUtil.toRotation(target[0].getEntityPos().add(target[0].getMovement()));
            MinecraftClient.getInstance().player.setYaw(rotation.getYaw());
            MinecraftClient.getInstance().player.setPitch(rotation.getPitch());
         }
      }
   }
}
