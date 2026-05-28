package dev.greencat.shimmer.event.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PreMotionEvent {
   Event<PreMotionEvent> EVENT = EventFactory.createArrayBacked(PreMotionEvent.class, listeners -> () -> {
         for (PreMotionEvent listener : listeners) {
            listener.call();
         }
      });

   void call();
}
