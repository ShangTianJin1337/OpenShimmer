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
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class FrozenTreasureESP extends Module {
   public FrozenTreasureESP() {
      super("FrozenTreasureESP", "Allow you see treasure remotely", -1, Module.Category.RENDER);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null && event.wrc != null && Shimmer.matrixStack != null) {
         if (LocationUtils.sideBarString.toLowerCase().contains("glacial cave")) {
            for (Entity e : mc.world.getEntities()) {
               if (e instanceof ArmorStandEntity) {
                  ArmorStandEntity entity = (ArmorStandEntity)e;
                  Camera camera = mc.gameRenderer.getCamera();
                  Vec3d start = new Vec3d(0.0, 0.0, 1.0)
                     .rotateX(-((float)Math.toRadians((double)camera.getPitch())))
                     .rotateY(-((float)Math.toRadians((double)camera.getYaw())));
                  Vec3d end = RenderUtil.smoothen(entity, 0.0F).add(0.0, (double)entity.getStandingEyeHeight(), 0.0);
                  float scaling = (float)Math.max(2.0, Math.log(entity.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos())));
                  if (entity.getEquippedStack(EquipmentSlot.HEAD) != null
                     && (
                        mc.world.getBlockState(entity.getBlockPos().up()).getBlock() == Blocks.ICE
                           || mc.world.getBlockState(entity.getBlockPos().up()).getBlock() == Blocks.PACKED_ICE
                     )) {
                     String itemName = entity.getEquippedStack(EquipmentSlot.HEAD).getName().getString();
                     if (itemName.contains("Ice Bait")
                        || itemName.contains("Glacial Fragment")
                        || itemName.contains("Packed Ice") && !itemName.contains("Enchanted")) {
                        RenderUtil.draw3DBox(new Box(entity.getBlockPos().up(2)), Color.GRAY, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                        TextRenderUtil.renderText(
                           entity.getEquippedStack(EquipmentSlot.HEAD).getName(),
                           entity.getEntityPos().add(0.0, 2.5, 0.0),
                           scaling,
                           true,
                           event.storage.getEntityVertexConsumers()
                        );
                     }

                     if (itemName.contains("White Gift") || itemName.contains("Enchanted Ice")) {
                        RenderUtil.draw3DBox(new Box(entity.getBlockPos().up(2)), Color.WHITE, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                        TextRenderUtil.renderText(
                           entity.getEquippedStack(EquipmentSlot.HEAD).getName(),
                           entity.getEntityPos().add(0.0, 2.5, 0.0),
                           scaling,
                           true,
                           event.storage.getEntityVertexConsumers()
                        );
                     }

                     if (itemName.contains("Green Gift")) {
                        RenderUtil.draw3DBox(new Box(entity.getBlockPos().up(2)), Color.GREEN, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                        TextRenderUtil.renderText(
                           entity.getEquippedStack(EquipmentSlot.HEAD).getName(),
                           entity.getEntityPos().add(0.0, 2.5, 0.0),
                           scaling,
                           true,
                           event.storage.getEntityVertexConsumers()
                        );
                     }

                     if (itemName.contains("Glacial Talisman")) {
                        RenderUtil.draw3DBox(new Box(entity.getBlockPos().up(2)), Color.BLUE, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                        TextRenderUtil.renderText(
                           entity.getEquippedStack(EquipmentSlot.HEAD).getName(),
                           entity.getEntityPos().add(0.0, 2.5, 0.0),
                           scaling,
                           true,
                           event.storage.getEntityVertexConsumers()
                        );
                     }

                     if (itemName.contains("Red Gift")) {
                        RenderUtil.draw3DBox(new Box(entity.getBlockPos().up(2)), Color.RED, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                        RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), Color.RED, event.storage.getEntityVertexConsumers());
                        TextRenderUtil.renderText(
                           entity.getEquippedStack(EquipmentSlot.HEAD).getName(),
                           entity.getEntityPos().add(0.0, 2.5, 0.0),
                           scaling,
                           true,
                           event.storage.getEntityVertexConsumers()
                        );
                     }

                     if (itemName.contains("Enchanted Packed Ice")) {
                        RenderUtil.draw3DBox(new Box(entity.getBlockPos().up(2)), Color.CYAN, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                        RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), Color.CYAN, event.storage.getEntityVertexConsumers());
                        TextRenderUtil.renderText(
                           entity.getEquippedStack(EquipmentSlot.HEAD).getName(),
                           entity.getEntityPos().add(0.0, 2.5, 0.0),
                           scaling,
                           true,
                           event.storage.getEntityVertexConsumers()
                        );
                     }
                  }
               }
            }
         }
      }
   }
}
