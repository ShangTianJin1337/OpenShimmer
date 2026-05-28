package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Items;

public class RatESP extends Module {
   public RatESP() {
      super("RatESP", "", -1, Module.Category.RENDER);
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.AfterEntities event) {
      if (mc.world != null && mc.player != null) {
         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ArmorStandEntity) {
               ArmorStandEntity target = (ArmorStandEntity)entity;
               if (((ArmorStandEntity)entity).getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.PLAYER_HEAD) {
                  int nearbyZombieStandNumber = 0;
                  if (target != null) {
                     for (Entity entity1 : mc.world.getEntities()) {
                        if (entity1 instanceof ZombieEntity) {
                           ZombieEntity armorStand = (ZombieEntity)entity1;
                           if (!armorStand.equals(target) && armorStand.distanceTo(target) <= 3.0F) {
                              nearbyZombieStandNumber++;
                           }
                        }
                     }

                     if (nearbyZombieStandNumber != 0) {
                        RenderUtil.draw3DOutline(target.getBoundingBox(), Color.ORANGE, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                        RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), Color.ORANGE, event.storage.getEntityVertexConsumers());
                     }
                  }
               }
            }
         }
      }
   }
}
