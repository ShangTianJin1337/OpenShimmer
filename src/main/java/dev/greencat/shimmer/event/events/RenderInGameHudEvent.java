package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.client.gui.DrawContext;

public class RenderInGameHudEvent extends Event {
   private final DrawContext context;

   public RenderInGameHudEvent(DrawContext context) {
      this.context = context;
   }

   public DrawContext getContext() {
      return this.context;
   }
}
