package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.ModeSetting;
import dev.greencat.shimmer.util.entity.EntityUtil;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ESP extends Module {
   public static final ModeSetting mode = new ModeSetting("Player Render Type", "Change Render Type", "3D", "3D", "2D", "Line");
   public final BooleanSetting useGuardian = new BooleanSetting("Elder Guardian", "Enable Elder Guardian ESP", true);
   public final BooleanSetting useSilverfish = new BooleanSetting("Silverfish", "Enable Silverfish ESP", true);
   public final BooleanSetting useGolem = new BooleanSetting("Iron Golem", "Enable Golem ESP", true);
   public final BooleanSetting usePlayer = new BooleanSetting("Player", "Enable Player ESP", true);
   private static final HashMap<String, Color> colorMap = new HashMap();

   public ESP() {
      super("ESP", "Allow you see Mob through the wall", -1, Module.Category.RENDER);
      this.addSettings(new Setting[]{mode, this.useGuardian, this.useSilverfish, this.useGolem, this.usePlayer});
   }

   @Override
   public void onEnable() {
      if (this.useGuardian.isEnabled()) {
         colorMap.put(ElderGuardianEntity.class.getSimpleName(), new Color(0, 38, 255, 160));
      }

      if (this.useSilverfish.isEnabled()) {
         colorMap.put(SilverfishEntity.class.getSimpleName(), new Color(76, 255, 0, 160));
      }

      if (this.useGolem.isEnabled()) {
         colorMap.put(IronGolemEntity.class.getSimpleName(), new Color(255, 0, 0, 160));
      }

      if (this.usePlayer.isEnabled()) {
         colorMap.put(OtherClientPlayerEntity.class.getSimpleName(), new Color(0, 255, 255, 160));
      }

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
            if ((!(entity instanceof OtherClientPlayerEntity player) || !EntityUtil.isNPC(player)) && colorMap.containsKey(entity.getClass().getSimpleName())) {
               if (mode.getMode().equals("3D")) {
                  RenderUtil.draw3DBox(
                     RenderUtil.smoothen(entity, entity.getBoundingBox(), 0.0F),
                     getColor(entity),
                     (MatrixStack)Objects.requireNonNull(Shimmer.matrixStack),
                     event.storage.getEntityVertexConsumers()
                  );
               } else if (mode.getMode().equals("2D")) {
                  if (entity instanceof PlayerEntity) {
                     RenderUtil.draw2DOutlinePlayer(entity, getColor(entity), Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                  } else {
                     RenderUtil.draw3DBox(
                        RenderUtil.smoothen(entity, entity.getBoundingBox(), 0.0F),
                        getColor(entity),
                        (MatrixStack)Objects.requireNonNull(Shimmer.matrixStack),
                        event.storage.getEntityVertexConsumers()
                     );
                  }
               } else if (mode.getMode().equals("Line")) {
                  if (entity instanceof PlayerEntity) {
                     RenderUtil.draw2DOutlinePlayer(entity, getColor(entity), Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
                     RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), getColor(entity), event.storage.getEntityVertexConsumers());
                  } else {
                     RenderUtil.draw3DBox(
                        RenderUtil.smoothen(entity, entity.getBoundingBox(), 0.0F),
                        getColor(entity),
                        (MatrixStack)Objects.requireNonNull(Shimmer.matrixStack),
                        event.storage.getEntityVertexConsumers()
                     );
                     RenderUtil.drawTracer(entity.getBoundingBox().getCenter(), getColor(entity), event.storage.getEntityVertexConsumers());
                  }
               }
            }
         }
      }
   }
}
