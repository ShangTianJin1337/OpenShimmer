package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.module.modules.misc.Cat;
import dev.greencat.shimmer.util.RankingOverrider;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHUD {
   @Shadow
   protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

   @Overwrite
   public Text getPlayerName(PlayerListEntry entry) {
      Text name = entry.getDisplayName();
      if (Cat.rankingOverride.isEnabled() && name != null && name.getString().contains("]") && name.getString().split("]")[0].length() <= 5) {
         name = name.copy();
         String level = name.getString().split("]")[0].replace("[", "");
         String newLevel = RankingOverrider.override(level);
         Text newLevelText = Text.literal(newLevel + "]");
         if (name.getSiblings().size() >= 3 && !newLevel.contains("null")) {
            Style color = name.getSiblings().get(1).getStyle();
            name.getSiblings().set(1, newLevelText.getWithStyle(color).getFirst());
            name.getSiblings().removeFirst();
            name.getSiblings().remove(1);
         }
      }

      return entry.getDisplayName() != null
         ? name
         : this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), Text.literal(entry.getProfile().name())));
   }
}
