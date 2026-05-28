package dev.greencat.shimmer.module.modules.combat;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.KeyBindAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;

public class AutoClicker extends Module {
   NumberSetting minCPS = new NumberSetting("Minimum CPS", "The lowest CPS that module can go", 5.0, 1.0, 20.0, 0.1);
   NumberSetting maxCPS = new NumberSetting("Maximum CPS", "The highest CPS that module can go", 8.0, 1.0, 20.0, 0.1);
   BooleanSetting useRight = new BooleanSetting("Right Click", "Enable right click AutoClicker", true);
   BooleanSetting useLeft = new BooleanSetting("Left Click", "Enable left click AutoClicker", true);
   BooleanSetting jitter = new BooleanSetting("Jitter", "Enable Jitter", false);
   final Random random = new Random();
   private long rightDelay = AutoClicker.Timer.randomClickDelay((int)this.minCPS.value, (int)this.maxCPS.value);
   private long rightLastSwing = 0L;
   private long leftDelay = AutoClicker.Timer.randomClickDelay((int)this.minCPS.value, (int)this.maxCPS.value);
   private long leftLastSwing = 0L;
   private long blockBrokenDelay = 400L;
   private long blockLastBroken = 0L;
   private boolean isBreakingBlock = false;
   private boolean wasBreakingBlock = false;

   public AutoClicker() {
      super("AutoClicker", "Click mouse Automaticly", -1, Module.Category.COMBAT);
      this.addSettings(new Setting[]{this.minCPS, this.maxCPS, this.useLeft, this.useRight, this.jitter});
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (this.maxCPS.getValue() < this.minCPS.getValue()) {
         this.maxCPS.setValue(this.minCPS.getValue());
      }
   }

   private boolean leftCanAutoClick(long currentTime) {
      return !this.isBreakingBlock
         && (
            currentTime - this.blockLastBroken >= this.blockBrokenDelay
               || MinecraftClient.getInstance().crosshairTarget == null
               || MinecraftClient.getInstance().crosshairTarget.getType() != Type.BLOCK
               || ((BlockHitResult)MinecraftClient.getInstance().crosshairTarget).getBlockPos() == null
               || MinecraftClient.getInstance().world == null
               || MinecraftClient.getInstance().world.getBlockState(((BlockHitResult)MinecraftClient.getInstance().crosshairTarget).getBlockPos()).getBlock()
                  == Blocks.AIR
         );
   }

   private boolean rightCanAutoClick() {
      return !MinecraftClient.getInstance().player.isUsingItem();
   }

   private void leftClick(long currentTime) {
      if (this.useLeft.isEnabled() && MinecraftClient.getInstance().options.attackKey.isPressed()) {
         this.isBreakingBlock = MinecraftClient.getInstance().interactionManager.isBreakingBlock();
         if (!this.isBreakingBlock && this.wasBreakingBlock) {
            this.blockLastBroken = currentTime;
         }

         this.wasBreakingBlock = this.isBreakingBlock;
         if (currentTime - this.leftLastSwing < this.leftDelay || !this.leftCanAutoClick(currentTime)) {
            return;
         }

         KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.attackKey).getBoundKey());
         this.leftLastSwing = currentTime;
         this.blockLastBroken = 0L;
         this.leftDelay = AutoClicker.Timer.randomClickDelay((int)this.minCPS.value, (int)this.maxCPS.value);
         if (this.leftDelay != 0L) {
         }
      }
   }

   private void rightClick(long currentTime) {
      if (this.useRight.isEnabled()
         && MinecraftClient.getInstance().options.useKey.isPressed()
         && currentTime - this.rightLastSwing >= this.rightDelay
         && this.rightCanAutoClick()) {
         KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.useKey).getBoundKey());
         this.rightLastSwing = currentTime;
         this.rightDelay = AutoClicker.Timer.randomClickDelay((int)this.minCPS.value, (int)this.maxCPS.value);
         if (this.rightDelay != 0L) {
         }
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      long currentTime = System.currentTimeMillis();
      this.leftClick(currentTime);
      this.rightClick(currentTime);
   }

   @ShimmerSubscribe
   public void onTick2(TickEvent event) {
      if (this.jitter.isEnabled()
         && (
            this.useLeft.isEnabled() && MinecraftClient.getInstance().options.attackKey.isPressed() && this.leftCanAutoClick(System.currentTimeMillis())
               || this.useRight.isEnabled() && MinecraftClient.getInstance().options.useKey.isPressed() && this.rightCanAutoClick()
         )) {
         if (MinecraftClient.getInstance().player == null) {
            return;
         }

         ClientPlayerEntity thePlayer = MinecraftClient.getInstance().player;
         if (this.random.nextBoolean()) {
            thePlayer.setYaw(thePlayer.getYaw() + (this.random.nextBoolean() ? -this.nextFloat(0.0F, 1.0F) : this.nextFloat(0.0F, 1.0F)));
         }

         if (this.random.nextBoolean()) {
            thePlayer.setPitch(thePlayer.getPitch() + (this.random.nextBoolean() ? -this.nextFloat(0.0F, 1.0F) : this.nextFloat(0.0F, 1.0F)));
            if (thePlayer.getPitch() > 90.0F) {
               thePlayer.setPitch(90.0F);
            } else if (thePlayer.getPitch() < -90.0F) {
               thePlayer.setPitch(-90.0F);
            }
         }
      }
   }

   public float nextFloat(float startInclusive, float endInclusive) {
      return startInclusive == endInclusive ? startInclusive : startInclusive + (endInclusive - startInclusive) * this.random.nextFloat();
   }

   public static final class Timer {
      private static final Random random = new Random();

      public static long randomDelay(int minDelay, int maxDelay) {
         return (long)nextInt(minDelay, maxDelay);
      }

      public static int nextInt(int startInclusive, int endExclusive) {
         return startInclusive == endExclusive ? startInclusive : startInclusive + random.nextInt(endExclusive - startInclusive);
      }

      public static long randomClickDelay(int minCPS, int maxCPS) {
         return (long)(Math.random() * (double)(1000 / minCPS - 1000 / maxCPS + 1) + (double)(1000 / maxCPS));
      }
   }
}
