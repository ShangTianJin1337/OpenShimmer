package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;

public class ParticleRenderEvent extends Event {
   public final Particle particle;
   public final Immediate context;

   public ParticleRenderEvent(Particle particle, Immediate context) {
      this.particle = particle;
      this.context = context;
   }
}
