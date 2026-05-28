package dev.greencat.shimmer.module.modules.misc;

import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.StringSetting;

public class ChatSuffix extends Module {
   public static final StringSetting suffix = new StringSetting("Suffix", "The string which will added to you chat message", "喵~");

   public ChatSuffix() {
      super("ChatSuffix", "Add a suffix when you chat", -1, Module.Category.MISC);
      this.addSettings(new Setting[]{suffix});
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }
}
