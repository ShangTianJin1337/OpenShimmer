package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.KeyEvent;
import dev.greencat.shimmer.event.events.KeyEvent.Status;
import dev.greencat.shimmer.module.Module;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Keyboard.class})
public class KeyboardMixin {
   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(Lnet/minecraft/client/util/Window;I)Z",
         ordinal = 2
      )},
      method = {"onKey"},
      cancellable = true
   )
   private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
      if (Shimmer.mc.currentScreen == null) {
         if (action == 2) {
            action = 1;
         }

         if (input.key() != -1) {
            switch (action) {
               case 0:
                  KeyEvent eventx = new KeyEvent(input.key(), input.scancode(), KeyEvent.Status.RELEASED);
                  Shimmer.getInstance().getEventBus().post(eventx);
                  if (eventx.isCancelled()) {
                     ci.cancel();
                  }
                  break;
               case 1:
                  KeyEvent event = new KeyEvent(input.key(), input.scancode(), KeyEvent.Status.PRESSED);
                  Shimmer.getInstance().getModuleManager().getModules().stream().filter(m -> m.getKey() == input.key()).forEach(Module::toggle);
                  Shimmer.getInstance().getEventBus().post(event);
                  if (event.isCancelled()) {
                     ci.cancel();
                  }
            }
         }
      }
   }
}
