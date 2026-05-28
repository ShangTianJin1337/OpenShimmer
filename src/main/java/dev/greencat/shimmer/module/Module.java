package dev.greencat.shimmer.module;

import dev.greencat.core.type.settings.SettingBase;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.module.modules.render.Hud;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.KeybindSetting;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class Module {
   public static final MinecraftClient mc = MinecraftClient.getInstance();
   public String name;
   public String description;
   public KeybindSetting keyCode = new KeybindSetting(0);
   public Module.Category category;
   public boolean enabled;
   public boolean needDisable = false;
   public List<Setting> settings = new ArrayList();

   public Module(String name, String description, int key, Module.Category category) {
      this(name, description, key, category, false);
   }

   public Module(String name, String description, int key, Module.Category category, boolean useServerRotation) {
      this.name = name;
      this.description = description;
      this.keyCode.code = key;
      this.category = category;
      if (useServerRotation) {
         ServerRotation.registerServerRotationModule(name);
      }

      this.addSettings(this.keyCode);
   }

   public void addSettings(Setting... settings) {
      this.settings.addAll(Arrays.asList(settings));
      this.settings.sort(Comparator.comparingInt(s -> s == this.keyCode ? 1 : 0));
   }

   public void onEnable() {
      Shimmer.getInstance().getEventBus().register(this);
      Shimmer.getInstance().getModuleManager().refreshEnabled();
      Text title = Text.literal("Module Manager");
      Text content = Text.literal(this.getName());
      Text end = Text.literal(Formatting.GREEN + "Enabled");
      Hud.onMessage(title, content, end);
   }

   public void onDisable() {
      Shimmer.getInstance().getEventBus().unregister(this);
      Shimmer.getInstance().getModuleManager().refreshEnabled();
      Text title = Text.literal("Module Manager");
      Text content = Text.literal(this.getName());
      Text end = Text.literal(Formatting.RED + "Disabled");
      Hud.onMessage(title, content, end);
   }

   public void toggle() {
      this.enabled = !this.enabled;
      if (this.enabled) {
         this.onEnable();
      } else {
         this.onDisable();
      }

      if (Shimmer.getInstance().moduleEnabledMap.get(this.name) != null) {
         Shimmer.getInstance().factory.setValue((SettingBase)Shimmer.getInstance().moduleEnabledMap.get(this.name), this.enabled);
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      if (this.enabled != enabled) {
         this.toggle();
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public Module.Category getCategory() {
      return this.category;
   }

   public int getKey() {
      return this.keyCode.code;
   }

   public void setKey(int key) {
      this.keyCode.code = key;
   }

   public static enum Category {
      COMBAT,
      MOVEMENT,
      RENDER,
      MACRO,
      PLAYER,
      MISC;
   }
}
