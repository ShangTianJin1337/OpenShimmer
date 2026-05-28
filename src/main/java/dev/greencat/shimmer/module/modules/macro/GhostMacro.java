package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;

public class GhostMacro extends Module {
   Entity target = null;
   double distance = 999999.0;

   public GhostMacro() {
      super("GhostMacro", "Auto attack Ghost", -1, Module.Category.MACRO);
      this.needDisable = true;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      Shimmer.getInstance().getModuleManager().getModule("Killaura").setEnabled(false);
      this.distance = 999999.0;
      this.target = null;
      WalkerUtils.cancel();
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (this.target == null) {
            MinecraftClient.getInstance()
               .world
               .getEntities()
               .forEach(
                  entity -> {
                     if (entity instanceof CreeperEntity
                        && entity.isInvisible()
                        && !((CreeperEntity)entity).isDead()
                        && entity.getBlockY() == MinecraftClient.getInstance().player.getBlockY()
                        && entity.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos()) < this.distance) {
                        this.target = entity;
                        this.distance = this.target.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos());
                     }
                  }
               );
         }

         if (this.target != null) {
            if (this.target.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos()) >= 1.0) {
               if (!WalkerUtils.isActive()) {
                  WalkerUtils.walkToBaritoneRotation(this.target.getBlockPos());
               }
            } else {
               Shimmer.getInstance().getModuleManager().getModule("Killaura").setEnabled(true);
            }

            if (((CreeperEntity)this.target).isDead()) {
               this.target = null;
               this.distance = 999999.0;
            }
         }
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         RenderUtil.draw3DBox(
            MinecraftClient.getInstance().world.getBlockState(this.target.getBlockPos()).getCullingShape().getBoundingBox(),
            Color.ORANGE,
            (MatrixStack)Objects.requireNonNull(Shimmer.matrixStack),
            event.storage.getEntityVertexConsumers()
         );
      }
   }
}
