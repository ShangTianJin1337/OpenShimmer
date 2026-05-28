package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ShulkerESP extends Module {
   private static final HashMap<String, Color> colorMap = new HashMap();

   public ShulkerESP() {
      super("ShulkerESP", "Allow you see Shulker Mob through the wall", -1, Module.Category.RENDER);
   }

   @Override
   public void onEnable() {
      colorMap.put(ShulkerEntity.class.getSimpleName(), new Color(200, 255, 0, 160));
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
            if (entity instanceof ShulkerEntity) {
               RenderUtil.draw3DOutline(entity.getBoundingBox(), getColor(entity), Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
               RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), getColor(entity), event.storage.getEntityVertexConsumers());
            }
         }
      }
   }
}
