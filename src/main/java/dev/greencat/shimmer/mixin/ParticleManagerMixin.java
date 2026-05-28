package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.AddParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ParticleManager.class})
public class ParticleManagerMixin {
   @Inject(
      method = {"addParticle(Lnet/minecraft/client/particle/Particle;)V"},
      at = {@At("HEAD")}
   )
   public void hookAddParticle(Particle particle, CallbackInfo ci) {
      Shimmer.getInstance().getEventBus().post(new AddParticleEvent(particle));
   }
}
