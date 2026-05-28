package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class LivingMetalESP extends Module {
   public static List<BlockPos> prevLapis = new ArrayList();
   public static final List<LivingMetalESP.NewAppearLapis> newAppearLapis = new CopyOnWriteArrayList();

   public LivingMetalESP() {
      super("LivingMetalESP", "Auto detect living metal", -1, Module.Category.RENDER);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         List<BlockPos> currentLapis = new ArrayList();

         for (BlockPos pos : BlockPos.iterate(
            new BlockPos(
               MinecraftClient.getInstance().player.getBlockX() + 20,
               MinecraftClient.getInstance().player.getBlockY() + 10,
               MinecraftClient.getInstance().player.getBlockZ() + 20
            ),
            new BlockPos(
               MinecraftClient.getInstance().player.getBlockX() - 20,
               MinecraftClient.getInstance().player.getBlockY() - 10,
               MinecraftClient.getInstance().player.getBlockZ() - 20
            )
         )) {
            if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.LAPIS_ORE) {
               currentLapis.add(pos.toImmutable());
            }
         }

         List<BlockPos> temporyList = new ArrayList(List.copyOf(currentLapis));
         temporyList.removeAll(prevLapis);

         for (BlockPos posx : temporyList) {
            LivingMetalESP.NewAppearLapis lapis = new LivingMetalESP.NewAppearLapis(posx, System.currentTimeMillis());
            if (posx.getSquaredDistance(MinecraftClient.getInstance().player.getEntityPos()) <= 35.0) {
               newAppearLapis.add(lapis);
            }
         }

         newAppearLapis.removeIf(
            lapisx -> System.currentTimeMillis() - lapisx.appearMills >= 3000L
                  || MinecraftClient.getInstance().world.getBlockState(lapisx.pos).getBlock() != Blocks.LAPIS_ORE
         );
         prevLapis = currentLapis;
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (MinecraftClient.getInstance().world != null) {
         for (BlockPos pos : prevLapis) {
            boolean noDraw = false;

            for (LivingMetalESP.NewAppearLapis lapis : newAppearLapis) {
               if (pos.equals(lapis.pos)) {
                  noDraw = true;
                  break;
               }
            }

            if (!noDraw) {
               RenderUtil.draw3DOutline(new Box(pos), Color.BLUE, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
            }
         }

         for (LivingMetalESP.NewAppearLapis lapisx : newAppearLapis) {
            RenderUtil.draw3DBox(new Box(lapisx.pos), Color.ORANGE, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
            Camera camera = mc.gameRenderer.getCamera();
            Vec3d start = new Vec3d(0.0, 0.0, 1.0)
               .rotateX(-((float)Math.toRadians((double)camera.getPitch())))
               .rotateY(-((float)Math.toRadians((double)camera.getYaw())));
            Vec3d var11 = lapisx.pos.toCenterPos();
         }
      }
   }

   public static record NewAppearLapis(BlockPos pos, long appearMills) {
   }
}
