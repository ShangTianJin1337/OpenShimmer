package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.text.Text;

public class ActionBarRenderEvent extends Event {
   public final Text text;

   public ActionBarRenderEvent(Text text) {
      this.text = text;
   }
}
