package dev.greencat.shimmer.module.modules.macro;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.greencat.shimmer.event.events.PacketEvent;
import dev.greencat.shimmer.event.events.PacketEvent.Type;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class HarpBot extends Module {
   private final NumberSetting delay = new NumberSetting("Delay", "Click Delay", 200.0, 0.0, 1000.0, 0.1);
   private static final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Shimmer_Threads-%d").build());

   public HarpBot() {
      super("HarpBot", "Do harp automatically", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{this.delay});
   }

   @ShimmerSubscribe
   public void invoke(PacketEvent event) {
      if (event.getType() == PacketEvent.Type.RECEIVE) {
         if (!(MinecraftClient.getInstance().currentScreen instanceof GenericContainerScreen containerScreen)) {
            return;
         }

         if (!containerScreen.getTitle().getString().contains("Harp")) {
            return;
         }

         if (event.getPacket().getPacketType().id().getPath().contains("container_set_slot")) {
            ScreenHandlerSlotUpdateS2CPacket packetSetSlot = (ScreenHandlerSlotUpdateS2CPacket)event.getPacket();
            ItemStack itemStack = packetSetSlot.getStack();
            if (itemStack != null) {
               int slotNumber = packetSetSlot.getSlot();
               if (slotNumber > 26
                  && slotNumber < 36
                  && itemStack.getItem() instanceof BlockItem
                  && ((BlockItem)itemStack.getItem()).getBlock().getTranslationKey().contains("wool")) {
                  executorService.submit(
                     () -> {
                        try {
                           Thread.sleep(50L);
                           Thread.sleep((long)((int)this.delay.getValue()));
                        } catch (InterruptedException var4x) {
                           throw new RuntimeException(var4x);
                        }

                        mc.interactionManager
                           .clickSlot(
                              ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotNumber + 9, 0, SlotActionType.CLONE, mc.player
                           );
                     }
                  );
               }

               if (slotNumber > 35
                  && slotNumber < 44
                  && itemStack.getItem() instanceof BlockItem
                  && ((BlockItem)itemStack.getItem()).getBlock().getTranslationKey().contains("quartz")
                  && ((BlockItem)((GenericContainerScreenHandler)containerScreen.getScreenHandler()).getSlot(slotNumber - 18).getStack().getItem())
                     .getBlock()
                     .getTranslationKey()
                     .contains("wool")) {
                  executorService.submit(
                     () -> {
                        try {
                           Thread.sleep(50L);
                           Thread.sleep((long)((int)this.delay.getValue()));
                           Thread.sleep(50L);
                        } catch (InterruptedException var4x) {
                           throw new RuntimeException(var4x);
                        }

                        mc.interactionManager
                           .clickSlot(
                              ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotNumber, 0, SlotActionType.CLONE, mc.player
                           );
                     }
                  );
               }
            }
         }
      }
   }
}
