package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.KeyEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.KeyBindAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.util.player.PlayerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class InstantSwitch extends Module {
   int currentSlot = 0;
   int status = 0;
   public static boolean use = false;
   public static NumberSetting slot = new NumberSetting("Slot", "", 1.0, 1.0, 9.0, 1.0);

   public InstantSwitch() {
      super("InstantSwitch", "Auto use Heal Wand when you at low health", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{slot});
   }

   @Override
   public void onDisable() {
      super.onDisable();
      use = false;
      this.status = 0;
      this.currentSlot = 0;
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (this.status == 0 && use) {
            use = false;
            this.currentSlot = MinecraftClient.getInstance().player.getInventory().getSelectedSlot();
            KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[(int)(slot.getValue() - 1.0)]).getBoundKey());
            this.status = 1;
         } else {
            if (this.status == 1) {
               PlayerUtil.useItem();
               this.status = 2;
            }

            if (this.status == 2) {
               KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[this.currentSlot]).getBoundKey());
               this.status = 0;
            }
         }
      }
   }

   @ShimmerSubscribe
   public void onKeyPress(KeyEvent event) {
      if (this.isEnabled() && Shimmer.getInstance().getKeyBindManager().instantSwitch.isPressed()) {
         use = true;
      }
   }
}
