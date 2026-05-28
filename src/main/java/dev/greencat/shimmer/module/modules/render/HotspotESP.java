package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import dev.greencat.shimmer.util.render.TextRenderUtil;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class HotspotESP extends Module {
   public HotspotESP() {
      super("HotspotESP", "Auto find Fishing hotspot", -1, Module.Category.RENDER);
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null && event.storage.getEntityVertexConsumers() != null && Shimmer.matrixStack != null) {
         for (Entity e : mc.world.getEntities()) {
            if (e instanceof ArmorStandEntity) {
               ArmorStandEntity entity = (ArmorStandEntity)e;
               float scaling = (float)Math.max(2.0, Math.log(entity.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos()))) * 3.0F;
               if (entity.getName().getString().contains("HOTSPOT")) {
                  TextRenderUtil.renderText(
                     Text.of(Formatting.LIGHT_PURPLE + "HOTSPOT"),
                     entity.getEntityPos().add(0.0, 10.0, 0.0),
                     scaling,
                     true,
                     event.storage.getEntityVertexConsumers()
                  );
               }

               if (entity.getName().getString().contains("Sea Creature Chance")
                  || entity.getName().getString().contains("Double Hook")
                  || entity.getName().getString().contains("Fishing Speed")
                  || entity.getName().getString().contains("Treasure Chance")) {
                  TextRenderUtil.renderText(
                     entity.getName(),
                     entity.getEntityPos().add(0.0, 10.0 - 0.25 * (double)scaling, 0.0),
                     scaling,
                     true,
                     event.storage.getEntityVertexConsumers()
                  );
               }

               RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), Color.MAGENTA, event.storage.getEntityVertexConsumers());
            }
         }
      }
   }
}
