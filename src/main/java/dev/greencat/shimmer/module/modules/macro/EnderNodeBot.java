package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.settings.ModeSetting;
import dev.greencat.shimmer.util.player.WalkerUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class EnderNodeBot extends Module {
   public static boolean needHugeSearch = false;

   public EnderNodeBot() {
      super("EnderNodeBot", "Break ender node automatically", -1, Module.Category.MACRO);
      this.needDisable = true;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      needHugeSearch = false;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
      WalkerUtils.cancel();
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         List<Block> list = new ArrayList();
         list.add(Blocks.PURPLE_TERRACOTTA);
         boolean hasNodeNearby = false;

         for (BlockPos pos : BlockPos.iterate(
            new BlockPos(
               MinecraftClient.getInstance().player.getBlockX() + 2,
               MinecraftClient.getInstance().player.getBlockY() - 2,
               MinecraftClient.getInstance().player.getBlockZ() + 2
            ),
            new BlockPos(
               MinecraftClient.getInstance().player.getBlockX() - 2,
               MinecraftClient.getInstance().player.getBlockY() + 2,
               MinecraftClient.getInstance().player.getBlockZ() - 2
            )
         )) {
            if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.PURPLE_TERRACOTTA) {
               hasNodeNearby = true;
               break;
            }
         }

         if (hasNodeNearby) {
            Shimmer.getInstance().getModuleManager().getModule("Nuker").settings.forEach(settings -> {
               if (settings.name.equals("Nuker Mode")) {
                  ((ModeSetting)settings).setMode("EnderNode");
               }
            });
            Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(true);
         } else {
            Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
         }

         if (!hasNodeNearby) {
            int searchRadius = needHugeSearch && !WalkerUtils.isActive() ? 150 : 30;
            double cacheDistance = 2.099999999E9;
            BlockPos needWalkPos = null;

            for (BlockPos posx : BlockPos.iterate(
               new BlockPos(
                  MinecraftClient.getInstance().player.getBlockX() + searchRadius,
                  MinecraftClient.getInstance().player.getBlockY() + searchRadius,
                  MinecraftClient.getInstance().player.getBlockZ() + searchRadius
               ),
               new BlockPos(
                  MinecraftClient.getInstance().player.getBlockX() - searchRadius,
                  MinecraftClient.getInstance().player.getBlockY() - searchRadius,
                  MinecraftClient.getInstance().player.getBlockZ() - searchRadius
               )
            )) {
               if (MinecraftClient.getInstance().world.getBlockState(posx).getBlock() == Blocks.PURPLE_TERRACOTTA
                  && (
                     MinecraftClient.getInstance().world.getBlockState(posx.up()).getBlock() == Blocks.AIR
                        || MinecraftClient.getInstance().world.getBlockState(posx.down()).getBlock() == Blocks.AIR
                        || MinecraftClient.getInstance().world.getBlockState(posx.east()).getBlock() == Blocks.AIR
                        || MinecraftClient.getInstance().world.getBlockState(posx.west()).getBlock() == Blocks.AIR
                        || MinecraftClient.getInstance().world.getBlockState(posx.south()).getBlock() == Blocks.AIR
                        || MinecraftClient.getInstance().world.getBlockState(posx.north()).getBlock() == Blocks.AIR
                  )
                  && posx.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos()) <= cacheDistance) {
                  double cacheWalkDistance = 9999.0;
                  BlockPos nearbyStandablePos = null;

                  for (BlockPos pos1 : BlockPos.iterate(posx.add(3, 3, 3), posx.add(-3, -3, -3))) {
                     if (MinecraftClient.getInstance().world.getBlockState(pos1).getBlock() != Blocks.AIR
                        && MinecraftClient.getInstance().world.getBlockState(pos1).getBlock() != Blocks.PURPLE_CARPET
                        && MinecraftClient.getInstance().world.getBlockState(pos1.up()).getBlock() == Blocks.AIR
                        && MinecraftClient.getInstance().world.getBlockState(pos1.up(2)).getBlock() == Blocks.AIR
                        && pos1.up(2).getSquaredDistance(posx) < cacheWalkDistance) {
                        cacheWalkDistance = pos1.up(2).getSquaredDistance(posx);
                        nearbyStandablePos = pos1.toImmutable();
                     }
                  }

                  needWalkPos = nearbyStandablePos;
                  if (nearbyStandablePos != null) {
                     cacheDistance = posx.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos());
                  }
               }
            }

            if (!needHugeSearch && needWalkPos == null) {
               needHugeSearch = true;
            } else if (needHugeSearch && needWalkPos == null && !WalkerUtils.isActive()) {
               this.setEnabled(false);
            }

            if (needWalkPos != null && !WalkerUtils.isActive()) {
               if (needHugeSearch) {
                  needHugeSearch = false;
               }

               WalkerUtils.walkToBaritoneRotation(needWalkPos);
            }
         }
      }
   }
}
