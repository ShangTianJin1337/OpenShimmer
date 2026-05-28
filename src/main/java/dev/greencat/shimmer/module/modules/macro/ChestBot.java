package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.PacketEvent;
import dev.greencat.shimmer.event.events.PacketEvent.Type;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.player.rotation.SmoothRotation;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ChestBot extends Module {
   public ChestBot() {
      super("ChestBot", "", -1, Module.Category.MACRO);
   }

   @ShimmerSubscribe
   public void onPacket(PacketEvent event) {
      if (event.getType() != PacketEvent.Type.SEND && MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
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
      SmoothRotation.smoothLook(RotationUtil.toRotation(particlePos), 300, () -> {
      });
   }
}
