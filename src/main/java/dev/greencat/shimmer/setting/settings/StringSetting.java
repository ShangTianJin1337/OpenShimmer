package dev.greencat.shimmer.setting.settings;

import dev.greencat.shimmer.setting.Setting;

public class StringSetting extends Setting {
   public String str;

   public StringSetting(String name, String description, String defaultValue) {
      super(name, description);
      this.str = defaultValue;
   }

   public String getString() {
      return this.str;
   }

   public void setString(String value) {
      this.str = value;
   }
}
