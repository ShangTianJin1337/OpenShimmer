package dev.greencat.shimmer.module.modules.misc;

import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;

public class HClip extends Module {
   NumberSetting distance = new NumberSetting("Distance", "Clip distance", 2.7, 0.01, 10.0, 0.1);
   NumberSetting delay = new NumberSetting("Delay", "Max floating delay", 70.0, 0.0, 500.0, 1.0);

   public HClip() {
      super("HClip", "Let you clip a distance", -1, Module.Category.MISC);
      this.addSettings(new Setting[]{this.distance, this.delay});
   }

   @Override
   public void toggle() {
      this.doClip();
   }

   public void doClip() {
      if (MinecraftClient.getInstance().player != null) {
         float speed = (float)((double)MinecraftClient.getInstance().player.getMovementSpeed() * this.distance.getValue());
         float yaw = MinecraftClient.getInstance().player.getYaw() * (float) Math.PI / 180.0F;
         double x = (double)(-MathHelper.sin(yaw) * speed);
         double z = (double)(MathHelper.cos(yaw) * speed);
         MinecraftClient.getInstance().options.forwardKey.setPressed(false);
         MinecraftClient.getInstance().player.setVelocity(0.0, MinecraftClient.getInstance().player.getMovement().getY(), 0.0);
         ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

         try {
            scheduler.schedule(
               () -> {
                  MinecraftClient.getInstance().player.setVelocity(x, MinecraftClient.getInstance().player.getMovement().getY(), z);
                  MinecraftClient.getInstance()
                     .options
                     .forwardKey
                     .setPressed(
                        InputUtil.isKeyPressed(
                           MinecraftClient.getInstance().getWindow(), MinecraftClient.getInstance().options.forwardKey.getDefaultKey().getCode()
                        )
                     );
               },
               Math.round(this.delay.getValue()),
               TimeUnit.MILLISECONDS
            );
         } catch (Throwable var11) {
            if (scheduler != null) {
               try {
                  scheduler.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (scheduler != null) {
            scheduler.close();
         }
      }
   }
}
