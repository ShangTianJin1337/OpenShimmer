package dev.greencat.shimmer.module.modules.combat;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;

public class AimAssistant extends Module {
   public static final NumberSetting factor = new NumberSetting("Factor", "The factor of sensitivity", 0.3, 0.01, 0.99, 0.1);
   public static final BooleanSetting onlyPlayer = new BooleanSetting("Only Player", "Only Player", true);
   private boolean currentAimedEntity = false;

   public AimAssistant() {
      super("AimAssistant", "AimAssistant", -1, Module.Category.COMBAT);
      this.addSettings(new Setting[]{factor, onlyPlayer});
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.currentAimedEntity) {
         MinecraftClient.getInstance()
            .options
            .getMouseSensitivity()
            .setValue((Double)MinecraftClient.getInstance().options.getMouseSensitivity().getValue() / factor.getValue());
      }

      this.currentAimedEntity = false;
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (MinecraftClient.getInstance().crosshairTarget != null && MinecraftClient.getInstance().crosshairTarget.getType() == Type.ENTITY) {
            EntityHitResult result = (EntityHitResult)MinecraftClient.getInstance().crosshairTarget;
            if (result.getEntity() instanceof PlayerEntity || !onlyPlayer.isEnabled()) {
               if (!this.currentAimedEntity) {
                  MinecraftClient.getInstance()
                     .options
                     .getMouseSensitivity()
                     .setValue((Double)MinecraftClient.getInstance().options.getMouseSensitivity().getValue() * factor.getValue());
               }

               this.currentAimedEntity = true;
            }
         } else {
            if (this.currentAimedEntity) {
               MinecraftClient.getInstance()
                  .options
                  .getMouseSensitivity()
                  .setValue((Double)MinecraftClient.getInstance().options.getMouseSensitivity().getValue() / factor.getValue());
            }

            this.currentAimedEntity = false;
         }
      } else {
         if (this.currentAimedEntity) {
            MinecraftClient.getInstance()
               .options
               .getMouseSensitivity()
               .setValue((Double)MinecraftClient.getInstance().options.getMouseSensitivity().getValue() / factor.getValue());
         }

         this.currentAimedEntity = false;
      }
   }
}
