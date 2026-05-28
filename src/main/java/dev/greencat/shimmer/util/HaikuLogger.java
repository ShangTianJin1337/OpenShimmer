package dev.greencat.shimmer.util;

import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HaikuLogger {
   public static final Logger logger = LogManager.getFormatterLogger("haiku");
   public static int INFO_COLOR = Formatting.LIGHT_PURPLE.getColorValue();
   public static int WARN_COLOR = Formatting.YELLOW.getColorValue();
   public static int ERROR_COLOR = Formatting.RED.getColorValue();

   public static void info(String s) {
      info(Text.literal(s));
   }

   public static void info(Text t) {
      try {
         if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(getText().append(((MutableText)t).styled(s -> s.withColor(Color.CYAN.getRGB()))), false);
         }
      } catch (Exception var2) {
         logger.log(Level.INFO, t.getString());
      }
   }

   public static void auctionTracker(Text auction) {
      if (MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance().player.sendMessage(Text.literal(Formatting.GOLD + "AuctionTracker > ").append(auction), false);
      }
   }

   public static void ircOnlinePlayerNumber(int i) {
      if (MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.GOLD + "Shimmer > " + Formatting.WHITE + "当前在线人数: " + i), false);
      }
   }

   public static void ircChatSplitter() {
      if (MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance()
            .player
            .sendMessage(Text.of(Formatting.GOLD + "Shimmer > " + Formatting.WHITE + "-------------------------------"), false);
      }
   }

   public static void ircSystemMessage(String message) {
      if (MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.GOLD + "Shimmer > " + Formatting.WHITE + message), false);
      }
   }

   public static void irc(String username, String message) {
      if (MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance()
            .player
            .sendMessage(Text.of(Formatting.GOLD + "Shimmer > " + Formatting.AQUA + username + Formatting.WHITE + ": " + message), false);
      }
   }

   public static void ircJoin(String username) {
      if (MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance()
            .player
            .sendMessage(Text.of(Formatting.GOLD + "Shimmer > " + Formatting.AQUA + username + Formatting.WHITE + "加入了频道."), false);
      }
   }

   public static void ircLeave(String username) {
      if (MinecraftClient.getInstance().player != null) {
         MinecraftClient.getInstance()
            .player
            .sendMessage(Text.of(Formatting.GOLD + "Shimmer > " + Formatting.AQUA + username + Formatting.WHITE + "离开了频道."), false);
      }
   }

   public static void warn(String s) {
      warn(Text.literal(s));
   }

   public static void warn(Text t) {
      try {
         MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(getText().append(((MutableText)t).styled(s -> s.withColor(WARN_COLOR))));
      } catch (Exception var2) {
         logger.log(Level.WARN, t.getString());
      }
   }

   public static void error(String s) {
      error(Text.literal(s));
   }

   public static void error(Text t) {
      try {
         MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(getText().append(((MutableText)t).styled(s -> s.withColor(ERROR_COLOR))));
      } catch (Exception var2) {
         logger.log(Level.ERROR, t.getString());
      }
   }

   public static void noPrefix(String s) {
      noPrefix(Text.literal(s));
   }

   public static void noPrefix(Text text) {
      try {
         if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(text, false);
         }
      } catch (Exception var2) {
         logger.log(Level.INFO, text.getString());
      }
   }

   private static MutableText getText() {
      return Text.literal(Formatting.AQUA + "[" + Formatting.WHITE + "Shimmer" + Formatting.AQUA + "] -> ");
   }
}
