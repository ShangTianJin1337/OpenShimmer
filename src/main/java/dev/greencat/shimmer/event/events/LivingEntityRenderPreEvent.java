package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.entity.LivingEntity;

public class LivingEntityRenderPreEvent extends Event {
   public final LivingEntity entity;

   public LivingEntityRenderPreEvent(LivingEntity entity) {
      this.entity = entity;
   }
}
