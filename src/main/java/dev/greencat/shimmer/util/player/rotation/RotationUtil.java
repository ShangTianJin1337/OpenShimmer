package dev.greencat.shimmer.util.player.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {
   public static Rotation toRotation(Vec3d vec3d) {
      return new Rotation((float)getYaw(vec3d), (float)getPitch(vec3d));
   }

   public static Vec3d getEyesPos() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      return new Vec3d(player.getX(), player.getY() + (double)player.getEyeHeight(player.getPose()), player.getZ());
   }

   public static double getYaw(Entity entity) {
      return (double)(
         MinecraftClient.getInstance().player.getYaw()
            + MathHelper.wrapDegrees(
               (float)Math.toDegrees(
                     Math.atan2(
                        entity.getZ() - MinecraftClient.getInstance().player.getZ(),
                        entity.getX() - MinecraftClient.getInstance().player.getX()
                     )
                  )
                  - 90.0F
                  - MinecraftClient.getInstance().player.getYaw()
            )
      );
   }

   public static double getYaw(Vec3d pos) {
      return (double)(
         MinecraftClient.getInstance().player.getYaw()
            + MathHelper.wrapDegrees(
               (float)Math.toDegrees(
                     Math.atan2(
                        pos.getZ() - MinecraftClient.getInstance().player.getZ(),
                        pos.getX() - MinecraftClient.getInstance().player.getX()
                     )
                  )
                  - 90.0F
                  - MinecraftClient.getInstance().player.getYaw()
            )
      );
   }

   public static double getPitch(Vec3d pos) {
      double diffX = pos.getX() - MinecraftClient.getInstance().player.getX();
      double diffY = pos.getY()
         - (
            MinecraftClient.getInstance().player.getY()
               + (double)MinecraftClient.getInstance().player.getEyeHeight(MinecraftClient.getInstance().player.getPose())
         );
      double diffZ = pos.getZ() - MinecraftClient.getInstance().player.getZ();
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      return (double)(
         MinecraftClient.getInstance().player.getPitch()
            + MathHelper.wrapDegrees((float)(-Math.toDegrees(Math.atan2(diffY, diffXZ))) - MinecraftClient.getInstance().player.getPitch())
      );
   }

   public static double getPitch(Entity entity, RotationUtil.Target target) {
      double y;
      if (target == RotationUtil.Target.HEAD) {
         y = entity.getEyeY();
      } else if (target == RotationUtil.Target.BODY) {
         y = entity.getY() + (double)(entity.getHeight() / 2.0F);
      } else {
         y = entity.getY();
      }

      double diffX = entity.getX() - MinecraftClient.getInstance().player.getX();
      double diffY = y
         - (
            MinecraftClient.getInstance().player.getY()
               + (double)MinecraftClient.getInstance().player.getEyeHeight(MinecraftClient.getInstance().player.getPose())
         );
      double diffZ = entity.getZ() - MinecraftClient.getInstance().player.getZ();
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      return (double)(
         MinecraftClient.getInstance().player.getPitch()
            + MathHelper.wrapDegrees((float)(-Math.toDegrees(Math.atan2(diffY, diffXZ))) - MinecraftClient.getInstance().player.getPitch())
      );
   }

   public static enum Target {
      HEAD,
      BODY,
      OTHER;
   }
}
