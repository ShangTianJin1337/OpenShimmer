package dev.greencat.shimmer.setting.settings;

import dev.greencat.shimmer.setting.Setting;

public class BooleanSetting extends Setting {
   public boolean enabled;

   public BooleanSetting(String name, String description, boolean enabled) {
      super(name, description);
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void toggle() {
      this.enabled = !this.enabled;
   }
}
