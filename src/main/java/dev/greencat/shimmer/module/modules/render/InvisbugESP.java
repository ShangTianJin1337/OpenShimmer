package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.ParticleRenderEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.concurrent.CopyOnWriteArraySet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class InvisbugESP extends Module {
   private static Color color = Color.RED;
   private static final Vec3d camp = new Vec3d(-627.0, 117.0, 49.0);
   private static long lastClear = 0L;
   private static CopyOnWriteArraySet<Box> currentInvisbug = new CopyOnWriteArraySet();

   public InvisbugESP() {
      super("InvisbugESP", "Allow you see invisbug", -1, Module.Category.RENDER);
   }

   @ShimmerSubscribe
   public void onParticle(ParticleRenderEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (event.particle instanceof DamageParticle && event.particle.getBoundingBox().getCenter().distanceTo(camp) >= 3.0) {
            currentInvisbug.add(event.particle.getBoundingBox());
         }
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null) {
         for (Box box : currentInvisbug) {
            Camera camera = mc.gameRenderer.getCamera();
            Vec3d start = new Vec3d(0.0, 0.0, 1.0)
               .rotateX(-((float)Math.toRadians((double)camera.getPitch())))
               .rotateY(-((float)Math.toRadians((double)camera.getYaw())));
            Vec3d end = box.getCenter();
            RenderUtil.draw3DOutline(box, color, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
         }

         if (System.currentTimeMillis() - lastClear >= 1000L) {
            currentInvisbug.clear();
            lastClear = System.currentTimeMillis();
         }
      }
   }
}
