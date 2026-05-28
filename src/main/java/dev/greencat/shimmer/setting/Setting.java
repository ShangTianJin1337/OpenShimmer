package dev.greencat.shimmer.setting;

public class Setting {
   public String name;
   public String description;

   public Setting(String name, String description) {
      this.name = name;
      this.description = description;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }
}
