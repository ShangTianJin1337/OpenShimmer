package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.TextRenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

public class DebugNPCAndBotESP extends Module {
   public DebugNPCAndBotESP() {
      super("DebugESP", "Allow you see Mob through the wall", -1, Module.Category.RENDER);
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
   public void onRender(RenderEvent.AfterEntities event) {
      if (MinecraftClient.getInstance().world != null) {
         for (Entity entity : MinecraftClient.getInstance().world.getEntities()) {
            float scaling = (float)Math.max(2.0, Math.log(entity.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos())));
            TextRenderUtil.renderText(
               Text.of(entity.getClass().getSimpleName()), entity.getEntityPos().add(0.0, 2.5, 0.0), scaling, true, event.storage.getEntityVertexConsumers()
            );
            TextRenderUtil.renderText(entity.getDisplayName(), entity.getEntityPos().add(0.0, 3.5, 0.0), scaling, true, event.storage.getEntityVertexConsumers());
            if (entity instanceof LivingEntity) {
               LivingEntity livingEntity = (LivingEntity)entity;
               if (((LivingEntity)entity).getEquippedStack(EquipmentSlot.HEAD) != null) {
                  TextRenderUtil.renderText(
                     livingEntity.getEquippedStack(EquipmentSlot.HEAD).getName(),
                     entity.getEntityPos().add(0.0, 4.5, 0.0),
                     scaling,
                     true,
                     event.storage.getEntityVertexConsumers()
                  );
                  TextRenderUtil.renderText(
                     Text.of(livingEntity.hasPassengers() + ""),
                     entity.getEntityPos().add(0.0, 5.5, 0.0),
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
