package dev.greencat.shimmer.module.modules.misc;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.BlockBreakingEvent;
import dev.greencat.shimmer.event.events.KeyEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class GhostBlock extends Module {
   public GhostBlock() {
      super("GhostBlock", "Allow you create ghost air block when you held Golden Pickaxe", -1, Module.Category.MISC);
   }

   @ShimmerSubscribe
   public void onBreaking(BlockBreakingEvent event) {
      if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult) {
         BlockHitResult blockHitResult = (BlockHitResult)MinecraftClient.getInstance().crosshairTarget;
         BlockPos blockPos = blockHitResult.getBlockPos();
         Block targetBlock = MinecraftClient.getInstance().world.getBlockState(blockPos).getBlock();
         if (targetBlock != Blocks.CHEST
            && targetBlock != Blocks.LEVER
            && targetBlock != Blocks.STONE_BUTTON
            && targetBlock != Blocks.PLAYER_HEAD
            && targetBlock != Blocks.PLAYER_WALL_HEAD
            && MinecraftClient.getInstance().player != null
            && MinecraftClient.getInstance().player.getInventory().getSelectedStack().getItem() == Items.GOLDEN_PICKAXE) {
            MinecraftClient.getInstance().world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 18);
            event.setCancelled(true);
         }
      }
   }

   @ShimmerSubscribe
   public void onKeyPress(KeyEvent event) {
      if (this.isEnabled()
         && Shimmer.getInstance().getKeyBindManager().useGhostBlock.isPressed()
         && MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult) {
         BlockHitResult blockHitResult = (BlockHitResult)MinecraftClient.getInstance().crosshairTarget;
         BlockPos blockPos = blockHitResult.getBlockPos();
         Block targetBlock = MinecraftClient.getInstance().world.getBlockState(blockPos).getBlock();
         if (targetBlock != Blocks.CHEST
            && targetBlock != Blocks.LEVER
            && targetBlock != Blocks.STONE_BUTTON
            && targetBlock != Blocks.PLAYER_HEAD
            && targetBlock != Blocks.PLAYER_WALL_HEAD) {
            MinecraftClient.getInstance().world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 18);
         }
      }
   }
}
