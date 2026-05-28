package dev.greencat.shimmer.module.modules.misc;

import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class ChestPlacer extends Module {
   public ChestPlacer() {
      super("ChestPlacer", "Place ghost block chest that you can use it cross wall", -1, Module.Category.MISC);
   }

   @Override
   public void toggle() {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult) {
            BlockHitResult blockHitResult = (BlockHitResult)MinecraftClient.getInstance().crosshairTarget;
            BlockPos blockPos = blockHitResult.getBlockPos();
            Block targetBlock = MinecraftClient.getInstance().world.getBlockState(blockPos.up()).getBlock();
            Block targetBlock2 = MinecraftClient.getInstance().world.getBlockState(blockPos.up(2)).getBlock();
            if (targetBlock == Blocks.AIR && targetBlock2 == Blocks.AIR) {
               MinecraftClient.getInstance().world.setBlockState(blockPos.up(), Blocks.ENDER_CHEST.getDefaultState(), 18);
               MinecraftClient.getInstance().world.setBlockState(blockPos.up(2), Blocks.ENDER_CHEST.getDefaultState(), 18);
            }
         }
      }
   }
}
