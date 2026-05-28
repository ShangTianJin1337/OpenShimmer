package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.PlayerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Items;

public class AutoReel extends Module {
   private static long lastReel = 0L;
   private static long lastCheck = 0L;

   public AutoReel() {
      super("AutoReel", "Auto right click when you need to do a reel", -1, Module.Category.MACRO);
   }

   @ShimmerSubscribe
   public void onClientTick(TickEvent event) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         if (System.currentTimeMillis() - lastCheck > 400L) {
            if (System.currentTimeMillis() - lastReel > 800L) {
               MinecraftClient.getInstance()
                  .world
                  .getEntities()
                  .forEach(
                     entity -> {
                        if (entity.hasCustomName()
                           && MinecraftClient.getInstance().player.getMainHandStack().getItem() == Items.LEAD
                           && entity instanceof ArmorStandEntity
                           && entity.getCustomName().getString().contains("REEL")) {
                           PlayerUtil.useItem();
                           lastReel = System.currentTimeMillis();
                        }
                     }
                  );
               lastCheck = System.currentTimeMillis();
            }
         }
      }
   }
}
