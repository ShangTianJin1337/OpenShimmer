package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event {
   private final Packet packet;
   private final PacketEvent.Type type;

   public PacketEvent(Packet packet, PacketEvent.Type type) {
      this.packet = packet;
      this.type = type;
   }

   public Packet getPacket() {
      return this.packet;
   }

   public PacketEvent.Type getType() {
      return this.type;
   }

   public static enum Type {
      SEND,
      RECEIVE;
   }
}
