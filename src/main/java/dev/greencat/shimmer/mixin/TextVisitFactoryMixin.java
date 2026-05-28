package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.module.modules.player.NickHider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({TextVisitFactory.class})
public abstract class TextVisitFactoryMixin {
   @ModifyArg(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
         ordinal = 0
      ),
      method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"},
      index = 0
   )
   private static String adjustText(String text) {
      String finalText = text;
      if (Shimmer.getInstance().getModuleManager().isModuleEnabled("NickHider")) {
         finalText = text.replace(MinecraftClient.getInstance().getSession().getUsername(), NickHider.nickname.getString());
      }

      return finalText;
   }
}
