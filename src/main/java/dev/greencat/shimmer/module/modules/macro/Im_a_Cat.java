package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.PlayerUtil;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.player.rotation.Rotation;
import dev.greencat.shimmer.util.player.rotation.SmoothRotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Im_a_Cat extends Module {
   private final Random random = new Random();
   private final Timer timer = new Timer();
   private Im_a_Cat.State currentState = Im_a_Cat.State.SEARCHING_RAT;
   private ClientWorld lastWorld = null;
   private Entity currentTargetRat = null;
   private long lastActionTime = 0L;
   private long worldLoadTime = 0L;
   private long lastCompassClickTime = 0L;
   private long lastGlobalItemUseTime = 0L;
   private final List<Integer> timeoutRatIds = new ArrayList();
   private Entity currentInteractRat = null;
   private long interactStartTime = 0L;
   private Vec3d lastStuckPos = Vec3d.ZERO;
   private long lastStuckTime = 0L;
   private Rotation targetRotation = null;
   private final BlockPos CHEST_WALK_POS = new BlockPos(-10, 69, -68);
   private final float CHEST_YAW = 49.0F;
   private final float CHEST_PITCH = 61.0F;
   private final double BLACKLIST_RADIUS_SQ = 1225.0;
   private final BlockPos BLACKLIST_POS_1 = new BlockPos(43, 72, -161);
   private final BlockPos BLACKLIST_POS_2 = new BlockPos(-34, 57, -117);

   public Im_a_Cat() {
      super("Im_a_Cat", "Meow", -1, Module.Category.MACRO);
      this.needDisable = true;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      WalkerUtils.cancel();
      this.targetRotation = null;
   }

   private void resetState() {
      this.currentState = Im_a_Cat.State.SEARCHING_RAT;
      this.currentTargetRat = null;
      this.lastWorld = mc.world;
      this.worldLoadTime = System.currentTimeMillis();
      this.targetRotation = null;
      this.timeoutRatIds.clear();
      this.currentInteractRat = null;
      this.interactStartTime = 0L;
      this.lastGlobalItemUseTime = 0L;
      this.lastStuckPos = Vec3d.ZERO;
      this.lastStuckTime = System.currentTimeMillis();
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (mc.player != null && mc.world != null) {
         if (System.currentTimeMillis() - this.lastGlobalItemUseTime >= 5000L) {
            PlayerUtil.useItem();
            this.lastGlobalItemUseTime = System.currentTimeMillis();
         }

         this.handleGlobalCompassClick();

         for (BlockPos pos : BlockPos.iterate(
            new BlockPos(
               MinecraftClient.getInstance().player.getBlockX() + 10,
               MinecraftClient.getInstance().player.getBlockY() + 10,
               MinecraftClient.getInstance().player.getBlockZ() + 10
            ),
            new BlockPos(
               MinecraftClient.getInstance().player.getBlockX() - 10,
               MinecraftClient.getInstance().player.getBlockY() - 10,
               MinecraftClient.getInstance().player.getBlockZ() - 10
            )
         )) {
            Block block = MinecraftClient.getInstance().world.getBlockState(pos).getBlock();
            if (block == Blocks.SLIME_BLOCK
               || block instanceof FenceGateBlock
               || block instanceof FenceBlock
               || block instanceof LadderBlock
               || block == Blocks.SNOW) {
               MinecraftClient.getInstance().world.setBlockState(pos, Blocks.STONE.getDefaultState(), 18);
               MinecraftClient.getInstance().world.setBlockState(pos.up(), Blocks.LAVA.getDefaultState(), 18);
               MinecraftClient.getInstance().world.setBlockState(pos.up(2), Blocks.STONE.getDefaultState(), 18);
            }
         }

         if (mc.world != this.lastWorld) {
            this.lastWorld = mc.world;
            this.currentState = Im_a_Cat.State.SEARCHING_RAT;
            this.worldLoadTime = System.currentTimeMillis();
            this.timeoutRatIds.clear();
            this.currentInteractRat = null;
            this.lastStuckPos = Vec3d.ZERO;
            this.lastStuckTime = System.currentTimeMillis();
         } else if (this.currentState != Im_a_Cat.State.SEARCHING_RAT) {
            switch (this.currentState) {
               case SENDING_HUB:
                  if (!WalkerUtils.isActive()) {
                     mc.player.networkHandler.sendChatCommand("hub");
                     this.lastActionTime = System.currentTimeMillis();
                     this.currentState = Im_a_Cat.State.WAITING_HUB_LOAD;
                  }
                  break;
               case WAITING_HUB_LOAD:
                  if (System.currentTimeMillis() - this.lastActionTime > 3000L) {
                     this.currentState = Im_a_Cat.State.WALKING_TO_CHEST;
                  }
                  break;
               case WALKING_TO_CHEST:
                  this.handleWalkingToChest();
                  break;
               case ALIGNING_CHEST:
                  this.handleAligningChestManual();
                  break;
               case OPENING_CHEST:
                  MinecraftClient.getInstance().options.useKey.setPressed(true);
                  this.timer.schedule(new TimerTask() {
                     public void run() {
                        MinecraftClient.getInstance().options.useKey.setPressed(false);
                     }
                  }, 300L);
                  this.lastActionTime = System.currentTimeMillis();
                  this.currentState = Im_a_Cat.State.GRABBING_COMPASS;
                  break;
               case GRABBING_COMPASS:
                  if (System.currentTimeMillis() - this.lastActionTime > 5000L) {
                     this.currentState = Im_a_Cat.State.OPENING_CHEST;
                  }
               case FINISHED:
            }
         } else if (System.currentTimeMillis() - this.worldLoadTime >= 2000L) {
            if (!this.handleNearbyRats()) {
               this.findAndWalkToNextRat();
            }
         }
      }
   }

   private boolean handleNearbyRats() {
      if (this.currentInteractRat != null && !this.currentInteractRat.isAlive()) {
         this.currentInteractRat = null;
      }

      for (Entity entity : mc.world.getEntities()) {
         if (this.isValidRat(entity)
            && !this.isInBlacklistedZone(entity)
            && !this.timeoutRatIds.contains(entity.getId())
            && (double)mc.player.distanceTo(entity) <= 3.0) {
            if (this.currentInteractRat == null || this.currentInteractRat.getId() != entity.getId()) {
               this.currentInteractRat = entity;
               this.interactStartTime = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - this.interactStartTime <= 8000L) {
               this.lastStuckTime = System.currentTimeMillis();
               return true;
            }

            this.timeoutRatIds.add(entity.getId());
            this.currentInteractRat = null;
         }
      }

      if (this.currentInteractRat != null && (double)mc.player.distanceTo(this.currentInteractRat) > 3.0) {
         this.currentInteractRat = null;
      }

      return false;
   }

   private void findAndWalkToNextRat() {
      Entity closestRat = null;
      double minDistance = Double.MAX_VALUE;

      for (Entity entity : mc.world.getEntities()) {
         if (this.isValidRat(entity) && !this.isInBlacklistedZone(entity) && !this.timeoutRatIds.contains(entity.getId())) {
            double dist = (double)mc.player.distanceTo(entity);
            if (dist < minDistance) {
               minDistance = dist;
               closestRat = entity;
            }
         }
      }

      if (closestRat != null) {
         if (WalkerUtils.isActive()) {
            if (mc.player.getEntityPos().distanceTo(this.lastStuckPos) < 1.0) {
               if (System.currentTimeMillis() - this.lastStuckTime > 3000L) {
                  WalkerUtils.cancel();
                  this.lastStuckTime = System.currentTimeMillis();
                  return;
               }
            } else {
               this.lastStuckPos = mc.player.getEntityPos();
               this.lastStuckTime = System.currentTimeMillis();
            }
         } else {
            this.lastStuckPos = mc.player.getEntityPos();
            this.lastStuckTime = System.currentTimeMillis();
         }

         BlockPos ratPos = closestRat.getBlockPos();
         BlockPos targetPos = this.findValidStandPos(ratPos);
         BlockPos finalTarget = targetPos != null ? targetPos : ratPos;
         if (!WalkerUtils.isActive()) {
            mc.player.setPitch(0.0F);
            WalkerUtils.walkTo(finalTarget);
         }
      } else {
         this.currentState = Im_a_Cat.State.SENDING_HUB;
      }
   }

   private BlockPos findValidStandPos(BlockPos ratPos) {
      List<BlockPos> candidates = new ArrayList();

      for (int x = -2; x <= 2; x++) {
         for (int z = -2; z <= 2; z++) {
            for (int y = 0; y >= -3; y--) {
               BlockPos candidate = ratPos.add(x, y, z);
               if (!(candidate.getSquaredDistance(ratPos) > 2.25)
                  && !mc.world.getBlockState(candidate).isAir()
                  && mc.world.getBlockState(candidate.up()).isAir()
                  && mc.world.getBlockState(candidate.up(2)).isAir()) {
                  candidates.add(candidate);
               }
            }
         }
      }

      return candidates.isEmpty() ? null : ((BlockPos)candidates.get(this.random.nextInt(candidates.size()))).up();
   }

   private void handleGlobalCompassClick() {
      if (mc.currentScreen instanceof GenericContainerScreen containerScreen && System.currentTimeMillis() - this.lastCompassClickTime >= 1000L) {
         for (Slot slot : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
            if (slot.inventory != mc.player.getInventory() && slot.hasStack() && slot.getStack().getItem() == Items.COMPASS) {
               mc.interactionManager
                  .clickSlot(((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slot.id, 0, SlotActionType.CLONE, mc.player);
               this.lastCompassClickTime = System.currentTimeMillis();
               break;
            }
         }
      }
   }

   private boolean isInBlacklistedZone(Entity entity) {
      double distSq1 = entity.squaredDistanceTo(
         (double)this.BLACKLIST_POS_1.getX(), (double)this.BLACKLIST_POS_1.getY(), (double)this.BLACKLIST_POS_1.getZ()
      );
      double distSq2 = entity.squaredDistanceTo(
         (double)this.BLACKLIST_POS_2.getX(), (double)this.BLACKLIST_POS_2.getY(), (double)this.BLACKLIST_POS_2.getZ()
      );
      return distSq1 <= 1225.0 || distSq2 <= 1225.0;
   }

   private boolean isValidRat(Entity entity) {
      if (entity instanceof ArmorStandEntity armorStand) {
         if (armorStand.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.PLAYER_HEAD) {
            return false;
         } else {
            for (Entity nearby : mc.world.getEntities()) {
               if (nearby instanceof ZombieEntity && nearby.distanceTo(armorStand) <= 3.0F) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private void handleWalkingToChest() {
      double dist = mc.player
         .squaredDistanceTo((double)this.CHEST_WALK_POS.getX(), (double)this.CHEST_WALK_POS.getY(), (double)this.CHEST_WALK_POS.getZ());
      if (dist > 2.0) {
         if (!WalkerUtils.isActive() && !mc.world.getBlockState(this.CHEST_WALK_POS).isAir()) {
            mc.player.setPitch(0.0F);
            WalkerUtils.walkTo(this.CHEST_WALK_POS);
         }
      } else {
         WalkerUtils.cancel();
         this.targetRotation = new Rotation(49.0F, 61.0F);
         SmoothRotation.smoothLook(this.targetRotation, 300, () -> {
         });
         this.currentState = Im_a_Cat.State.ALIGNING_CHEST;
      }
   }

   private void handleAligningChestManual() {
      if (this.targetRotation == null) {
         this.targetRotation = new Rotation(49.0F, 61.0F);
      }

      if (this.isRotatedTo(this.targetRotation, 10.0F)) {
         this.currentState = Im_a_Cat.State.OPENING_CHEST;
      }
   }

   private boolean isRotatedTo(Rotation target, float tolerance) {
      if (target == null) {
         return false;
      } else {
         float yawDiff = Math.abs(MathHelper.wrapDegrees(mc.player.getYaw() - target.getYaw()));
         float pitchDiff = Math.abs(mc.player.getPitch() - target.getPitch());
         return yawDiff < tolerance && pitchDiff < tolerance;
      }
   }

   private static enum State {
      SEARCHING_RAT,
      SENDING_HUB,
      WAITING_HUB_LOAD,
      WALKING_TO_CHEST,
      ALIGNING_CHEST,
      OPENING_CHEST,
      GRABBING_COMPASS,
      FINISHED;
   }
}
