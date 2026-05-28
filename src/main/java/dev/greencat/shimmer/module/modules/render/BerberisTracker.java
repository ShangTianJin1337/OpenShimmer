package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.ParticleRenderEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import dev.greencat.shimmer.util.world.LocationUtils;
import java.awt.Color;
import java.util.HashSet;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BerberisTracker extends Module {
   private static HashSet<BlockPos> targetList = new HashSet();
   public static long lastRefresh = 0L;

   public BerberisTracker() {
      super("BerberisTracker", "Auto track berberis in rift dreadfarm", -1, Module.Category.RENDER);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      targetList.clear();
   }

   @ShimmerSubscribe
   public void onParticle(ParticleRenderEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (LocationUtils.sideBarString.toLowerCase().contains("dreadfarm")) {
            if (event.particle.getClass().getSimpleName().contains("class_680")
               && MinecraftClient.getInstance().world.getBlockState(BlockPos.ofFloored(event.particle.getBoundingBox().getCenter())).getBlock()
                  == Blocks.DEAD_BUSH) {
               targetList.add(BlockPos.ofFloored(event.particle.getBoundingBox().getCenter()).toImmutable());
            }
         }
      }
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (System.currentTimeMillis() - lastRefresh >= 500L) {
            targetList.clear();
            lastRefresh = System.currentTimeMillis();
         }
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null) {
         for (BlockPos pos : targetList) {
            Camera camera = mc.gameRenderer.getCamera();
            Vec3d start = new Vec3d(0.0, 0.0, 1.0)
               .rotateX(-((float)Math.toRadians((double)camera.getPitch())))
               .rotateY(-((float)Math.toRadians((double)camera.getYaw())));
            Vec3d end = pos.toCenterPos();
            RenderUtil.draw3DOutline(new Box(pos), Color.GREEN, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
         }
      }
   }
}
