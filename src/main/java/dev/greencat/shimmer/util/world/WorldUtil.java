package dev.greencat.shimmer.util.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;

public class WorldUtil {
   public static boolean isOnSkyBlock() {
      if (MinecraftClient.getInstance().world == null) {
         return false;
      } else if (MinecraftClient.getInstance().world.getScoreboard().getObjectiveForSlot((ScoreboardDisplaySlot)ScoreboardDisplaySlot.FROM_ID.apply(0)) == null
         && MinecraftClient.getInstance().world.getScoreboard().getObjectiveForSlot((ScoreboardDisplaySlot)ScoreboardDisplaySlot.FROM_ID.apply(1)) == null) {
         return false;
      } else {
         return MinecraftClient.getInstance().world.getScoreboard().getObjectiveForSlot((ScoreboardDisplaySlot)ScoreboardDisplaySlot.FROM_ID.apply(0)) != null
            ? MinecraftClient.getInstance()
               .world
               .getScoreboard()
               .getObjectiveForSlot((ScoreboardDisplaySlot)ScoreboardDisplaySlot.FROM_ID.apply(0))
               .getDisplayName()
               .getString()
               .contains("SKYBLOCK")
            : MinecraftClient.getInstance()
               .world
               .getScoreboard()
               .getObjectiveForSlot((ScoreboardDisplaySlot)ScoreboardDisplaySlot.FROM_ID.apply(1))
               .getDisplayName()
               .getString()
               .contains("SKYBLOCK");
      }
   }
}
