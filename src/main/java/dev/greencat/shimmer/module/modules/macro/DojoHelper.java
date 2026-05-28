package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.render.RenderUtil;
import dev.greencat.shimmer.util.world.LocationUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class DojoHelper extends Module {
   private static final NumberSetting movePredict = new NumberSetting("Move Predict factor", "Change the factor of move predict", 4.75, 0.0, 10.0, 0.01);
   private static final HashSet<BlockPos> lastLimePos = new HashSet();
   private static final HashSet<BlockPos> limePos = new HashSet();
   private static final List<Entity> blacklist = new ArrayList();
   private static BlockPos lastWalkPos = null;
   private static Vec3d lastVec = null;
   private static long lastShoot = 0L;
   private static Vec3d lastNeedFix = new Vec3d(0.0, 0.0, 0.0);
   private static BlockPos furtherTarget = null;
   private static List<DojoHelper.YellowBlock> yellowPos = new ArrayList();
   private static long lastRecord = 0L;

   public DojoHelper() {
      super("DojoHelper", "Just a dojo helper", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{movePredict});
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (isInSwiftness()) {
            limePos.clear();
            if (MinecraftClient.getInstance().player.getBlockPos().down().equals(lastWalkPos)) {
               WalkerUtils.cancel();
            }

            BlockPos.iterate(
                  new BlockPos(
                     MinecraftClient.getInstance().player.getBlockX() + 5,
                     MinecraftClient.getInstance().player.getBlockY() - 1,
                     MinecraftClient.getInstance().player.getBlockZ() + 5
                  ),
                  new BlockPos(
                     MinecraftClient.getInstance().player.getBlockX() - 5,
                     MinecraftClient.getInstance().player.getBlockY() - 1,
                     MinecraftClient.getInstance().player.getBlockZ() - 5
                  )
               )
               .forEach(posx -> {
                  if (MinecraftClient.getInstance().world.getBlockState(posx).getBlock() == Blocks.LIME_WOOL) {
                     limePos.add(posx.toImmutable());
                  }
               });
            List<BlockPos> newPos = new ArrayList();

            for (BlockPos pos : limePos) {
               if (!lastLimePos.contains(pos)) {
                  newPos.add(pos);
               }
            }

            newPos.remove(MinecraftClient.getInstance().player.getBlockPos().down());
            if (!newPos.isEmpty()) {
               BlockPos furtherTarget = null;
               double distance = -1.0;

               for (BlockPos posx : newPos) {
                  if (posx.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos()) > distance) {
                     distance = posx.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos());
                     furtherTarget = posx;
                  }
               }

               newPos.remove(furtherTarget);
               DojoHelper.furtherTarget = furtherTarget;
               if (!WalkerUtils.isActive() || furtherTarget != lastWalkPos) {
                  WalkerUtils.walkToBaritoneRotation(furtherTarget);
                  lastWalkPos = furtherTarget;
               }
            }

            lastLimePos.clear();
            lastLimePos.addAll(limePos);
         } else if (!lastLimePos.isEmpty()) {
            lastLimePos.clear();
         }

         if (isInControl()) {
            WitherSkeletonEntity target = null;
            float distance = 9999.0F;

            for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
               if (entity instanceof WitherSkeletonEntity
                  && entity.distanceTo(MinecraftClient.getInstance().player) < distance
                  && !entity.isInvisible()
                  && !blacklist.contains(entity)) {
                  distance = entity.distanceTo(MinecraftClient.getInstance().player);
                  target = (WitherSkeletonEntity)entity;
               }
            }

            if (target != null) {
               if (target.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.REDSTONE_BLOCK) {
                  blacklist.add(target);
               }

               Vec3d needFix = new Vec3d(0.0, 0.0, 0.0);
               if (lastVec != null) {
                  needFix = new Vec3d(
                     movePredict.getValue() * (target.getX() - lastVec.getX()),
                     0.5 * (target.getY() - lastVec.getY()),
                     movePredict.getValue() * (target.getZ() - lastVec.getZ())
                  );
               }

               if (Math.abs(needFix.getX()) <= 0.1 && Math.abs(needFix.getY()) <= 0.03 && Math.abs(needFix.getZ()) <= 0.1) {
                  needFix = lastNeedFix;
               } else {
                  lastNeedFix = needFix;
               }

               MinecraftClient.getInstance().player.setYaw((float)RotationUtil.getYaw(target.getEyePos().add(needFix)));
               MinecraftClient.getInstance().player.setPitch((float)RotationUtil.getPitch(target.getEyePos().add(needFix)));
               if (System.currentTimeMillis() - lastRecord >= 50L) {
                  lastVec = target.getEntityPos();
                  lastRecord = System.currentTimeMillis();
               }
            }
         } else if (!blacklist.isEmpty()) {
            blacklist.clear();
         }

         if (isInMastery()) {
            List<BlockPos> currentYellowPos = new ArrayList();

            for (BlockPos posxx : BlockPos.iterate(
               new BlockPos(
                  MinecraftClient.getInstance().player.getBlockX() + 20,
                  MinecraftClient.getInstance().player.getBlockY() + 20,
                  MinecraftClient.getInstance().player.getBlockZ() + 20
               ),
               new BlockPos(
                  MinecraftClient.getInstance().player.getBlockX() - 20,
                  MinecraftClient.getInstance().player.getBlockY() - 20,
                  MinecraftClient.getInstance().player.getBlockZ() - 20
               )
            )) {
               if (MinecraftClient.getInstance().world.getBlockState(posxx).getBlock() == Blocks.YELLOW_WOOL) {
                  currentYellowPos.add(posxx.toImmutable());
               }
            }

            for (DojoHelper.YellowBlock yellowBlock : yellowPos) {
               if (currentYellowPos.contains(yellowBlock.pos())) {
                  currentYellowPos.remove(yellowBlock.pos);
               }
            }

            for (BlockPos posxxx : currentYellowPos) {
               yellowPos.add(new DojoHelper.YellowBlock(System.currentTimeMillis(), posxxx));
            }

            List<DojoHelper.YellowBlock> needRemove = new ArrayList();
            BlockPos needShot = null;

            for (DojoHelper.YellowBlock yellowBlockx : yellowPos) {
               if (System.currentTimeMillis() - yellowBlockx.time > 3000L) {
                  needRemove.add(yellowBlockx);
               }

               if (System.currentTimeMillis() - yellowBlockx.time < 3000L && System.currentTimeMillis() - yellowBlockx.time > 2650L) {
                  needShot = yellowBlockx.pos;
               }
            }

            yellowPos.removeAll(needRemove);
            if (needShot != null) {
               MinecraftClient.getInstance()
                  .player
                  .setYaw(
                     (float)RotationUtil.getYaw(
                        needShot.toCenterPos().add(0.0, Math.sqrt(MinecraftClient.getInstance().player.squaredDistanceTo(Vec3d.of(needShot))) * 0.05, 0.0)
                     )
                  );
               MinecraftClient.getInstance()
                  .player
                  .setPitch(
                     (float)RotationUtil.getPitch(
                        needShot.toCenterPos().add(0.0, Math.sqrt(MinecraftClient.getInstance().player.squaredDistanceTo(Vec3d.of(needShot))) * 0.05, 0.0)
                     )
                  );
               if (MinecraftClient.getInstance().player.getMainHandStack().getItem() != Items.BOW || System.currentTimeMillis() - lastShoot < 1250L) {
                  MinecraftClient.getInstance().options.useKey.setPressed(true);
               } else if (MinecraftClient.getInstance().world.getBlockState(needShot).getBlock() != Blocks.AIR) {
                  MinecraftClient.getInstance().options.useKey.setPressed(false);
                  lastShoot = System.currentTimeMillis();
               }
            }
         }
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null && event.wrc != null && Shimmer.matrixStack != null) {
         if (isInSwiftness() && furtherTarget != null) {
            RenderUtil.draw3DBox(new Box(furtherTarget), Color.CYAN, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
         }
      }
   }

   public static boolean isInForce() {
      return LocationUtils.sideBarString.toLowerCase().contains("challenge: force");
   }

   public static boolean isInStamina() {
      return LocationUtils.sideBarString.toLowerCase().contains("challenge: stamina");
   }

   public static boolean isInMastery() {
      return LocationUtils.sideBarString.toLowerCase().contains("challenge: mastery");
   }

   public static boolean isInDiscipline() {
      return LocationUtils.sideBarString.toLowerCase().contains("challenge: discipline");
   }

   public static boolean isInSwiftness() {
      return LocationUtils.sideBarString.toLowerCase().contains("challenge: swiftness");
   }

   public static boolean isInControl() {
      return LocationUtils.sideBarString.toLowerCase().contains("challenge: control");
   }

   public static boolean isInTenacity() {
      return LocationUtils.sideBarString.toLowerCase().contains("challenge: tenacity");
   }

   private static record YellowBlock(long time, BlockPos pos) {
   }
}
