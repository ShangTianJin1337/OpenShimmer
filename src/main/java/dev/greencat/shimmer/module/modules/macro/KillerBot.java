package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.MinecraftAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.module.modules.combat.Killaura;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.ModeSetting;
import dev.greencat.shimmer.util.player.PlayerUtil;
import dev.greencat.shimmer.util.player.WalkerUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class KillerBot extends Module {
   public final ModeSetting mode = new ModeSetting(
      "Mob Type", "Choose the mob type you want to kill", "Crypt", "Crypt", "Zombie", "Wolf", "Spider", "Enderman", "MagmaCube", "Glacite Walker", "Goblin"
   );
   private final List<LivingEntity> blacklist = new ArrayList();
   private LivingEntity prevTarget = null;
   private long lastSearch = 0L;
   private int count = 0;

   public KillerBot() {
      super("KillerBot", "Kill mob automatically", -1, Module.Category.MACRO);
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
               if (mc.world.getBlockState(target.getBlockPos()).getBlock() instanceof SlabBlock
                  && mc.world.getBlockState(target.getBlockPos()).get(SlabBlock.TYPE) == SlabType.BOTTOM) {
                  WalkerUtils.walkTo(target.getBlockPos());
               } else if (!this.mode.equals("Goblin") && !this.mode.equals("Glacite Walker")) {
                  WalkerUtils.walkTo(target.getBlockPos().down());
               } else {
                  WalkerUtils.walkTo(target.getBlockPos().down(2));
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
               if (entity instanceof LivingEntity && entity.isInvisible() && !(entity instanceof ArmorStandEntity) && !this.mode.equals("Ghost")) {
                  return false;
               } else if (entity instanceof LivingEntity && !entity.isAlive()) {
                  return false;
               } else if (entity instanceof LivingEntity && !entity.isAttackable()) {
                  return false;
               } else if (this.blacklist.contains(entity)) {
                  return false;
               } else if (this.mode.equals("Crypt")) {
                  return entity instanceof ZombieEntity && entity.getY() <= 70.0;
               } else if (this.mode.equals("Zombie")) {
                  return entity instanceof ZombieEntity && entity.getY() > 70.0;
               } else if (this.mode.equals("Wolf")) {
                  return entity instanceof WolfEntity;
               } else if (this.mode.equals("Enderman")) {
                  return entity instanceof EndermanEntity;
               } else if (this.mode.equals("Spider")) {
                  return entity instanceof SpiderEntity;
               } else if (this.mode.equals("MagmaCube")) {
                  if (entity instanceof MagmaCubeEntity magmaCube && !magmaCube.hasPassengers()) {
                     return true;
                  }

                  return false;
               } else if (this.mode.equals("Glacite Walker")) {
                  return entity.hasCustomName()
                     && entity instanceof ArmorStandEntity
                     && entity.getCustomName().getString().contains("Glacite Walker")
                     && !entity.getCustomName().getString().contains("0");
               } else {
                  return !this.mode.equals("Goblin")
                     ? false
                     : entity.hasCustomName()
                        && entity instanceof ArmorStandEntity
                        && (
                           entity.getCustomName().getString().contains("Goblin")
                              || entity.getCustomName().getString().contains("Knifethrower")
                              || entity.getCustomName().getString().contains("Fireslinger")
                        )
                        && entity.getCustomName().getString().contains("800/800");
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

      targetList.sort(Comparator.comparingDouble(PlayerUtil::squaredDistanceTo));
      return targetList.isEmpty() ? null : (LivingEntity)targetList.getFirst();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.mode.equals("Goblin") || this.mode.equals("Glacite Walker")) {
         ((Killaura)Shimmer.getInstance().getModuleManager().getModule("Killaura")).checkTeam.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      ((Killaura)Shimmer.getInstance().getModuleManager().getModule("Killaura")).checkTeam.setEnabled(true);
      WalkerUtils.cancel();
   }
}
