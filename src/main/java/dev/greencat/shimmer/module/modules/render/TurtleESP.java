package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.HashMap;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TurtleESP extends Module {
   private static final HashMap<String, Color> colorMap = new HashMap();

   public TurtleESP() {
      super("TurtleESP", "Allow you see Turtle Mob through the wall", -1, Module.Category.RENDER);
   }

   @Override
   public void onEnable() {
      colorMap.put(TurtleEntity.class.getSimpleName(), new Color(15, 255, 220, 160));
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      colorMap.clear();
   }

   private static Color getColor(Entity entity) {
      if (entity instanceof PlayerEntity) {
         return entity.getDisplayName().getStyle().getColor() != null
            ? new Color(entity.getDisplayName().getStyle().getColor().getRgb())
            : (Color)colorMap.get(entity.getClass().getSimpleName());
      } else {
         return (Color)colorMap.get(entity.getClass().getSimpleName());
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null) {
         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof TurtleEntity) {
               Camera camera = mc.gameRenderer.getCamera();
               Vec3d start = new Vec3d(0.0, 0.0, 1.0)
                  .rotateX(-((float)Math.toRadians((double)camera.getPitch())))
                  .rotateY(-((float)Math.toRadians((double)camera.getYaw())));
               Vec3d end = RenderUtil.smoothen(entity, 0.0F).add(0.0, (double)entity.getStandingEyeHeight(), 0.0);
               RenderUtil.draw3DOutline(entity.getBoundingBox(), getColor(entity), Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
               RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), getColor(entity), event.storage.getEntityVertexConsumers());
            }
         }
      }
   }
}
