package dev.greencat.shimmer.setting.settings;

import dev.greencat.shimmer.setting.Setting;

public class KeybindSetting extends Setting {
   public int code;

   public KeybindSetting(int keyCode) {
      super("KeyBind", "Sets a keybind for the module.");
      this.code = keyCode;
   }

   public int getKeyCode() {
      return this.code;
   }

   public void setKeyCode(int keyCode) {
      this.code = keyCode;
   }
}
