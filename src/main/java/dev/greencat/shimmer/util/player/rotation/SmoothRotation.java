package dev.greencat.shimmer.util.player.rotation;

import net.minecraft.client.MinecraftClient;

public class SmoothRotation {
   private static float pitchDifference;
   public static float yawDifference;
   private static int ticks = -1;
   private static int tickCounter = 0;
   private static boolean lastTickChangeYaw = false;
   private static Runnable callback = null;
   public static boolean isServerRotation = false;
   public static boolean running = false;

   public static void smoothLook(Rotation rotation, int ms, Runnable callback) {
      smoothLook(rotation, ms, callback, false);
   }

   public static void smoothLook(Rotation rotation, int ms, Runnable callback, boolean isServerRotation) {
      if (MinecraftClient.getInstance().player != null) {
         SmoothRotation.isServerRotation = isServerRotation;
         if (isServerRotation) {
            ServerRotation.useServerRotation = true;
         }

         if (ms == 0) {
            look(rotation);
            callback.run();
            if (isServerRotation) {
               ServerRotation.useServerRotation = false;
            }
         } else {
            SmoothRotation.callback = callback;
            pitchDifference = wrapAngleTo180(rotation.getPitch() - MinecraftClient.getInstance().player.getPitch());
            yawDifference = wrapAngleTo180(rotation.getYaw() - MinecraftClient.getInstance().player.getYaw());
            float second = (float)ms / 1000.0F;
            ticks = (int)(second * 20.0F);
            tickCounter = 0;
         }
      }
   }

   public static void look(Rotation rotation) {
      if (!isServerRotation) {
         MinecraftClient.getInstance().player.setYaw(rotation.getYaw());
         MinecraftClient.getInstance().player.setPitch(rotation.getPitch());
      } else {
         ServerRotation.serverYaw = rotation.getYaw();
         ServerRotation.serverPitch = rotation.getPitch();
         lastTickChangeYaw = !lastTickChangeYaw;
         MinecraftClient.getInstance().player.setYaw(MinecraftClient.getInstance().player.getYaw() + (lastTickChangeYaw ? -0.1F : 0.1F));
      }
   }

   public static void onTick(MinecraftClient mc) {
      if (mc.player != null) {
         if (tickCounter < ticks) {
            running = true;
            if (!isServerRotation) {
               mc.player.setPitch(mc.player.getPitch() + pitchDifference / (float)ticks);
               mc.player.setYaw(mc.player.getYaw() + yawDifference / (float)ticks);
            } else {
               ServerRotation.serverYaw = ServerRotation.serverYaw + pitchDifference / (float)ticks;
               ServerRotation.serverPitch = ServerRotation.serverPitch + yawDifference / (float)ticks;
               lastTickChangeYaw = !lastTickChangeYaw;
               MinecraftClient.getInstance().player.setYaw(MinecraftClient.getInstance().player.getYaw() + (lastTickChangeYaw ? -0.1F : 0.1F));
            }

            tickCounter++;
         } else if (callback != null) {
            running = false;
            if (isServerRotation) {
               ServerRotation.useServerRotation = false;
            }

            callback.run();
            callback = null;
         }
      }
   }

   private static double wrapAngleTo180(double angle) {
      return angle - Math.floor(angle / 360.0 + 0.5) * 360.0;
   }

   private static float wrapAngleTo180(float angle) {
      return (float)((double)angle - Math.floor((double)(angle / 360.0F) + 0.5) * 360.0);
   }
}
