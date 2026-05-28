package dev.greencat.shimmer.command.commands;

import dev.greencat.shimmer.util.HaikuLogger;
import java.util.LinkedList;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RayCastTestClass {
   public static BlockPos debugStart;
   public static BlockPos debugEnd;
   public static LinkedList<BlockPos> allPos = new LinkedList();

   public static void nextStep() {
      if (debugStart != null && debugEnd != null) {
         Vec3d playerVec = Vec3d.of(debugStart.up(2)).add(0.5, 0.0, -0.5);
         Vec3d blockVec = Vec3d.of(debugEnd);
         if (debugStart.up(2).getY() - debugEnd.getY() > 0) {
            blockVec = blockVec.add(0.5, 1.0, 0.5);
         } else {
            if (debugStart.getX() > debugEnd.getX()) {
               if (MinecraftClient.getInstance().world.getBlockState(debugEnd.add(1, 0, 0)).isAir()) {
                  blockVec = blockVec.add(1.0, 0.0, 0.0);
               } else {
                  blockVec = blockVec.add(0.5, 0.0, 0.0);
               }
            }

            if (debugStart.getX() < debugEnd.getX() && !MinecraftClient.getInstance().world.getBlockState(debugEnd.add(-1, 0, 0)).isAir()) {
               blockVec = blockVec.add(0.5, 0.0, 0.0);
            }

            if (debugStart.getZ() > debugEnd.getZ()) {
               if (MinecraftClient.getInstance().world.getBlockState(debugEnd.add(0, 0, 1)).isAir()) {
                  blockVec = blockVec.add(0.0, 0.0, 1.0);
               } else {
                  blockVec = blockVec.add(0.0, 0.0, 0.5);
               }
            }

            if (debugStart.getZ() > debugEnd.getZ() && MinecraftClient.getInstance().world.getBlockState(debugEnd.add(0, 0, 1)).isAir()) {
               blockVec = blockVec.add(0.0, 0.0, 0.5);
            }

            blockVec = blockVec.add(0.0, 0.5, 0.0);
            if (debugStart.getX() == debugEnd.getX()
               && debugStart.getZ() == debugEnd.getZ()
               && debugStart.up(2).getY() < debugEnd.getY()) {
               blockVec = blockVec.add(0.5, -0.5, 0.5);
            }
         }

         Vec3d unitVector = blockVec.subtract(playerVec).multiply(1.0 / playerVec.distanceTo(blockVec));
         boolean hasBlock = false;

         for (double currentJ = 0.0; currentJ < playerVec.distanceTo(blockVec); currentJ += unitVector.distanceTo(new Vec3d(0.0, 0.0, 0.0))) {
            Vec3d currentVec = playerVec.add(unitVector.multiply(currentJ));
            BlockPos waitedCheck = new BlockPos((int)currentVec.getX(), (int)currentVec.getY(), (int)currentVec.getZ());
            allPos.add(waitedCheck);
            if (!waitedCheck.equals(debugEnd)
               && !waitedCheck.equals(debugStart)
               && MinecraftClient.getInstance().world.getBlockState(waitedCheck).getBlock() != Blocks.AIR) {
               hasBlock = true;
            }
         }

         HaikuLogger.info(hasBlock + " is status.");
      }
   }
}
