package dev.greencat.shimmer.setting.settings;

import dev.greencat.shimmer.setting.Setting;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
   public int index;
   public List<String> modes;

   public ModeSetting(String name, String description, String defaultMode, String... modes) {
      super(name, description);
      this.modes = Arrays.asList(modes);
      this.index = Arrays.asList(modes).indexOf(defaultMode);
   }

   public String getMode() {
      return (String)this.modes.get(this.index);
   }

   public void setMode(String mode) {
      this.index = this.modes.indexOf(mode);
   }

   public boolean equals(String mode) {
      return this.index == this.modes.indexOf(mode);
   }

   public List<String> getModes() {
      return this.modes;
   }

   public void cycle() {
      if (this.index < this.modes.size() - 1) {
         this.index++;
      } else {
         this.index = 0;
      }
   }
}
