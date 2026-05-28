package dev.greencat.shimmer.module.modules.movement;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import net.minecraft.block.Blocks;

public class Eagle extends Module {
   public Eagle() {
      super("Eagle", "Keep you sneak when you at block edge", -1, Module.Category.MOVEMENT);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (mc.player != null && mc.world != null) {
         if (mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock() == Blocks.AIR) {
            mc.options.sneakKey.setPressed(true);
         } else {
            mc.options.sneakKey.setPressed(false);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      mc.options.sneakKey.setPressed(false);
   }
}
