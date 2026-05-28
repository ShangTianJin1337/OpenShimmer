package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;

public class RenderEvent extends Event {
   public WorldRenderer wrc;
   public BufferBuilderStorage storage;

   public RenderEvent(WorldRenderer wrc, BufferBuilderStorage storage) {
      this.wrc = wrc;
      this.storage = storage;
   }

   public static class AfterEntities extends RenderEvent {
      public AfterEntities(WorldRenderer wrc, BufferBuilderStorage storage) {
         super(wrc, storage);
      }
   }

   public static class Post extends RenderEvent {
      public Post(WorldRenderer wrc, BufferBuilderStorage storage) {
         super(wrc, storage);
      }
   }

   public static class Pre extends RenderEvent {
      public Pre(WorldRenderer wrc, BufferBuilderStorage storage) {
         super(wrc, storage);
      }
   }
}
