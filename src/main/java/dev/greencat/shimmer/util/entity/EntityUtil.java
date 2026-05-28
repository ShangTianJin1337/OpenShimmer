package dev.greencat.shimmer.util.entity;

import dev.greencat.shimmer.util.world.WorldUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.VillagerEntity;

public class EntityUtil {
   public static boolean isNPC(Entity entity) {
      if (entity instanceof VillagerEntity) {
         return true;
      } else if (!(entity instanceof OtherClientPlayerEntity)) {
         return false;
      } else {
         LivingEntity entityLivingBase = (LivingEntity)entity;
         return entity.getUuid().version() == 2 && entityLivingBase.getHealth() == 20.0F;
      }
   }

   public static boolean isTeamMember(LivingEntity e, LivingEntity e2) {
      if (e.getDisplayName() != null && e2.getDisplayName() != null) {
         if (e.getDisplayName().getString().length() < 4) {
            return false;
         } else if (e.getDisplayName().getStyle().isEmpty() || e2.getDisplayName().getStyle().isEmpty()) {
            return false;
         } else if (WorldUtil.isOnSkyBlock()) {
            return true;
         } else {
            return e.getDisplayName().getStyle().getColor() != null && e.getDisplayName().getStyle().getColor() != null
               ? e.getDisplayName().getStyle().getColor().equals(e2.getDisplayName().getStyle().getColor())
               : e.getDisplayName().getStyle().getColor() == null && e2.getDisplayName().getStyle().getColor() == null;
         }
      } else {
         return false;
      }
   }

   public static List<LivingEntity> getEntityByArmorStandName(String name, LivingEntity targetType) {
      List<ArmorStandEntity> signEntity = new ArrayList();

      for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
         if (entity instanceof ArmorStandEntity && entity.hasCustomName() && entity.getCustomName().getString().contains(name)) {
            signEntity.add((ArmorStandEntity)entity);
         }
      }

      List<LivingEntity> finalEntity = new ArrayList();
      if (!signEntity.isEmpty()) {
         for (Entity entityx : MinecraftClient.getInstance().world.getEntities()) {
            for (ArmorStandEntity sign : signEntity) {
               if ((double)entityx.distanceTo(sign) < 2.5 && entityx.getClass().getName().equals(targetType.getClass().getName())) {
                  finalEntity.add((LivingEntity)entityx);
               }
            }
         }
      }

      return finalEntity;
   }
}
