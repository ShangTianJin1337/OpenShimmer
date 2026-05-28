package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.PacketEvent;
import dev.greencat.shimmer.event.events.PacketEvent.Type;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerEvent.Era;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.player.rotation.SmoothRotation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PowderBot extends Module {
   List<BlockPos> blackList = new ArrayList();
   public boolean lookingChest = false;
   public long lastLookingChest = 0L;

   public PowderBot() {
      super("PowderBot", "", -1, Module.Category.MACRO);
      this.needDisable = true;
   }

   @ShimmerSubscribe
   public void onPacket(PacketEvent event) {
      if (event.getType() != PacketEvent.Type.SEND && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (System.currentTimeMillis() - this.lastLookingChest >= 1000L) {
            this.lookingChest = false;
         }

         if (event.getPacket() instanceof ParticleS2CPacket particleS2CPacket && ParticleTypes.CRIT.equals(particleS2CPacket.getParameters().getType())) {
            Vec3d vec3d = new Vec3d(particleS2CPacket.getX(), particleS2CPacket.getY(), particleS2CPacket.getZ());
            BlockPos pos = BlockPos.ofFloored(vec3d);

            for (BlockPos pos1 : BlockPos.iterate(pos.add(1, 1, 1), pos.add(-1, -1, -1))) {
               if (vec3d.distanceTo(pos1.toCenterPos()) <= 1.007 && MinecraftClient.getInstance().world.getBlockState(pos1).getBlock() == Blocks.CHEST) {
                  this.seeParticle(vec3d);
                  break;
               }
            }
         }
      }
   }

   public void seeParticle(Vec3d particlePos) {
      this.lookingChest = true;
      this.lastLookingChest = System.currentTimeMillis();
      SmoothRotation.smoothLook(RotationUtil.toRotation(particlePos), 300, () -> {
      });
   }

   @Override
   public void onEnable() {
      super.onEnable();
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(true);
      Shimmer.getInstance().getModuleManager().getModule("ChestBot").setEnabled(false);
      this.lookingChest = false;
      this.blackList.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (event.getEra() != Era.POST) {
         if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
            if (this.lookingChest) {
               Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
            } else {
               boolean hasTarget = false;
               Nuker nuker = (Nuker)Shimmer.getInstance().getModuleManager().getModule("Nuker");

               for (BlockPos pos : BlockPos.iterate(
                  new BlockPos(
                     MinecraftClient.getInstance().player.getBlockX() + 1,
                     MinecraftClient.getInstance().player.getBlockY(),
                     MinecraftClient.getInstance().player.getBlockZ() + 1
                  ),
                  new BlockPos(
                     MinecraftClient.getInstance().player.getBlockX() - 1,
                     MinecraftClient.getInstance().player.getBlockY() + 1,
                     MinecraftClient.getInstance().player.getBlockZ() - 1
                  )
               )) {
                  if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.STONE && !this.blackList.contains(pos)) {
                     hasTarget = true;
                  }
               }

               if (!hasTarget && !WalkerUtils.isActive()) {
                  nuker.setEnabled(false);
                  double distance = 999999.0;
                  BlockPos targetBlock = null;

                  for (BlockPos posx : BlockPos.iterate(
                     new BlockPos(MinecraftClient.getInstance().player.getBlockX() + 32, 0, MinecraftClient.getInstance().player.getBlockZ() + 32),
                     new BlockPos(MinecraftClient.getInstance().player.getBlockX() - 32, 160, MinecraftClient.getInstance().player.getBlockZ() - 32)
                  )) {
                     if (MinecraftClient.getInstance().world.getBlockState(posx).getBlock() == Blocks.STONE
                        && !this.blackList.contains(posx)
                        && posx.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos()) < distance) {
                        distance = posx.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos());
                        targetBlock = posx.toImmutable();
                     }
                  }

                  this.blackList.add(targetBlock);
                  BlockPos target = null;
                  BlockPos perferredTarget = null;

                  for (BlockPos posxx : BlockPos.iterate(
                     new BlockPos(targetBlock.getX() + 3, targetBlock.getY() + 3, targetBlock.getZ() + 3),
                     new BlockPos(targetBlock.getX() - 3, targetBlock.getY() - 3, targetBlock.getZ() - 3)
                  )) {
                     if (MinecraftClient.getInstance().world.getBlockState(posxx).getBlock() != Blocks.AIR
                        && MinecraftClient.getInstance().world.getBlockState(posxx.up()).getBlock() == Blocks.AIR
                        && MinecraftClient.getInstance().world.getBlockState(posxx.up(2)).getBlock() == Blocks.AIR) {
                        target = posxx.toImmutable();
                        if (posxx.getY() == MinecraftClient.getInstance().player.getBlockY() - 1) {
                           perferredTarget = posxx.toImmutable();
                        }
                     }
                  }

                  if (perferredTarget != null) {
                     WalkerUtils.walkTo(perferredTarget);
                  } else if (target != null) {
                     WalkerUtils.walkTo(target);
                  }
               }

               nuker.setEnabled(!WalkerUtils.isActive());
               if (this.blackList.size() >= 300) {
                  this.blackList.clear();
               }
            }
         }
      }
   }
}
