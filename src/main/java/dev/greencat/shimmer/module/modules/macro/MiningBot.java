package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerEvent.Era;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import java.util.Timer;
import java.util.TimerTask;

public class MiningBot extends Module {
   public static final NumberSetting VL = new NumberSetting("VL", "", 16.0, 1.0, 1200.0, 1.0);
   Timer timer = new Timer();
   public int vl = 0;

   public MiningBot() {
      super("MiningBot", "", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{VL});
   }

   @Override
   public void onEnable() {
      super.onEnable();
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(true);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (event.getEra() != Era.POST) {
         final Nuker nuker = (Nuker)Shimmer.getInstance().getModuleManager().getModule("Nuker");
         if (dev.greencat.shimmer.util.world.MiningBot.target == null) {
            this.vl++;
         } else {
            this.vl = 0;
         }

         if ((double)this.vl >= VL.getValue()) {
            this.vl = 0;
            nuker.setEnabled(false);
            Shimmer.etherwarpHelper.next();
            this.timer.schedule(new TimerTask() {
               public void run() {
                  nuker.setEnabled(true);
               }
            }, 600L);
         }
      }
   }
}
