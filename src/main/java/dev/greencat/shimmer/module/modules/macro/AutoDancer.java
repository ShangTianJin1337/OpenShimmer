package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.InGameHudAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.WalkerUtils;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.block.Blocks;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class AutoDancer extends Module {
   String title = "";
   boolean hasDone = false;
   boolean hasDone2 = false;
   Timer timer = new Timer();
   long lastPunch = 0L;

   public AutoDancer() {
      super("AutoDancer", "Auto do dance room in rift", -1, Module.Category.MACRO);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (!(
               MinecraftClient.getInstance().world.getBlockState(MinecraftClient.getInstance().player.getBlockPos().down()).getBlock() instanceof TransparentBlock
            )
            && !(
               MinecraftClient.getInstance().world.getBlockState(MinecraftClient.getInstance().player.getBlockPos().down(2)).getBlock() instanceof TransparentBlock
            )) {
            BlockPos glassPos = null;

            for (BlockPos pos : BlockPos.iterate(
               new BlockPos(
                  MinecraftClient.getInstance().player.getBlockX() + 2,
                  MinecraftClient.getInstance().player.getBlockY() - 1,
                  MinecraftClient.getInstance().player.getBlockZ() + 2
               ),
               new BlockPos(
                  MinecraftClient.getInstance().player.getBlockX() - 2,
                  MinecraftClient.getInstance().player.getBlockY() - 1,
                  MinecraftClient.getInstance().player.getBlockZ() - 2
               )
            )) {
               if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() instanceof TransparentBlock
                  && MinecraftClient.getInstance().world.getBlockState(pos).getBlock() != Blocks.GRAY_STAINED_GLASS) {
                  glassPos = pos;
                  break;
               }
            }

            if (glassPos != null && !WalkerUtils.isActive()) {
               WalkerUtils.walkToBaritoneRotation(glassPos);
               MinecraftClient.getInstance().options.attackKey.setPressed(false);
               MinecraftClient.getInstance().options.sneakKey.setPressed(false);
               MinecraftClient.getInstance().options.jumpKey.setPressed(false);
               this.hasDone = false;
               this.hasDone2 = false;
            }
         }

         Text titleText = ((InGameHudAccessor)MinecraftClient.getInstance().inGameHud).getTitle();
         if (titleText != null && !titleText.getString().isEmpty()) {
            this.title = titleText.getString();
         }

         if ((
               MinecraftClient.getInstance().world.getBlockState(MinecraftClient.getInstance().player.getBlockPos().down()).getBlock() instanceof TransparentBlock
                  || MinecraftClient.getInstance().world.getBlockState(MinecraftClient.getInstance().player.getBlockPos().down(2)).getBlock() instanceof TransparentBlock
            )
            && !this.hasDone
            && Math.abs(MinecraftClient.getInstance().player.getMovement().getX()) <= 0.1
            && Math.abs(MinecraftClient.getInstance().player.getMovement().getZ()) <= 0.1) {
            this.hasDone = true;
            WalkerUtils.cancel();
            this.timer.schedule(new TimerTask() {
               public void run() {
                  MinecraftClient.getInstance().options.sneakKey.setPressed(false);
                  MinecraftClient.getInstance().options.jumpKey.setPressed(false);
                  MinecraftClient.getInstance().options.attackKey.setPressed(false);
               }
            }, 700L);
            this.timer.schedule(new TimerTask() {
               public void run() {
                  MinecraftClient.getInstance().options.jumpKey.setPressed(false);
               }
            }, 550L);
            if (this.title.contains("Sneak")) {
               MinecraftClient.getInstance().options.sneakKey.setPressed(true);
            }

            if (this.title.contains("Jump") && !this.title.contains("Don")) {
               MinecraftClient.getInstance().options.jumpKey.setPressed(true);
            }
         }

         if ((
               MinecraftClient.getInstance().world.getBlockState(MinecraftClient.getInstance().player.getBlockPos().down()).getBlock() instanceof TransparentBlock
                  || MinecraftClient.getInstance().world.getBlockState(MinecraftClient.getInstance().player.getBlockPos().down(2)).getBlock() instanceof TransparentBlock
            )
            && !this.hasDone2
            && Math.abs(MinecraftClient.getInstance().player.getMovement().getX()) <= 0.1
            && Math.abs(MinecraftClient.getInstance().player.getMovement().getZ()) <= 0.1) {
            this.hasDone2 = true;
            if (this.title.contains("Punch!") && this.title.length() > 12 && System.currentTimeMillis() - this.lastPunch >= 900L) {
               this.timer.schedule(new TimerTask() {
                  public void run() {
                     KeyBinding.onKeyPressed(Type.MOUSE.createFromCode(0));
                  }
               }, 500L);
               this.lastPunch = System.currentTimeMillis();
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.title = "";
      this.hasDone = false;
      this.hasDone2 = false;
   }
}
