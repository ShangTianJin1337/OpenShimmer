package dev.greencat.shimmer.setting;

import dev.greencat.shimmer.module.Module;
import java.util.ArrayList;

public class SettingManager {
   private final ArrayList<Setting> settings = new ArrayList();

   public ArrayList<Setting> getSettings() {
      return this.settings;
   }

   public Setting getSettingsByName(Module module, String name) {
      for (Setting setting : module.settings) {
         if (setting.name.equalsIgnoreCase(name)) {
            return setting;
         }
      }

      return null;
   }

   public ArrayList<Setting> getSettingsByModule(Module module) {
      return new ArrayList(module.settings);
   }
}
