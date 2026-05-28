package dev.greencat.shimmer.util.world;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LocationUtils {
   public static final ObjectArrayList<String> STRING_SCOREBOARD = new ObjectArrayList();
   public static final ObjectArrayList<Text> TEXT_SCOREBOARD = new ObjectArrayList();
   public static final CopyOnWriteArrayList<String> sideBar = new CopyOnWriteArrayList();
   public static String sideBarString = "";

   public static void update() {
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      updateScoreboard(minecraftClient);
      updatePlayerPresenceFromScoreboard(minecraftClient);
   }

   public static void updatePlayerPresenceFromScoreboard(MinecraftClient client) {
      List<String> sidebar = STRING_SCOREBOARD;
      FabricLoader fabricLoader = FabricLoader.getInstance();
      if (!sidebar.isEmpty() || fabricLoader.isDevelopmentEnvironment()) {
         String string = sidebar.toString();
         if (!sidebar.isEmpty()) {
            sideBar.clear();
            sideBar.addAll(sidebar);
            sideBarString = string;
         }
      }
   }

   private static void updateScoreboard(MinecraftClient client) {
      try {
         TEXT_SCOREBOARD.clear();
         STRING_SCOREBOARD.clear();
         ClientPlayerEntity player = client.player;
         if (player == null || MinecraftClient.getInstance().world == null) {
            return;
         }

         Scoreboard scoreboard = MinecraftClient.getInstance().world.getScoreboard();
         ScoreboardObjective objective = scoreboard.getObjectiveForSlot((ScoreboardDisplaySlot)ScoreboardDisplaySlot.FROM_ID.apply(1));
         ObjectArrayList<Text> textLines = new ObjectArrayList();
         ObjectArrayList<String> stringLines = new ObjectArrayList();

         for (ScoreHolder scoreHolder : scoreboard.getKnownScoreHolders()) {
            if (scoreboard.getScoreHolderObjectives(scoreHolder).containsKey(objective)) {
               Team team = scoreboard.getScoreHolderTeam(scoreHolder.getNameForScoreboard());
               if (team != null) {
                  Text textLine = Text.empty().append(team.getPrefix().copy()).append(team.getSuffix().copy());
                  String strLine = team.getPrefix().getString() + team.getSuffix().getString();
                  if (!strLine.trim().isEmpty()) {
                     String formatted = Formatting.strip(strLine);
                     textLines.add(textLine);
                     stringLines.add(formatted);
                  }
               }
            }
         }

         if (objective != null) {
            stringLines.add(objective.getDisplayName().getString());
            textLines.add(Text.empty().append(objective.getDisplayName().copy()));
            Collections.reverse(stringLines);
            Collections.reverse(textLines);
         }

         TEXT_SCOREBOARD.addAll(textLines);
         STRING_SCOREBOARD.addAll(stringLines);
      } catch (NullPointerException var12) {
      }
   }
}
