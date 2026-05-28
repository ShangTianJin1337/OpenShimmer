package dev.greencat.shimmer.event.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PostMotionEvent {
   Event<PostMotionEvent> EVENT = EventFactory.createArrayBacked(PostMotionEvent.class, listeners -> () -> {
         for (PostMotionEvent listener : listeners) {
            listener.call();
         }
      });

   void call();
}
