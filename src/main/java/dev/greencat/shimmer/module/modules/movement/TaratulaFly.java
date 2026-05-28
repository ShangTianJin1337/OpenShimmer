package dev.greencat.shimmer.module.modules.movement;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import net.minecraft.client.MinecraftClient;

public class TaratulaFly extends Module {
   private int currentY = 0;
   private boolean sneaked = false;
   private long lastSneak = 0L;

   public TaratulaFly() {
      super("TaratulaFly", "Allow you fly in sky when you wearing taratula boots", -1, Module.Category.MOVEMENT);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (MinecraftClient.getInstance().player != null) {
         this.currentY = (int)MinecraftClient.getInstance().player.getY();
         this.sneaked = false;
      }
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null) {
         if (this.sneaked) {
            MinecraftClient.getInstance().options.sneakKey.setPressed(false);
            this.sneaked = false;
         } else if (MinecraftClient.getInstance().player.getY() < (double)this.currentY && System.currentTimeMillis() - this.lastSneak >= 500L) {
            MinecraftClient.getInstance().options.sneakKey.setPressed(true);
            this.sneaked = true;
            this.lastSneak = System.currentTimeMillis();
         }

         if (MinecraftClient.getInstance().player.isOnGround()) {
            this.setEnabled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      MinecraftClient.getInstance().options.sneakKey.setPressed(false);
   }
}
