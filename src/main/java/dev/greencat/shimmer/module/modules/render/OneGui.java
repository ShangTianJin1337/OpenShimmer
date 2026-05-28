package dev.greencat.shimmer.module.modules.render;

import dev.greencat.core.MaterialGui;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import net.minecraft.client.gui.screen.Screen;

public class OneGui extends Module {
   public static BooleanSetting backgroundEffect = new BooleanSetting("Background Effect", "Enable the background effect", true);

   public OneGui() {
      super("OneGui", "One window, no tabs.", 344, Module.Category.RENDER);
      this.addSettings(new Setting[]{backgroundEffect});
   }

   @Override
   public void toggle() {
      this.openGui();
   }

   public void openGui() {
      MaterialGui materialGui = Shimmer.getInstance().factory.buildGUI();
      mc.setScreen((Screen)materialGui);
   }
}
