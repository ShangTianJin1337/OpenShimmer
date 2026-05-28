package dev.greencat.shimmer.module.modules.movement;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import net.minecraft.client.MinecraftClient;

public class Sneak extends Module {
   public Sneak() {
      super("Sneak", "Keep sneak for you.", -1, Module.Category.MOVEMENT);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (mc.world != null && mc.player != null) {
         if (!MinecraftClient.getInstance().options.sneakKey.isPressed()) {
            MinecraftClient.getInstance().options.sneakKey.setPressed(true);
         }
      }
   }

   @Override
   public void onDisable() {
      MinecraftClient.getInstance().options.sneakKey.setPressed(false);
      super.onDisable();
   }
}
