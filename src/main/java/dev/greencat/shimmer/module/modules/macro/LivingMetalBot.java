package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.module.modules.render.LivingMetalESP;
import dev.greencat.shimmer.module.modules.render.LivingMetalESP.NewAppearLapis;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;
import dev.greencat.shimmer.util.world.MiningBot.isTargetCallback;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class LivingMetalBot extends Module {
   public isTargetCallback callback = pos -> {
      boolean hasCurrentTarget = false;
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getMainHandStack().getItem() == Items.DIAMOND_PICKAXE) {
         for (LivingMetalESP.NewAppearLapis lapis : LivingMetalESP.newAppearLapis) {
            if (lapis.pos().equals(pos)) {
               hasCurrentTarget = true;
               break;
            }
         }

         return hasCurrentTarget;
      } else {
         return false;
      }
   };

   public LivingMetalBot() {
      super("LivingMetalBot", "Mining LivingMetal Automatically", -1, Module.Category.MACRO, true);
      this.needDisable = true;
   }

   @Override
   public void onEnable() {
      List<Block> list = new ArrayList();
      list.add(Blocks.LAPIS_ORE);
      super.onEnable();
      Shimmer.getInstance().getModuleManager().getModule("LivingMetalESP").setEnabled(true);
      if (!dev.greencat.shimmer.util.world.MiningBot.setup("LivingMetal", list, false, false, false, false, false, false, false, this.callback)) {
         Shimmer.getInstance().getModuleManager().getModule("LivingMetal").setEnabled(false);
      }

      ServerRotation.useServerRotation = true;
      if (dev.greencat.shimmer.util.world.MiningBot.target == null) {
         ServerRotation.useServerRotation = false;
      }
   }

   @Override
   public void onDisable() {
      dev.greencat.shimmer.util.world.MiningBot.release("LivingMetal");
      ServerRotation.useServerRotation = false;
      super.onDisable();
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      dev.greencat.shimmer.util.world.MiningBot.onTickPre();
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      dev.greencat.shimmer.util.world.MiningBot.onRender(event);
   }
}
