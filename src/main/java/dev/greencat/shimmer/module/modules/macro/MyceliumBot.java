package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.KeyBindAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MyceliumBot extends Module {
   private MyceliumBot.Type currentType = MyceliumBot.Type.MINE;
   private boolean lastPress = false;
   private boolean needPlace = false;
   private long lastAction = 0L;
   private Timer timer = new Timer();

   public MyceliumBot() {
      super("MyceliumBot", "Auto havest mycelium at X axis", -1, Module.Category.MACRO, true);
      this.needPlace = true;
   }

   @ShimmerSubscribe
   public void onTick(TickEvent events) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         if (this.currentType == MyceliumBot.Type.MINE && !WalkerUtils.isActive()) {
            boolean hasItem = false;
            int slotId = 0;

            for (int i = 0; i < 9; i++) {
               ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(i);
               if (!stack.isEmpty() && stack.getItem() == Items.GOLDEN_SHOVEL) {
                  slotId = i;
                  hasItem = true;
                  break;
               }
            }

            if (hasItem) {
               KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[slotId]).getBoundKey());
            }

            MinecraftClient.getInstance().player.setYaw(-90.0F);
            if (!dev.greencat.shimmer.util.world.MiningBot.isWorking()) {
               Nuker nuker = (Nuker)Shimmer.getInstance().getModuleManager().getModule("Nuker");
               nuker.mode.setMode("Mycelium");
               nuker.randomMoveEnable.setEnabled(false);
               nuker.upperEnable.setEnabled(false);
               nuker.sneakEnable.setEnabled(false);
               nuker.realNuker.setEnabled(true);
               nuker.serverRotation.setEnabled(true);
               nuker.oneTick.setEnabled(true);
               nuker.callback = posx -> posx.getY() == MinecraftClient.getInstance().player.getBlockY() + 1
                     && (double)posx.getX() > MinecraftClient.getInstance().player.getX();
               nuker.allowReset = false;
               if (!Shimmer.getInstance().getModuleManager().isModuleEnabled("Nuker")) {
                  nuker.toggle();
               }
            }

            if (this.lastPress) {
               MinecraftClient.getInstance().options.leftKey.setPressed(true);
            } else {
               MinecraftClient.getInstance().options.rightKey.setPressed(true);
            }

            if (MinecraftClient.getInstance().player.getMovement().distanceTo(new Vec3d(0.0, 0.0, 0.0)) <= 0.1
               && MinecraftClient.getInstance().world.getBlockState(MinecraftClient.getInstance().player.getBlockPos()).getBlock() != Blocks.END_PORTAL_FRAME
               && MinecraftClient.getInstance().world.getBlockState(MinecraftClient.getInstance().player.getBlockPos().down()).getBlock()
                  != Blocks.END_PORTAL_FRAME
               && System.currentTimeMillis() - this.lastAction >= 10000L) {
               this.currentType = MyceliumBot.Type.PLACE;
               this.lastAction = System.currentTimeMillis();
               MinecraftClient.getInstance().options.rightKey.setPressed(false);
               MinecraftClient.getInstance().options.leftKey.setPressed(false);
               MinecraftClient.getInstance().player.networkHandler.sendChatCommand("sethome");
               this.lastPress = !this.lastPress;
               WalkerUtils.walkTo(MinecraftClient.getInstance().player.getBlockPos().east(2).down());
               Nuker nuker = (Nuker)Shimmer.getInstance().getModuleManager().getModule("Nuker");
               nuker.callback = posx -> true;
               nuker.allowReset = true;
               if (Shimmer.getInstance().getModuleManager().isModuleEnabled("Nuker")) {
                  nuker.toggle();
               }
            }
         }

         if (this.currentType == MyceliumBot.Type.PLACE && !WalkerUtils.isActive() && System.currentTimeMillis() - this.lastAction >= 300L) {
            boolean hasItem = false;
            int slotId = 0;

            for (int ix = 0; ix < 9; ix++) {
               ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(ix);
               if (!stack.isEmpty() && stack.getItem() == Items.BLAZE_ROD) {
                  slotId = ix;
                  hasItem = true;
                  break;
               }
            }

            if (hasItem) {
               KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[slotId]).getBoundKey());
            }

            if (!this.needPlace) {
               BlockPos needPlacePos = null;

               for (BlockPos pos : BlockPos.iterate(
                  new BlockPos(
                     MinecraftClient.getInstance().player.getBlockX() + 3,
                     MinecraftClient.getInstance().player.getBlockY() + 2,
                     MinecraftClient.getInstance().player.getBlockZ() + 3
                  ),
                  new BlockPos(
                     MinecraftClient.getInstance().player.getBlockX() - 3,
                     MinecraftClient.getInstance().player.getBlockY(),
                     MinecraftClient.getInstance().player.getBlockZ() - 3
                  )
               )) {
                  if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() != Blocks.MYCELIUM
                     && MinecraftClient.getInstance().world.getBlockState(pos).getBlock() != Blocks.DIRT
                     && MinecraftClient.getInstance().world.getBlockState(pos).getBlock() != Blocks.END_PORTAL_FRAME
                     && MinecraftClient.getInstance().world.getBlockState(pos).getBlock() != Blocks.SEA_LANTERN
                     && MinecraftClient.getInstance().world.getBlockState(pos).getBlock() != Blocks.AIR
                     && MinecraftClient.getInstance().world.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
                     needPlacePos = pos;
                     break;
                  }
               }

               if (needPlacePos != null) {
                  this.needPlace = true;
                  this.lastAction = System.currentTimeMillis();
                  MinecraftClient.getInstance().player.setYaw((float)RotationUtil.getYaw(needPlacePos.toBottomCenterPos().add(0.0, 1.0, 0.0)));
                  MinecraftClient.getInstance().player.setPitch((float)RotationUtil.getPitch(needPlacePos.toBottomCenterPos().add(0.0, 1.0, 0.0)));
               } else {
                  WalkerUtils.walkTo(MinecraftClient.getInstance().player.getBlockPos().east(2).down());
                  this.currentType = MyceliumBot.Type.CACHE;
                  this.lastAction = System.currentTimeMillis();
                  this.timer.schedule(new TimerTask() {
                     public void run() {
                        boolean hasItem1 = false;
                        int slotId1 = 0;

                        for (int i = 0; i < 9; i++) {
                           ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(i);
                           if (!stack.isEmpty() && stack.getItem() == Items.GOLDEN_SHOVEL) {
                              slotId1 = i;
                              hasItem1 = true;
                              break;
                           }
                        }

                        if (hasItem1) {
                           KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.hotbarKeys[slotId1]).getBoundKey());
                        }
                     }
                  }, 1000L);
                  this.timer.schedule(new TimerTask() {
                     public void run() {
                        MyceliumBot.this.currentType = MyceliumBot.Type.MINE;
                     }
                  }, 2000L);
               }
            } else {
               this.needPlace = false;
               this.lastAction = System.currentTimeMillis();
               KeyBinding.onKeyPressed(((KeyBindAccessor)MinecraftClient.getInstance().options.useKey).getBoundKey());
            }
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.currentType = MyceliumBot.Type.MINE;
      this.lastAction = System.currentTimeMillis();
      this.needPlace = false;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
      MinecraftClient.getInstance().options.rightKey.setPressed(false);
      MinecraftClient.getInstance().options.leftKey.setPressed(false);
   }

   static enum Type {
      MINE,
      PLACE,
      CACHE;
   }
}
