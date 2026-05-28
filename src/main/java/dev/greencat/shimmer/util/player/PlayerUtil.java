package dev.greencat.shimmer.util.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.joml.Math;

public class PlayerUtil {
   public static void useItem() {
      if (MinecraftClient.getInstance().interactionManager != null) {
         MinecraftClient.getInstance()
            .interactionManager
            .interactItem(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getActiveHand());
      }
   }

   public static boolean isWithin(Entity entity, double r) {
      return squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) <= r * r;
   }

   public static boolean isWithin(double x, double y, double z, double r) {
      return squaredDistanceTo(x, y, z) <= r * r;
   }

   public static double squaredDistanceTo(Entity entity) {
      return squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ());
   }

   public static double squaredDistanceTo(double x, double y, double z) {
      return squaredDistance(
         MinecraftClient.getInstance().player.getX(),
         MinecraftClient.getInstance().player.getY(),
         MinecraftClient.getInstance().player.getZ(),
         x,
         y,
         z
      );
   }

   public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
      double f = x1 - x2;
      double g = y1 - y2;
      double h = z1 - z2;
      return Math.fma(f, f, Math.fma(g, g, h * h));
   }

   public static boolean canSeeEntity(Entity entity) {
      return MinecraftClient.getInstance().player.canSee(entity);
   }
}
