package dev.greencat.shimmer.util.player.rotation;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.mixin.PlayerMoveC2SPacketAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;

public class ServerRotation {
   public static boolean useServerRotation = false;
   public static float serverYaw = 0.0F;
   private static final List<String> serverRotationModuleList = new ArrayList();
   public static float serverPitch = 0.0F;

   public static void registerServerRotationModule(String name) {
      serverRotationModuleList.add(name);
   }

   public static boolean onPacketSent(Packet<?> packet) {
      check();
      if (packet instanceof PlayerMoveC2SPacket newPacket && useServerRotation) {
         PlayerMoveC2SPacketAccessor oldPacket = (PlayerMoveC2SPacketAccessor)packet;
         if (packet instanceof Full) {
            newPacket = new Full(
               oldPacket.getX(), oldPacket.getY(), oldPacket.getZ(), serverYaw, serverPitch, oldPacket.isOnGround(), oldPacket.horizontalCollision()
            );
         }

         if (packet instanceof LookAndOnGround) {
            newPacket = new LookAndOnGround(serverYaw, serverPitch, oldPacket.isOnGround(), oldPacket.horizontalCollision());
         }

         ((ClientPlayNetworkHandler)Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())).getConnection().send(newPacket, null);
         return false;
      }

      if (packet instanceof PlayerInteractItemC2SPacket oldPacketx && useServerRotation) {
         ((ClientPlayNetworkHandler)Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()))
            .getConnection()
            .send(new PlayerInteractItemC2SPacket(oldPacketx.getHand(), oldPacketx.getSequence(), serverYaw, serverPitch), null);
         return false;
      }

      return true;
   }

   private static void check() {
      boolean hasServerRotationModuleEnabled = false;

      for (String modules : serverRotationModuleList) {
         if (Shimmer.getInstance().getModuleManager().isModuleEnabled(modules)) {
            hasServerRotationModuleEnabled = true;
            break;
         }
      }

      if (SmoothRotation.running && !SmoothRotation.isServerRotation || !hasServerRotationModuleEnabled) {
         useServerRotation = false;
      }
   }
}
