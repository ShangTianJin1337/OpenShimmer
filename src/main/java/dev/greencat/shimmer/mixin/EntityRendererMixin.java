package dev.greencat.shimmer.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Leashable;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({EntityRenderer.class})
public abstract class EntityRendererMixin<T extends Entity> {
   @Shadow
   @Final
   protected EntityRenderManager dispatcher;

   @Shadow
   protected abstract boolean canBeCulled(T var1);

   @Overwrite
   public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
      try {
         if (!entity.shouldRender(x, y, z)) {
            return false;
         } else if (this.canBeCulled(entity)) {
            return true;
         } else {
            Box box = entity.getBoundingBox().expand(0.5);
            if (box.isNaN() || box.getAverageSideLength() == 0.0) {
               box = new Box(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
            }

            if (frustum.isVisible(box)) {
               return true;
            } else {
               if (entity instanceof Leashable leashable) {
                  Entity entity2 = leashable.getLeashHolder();
                  if (entity2 != null) {
                     return frustum.isVisible(((EntityRendererAccessor)this.dispatcher.getRenderer(entity2)).getBox(entity2));
                  }
               }

               return false;
            }
         }
      } catch (Exception var12) {
         return true;
      }
   }
}
