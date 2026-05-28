package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.module.modules.render.Animation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({LivingEntity.class})
public class LivingEntityMixin {
   @ModifyConstant(
      method = {"getHandSwingDuration"},
      constant = {@Constant(
         intValue = 6
      )}
   )
   private int getHandSwingDuration(int constant) {
      if ((Object)this != MinecraftClient.getInstance().player) {
         return constant;
      } else {
         return Shimmer.getInstance().getModuleManager().getModule("Animation") != null
               && Shimmer.getInstance().getModuleManager().getModule("Animation").isEnabled()
               && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()
            ? (int)Animation.swingSpeed.getValue()
            : constant;
      }
   }
}
