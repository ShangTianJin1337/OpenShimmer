package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.client.gui.screen.Screen;

public class OnScreenOpenEvent extends Event {
   private final Screen currentScreen;

   public OnScreenOpenEvent(Screen currentScreen) {
      this.currentScreen = currentScreen;
   }

   public Screen getScreen() {
      return this.currentScreen;
   }
}
