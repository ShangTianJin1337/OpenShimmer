package dev.greencat.shimmer.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;

public class VanillaKeyBind {
   public KeyBinding useGhostBlock = KeyBindingHelper.registerKeyBinding(
      new KeyBinding("无镐子GhostBlock", Type.KEYSYM, 71, new Category(Identifier.of("shimmer")))
   );
   public KeyBinding instantSwitch = KeyBindingHelper.registerKeyBinding(
      new KeyBinding("InstantSwitch", Type.KEYSYM, 70, new Category(Identifier.of("shimmer")))
   );
}
