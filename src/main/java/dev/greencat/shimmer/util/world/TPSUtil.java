package dev.greencat.shimmer.util.world;

import dev.greencat.shimmer.event.events.PacketEvent;
import dev.greencat.shimmer.event.events.PacketEvent.Type;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

public class TPSUtil {
   public static TPSUtil INSTANCE = new TPSUtil();
   private static double ticks = 0.0;
   private static long prevTime = 0L;

   @ShimmerSubscribe
   public void onPacket(PacketEvent event) {
      if (event.getType() == PacketEvent.Type.RECEIVE && event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
         long time = System.currentTimeMillis();
         long timeOffset = Math.abs(1000L - (time - prevTime)) + 1000L;
         ticks = MathHelper.clamp(20.0 / ((double)timeOffset / 1000.0), 0.0, 20.0) * 100.0 / 100.0;
         prevTime = time;
      }
   }

   public double getTPS() {
      return (double)Math.round(ticks * 100.0) / 100.0;
   }
}
