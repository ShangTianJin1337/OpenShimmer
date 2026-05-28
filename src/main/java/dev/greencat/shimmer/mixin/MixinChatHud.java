package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.module.modules.misc.Cat;
import dev.greencat.shimmer.util.RankingOverrider;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({ChatHud.class})
public class MixinChatHud {
   @ModifyArg(
      method = {"addMessage(Lnet/minecraft/text/Text;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"
      ),
      index = 0
   )
   public Text addMessage(Text message) {
      Text text = message;
      if (Cat.rankingOverride.isEnabled() && message.getString().contains("] ") && message.getString().split("] ")[0].length() <= 6) {
         text = message.copy();
         String level = text.getString().split("] ")[0].replace("[", "");
         String newLevel = RankingOverrider.override(level);
         Text newLevelText = Text.literal(newLevel + " ");
         if (text.getSiblings().size() >= 3 && !newLevel.contains("null")) {
            Style color = ((Text)text.getSiblings().get(1)).getStyle();
            text.getSiblings().set(1, (Text)newLevelText.getWithStyle(color).getFirst());
            text.getSiblings().removeFirst();
            text.getSiblings().remove(1);
         }
      }

      return text;
   }
}
