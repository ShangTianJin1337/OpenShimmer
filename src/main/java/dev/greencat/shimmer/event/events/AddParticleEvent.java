package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;
import net.minecraft.client.particle.Particle;

public class AddParticleEvent extends Event {
   public final Particle particle;

   public AddParticleEvent(Particle particle) {
      this.particle = particle;
   }
}
