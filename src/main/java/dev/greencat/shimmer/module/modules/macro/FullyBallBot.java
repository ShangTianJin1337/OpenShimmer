package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.ActionBarRenderEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.HaikuLogger;
import dev.greencat.shimmer.util.player.PlayerUtil;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class FullyBallBot extends Module {
   Timer timer = new Timer();
   public boolean onUsedBall = false;
   public long lastUsedBall = 0L;
   public long lastFinishBall = 0L;
   public BlockPos currentPlayerPos = null;

   public FullyBallBot() {
      super("FullyBallBot", "", -1, Module.Category.MACRO, true);
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEnable() {
      if (MinecraftClient.getInstance().player != null) {
         super.onEnable();
         this.currentPlayerPos = MinecraftClient.getInstance().player.getBlockPos();
         this.onUsedBall = false;
      }
   }

   @ShimmerSubscribe
   public void onMessage(ActionBarRenderEvent event) {
      if (event.text != null && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         StringBuilder finalText = new StringBuilder();
         boolean shouldSkipNext = false;

         for (int i = 0; i < event.text.getString().length(); i++) {
            if (event.text.getString().substring(i, i + 1).contains("§")) {
               shouldSkipNext = true;
            } else if (shouldSkipNext) {
               shouldSkipNext = false;
            } else {
               finalText.append(event.text.getString().substring(i, i + 1));
            }
         }

         int currentBallCount = 0;

         for (Entity it : MinecraftClient.getInstance().world.getEntities()) {
            if (it instanceof ArmorStandEntity) {
               ArmorStandEntity armorStand = (ArmorStandEntity)it;
               if (it.distanceTo(MinecraftClient.getInstance().player) <= 15.0F && !armorStand.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                  String origin = armorStand.getEquippedStack(EquipmentSlot.HEAD).getName().toString();
                  String id = origin.split("\\[")[1].split("]")[0];
                  if (id.length() == 16) {
                     currentBallCount++;
                  }
               }
            }
         }

         if (finalText.toString().contains("Bounces: 40")) {
            MinecraftClient.getInstance()
               .world
               .playSound(
                  MinecraftClient.getInstance().player,
                  MinecraftClient.getInstance().player.getBlockPos().up(),
                  SoundEvents.ENTITY_PLAYER_LEVELUP,
                  SoundCategory.PLAYERS,
                  1.0F,
                  1.0F
               );
            Shimmer.getInstance().getModuleManager().getModule("BallBot").setEnabled(false);
            this.resetAndGoPosition();
         } else if (currentBallCount == 0 && this.onUsedBall && System.currentTimeMillis() - this.lastUsedBall >= 10000L) {
            MinecraftClient.getInstance()
               .world
               .playSound(
                  MinecraftClient.getInstance().player,
                  MinecraftClient.getInstance().player.getBlockPos().up(),
                  SoundEvents.ENTITY_PLAYER_LEVELUP,
                  SoundCategory.PLAYERS,
                  1.0F,
                  1.0F
               );
            Shimmer.getInstance().getModuleManager().getModule("BallBot").setEnabled(false);
            this.resetAndGoPosition();
         }
      }
   }

   @ShimmerSubscribe
   public void onTick(TickEvent events) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (!Shimmer.getInstance().getModuleManager().getModule("BallBot").isEnabled()
            && !WalkerUtils.isActive()
            && System.currentTimeMillis() - this.lastFinishBall >= 3000L) {
            int currentBallCount = 0;

            for (Entity it : MinecraftClient.getInstance().world.getEntities()) {
               if (it instanceof ArmorStandEntity) {
                  ArmorStandEntity armorStand = (ArmorStandEntity)it;
                  if (MinecraftClient.getInstance().world.getBlockState(it.getBlockPos()).isAir()
                     && !armorStand.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                     String origin = armorStand.getEquippedStack(EquipmentSlot.HEAD).getName().toString();
                     String id = origin.split("\\[")[1].split("]")[0];
                     if (id.length() == 16) {
                        currentBallCount++;
                     }
                  }
               }
            }

            if (currentBallCount != 0) {
               HaikuLogger.info("[FullyBallBot] 此区域存在其他球,正在等待其他球消失...");
            } else if (MinecraftClient.getInstance().player.getMainHandStack() != null
               && MinecraftClient.getInstance().player.getMainHandStack().getItem() == Items.PLAYER_HEAD) {
               PlayerUtil.useItem();
               this.onUsedBall = true;
               this.lastUsedBall = System.currentTimeMillis();
               Shimmer.getInstance().getModuleManager().getModule("BallBot").setEnabled(true);
               ServerRotation.useServerRotation = true;
               HaikuLogger.info("[FullyBallBot] 正在拉起BallBot进程...");
            } else {
               HaikuLogger.info("[FullyBallBot] 当前没有手持球,请将球放在手上");
               this.setEnabled(false);
            }
         }
      }
   }

   public void resetAndGoPosition() {
      HaikuLogger.info("[FullyBallBot] BallBot进程已经退出,正在执行后续操作...");
      this.onUsedBall = false;
      this.lastFinishBall = System.currentTimeMillis();
      Shimmer.getInstance().getModuleManager().getModule("BallBot").setEnabled(false);
      ServerRotation.useServerRotation = false;
      MinecraftClient.getInstance().player.setPitch(0.1F);
      this.timer.schedule(new TimerTask() {
         public void run() {
            MinecraftClient.getInstance().player.setPitch(0.0F);
            WalkerUtils.walkTo(FullyBallBot.this.currentPlayerPos.down());
         }
      }, 1000L);
   }
}
