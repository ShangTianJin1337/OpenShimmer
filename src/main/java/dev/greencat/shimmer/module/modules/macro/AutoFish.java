package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.module.modules.render.Hud;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.ModeSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.util.player.PlayerUtil;
import dev.greencat.shimmer.util.player.rotation.Rotation;
import dev.greencat.shimmer.util.player.rotation.SmoothRotation;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AutoFish extends Module {
   private static boolean fishHooked;
   private static int tickAfterHook;
   private static long lastThrow;
   private static boolean lastRotate = false;
   private static boolean lastMove = false;
   private static long lastFishUP = 0L;
   private static float originYaw = 0.0F;
   private static float originPitch = 0.0F;
   private static boolean isThrowed = false;
   private static boolean prevThrowed = false;
   private static final Timer timer = new Timer();
   private static Random random = new Random();
   public final BooleanSetting enableMove = new BooleanSetting("Move", "Allow move when finish fishing", true);
   public final BooleanSetting enableSneak = new BooleanSetting("SneakMove", "Allow sneak when move", true);
   public final BooleanSetting alwaysSneak = new BooleanSetting("Always Sneak", "Allow sneak when fishing", true);
   public final BooleanSetting enableAutoRethrow = new BooleanSetting("AutoReset", "Auto reset status after 20s if no fish hooked", true);
   public final ModeSetting moveMethod = new ModeSetting("Move Method", "How to move when finish fishing", "WS", "WS", "AD");
   public final NumberSetting throwDelay = new NumberSetting("Throw Delay (tick)", "How many ticks will wait when finish fishing", 10.0, 1.0, 30.0, 1.0);
   public final BooleanSetting enableRotate = new BooleanSetting("Rotate", "Allow rotate when finish fishing", true);

   public AutoFish() {
      super("AutoFish", "Fishing Automatically", -1, Module.Category.MACRO);
      this.addSettings(
         new Setting[]{this.enableMove, this.enableSneak, this.moveMethod, this.throwDelay, this.enableRotate, this.enableAutoRethrow, this.alwaysSneak}
      );
      this.needDisable = true;
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null) {
         for (Entity entity : mc.world.getEntities()) {
            if (entity.hasCustomName() && entity.getCustomName().getString().contains("!!!") && System.currentTimeMillis() - lastFishUP >= 500L) {
               fishHooked = true;
               tickAfterHook = 0;
               lastFishUP = System.currentTimeMillis();
               break;
            }
         }
      }
   }

   @Override
   public void onEnable() {
      if (MinecraftClient.getInstance().player != null) {
         super.onEnable();
         fishHooked = false;
         random = new Random();
         originYaw = MinecraftClient.getInstance().player.getYaw();
         originPitch = MinecraftClient.getInstance().player.getPitch();
         if (this.alwaysSneak.isEnabled()) {
            Shimmer.getInstance().getModuleManager().getModule("Sneak").setEnabled(true);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.alwaysSneak.isEnabled()) {
         Shimmer.getInstance().getModuleManager().getModule("Sneak").setEnabled(false);
      }
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      float randomYaw = random.nextFloat() * 4.5F - 2.25F;
      float randomPitch = random.nextFloat() * 4.5F - 2.25F;
      if (MinecraftClient.getInstance().player != null) {
         isThrowed = false;

         for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
            if (entity instanceof FishingBobberEntity hook
               && hook.getPlayerOwner() != null
               && hook.getPlayerOwner().getUuidAsString().equals(MinecraftClient.getInstance().player.getUuidAsString())) {
               isThrowed = true;
               break;
            }
         }

         if (this.enableAutoRethrow.isEnabled()
            && System.currentTimeMillis() - lastThrow >= (long)(isThrowed ? 20000 : 3000)
            && !MinecraftClient.getInstance().player.getMainHandStack().isEmpty()
            && MinecraftClient.getInstance().player.getMainHandStack().getItem() == Items.FISHING_ROD) {
            SmoothRotation.smoothLook(new Rotation(originYaw, originPitch), 300, () -> {
            });
            PlayerUtil.useItem();
            lastThrow = System.currentTimeMillis();
            Hud.onMessage(
               Text.literal(Formatting.BOLD + "AutoFish"), Text.literal("After " + (isThrowed ? 20 : 3) + "s"), Text.literal("Auto reset triggered")
            );
            if (isThrowed) {
               timer.schedule(new TimerTask() {
                  public void run() {
                     PlayerUtil.useItem();
                     AutoFish.lastThrow = System.currentTimeMillis();
                  }
               }, 1000L);
            }
         }

         if (isThrowed && !prevThrowed) {
            lastThrow = System.currentTimeMillis();
            Hud.onMessage(Text.literal(Formatting.BOLD + "AutoFish"), Text.literal("Throw hook detected"), Text.literal("Already reset the hook timer"));
         }

         if (fishHooked) {
            if (tickAfterHook == 0) {
               PlayerUtil.useItem();
               lastThrow = System.currentTimeMillis();
               if (this.enableRotate.isEnabled()) {
                  SmoothRotation.smoothLook(
                     new Rotation(
                        MinecraftClient.getInstance().player.getYaw() + randomYaw, MinecraftClient.getInstance().player.getPitch() + randomPitch
                     ),
                     300,
                     () -> {
                     }
                  );
               }
            }

            if (tickAfterHook == 3 && this.enableRotate.isEnabled()) {
               SmoothRotation.smoothLook(
                  new Rotation(
                     MinecraftClient.getInstance().player.getYaw() - randomYaw, MinecraftClient.getInstance().player.getPitch() - randomPitch
                  ),
                  300,
                  () -> {
                  }
               );
            }

            if (tickAfterHook == (int)this.throwDelay.getValue()) {
               if (Math.abs(MinecraftClient.getInstance().player.getYaw() - originYaw) > 2.5F
                  || Math.abs(MinecraftClient.getInstance().player.getPitch() - originPitch) > 2.5F) {
                  SmoothRotation.smoothLook(new Rotation(originYaw, originPitch), 300, () -> {
                  });
               }

               PlayerUtil.useItem();
               if (this.enableRotate.isEnabled()) {
                  MinecraftClient.getInstance().player.setYaw(MinecraftClient.getInstance().player.getYaw() + (float)(lastRotate ? 3 : -3));
               }

               lastRotate = !lastRotate;
               lastThrow = System.currentTimeMillis();
            }

            if (tickAfterHook == 2 && this.enableSneak.isEnabled()) {
               MinecraftClient.getInstance().options.sneakKey.setPressed(true);
            }

            if (tickAfterHook == 3 && this.enableMove.isEnabled()) {
               if (this.moveMethod.getMode().equals("WS")) {
                  if (lastMove) {
                     MinecraftClient.getInstance().options.leftKey.setPressed(true);
                  } else {
                     MinecraftClient.getInstance().options.rightKey.setPressed(true);
                  }
               }

               if (this.moveMethod.getMode().equals("AD")) {
                  if (lastMove) {
                     MinecraftClient.getInstance().options.forwardKey.setPressed(true);
                  } else {
                     MinecraftClient.getInstance().options.backKey.setPressed(true);
                  }
               }
            }

            if (tickAfterHook == 4 && this.enableMove.isEnabled()) {
               if (this.moveMethod.getMode().equals("WS")) {
                  if (lastMove) {
                     MinecraftClient.getInstance().options.leftKey.setPressed(false);
                     MinecraftClient.getInstance().options.rightKey.setPressed(true);
                  } else {
                     MinecraftClient.getInstance().options.rightKey.setPressed(false);
                     MinecraftClient.getInstance().options.leftKey.setPressed(true);
                  }
               }

               if (this.moveMethod.getMode().equals("AD")) {
                  if (lastMove) {
                     MinecraftClient.getInstance().options.forwardKey.setPressed(false);
                     MinecraftClient.getInstance().options.backKey.setPressed(true);
                  } else {
                     MinecraftClient.getInstance().options.backKey.setPressed(false);
                     MinecraftClient.getInstance().options.forwardKey.setPressed(true);
                  }
               }
            }

            if (tickAfterHook == 4 && this.enableMove.isEnabled()) {
               if (this.moveMethod.getMode().equals("WS")) {
                  if (lastMove) {
                     MinecraftClient.getInstance().options.rightKey.setPressed(false);
                  } else {
                     MinecraftClient.getInstance().options.leftKey.setPressed(false);
                  }
               }

               if (this.moveMethod.getMode().equals("AD")) {
                  if (lastMove) {
                     MinecraftClient.getInstance().options.backKey.setPressed(false);
                  } else {
                     MinecraftClient.getInstance().options.forwardKey.setPressed(false);
                  }
               }

               lastMove = !lastMove;
            }

            if (tickAfterHook == 5 && this.enableSneak.isEnabled()) {
               MinecraftClient.getInstance().options.sneakKey.setPressed(false);
            }

            if (tickAfterHook > 31) {
               fishHooked = false;
            }

            tickAfterHook++;
         }

         if (isThrowed != prevThrowed) {
            prevThrowed = isThrowed;
         }
      }
   }
}
