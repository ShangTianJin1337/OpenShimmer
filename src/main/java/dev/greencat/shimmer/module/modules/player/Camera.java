package dev.greencat.shimmer.module.modules.player;

import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;

public class Camera extends Module {
   public static final BooleanSetting CameraClip = new BooleanSetting("CameraClip", "Allow you camera clip into wall", true);
   public static final NumberSetting Distance = new NumberSetting("Distance", "Modify your camera distance", 4.0, 0.05, 50.0, 0.01);

   public Camera() {
      super("Camera", "Modify Your Camera", -1, Module.Category.PLAYER);
      this.addSettings(new Setting[]{CameraClip, Distance});
   }
}
