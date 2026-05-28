package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.MinecraftClient;

public class AutoJump extends Module {
   public final NumberSetting numberSetting = new NumberSetting("Time(Millsecond)", "", 3000.0, 0.01, Double.MAX_VALUE, 0.01);
   Timer timer = new Timer();
   long lastJump = 0L;

   public AutoJump() {
      super("AutoJump", "", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{this.numberSetting});
      this.needDisable = true;
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         if ((double)(System.currentTimeMillis() - this.lastJump) >= this.numberSetting.getValue()) {
            MinecraftClient.getInstance().options.jumpKey.setPressed(true);
            this.timer.schedule(new TimerTask() {
               public void run() {
                  MinecraftClient.getInstance().options.jumpKey.setPressed(false);
               }
            }, 500L);
            this.lastJump = System.currentTimeMillis();
         }
      }
   }
}
