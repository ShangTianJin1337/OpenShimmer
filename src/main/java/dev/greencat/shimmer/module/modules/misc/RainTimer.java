package dev.greencat.shimmer.module.modules.misc;

import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RainTimer extends Module {
   private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
   private static final int cooldown = 2400;
   private static final int duration = 1200;
   private static final int thunderstormInterval = 3;
   private static String displayTimeLeft = secsToTime(0L);
   private static String displayNextRain = secsToTime(0L);
   private static boolean netxIsStormCache;
   private static boolean thunderStormNowCache;
   private static boolean rainNowCache;

   public RainTimer() {
      super("RainTimer", "The timer for spider den rain", -1, Module.Category.MISC);
   }

   private static String secsToTime(long seconds) {
      return sdf.format(new Date(seconds * 1000L)).substring(14, 19);
   }

   public static Text getStringForRenderAndUpdate() {
      Text displayText = null;
      MutableText var14;
      if (netxIsStormCache) {
         var14 = Text.literal("下一场雷雨").append(Text.literal(": " + displayNextRain + "⚡")).fillStyle(Style.EMPTY.withColor(Formatting.GOLD));
      } else if (thunderStormNowCache) {
         var14 = Text.literal("这场雨持续时长").append(Text.literal(": " + displayTimeLeft + "⚡")).fillStyle(Style.EMPTY.withColor(Formatting.GOLD));
      } else if (rainNowCache) {
         var14 = Text.literal("这场雨持续时长").append(Text.literal(": " + displayTimeLeft)).fillStyle(Style.EMPTY.withColor(Formatting.AQUA));
      } else {
         var14 = Text.literal("下一场雨").append(Text.literal(": " + displayNextRain));
      }

      long timestamp = (long)Math.floor((double)System.currentTimeMillis() / 1000.0);
      long skyblockAge = timestamp - 1560275700L;
      long thunderstorm = skyblockAge % 10800L;
      long rain = skyblockAge % 3600L;
      boolean rainNow = false;
      if (2400L <= rain) {
         rainNow = true;
         long timeLeft = 3600L - rain;
         displayTimeLeft = secsToTime(timeLeft);
         displayNextRain = secsToTime(timeLeft + 2400L);
      } else {
         rainNow = false;
         displayNextRain = secsToTime(2400L - rain);
      }

      boolean thunderStormNow = false;
      boolean netxIsStorm = false;
      if (2400L <= thunderstorm && thunderstorm < 3600L) {
         thunderStormNow = true;
         long timeLeft = 3600L - rain;
         displayTimeLeft = secsToTime(timeLeft);
      } else {
         thunderStormNow = false;
         long nextThunderstorm = 0L;
         if (thunderstorm < 2400L) {
            nextThunderstorm = 2400L - thunderstorm;
         } else if (3600L <= thunderstorm) {
            nextThunderstorm = 10800L - thunderstorm + 2400L;
         }

         if (nextThunderstorm == 2400L - rain) {
            netxIsStorm = true;
            displayNextRain = secsToTime(nextThunderstorm);
         } else {
            netxIsStorm = false;
         }
      }

      thunderStormNowCache = thunderStormNow;
      rainNowCache = rainNow;
      netxIsStormCache = netxIsStorm;
      return var14;
   }
}
