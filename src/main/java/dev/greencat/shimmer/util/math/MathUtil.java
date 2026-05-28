package dev.greencat.shimmer.util.math;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
   public static double calculateAngle(Vec2f v1, Vec2f v2) {
      if (v1.equals(v2)) {
         return 0.0;
      } else {
         double dotProduct = (double)(v1.x * v2.x + v1.y * v2.y);
         double magnitudeV1 = (double)v1.length();
         double magnitudeV2 = (double)v2.length();
         double angleInRadians = Math.acos(dotProduct / (magnitudeV1 * magnitudeV2));
         double angleInDegrees = Math.toDegrees(angleInRadians);
         return Double.isNaN(angleInDegrees) ? 0.0 : angleInDegrees;
      }
   }

   public static double calculateAngle(Vec3d v1, Vec3d v2) {
      double dotProduct = v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();
      double magnitudeV1 = Math.sqrt(v1.getX() * v1.getX() + v1.getY() * v1.getY() + v1.getZ() * v1.getZ());
      double magnitudeV2 = Math.sqrt(v2.getX() * v2.getX() + v2.getY() * v2.getY() + v2.getZ() * v2.getZ());
      double angleInRadians = Math.acos(dotProduct / (magnitudeV1 * magnitudeV2));
      return Math.toDegrees(angleInRadians);
   }
}
