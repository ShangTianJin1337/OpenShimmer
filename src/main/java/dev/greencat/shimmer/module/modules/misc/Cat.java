package dev.greencat.shimmer.module.modules.misc;

import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import java.util.Properties;
import java.util.Random;
import net.minecraft.util.math.Vec3d;

public class Cat extends Module {
   public static final Vec3d RENDER_POSITION = new Vec3d(6.0, 72.0, -92.0);
   public static final Vec3d BIG_RENDER_POSITION = new Vec3d(-2.0, 70.0, -96.0);
   public static String TEXTURE = "logo_2.png";
   public static final BooleanSetting rankingOverride = new BooleanSetting("Ranking Override", "Override ranking text", true);
   public static final BooleanSetting rankingOverrideOriginal = new BooleanSetting(
      "Ranking Override Original Level", "Override ranking text with Original Level", true
   );
   public static Properties titles = new Properties();
   public static Random random = new Random();
   public static long lastChange = 0L;

   public Cat() {
      super("Cat", "A cute cat", -1, Module.Category.MISC);
      this.addSettings(new Setting[]{rankingOverride, rankingOverrideOriginal});
   }

   @Override
   public void toggle() {
      this.enabled = true;
   }
}
