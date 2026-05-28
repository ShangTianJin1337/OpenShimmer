package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import dev.greencat.shimmer.util.render.TextRenderUtil;
import dev.greencat.shimmer.util.world.LocationUtils;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class TerminalESP extends Module {
   static final String InactiveTerminal = "Inactive Terminal";
   static final String InactiveLevel = "Not Activate";
   static final String ActiveTerminal = "Terminal Active";

   public TerminalESP() {
      super("TerminalESP", "See the uncompleted terminal through the wall", -1, Module.Category.RENDER);
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null && event.storage.getEntityVertexConsumers() != null && Shimmer.matrixStack != null) {
         if (LocationUtils.sideBarString.toLowerCase().contains("catacomb")) {
            for (Entity e : mc.world.getEntities()) {
               if (e instanceof ArmorStandEntity) {
                  ArmorStandEntity entity = (ArmorStandEntity)e;
                  Camera camera = mc.gameRenderer.getCamera();
                  Vec3d start = new Vec3d(0.0, 0.0, 1.0)
                     .rotateX(-((float)Math.toRadians((double)camera.getPitch())))
                     .rotateY(-((float)Math.toRadians((double)camera.getYaw())));
                  Vec3d end = RenderUtil.smoothen(entity, 0.0F).add(0.0, (double)entity.getStandingEyeHeight(), 0.0);
                  float scaling = (float)Math.max(2.0, Math.log(entity.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos())));
                  if (entity.getName().getString().contains("Inactive Terminal")) {
                     RenderUtil.draw3DBox(new Box(entity.getBlockPos().up()), Color.RED, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                     TextRenderUtil.renderText(
                        Text.of("Inactive Terminal"), entity.getEntityPos().add(0.0, 2.5, 0.0), scaling, true, event.storage.getEntityVertexConsumers()
                     );
                     RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), Color.RED, event.storage.getEntityVertexConsumers());
                  }

                  if (entity.getName().getString().contains("Not Activate")) {
                     RenderUtil.draw3DBox(new Box(entity.getBlockPos().up()), Color.RED, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                     RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), Color.RED, event.storage.getEntityVertexConsumers());
                     TextRenderUtil.renderText(
                        Text.of("Not Activate"), entity.getEntityPos().add(0.0, 2.5, 0.0), scaling, true, event.storage.getEntityVertexConsumers()
                     );
                  }

                  if (entity.getName().getString().contains("Terminal Active")) {
                     RenderUtil.draw3DBox(new Box(entity.getBlockPos().up()), Color.GREEN, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                     TextRenderUtil.renderText(
                        Text.of("Terminal Active"), entity.getEntityPos().add(0.0, 2.5, 0.0), scaling, true, event.storage.getEntityVertexConsumers()
                     );
                  }
               }
            }
         }
      }
   }
}
