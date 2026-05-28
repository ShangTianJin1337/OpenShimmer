package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class TargetESP extends Module {
   public LivingEntity target = null;
   public long targetOutSpace = 0L;
   private long colorCounter = 0L;
   int tickOffset = 0;

   public TargetESP() {
      super("TargetESP", "", -1, Module.Category.RENDER);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (MinecraftClient.getInstance().crosshairTarget instanceof EntityHitResult entityHitResult
            && entityHitResult.getEntity() instanceof LivingEntity entity
            && !entity.isInvisible()) {
            this.target = entity;
            this.targetOutSpace = System.currentTimeMillis();
         }

         if (this.target != null && this.target.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos()) <= 3.0) {
            this.targetOutSpace = System.currentTimeMillis();
         }

         if (System.currentTimeMillis() - this.targetOutSpace >= 2500L || !this.target.isAlive()) {
            this.target = null;
         }

         this.tickOffset += 3;
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.AfterEntities event) {
      if (MinecraftClient.getInstance().world != null && this.target != null) {
         Immediate immediate = event.storage.getEntityVertexConsumers();
         VertexConsumer consumer = immediate.getBuffer(RenderLayer.getDebugQuads());
         MatrixStack stack = Shimmer.matrixStack;
         Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
         stack.push();
         stack.translate(-camera.getCameraPos().getX(), -camera.getCameraPos().getY(), -camera.getCameraPos().getZ());
         float radius = 0.5F
            + MathHelper.sqrt(
                  (float)(
                     (this.target.getBoundingBox().maxX - this.target.getBoundingBox().minX) * (this.target.getBoundingBox().maxX - this.target.getBoundingBox().minX)
                        + (this.target.getBoundingBox().maxZ - this.target.getBoundingBox().minZ) * (this.target.getBoundingBox().maxZ - this.target.getBoundingBox().minZ)
                  )
               )
               / 2.0F;
         float lastX = 0.0F;
         float lastY = 0.0F;
         float lastZ = 0.0F;
         boolean isFirst = true;
         if (this.tickOffset >= 360) {
            this.tickOffset = 0;
         }

         int offset = this.tickOffset;

         for (int i = 0; (double)i < 360.0 * (this.target.getBoundingBox().maxY - this.target.getBoundingBox().minY); i += 3) {
            float x = (float)(
               this.target.getEntityPos().getX() + (double)(radius * MathHelper.cos((float)((double)((float)(i + offset) / 180.0F) * Math.PI)))
            );
            float z = (float)(
               this.target.getEntityPos().getZ() + (double)(radius * MathHelper.sin((float)((double)((float)(i + offset) / 180.0F) * Math.PI)))
            );
            float y = (float)(this.target.getBoundingBox().minY + (double)((float)i / 360.0F));
            boolean swap = (int)((float)((long)i + this.colorCounter) / 3.0F / 30.0F) % 2 == 0;
            Color currentColor = Color.getHSBColor(
               0.925F,
               ((swap ? 30.0F - (float)((long)i + this.colorCounter) / 3.0F % 30.0F : (float)((long)i + this.colorCounter) / 3.0F % 30.0F) + 35.0F) / 100.0F,
               1.0F
            );
            int colorRGB = currentColor.getRGB();
            if (!isFirst) {
               Matrix4f matrix = stack.peek().getPositionMatrix();
               consumer.vertex(matrix, lastX, lastY, lastZ).color(colorRGB);
               consumer.vertex(matrix, x, y, z).color(colorRGB);
               consumer.vertex(matrix, x, y + 0.1F, z).color(colorRGB);
               consumer.vertex(matrix, lastX, lastY + 0.1F, lastZ).color(colorRGB);
            }

            lastX = x;
            lastY = y;
            lastZ = z;
            isFirst = false;
         }

         stack.pop();
      }
   }
}
