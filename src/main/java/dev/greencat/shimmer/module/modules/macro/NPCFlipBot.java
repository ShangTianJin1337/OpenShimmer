package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.setting.settings.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class NPCFlipBot extends Module {
   private final String BZ = "bz";
   private final String SBMENU = "sbmenu";
   private final NumberSetting delay = new NumberSetting("Delay", "Click Delay", 300.0, 0.0, 1000.0, 0.1);
   private final NumberSetting claimDelay = new NumberSetting("Claim Delay", "How long will wait when u claimed order", 500.0, 0.0, 1000.0, 0.1);
   private final StringSetting name = new StringSetting("Name", "ItemName", "Gemstone");
   private NPCFlipBot.State state = NPCFlipBot.State.IDLE;
   private long lastFinish = 0L;
   private boolean locked = false;
   private long lastWait = 0L;

   public NPCFlipBot() {
      super("NPCFlipBot", "Auto Flip to NPC", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{this.delay, this.claimDelay, this.name});
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.locked = false;
      this.state = NPCFlipBot.State.IDLE;
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         Screen currentScreen = MinecraftClient.getInstance().currentScreen;
         if (currentScreen instanceof GenericContainerScreen || currentScreen == null) {
            GenericContainerScreen containerScreen = (GenericContainerScreen)currentScreen;
            if (this.state == NPCFlipBot.State.IDLE && System.currentTimeMillis() - this.lastFinish >= 200L) {
               MinecraftClient.getInstance().player.networkHandler.sendChatCommand("bz");
               this.state = NPCFlipBot.State.OPEN_BZ;
            }

            if (this.state == NPCFlipBot.State.OPEN_BZ && containerScreen.getTitle().getString().contains("Bazaar")) {
               for (Slot slot : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                  if (slot.inventory != MinecraftClient.getInstance().player.getInventory()
                     && slot.getStack() != null
                     && slot.getStack().getItem() != null
                     && slot.getStack().getItem() == Items.BOOK) {
                     mc.interactionManager
                        .clickSlot(((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slot.id, 0, SlotActionType.CLONE, mc.player);
                     this.state = NPCFlipBot.State.OPENED_ORDER;
                     break;
                  }
               }
            }

            if (this.state == NPCFlipBot.State.OPENED_ORDER && containerScreen.getTitle().getString().contains("Your")) {
               for (Slot slotx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                  if (slotx.inventory != MinecraftClient.getInstance().player.getInventory()
                     && slotx.getStack() != null
                     && slotx.getStack().getItem() != null
                     && slotx.getStack().getName() != null
                     && slotx.getStack().getName().toString().contains(this.name.getString())) {
                     mc.interactionManager
                        .clickSlot(((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotx.id, 0, SlotActionType.CLONE, mc.player);
                     this.lastWait = System.currentTimeMillis();
                     this.state = NPCFlipBot.State.WAITING;
                     break;
                  }
               }
            }

            if ((double)(System.currentTimeMillis() - this.lastWait) >= this.claimDelay.getValue() && this.state == NPCFlipBot.State.WAITING) {
               MinecraftClient.getInstance().player.networkHandler.sendChatCommand("sbmenu");
               this.state = NPCFlipBot.State.CLICKED_ITEM;
            }

            if (this.state == NPCFlipBot.State.CLICKED_ITEM && containerScreen.getTitle().getString().contains("Menu")) {
               for (Slot slotxx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                  if (slotxx.inventory != MinecraftClient.getInstance().player.getInventory()
                     && slotxx.getStack() != null
                     && slotxx.getStack().getItem() != null
                     && slotxx.getStack().getItem() == Items.COOKIE) {
                     mc.interactionManager
                        .clickSlot(((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotxx.id, 0, SlotActionType.CLONE, mc.player);
                     this.state = NPCFlipBot.State.OPENED_COOKIE;
                     break;
                  }
               }
            }

            if (this.state == NPCFlipBot.State.OPENED_COOKIE && !this.locked && containerScreen.getTitle().getString().contains("Cookie")) {
               new Thread(
                     () -> {
                        this.locked = true;

                        for (Slot slotxxx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                           if (slotxxx.inventory == MinecraftClient.getInstance().player.getInventory()
                              && slotxxx.getStack() != null
                              && slotxxx.getStack().getItem() != null
                              && slotxxx.getStack().getName() != null
                              && slotxxx.getStack().getName().toString().contains(this.name.getString())) {
                              mc.interactionManager
                                 .clickSlot(
                                    ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotxxx.id, 0, SlotActionType.CLONE, mc.player
                                 );

                              try {
                                 Thread.sleep((long)this.delay.getValue());
                              } catch (InterruptedException var5x) {
                                 throw new RuntimeException(var5x);
                              }
                           }
                        }

                        this.locked = false;
                        this.state = NPCFlipBot.State.SOLD;
                     }
                  )
                  .start();
            }

            if (this.state == NPCFlipBot.State.SOLD) {
               this.lastFinish = System.currentTimeMillis();
               this.state = NPCFlipBot.State.IDLE;
            }
         }
      }
   }

   static enum State {
      IDLE,
      OPEN_BZ,
      OPENED_ORDER,
      CLICKED_ITEM,
      WAITING,
      OPENED_COOKIE,
      SOLD;
   }
}
