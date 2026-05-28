package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.client.gui.screen.Screen;

public class BackgroundDrawnEvent extends Event {
   private final Screen currentScreen;

   public BackgroundDrawnEvent(Screen currentScreen) {
      this.currentScreen = currentScreen;
   }

   public Screen getScreen() {
      return this.currentScreen;
   }
}
