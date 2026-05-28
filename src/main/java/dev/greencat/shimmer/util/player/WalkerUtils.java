package dev.greencat.shimmer.util.player;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import dev.greencat.shimmer.util.player.rotation.Rotation;
import dev.greencat.shimmer.util.player.rotation.SmoothRotation;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class WalkerUtils {
   private static boolean isConfigLoaded = false;

   public static boolean walkTo(BlockPos pos) {
      if (isActive()) {
         cancel();
      }

      if (MinecraftClient.getInstance().player == null) {
         return false;
      } else {
         BaritoneAPI.getSettings().allowSprint.value = true;
         BaritoneAPI.getSettings().considerPotionEffects.value = true;
         BaritoneAPI.getSettings().avoidance.value = false;
         BaritoneAPI.getSettings().allowWalkOnBottomSlab.value = true;
         BaritoneAPI.getSettings().antiCheatCompatibility.value = true;
         BaritoneAPI.getSettings().allowParkour.value = true;
         BaritoneAPI.getSettings().allowBreak.value = false;
         BaritoneAPI.getSettings().allowParkourAscend.value = true;
         BaritoneAPI.getSettings().renderGoal.value = true;
         BaritoneAPI.getSettings().allowPlace.value = false;
         BaritoneAPI.getSettings().renderPath.value = true;
         BaritoneAPI.getSettings().assumeSafeWalk.value = false;
         if (!isConfigLoaded) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage("#blocksToAvoid oak_fence_gate");
            isConfigLoaded = true;
         }

         BaritoneAPI.getProvider()
            .getPrimaryBaritone()
            .getCustomGoalProcess()
            .setGoalAndPath(new GoalBlock(pos.getX(), pos.getY() + 1, pos.getZ()));
         double motionX = MinecraftClient.getInstance().player.getMovement().getX();
         double motionZ = MinecraftClient.getInstance().player.getMovement().getZ();
         double playerYawX = Math.cos((double)(MathHelper.wrapDegrees(MinecraftClient.getInstance().player.getYaw()) / 180.0F) * Math.PI);
         double playerYawY = Math.sin((double)(MathHelper.wrapDegrees(MinecraftClient.getInstance().player.getYaw()) / 180.0F) * Math.PI);
         double degree = motionX * motionZ != 0.0 && playerYawX * playerYawY != 0.0
            ? Math.acos((motionX * playerYawX + motionZ * playerYawY) / Math.sqrt(motionX * motionX + motionZ * motionZ)) * 180.0 / Math.PI
            : -10000.0;
         float yaw = (float)(
            degree != -10000.0 && !Double.isNaN(degree)
               ? (double)MinecraftClient.getInstance().player.getYaw() + degree / 2.0 - 45.0
               : (double)MinecraftClient.getInstance().player.getYaw()
         );
         SmoothRotation.smoothLook(new Rotation(yaw, MinecraftClient.getInstance().player.getPitch()), 100, () -> {
         });
         return true;
      }
   }

   public static boolean walkToBaritoneRotation(BlockPos pos) {
      if (isActive()) {
         cancel();
      }

      if (MinecraftClient.getInstance().player == null) {
         return false;
      } else {
         BaritoneAPI.getSettings().allowSprint.value = true;
         BaritoneAPI.getSettings().considerPotionEffects.value = true;
         BaritoneAPI.getSettings().avoidance.value = false;
         BaritoneAPI.getSettings().allowWalkOnBottomSlab.value = true;
         BaritoneAPI.getSettings().antiCheatCompatibility.value = true;
         BaritoneAPI.getSettings().allowParkour.value = true;
         BaritoneAPI.getSettings().allowBreak.value = false;
         BaritoneAPI.getSettings().allowParkourAscend.value = true;
         BaritoneAPI.getSettings().renderGoal.value = true;
         BaritoneAPI.getSettings().allowPlace.value = false;
         BaritoneAPI.getSettings().renderPath.value = true;
         BaritoneAPI.getSettings().assumeSafeWalk.value = false;
         BaritoneAPI.getSettings().smoothLook.value = true;
         BaritoneAPI.getProvider()
            .getPrimaryBaritone()
            .getCustomGoalProcess()
            .setGoalAndPath(new GoalBlock(pos.getX(), pos.getY() + 1, pos.getZ()));
         if (!isConfigLoaded) {
            MinecraftClient.getInstance().player.networkHandler.sendChatMessage("#blocksToAvoid oak_fence_gate");
            isConfigLoaded = true;
         }

         return true;
      }
   }

   public static boolean isActive() {
      return BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive();
   }

   public static void cancel() {
      BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
   }

   public static void handleRotation(MinecraftClient mc) {
      if (mc.player != null && isActive()) {
         double motionX = mc.player.getMovement().getX();
         double motionZ = mc.player.getMovement().getZ();
         double playerYawX = Math.cos((double)(MathHelper.wrapDegrees(mc.player.getYaw()) / 180.0F) * Math.PI);
         double playerYawY = Math.sin((double)(MathHelper.wrapDegrees(mc.player.getYaw()) / 180.0F) * Math.PI);
         double degree = motionX * motionZ != 0.0 && playerYawX * playerYawY != 0.0
            ? Math.acos((motionX * playerYawX + motionZ * playerYawY) / Math.sqrt(motionX * motionX + motionZ * motionZ)) * 180.0 / Math.PI
            : -10000.0;
         float yaw = (float)(
            degree != -10000.0 && !Double.isNaN(degree) ? (double)mc.player.getYaw() + degree / 2.0 - 45.0 : (double)mc.player.getYaw()
         );
         if ((double)Math.abs(mc.player.getYaw() - yaw) >= new Random().nextDouble(3.0, 8.0)) {
            SmoothRotation.smoothLook(new Rotation(yaw, mc.player.getPitch()), 300, () -> {
            });
         }
      }
   }
}
