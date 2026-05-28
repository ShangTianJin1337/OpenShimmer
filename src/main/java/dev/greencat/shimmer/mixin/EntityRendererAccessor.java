package dev.greencat.shimmer.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({EntityRenderer.class})
public interface EntityRendererAccessor<T extends Entity, S extends EntityRenderState> {
   @Invoker("getBoundingBox")
   Box getBox(T var1);
}
