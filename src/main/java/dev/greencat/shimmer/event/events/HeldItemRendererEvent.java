package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

public class HeldItemRendererEvent extends Event {
   public final Hand hand;
   public final MatrixStack matrices;

   public HeldItemRendererEvent(Hand hand, MatrixStack matrices) {
      this.hand = hand;
      this.matrices = matrices;
   }
}
