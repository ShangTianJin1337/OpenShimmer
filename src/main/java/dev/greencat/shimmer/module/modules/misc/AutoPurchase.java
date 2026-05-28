package dev.greencat.shimmer.module.modules.misc;

import dev.greencat.shimmer.event.events.PacketEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.world.LocationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class AutoPurchase extends Module {
   public AutoPurchase() {
      super("AutoPurchase", "Auto buy the crystal hollow pass", -1, Module.Category.MISC);
   }

   @ShimmerSubscribe
   public void onMessage(PacketEvent event) {
      if (event.getPacket() instanceof GameMessageS2CPacket packet
         && packet.content().getString().contains("purchase")
         && MinecraftClient.getInstance().player != null
         && (
            LocationUtils.sideBarString.toLowerCase().contains("jungle")
               || LocationUtils.sideBarString.toLowerCase().contains("goblin")
               || LocationUtils.sideBarString.toLowerCase().contains("mithril")
               || LocationUtils.sideBarString.toLowerCase().contains("precursor")
               || LocationUtils.sideBarString.toLowerCase().contains("crystal")
         )) {
         MinecraftClient.getInstance().player.networkHandler.sendChatMessage("/purchasecrystallhollowspass");
      }
   }
}
