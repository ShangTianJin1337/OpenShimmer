package dev.greencat.shimmer.mixin;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PlayerMoveC2SPacket.class})
public interface PlayerMoveC2SPacketAccessor {
   @Accessor("yaw")
   float getYaw();

   @Accessor("pitch")
   float getPitch();

   @Accessor("onGround")
   boolean isOnGround();

   @Accessor("x")
   double getX();

   @Accessor("y")
   double getY();

   @Accessor("z")
   double getZ();

   @Accessor("changePosition")
   boolean changePosition();

   @Accessor("changeLook")
   boolean changeLook();

   @Accessor("horizontalCollision")
   boolean horizontalCollision();
}
