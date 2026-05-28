package dev.greencat.shimmer.module.modules.movement;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;

public class Sprint extends Module {
   public Sprint() {
      super("Sprint", "Automatically sprints for you.", -1, Module.Category.MOVEMENT);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (mc.world != null && mc.player != null) {
         if (mc.player.forwardSpeed > 0.0F && !mc.player.horizontalCollision && !mc.player.isSneaking() && !mc.player.isUsingItem()) {
            mc.player.setSprinting(true);
         }
      }
   }
}
