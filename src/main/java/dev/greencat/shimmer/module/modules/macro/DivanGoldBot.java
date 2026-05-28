package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerEvent.Era;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.util.player.WalkerUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class DivanGoldBot extends Module {
   public static final NumberSetting VL = new NumberSetting("VL", "", 3.0, 1.0, 1200.0, 1.0);
   List<BlockPos> blackList = new ArrayList();
   public int vl = 0;

   public DivanGoldBot() {
      super("DivanGoldBot", "", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{VL});
   }

   @Override
   public void onEnable() {
      super.onEnable();
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(true);
      this.blackList.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (event.getEra() != Era.POST) {
         Nuker nuker = (Nuker)Shimmer.getInstance().getModuleManager().getModule("Nuker");
         if (dev.greencat.shimmer.util.world.MiningBot.target == null) {
            this.vl++;
         } else {
            this.vl = 0;
         }

         if (WalkerUtils.isActive()) {
            this.vl = 0;
         }

         if ((double)this.vl >= VL.getValue() && !WalkerUtils.isActive()) {
            this.vl = 0;
            nuker.setEnabled(false);
            double distance = 999999.0;
            BlockPos targetBlock = null;

            for (BlockPos pos : BlockPos.iterate(
               new BlockPos(MinecraftClient.getInstance().player.getBlockX() + 32, 0, MinecraftClient.getInstance().player.getBlockZ() + 32),
               new BlockPos(MinecraftClient.getInstance().player.getBlockX() - 32, 160, MinecraftClient.getInstance().player.getBlockZ() - 32)
            )) {
               if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.GOLD_BLOCK
                  && !this.blackList.contains(pos)
                  && pos.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos()) < distance) {
                  distance = pos.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos());
                  targetBlock = pos.toImmutable();
               }
            }

            this.blackList.add(targetBlock);
            BlockPos target = null;
            BlockPos perferredTarget = null;

            for (BlockPos posx : BlockPos.iterate(
               new BlockPos(targetBlock.getX() + 3, targetBlock.getY() + 3, targetBlock.getZ() + 3),
               new BlockPos(targetBlock.getX() - 3, targetBlock.getY() - 3, targetBlock.getZ() - 3)
            )) {
               if (MinecraftClient.getInstance().world.getBlockState(posx).getBlock() != Blocks.AIR
                  && MinecraftClient.getInstance().world.getBlockState(posx.up()).getBlock() == Blocks.AIR
                  && MinecraftClient.getInstance().world.getBlockState(posx.up(2)).getBlock() == Blocks.AIR) {
                  target = posx.toImmutable();
                  if (posx.getY() == MinecraftClient.getInstance().player.getBlockY() - 1) {
                     perferredTarget = posx.toImmutable();
                  }
               }
            }

            if (perferredTarget != null) {
               WalkerUtils.walkTo(perferredTarget);
            } else if (target != null) {
               WalkerUtils.walkTo(target);
            }
         }

         nuker.setEnabled(!WalkerUtils.isActive());
         if (this.blackList.size() >= 300) {
            this.blackList.clear();
         }
      }
   }
}
