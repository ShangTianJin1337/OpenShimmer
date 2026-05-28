package dev.greencat.shimmer.module.modules.combat;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.MinecraftAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.util.entity.EntityUtil;
import dev.greencat.shimmer.util.player.PlayerUtil;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.player.rotation.RotationUtil.Target;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class Killaura extends Module {
   public final NumberSetting range = new NumberSetting("Range", "Attack range", 4.0, 0.0, 6.0, 0.1);
   public final NumberSetting wallRange = new NumberSetting("Wall Range", "Attack range through wall", 2.0, 0.0, 6.0, 0.1);
   public final BooleanSetting ignoreTamed = new BooleanSetting("Ignore Tamed", "Killaura will not attack tamed mob", false);
   public final BooleanSetting onlyOnClick = new BooleanSetting("Only On Click", "Killaura will attack when you click", false);
   public final BooleanSetting onlyOnLook = new BooleanSetting("Only On Look", "Killaura will attack when you look", false);
   public final BooleanSetting invisible = new BooleanSetting("Attack Invisible", "Killaura will attack invisible mob", false);
   public final BooleanSetting checkTeam = new BooleanSetting("Team Check", "Killaura will not attack team member", true);
   public final BooleanSetting checkNPC = new BooleanSetting("NPC Check", "Killaura will not attack NPC", true);
   public final BooleanSetting ghost = new BooleanSetting("Attack Ghost", "Killaura will attack ghost", false);
   public boolean forward = false;
   public boolean backward = false;
   private final List<Entity> targets = new ArrayList();
   public boolean attacking;
   private boolean lastTickChangeYaw = false;
   private Entity lastTarget = null;
   private boolean restored = false;

   public Killaura() {
      super("Killaura", "Auto kill mob around you", -1, Module.Category.COMBAT, true);
      this.addSettings(
         new Setting[]{
            this.range, this.wallRange, this.ignoreTamed, this.onlyOnClick, this.onlyOnLook, this.invisible, this.checkTeam, this.checkNPC, this.ghost
         }
      );
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (mc.player.isAlive()) {
            if (!mc.interactionManager.isBreakingBlock() && !mc.player.isUsingItem()) {
               if (!this.restored
                  && (
                     this.lastTarget == null
                        || !this.lastTarget.isAlive()
                        || this.lastTarget.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos()) > this.range.getValue()
                  )) {
                  MinecraftClient.getInstance().options.forwardKey.setPressed(this.forward);
                  MinecraftClient.getInstance().options.backKey.setPressed(this.backward);
                  this.restored = true;
               }

               if (this.onlyOnClick.isEnabled() && !mc.options.attackKey.isPressed()) {
                  ServerRotation.useServerRotation = false;
               } else {
                  if (this.onlyOnLook.isEnabled()) {
                     ServerRotation.useServerRotation = false;
                     Entity targeted = mc.targetedEntity;
                     if (targeted == null) {
                        return;
                     }

                     if (!this.entityCheck(targeted)) {
                        return;
                     }

                     this.targets.clear();
                     this.targets.add(mc.targetedEntity);
                  } else {
                     this.targets.clear();
                     getList(this.targets, this::entityCheck, 1);
                  }

                  Entity primary = this.targets.isEmpty() ? null : (Entity)this.targets.getFirst();
                  this.attacking = true;
                  if (primary != null) {
                     ServerRotation.useServerRotation = true;
                     ServerRotation.serverYaw = (float)RotationUtil.getYaw(primary);
                     ServerRotation.serverPitch = (float)RotationUtil.getPitch(primary, RotationUtil.Target.BODY);
                     this.lastTickChangeYaw = !this.lastTickChangeYaw;
                     MinecraftClient.getInstance()
                        .player
                        .setYaw(MinecraftClient.getInstance().player.getYaw() + (this.lastTickChangeYaw ? -0.1F : 0.1F));
                  } else {
                     ServerRotation.useServerRotation = false;
                  }

                  if (primary != null) {
                     if (this.delayCheck()) {
                        this.targets.forEach(this::attack);
                     }
                  }
               }
            }
         }
      }
   }

   private void attack(Entity target) {
      if (this.targets != null && target.isAlive() && target.isAttackable()) {
         if (this.restored) {
            this.forward = MinecraftClient.getInstance().options.forwardKey.isPressed();
            this.backward = MinecraftClient.getInstance().options.backKey.isPressed();
            this.lastTarget = target;
            this.restored = false;
         }

         MinecraftClient.getInstance().options.forwardKey.setPressed(false);
         MinecraftClient.getInstance().options.backKey.setPressed(false);
         MinecraftClient.getInstance().options.leftKey.setPressed(false);
         MinecraftClient.getInstance().options.rightKey.setPressed(false);
         if (MinecraftClient.getInstance().player != null
            && MinecraftClient.getInstance().player.getMovement().x <= 0.08
            && MinecraftClient.getInstance().player.getMovement().z <= 0.08) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
         }
      }
   }

   private boolean entityCheck(Entity entity) {
      if (!entity.equals(mc.player) && !entity.equals(((MinecraftAccessor)mc).camera())) {
         if ((!(entity instanceof LivingEntity livingEntity) || !livingEntity.isDead()) && entity.isAlive()) {
            if (entity instanceof LivingEntity livingEntityx
               && livingEntityx.isInvisible()
               && !this.invisible.isEnabled()
               && (!(entity instanceof CreeperEntity) || !this.ghost.isEnabled())) {
               return false;
            }

            if (entity instanceof PlayerEntity player && this.checkNPC.isEnabled() && EntityUtil.isNPC(player)) {
               return false;
            }

            if (!entity.isAttackable()) {
               return false;
            } else if (entity instanceof FireballEntity) {
               return false;
            } else if (entity instanceof MinecartEntity) {
               return false;
            } else if (entity instanceof BoatEntity) {
               return false;
            } else if (entity instanceof ItemFrameEntity) {
               return false;
            } else if (entity instanceof PotionEntity) {
               return false;
            } else if (entity instanceof AreaEffectCloudEntity) {
               return false;
            } else {
               Box hitbox = entity.getBoundingBox();
               if (!PlayerUtil.isWithin(
                  MathHelper.clamp(mc.player.getX(), hitbox.minX, hitbox.maxX),
                  MathHelper.clamp(mc.player.getY(), hitbox.minY, hitbox.maxY),
                  MathHelper.clamp(mc.player.getZ(), hitbox.minZ, hitbox.maxZ),
                  this.range.getValue()
               )) {
                  return false;
               } else if (!PlayerUtil.canSeeEntity(entity) && !PlayerUtil.isWithin(entity, this.wallRange.getValue())) {
                  return false;
               } else {
                  if (this.ignoreTamed.isEnabled()
                     && entity instanceof Tameable tameable
                     && tameable.getOwner() != null
                     && tameable.getOwner().getUuid() != null
                     && tameable.getOwner().getUuid().equals(mc.player.getUuid())) {
                     return false;
                  }

                  if (entity instanceof PlayerEntity player && this.checkTeam.isEnabled() && EntityUtil.isTeamMember(mc.player, player)) {
                     return false;
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean delayCheck() {
      return mc.player.getAttackCooldownProgress(0.5F) >= 1.0F;
   }

   public static void getList(List<Entity> targetList, Predicate<Entity> isGood, int maxCount) {
      targetList.clear();

      for (Entity entity : mc.world.getEntities()) {
         if (entity != null && isGood.test(entity)) {
            targetList.add(entity);
         }
      }

      targetList.sort(Comparator.comparingDouble(PlayerUtil::squaredDistanceTo));
      if (targetList.size() > maxCount) {
         targetList.subList(maxCount, targetList.size()).clear();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      ServerRotation.useServerRotation = false;
      this.targets.clear();
      this.attacking = false;
   }
}
