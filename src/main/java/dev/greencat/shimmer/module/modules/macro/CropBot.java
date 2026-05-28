package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.ModeSetting;
import dev.greencat.shimmer.util.player.WalkerUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class CropBot extends Module {
   public static long blacklistCleanTimer = 0L;
   public static boolean needHugeSearch = false;
   public static final List<BlockPos> blacklist = new ArrayList();
   public final ModeSetting mode = new ModeSetting(
      "CropBot Mode", "Change your CropBot mode", "Sugar Cane", "Sugar Cane", "Pumpkin", "Wheat", "Melon", "Carrot"
   );

   public CropBot() {
      super("CropBot", "Break crop automatically", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{this.mode});
      this.needDisable = true;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      blacklist.clear();
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
         if (this.mode.getMode().equals("Sugar Cane")) {
            list.add(Blocks.SUGAR_CANE);
         }

         if (this.mode.getMode().equals("Pumpkin")) {
            list.add(Blocks.PUMPKIN);
            list.add(Blocks.CARVED_PUMPKIN);
         }

         if (this.mode.getMode().equals("Wheat")) {
            list.add(Blocks.WHEAT);
         }

         if (this.mode.getMode().equals("Melon")) {
            list.add(Blocks.MELON);
         }

         if (this.mode.getMode().equals("Carrot")) {
            list.add(Blocks.CARROTS);
         }

         boolean hasCropNearby = false;

         for (BlockPos pos : BlockPos.iterate(
            new BlockPos(
               MinecraftClient.getInstance().player.getBlockX() + 2,
               MinecraftClient.getInstance().player.getBlockY() - 1,
               MinecraftClient.getInstance().player.getBlockZ() + 2
            ),
            new BlockPos(
               MinecraftClient.getInstance().player.getBlockX() - 2,
               MinecraftClient.getInstance().player.getBlockY() + 1,
               MinecraftClient.getInstance().player.getBlockZ() - 2
            )
         )) {
            if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() instanceof CropBlock
               && (Integer)MinecraftClient.getInstance().world.getBlockState(pos).get(CropBlock.AGE) == 7
               && list.contains(MinecraftClient.getInstance().world.getBlockState(pos).getBlock())) {
               hasCropNearby = true;
               break;
            }

            if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.SUGAR_CANE
               && MinecraftClient.getInstance().world.getBlockState(pos.up()).getBlock() == Blocks.SUGAR_CANE
               && MinecraftClient.getInstance().world.getBlockState(pos.down()).getBlock() == Blocks.SUGAR_CANE
               && list.contains(MinecraftClient.getInstance().world.getBlockState(pos).getBlock())) {
               hasCropNearby = true;
               break;
            }

            if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.MELON
               && list.contains(MinecraftClient.getInstance().world.getBlockState(pos).getBlock())) {
               hasCropNearby = true;
               break;
            }

            if ((
                  MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.PUMPKIN
                     || MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.CARVED_PUMPKIN
               )
               && list.contains(MinecraftClient.getInstance().world.getBlockState(pos).getBlock())) {
               hasCropNearby = true;
               break;
            }
         }

         if (hasCropNearby) {
            Shimmer.getInstance().getModuleManager().getModule("Nuker").settings.forEach(settings -> {
               if (settings.name.equals("Nuker Mode")) {
                  ((ModeSetting)settings).setMode(this.mode.getMode());
               }
            });
            Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(true);
         } else {
            Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
         }

         if (!hasCropNearby) {
            int searchRadius = needHugeSearch && !WalkerUtils.isActive() ? 300 : 30;
            double cacheDistance = 9999.0;
            BlockPos needWalkPos = null;

            for (BlockPos pos : BlockPos.iterate(
               new BlockPos(
                  MinecraftClient.getInstance().player.getBlockX() + searchRadius,
                  MinecraftClient.getInstance().player.getBlockY() - 1,
                  MinecraftClient.getInstance().player.getBlockZ() + searchRadius
               ),
               new BlockPos(
                  MinecraftClient.getInstance().player.getBlockX() - searchRadius,
                  MinecraftClient.getInstance().player.getBlockY() + 2,
                  MinecraftClient.getInstance().player.getBlockZ() - searchRadius
               )
            )) {
               if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() instanceof CropBlock
                  && (Integer)MinecraftClient.getInstance().world.getBlockState(pos).get(CropBlock.AGE) == 7
                  && list.contains(MinecraftClient.getInstance().world.getBlockState(pos).getBlock())
                  && pos.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos()) <= cacheDistance
                  && !blacklist.contains(pos)) {
                  needWalkPos = pos.down().toImmutable();
                  cacheDistance = pos.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos());
               }

               if ((
                     MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.MELON
                        || MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.PUMPKIN
                        || MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.CARVED_PUMPKIN
                  )
                  && list.contains(MinecraftClient.getInstance().world.getBlockState(pos).getBlock())
                  && pos.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos()) <= cacheDistance
                  && !blacklist.contains(pos)) {
                  needWalkPos = pos.toImmutable();
                  cacheDistance = pos.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos());
               }

               if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.SUGAR_CANE
                  && MinecraftClient.getInstance().world.getBlockState(pos.up()).getBlock() == Blocks.SUGAR_CANE
                  && MinecraftClient.getInstance().world.getBlockState(pos.down()).getBlock() == Blocks.SUGAR_CANE
                  && list.contains(MinecraftClient.getInstance().world.getBlockState(pos).getBlock())
                  && pos.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos()) <= cacheDistance
                  && !blacklist.contains(pos)) {
                  needWalkPos = pos.down(2).toImmutable();
                  cacheDistance = pos.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos());
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

            if (System.currentTimeMillis() - blacklistCleanTimer >= 1800000L) {
               blacklistCleanTimer = System.currentTimeMillis();
               blacklist.clear();
            }

            if (dev.greencat.shimmer.util.world.MiningBot.target != null) {
               blacklist.add(dev.greencat.shimmer.util.world.MiningBot.target);
            }
         }
      }
   }
}
