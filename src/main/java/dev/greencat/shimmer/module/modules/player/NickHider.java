package dev.greencat.shimmer.module.modules.player;

import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.StringSetting;

public class NickHider extends Module {
   public static final StringSetting nickname = new StringSetting("NickName", "Your NickName", "CoolGuy123");

   public NickHider() {
      super("NickHider", "Hide your real name", -1, Module.Category.PLAYER);
      this.addSettings(new Setting[]{nickname});
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }
}
