package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.PacketEvent;
import dev.greencat.shimmer.event.events.WorldChangeEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.util.WindowsNotificationUtils;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class MacroProtector extends Module {
   public static long lastWarp = 0L;
   private static Timer timer = new Timer();
   public final BooleanSetting sound = new BooleanSetting("Sound", "", false);
   public final BooleanSetting systemTray = new BooleanSetting("System Tray", "", true);

   public MacroProtector() {
      super("MacroProtector", "Auto disable macro when admin is checking you", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{this.sound, this.systemTray});
   }

   @ShimmerSubscribe
   public void onPacket(PacketEvent events) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         if ((events.getPacket() instanceof PlayerPositionLookS2CPacket || events.getPacket() instanceof PlayerRotationS2CPacket)
            && System.currentTimeMillis() - lastWarp >= 2000L
            && !MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains("Aspect of the")
            && !MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains("Hyperion")
            && !MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains("Scylla")
            && !MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains("Valk")
            && !MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains("Astr")) {
            if (this.systemTray.isEnabled()) {
               WindowsNotificationUtils.sendNotification("MacroProtector", "检测到异常传送或转头,已自动停用Macro", 2);
            }

            if (this.sound.isEnabled()) {
               MinecraftClient.getInstance()
                  .world
                  .playSound(
                     MinecraftClient.getInstance().player,
                     MinecraftClient.getInstance().player.getBlockPos().up(),
                     SoundEvents.ENTITY_BAT_DEATH,
                     SoundCategory.PLAYERS,
                     3.0F,
                     1.0F
                  );
            }

            this.disableAllMacro();
            lastWarp = System.currentTimeMillis();
         }

         if (events.getPacket() instanceof UpdateSelectedSlotS2CPacket) {
            if (this.systemTray.isEnabled()) {
               WindowsNotificationUtils.sendNotification("MacroProtector", "检测到异常物品栏改变,已自动停用Macro", 2);
            }

            if (this.sound.isEnabled()) {
               MinecraftClient.getInstance()
                  .world
                  .playSound(
                     MinecraftClient.getInstance().player,
                     MinecraftClient.getInstance().player.getBlockPos().up(),
                     SoundEvents.ENTITY_BAT_DEATH,
                     SoundCategory.PLAYERS,
                     3.0F,
                     1.0F
                  );
            }

            this.disableAllMacro();
            lastWarp = System.currentTimeMillis();
         }
      }
   }

   @ShimmerSubscribe
   public void onWorldChange(WorldChangeEvent event) {
      if (System.currentTimeMillis() - lastWarp >= 2000L) {
         timer.schedule(new TimerTask() {
            public void run() {
               WindowsNotificationUtils.sendNotification("MacroProtector", "检测到世界改变,已自动停用Macro", 2);
               MacroProtector.this.disableAllMacro();
            }
         }, 500L);
         lastWarp = System.currentTimeMillis();
      }
   }

   public void disableAllMacro() {
      for (Module module : Shimmer.getInstance().getModuleManager().modules) {
         if (module.needDisable && module.enabled) {
            module.setEnabled(false);
         }
      }
   }
}
