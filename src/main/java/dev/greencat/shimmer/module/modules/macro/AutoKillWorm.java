package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.KeyBindAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.module.modules.render.Hud;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.setting.settings.StringSetting;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.player.rotation.RotationUtil.Target;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AutoKillWorm extends Module {
   public static final NumberSetting delay = new NumberSetting("Delay", "The delay between another action (Second)", 100.0, 10.0, 1200.0, 1.0);
   public static final StringSetting name = new StringSetting("Item Name", "The item you want to use", "Yeti Sword");
   public static long lastClear = 0L;
   public static float lastYaw = 0.0F;
   public static float lastPitch = 0.0F;
   public static boolean startedClear = false;
   public static long lastRightClick = 0L;

   public AutoKillWorm() {
      super("AutoKillWorm", "Kill Flaming Worm Automatically", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{delay, name});
   }

   @Override
   public void onEnable() {
      lastClear = System.currentTimeMillis();
      startedClear = false;
      super.onEnable();
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         if ((double)(System.currentTimeMillis() - lastClear) >= delay.getValue() * 1000.0) {
            if (MinecraftClient.getInstance().player.getMainHandStack().isEmpty()
               || MinecraftClient.getInstance().player.getMainHandStack().getItem() == Items.FISHING_ROD) {
               String targetItemName = name.getString();
               boolean hasName = false;
               lastYaw = MinecraftClient.getInstance().player.getYaw();
               lastPitch = MinecraftClient.getInstance().player.getPitch();
               int slotId = 0;

               for (int i = 0; i < 9; i++) {
                  ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(i);
                  if (!stack.isEmpty() && stack.getName().getString().contains(targetItemName)) {
                     hasName = true;
                     slotId = i;
                     break;
                  }
               }

               if (hasName) {
                  KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[slotId]).getBoundKey());
                  startedClear = true;
               } else {
                  Hud.onMessage(Text.literal(Formatting.BOLD + "AutoClearWorm"), Text.literal("Cannot find target weapon"));
                  lastClear = System.currentTimeMillis();
               }
            } else if (MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains(name.getString())) {
               Shimmer.getInstance().getModuleManager().getModule("AutoFish").setEnabled(false);
               int count = 0;
               Entity lastTarget = null;

               for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
                  if (entity instanceof SilverfishEntity && entity.distanceTo(MinecraftClient.getInstance().player) <= 5.0F) {
                     count++;
                     lastTarget = entity;
                  }
               }

               if (count > 0 && System.currentTimeMillis() - lastRightClick >= 1000L) {
                  KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.useKey).getBoundKey());
                  MinecraftClient.getInstance().player.setYaw((float)RotationUtil.getYaw(lastTarget));
                  MinecraftClient.getInstance().player.setPitch((float)RotationUtil.getPitch(lastTarget, RotationUtil.Target.BODY));
                  lastRightClick = System.currentTimeMillis();
               }

               if (count <= 0) {
                  int slotId = 0;

                  for (int ix = 0; ix < 9; ix++) {
                     ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(ix);
                     if (!stack.isEmpty() && stack.getItem() == Items.FISHING_ROD) {
                        slotId = ix;
                        break;
                     }
                  }

                  KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[slotId]).getBoundKey());
                  lastClear = System.currentTimeMillis();
                  MinecraftClient.getInstance().player.setYaw(lastYaw);
                  MinecraftClient.getInstance().player.setPitch(lastPitch);
                  Shimmer.getInstance().getModuleManager().getModule("AutoFish").setEnabled(true);
                  startedClear = false;
               }
            }
         }
      }
   }
}
