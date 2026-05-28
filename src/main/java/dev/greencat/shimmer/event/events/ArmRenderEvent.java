package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

public class ArmRenderEvent extends Event {
   public final Hand hand;
   public final MatrixStack matrices;

   public ArmRenderEvent(Hand hand, MatrixStack matrices) {
      this.hand = hand;
      this.matrices = matrices;
   }
}
