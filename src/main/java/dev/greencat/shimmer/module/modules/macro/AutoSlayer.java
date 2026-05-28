package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.MinecraftAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.module.modules.player.NickHider;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.ModeSetting;
import dev.greencat.shimmer.util.HaikuLogger;
import dev.greencat.shimmer.util.player.PlayerUtil;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AutoSlayer extends Module {
   public final ModeSetting mode = new ModeSetting("Mob Type", "Choose the mob type you want to kill", "Zombie", "Zombie", "Wolf", "Spider");
   private final List<LivingEntity> blacklist = new ArrayList();
   private LivingEntity prevTarget = null;
   private long lastSearch = 0L;
   private int count = 0;

   public AutoSlayer() {
      super("AutoSlayer", "Do slayer automatically", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{this.mode});
      this.needDisable = true;
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (mc.player != null && mc.world != null) {
         if (this.count > 3) {
            this.blacklist.add(this.prevTarget);
         }

         List<LivingEntity> withInList = new ArrayList();

         for (Entity entity : mc.world.getEntities()) {
            Box hitbox = entity.getBoundingBox();
            if (entity instanceof LivingEntity
               && PlayerUtil.isWithin(
                  MathHelper.clamp(mc.player.getX(), hitbox.minX, hitbox.maxX),
                  MathHelper.clamp(mc.player.getY(), hitbox.minY, hitbox.maxY),
                  MathHelper.clamp(mc.player.getZ(), hitbox.minZ, hitbox.maxZ),
                  2.5
               )
               && PlayerUtil.canSeeEntity(entity)
               && this.checkMob((LivingEntity)entity)) {
               withInList.add((LivingEntity)entity);
            }
         }

         if (withInList.isEmpty() && !WalkerUtils.isActive()) {
            LivingEntity target = this.getNearestMob();
            if (target == null) {
               return;
            }

            if (this.prevTarget == null || !this.prevTarget.equals(target)) {
               this.prevTarget = target;
               this.count = 0;
            }

            if (System.currentTimeMillis() - this.lastSearch >= 2000L) {
               this.lastSearch = System.currentTimeMillis();
               HaikuLogger.info(target.toString());
               if (mc.world.getBlockState(target.getBlockPos()).getBlock() instanceof SlabBlock
                  && mc.world.getBlockState(target.getBlockPos()).get(SlabBlock.TYPE) == SlabType.BOTTOM) {
                  WalkerUtils.walkTo(target.getBlockPos());
               } else {
                  WalkerUtils.walkTo(target.getBlockPos().down());
               }

               this.count++;
            }
         }

         if (!withInList.isEmpty() && WalkerUtils.isActive()) {
            WalkerUtils.cancel();
         }

         if (!withInList.isEmpty()) {
            this.lastSearch = System.currentTimeMillis() - 3000L;
         }

         Shimmer.getInstance().getModuleManager().getModule("Killaura").setEnabled(!withInList.isEmpty());
      }
   }

   public boolean checkMob(LivingEntity entity) {
      if (!entity.equals(mc.player) && !entity.equals(((MinecraftAccessor)mc).camera())) {
         if ((!(entity instanceof LivingEntity) || !entity.isDead()) && entity.isAlive()) {
            if (entity.equals(mc.player) || entity.equals(((MinecraftAccessor)mc).camera())) {
               return false;
            } else if ((!(entity instanceof LivingEntity) || !entity.isDead()) && entity.isAlive()) {
               if (entity instanceof LivingEntity && entity.isInvisible() && !this.mode.equals("Ghost")) {
                  return false;
               } else if (entity instanceof LivingEntity && !entity.isAlive()) {
                  return false;
               } else if (entity instanceof LivingEntity && !entity.isAttackable()) {
                  return false;
               } else if (entity instanceof PlayerEntity) {
                  return false;
               } else if (this.blacklist.contains(entity)) {
                  return false;
               } else if (this.mode.equals("Zombie")) {
                  return entity instanceof ZombieEntity && entity.getY() <= 70.0;
               } else if (this.mode.equals("Wolf")) {
                  return entity instanceof WolfEntity;
               } else {
                  return this.mode.equals("Spider") ? entity instanceof SpiderEntity : false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public LivingEntity getNearestMob() {
      List<LivingEntity> targetList = new ArrayList();

      for (Entity entity : mc.world.getEntities()) {
         if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            if (this.checkMob(livingEntity)) {
               targetList.add((LivingEntity)entity);
            }
         }
      }

      List<ArmorStandEntity> signEntity = new ArrayList();

      for (Entity entityx : mc.world.getEntities()) {
         if (entityx instanceof ArmorStandEntity
            && entityx.hasCustomName()
            && entityx.getCustomName().getString().contains("Spawned by")
            && (
               entityx.getCustomName().getString().contains(MinecraftClient.getInstance().getSession().getUsername())
                  || entityx.getCustomName().getString().contains(NickHider.nickname.getString())
            )) {
            signEntity.add((ArmorStandEntity)entityx);
         }
      }

      List<LivingEntity> finalEntity = new ArrayList();
      if (!signEntity.isEmpty()) {
         for (Entity entityxx : mc.world.getEntities()) {
            for (ArmorStandEntity sign : signEntity) {
               if ((double)entityxx.distanceTo(sign) < 2.5
                  && !(entityxx instanceof PlayerEntity)
                  && !(entityxx instanceof ArmorStandEntity)
                  && entityxx instanceof LivingEntity) {
                  finalEntity.add((LivingEntity)entityxx);
               }
            }
         }
      } else {
         finalEntity = targetList;
      }

      if (!signEntity.isEmpty() && finalEntity.isEmpty()) {
         finalEntity = targetList;
      }

      finalEntity.sort(Comparator.comparingDouble(PlayerUtil::squaredDistanceTo));
      return finalEntity.isEmpty() ? null : (LivingEntity)finalEntity.getFirst();
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null) {
         for (Entity entity : mc.world.getEntities()) {
            if (!entity.getUuidAsString().equals(mc.player.getUuidAsString())
               && entity instanceof ArmorStandEntity
               && entity.hasCustomName()
               && entity.getCustomName().getString().contains("Spawned by")
               && (
                  entity.getCustomName().getString().contains(MinecraftClient.getInstance().getSession().getUsername())
                     || entity.getCustomName().getString().contains(NickHider.nickname.getString())
               )) {
               RenderUtil.draw3DOutline(
                  RenderUtil.smoothen(entity, entity.getBoundingBox(), 0.0F), Color.GREEN, Shimmer.matrixStack, event.storage.getEntityVertexConsumers()
               );
               Camera camera = mc.gameRenderer.getCamera();
               Vec3d start = new Vec3d(0.0, 0.0, 1.0)
                  .rotateX(-((float)Math.toRadians((double)camera.getPitch())))
                  .rotateY(-((float)Math.toRadians((double)camera.getYaw())));
               Vec3d var6 = RenderUtil.smoothen(entity, 0.0F).add(0.0, (double)entity.getStandingEyeHeight(), 0.0);
            }
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      WalkerUtils.cancel();
   }
}
