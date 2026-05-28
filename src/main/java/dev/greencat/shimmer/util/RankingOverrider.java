package dev.greencat.shimmer.util;

import dev.greencat.shimmer.module.modules.misc.Cat;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class RankingOverrider {
   private static Properties ranking = new Properties();

   public RankingOverrider() {
      InputStream rankingStream = RankingOverrider.class.getResourceAsStream("/assets/shimmer/ranking.properties");

      try {
         ranking.load(new InputStreamReader(rankingStream, StandardCharsets.UTF_8));
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }

   public static String override(String text) {
      String newLevel = ranking.getProperty(text);
      if (Cat.rankingOverrideOriginal.isEnabled()) {
         newLevel = newLevel + "(" + text + ")";
      }

      return newLevel == null ? text : newLevel;
   }
}
