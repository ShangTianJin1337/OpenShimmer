package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class SealAutoStorage extends Module {
   private int backpackIndex = 1;
   private int status = 0;
   private int timer = 0;
   private boolean allBackpacksAttempted = false;
   private final List<String> targetItems = Arrays.asList("Wet Book", "Bouncy Beach Ball", "Moby-Duck");

   public SealAutoStorage() {
      super("SealAutoStorage", "背包满时自动存入特定物品", -1, Module.Category.MACRO);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetMacro();
   }

   private void resetMacro() {
      this.backpackIndex = 1;
      this.status = 0;
      this.timer = 0;
      this.allBackpacksAttempted = false;
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.world != null) {
         boolean isInvFull = mc.player.getInventory().getEmptySlot() == -1;
         if (!isInvFull) {
            this.allBackpacksAttempted = false;
            if (this.status != 0 && !this.hasTargetItems(mc)) {
               this.status = 0;
               if (mc.currentScreen instanceof GenericContainerScreen) {
                  mc.player.closeHandledScreen();
               }
            }
         }

         switch (this.status) {
            case 0:
               if (isInvFull && !this.allBackpacksAttempted && this.hasTargetItems(mc)) {
                  this.status = 1;
                  this.timer = 0;
                  this.backpackIndex = 1;
               }
               break;
            case 1:
               if (this.timer < 20) {
                  this.timer++;
               } else {
                  mc.player.networkHandler.sendChatCommand("backpack " + this.backpackIndex);
                  this.status = 2;
                  this.timer = 0;
               }
               break;
            case 2:
               if (!(mc.currentScreen instanceof GenericContainerScreen containerScreen)) {
                  if (this.timer > 100) {
                     this.status = 0;
                  }

                  this.timer++;
                  return;
               }

               if (this.timer < 10) {
                  this.timer++;
                  return;
               }

               GenericContainerScreenHandler handler = (GenericContainerScreenHandler)containerScreen.getScreenHandler();
               int containerSize = handler.getInventory().size();
               boolean movedAny = false;
               boolean currentBackpackFull = false;

               for (int i = containerSize; i < handler.slots.size(); i++) {
                  Slot slot = handler.getSlot(i);
                  ItemStack stack = slot.getStack();
                  if (!stack.isEmpty() && this.isTargetItem(stack)) {
                     int countBefore = stack.getCount();
                     mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                     if (handler.getSlot(i).getStack().getCount() == countBefore) {
                        currentBackpackFull = true;
                     } else {
                        movedAny = true;
                        this.timer = 3;
                     }
                     break;
                  }
               }

               if (currentBackpackFull) {
                  this.switchToNextBackpack(mc);
               } else if (!movedAny) {
                  mc.player.closeHandledScreen();
                  this.status = 0;
               }
         }
      }
   }

   private void switchToNextBackpack(MinecraftClient mc) {
      mc.player.closeHandledScreen();
      if (this.backpackIndex >= 18) {
         this.allBackpacksAttempted = true;
         this.status = 0;
         mc.player.sendMessage(Text.literal("§6[SealAutoStorage] 背包已满且目标物品无法存入，停止尝试。"), false);
      } else {
         this.backpackIndex++;
         this.status = 1;
         this.timer = 0;
      }
   }

   private boolean isTargetItem(ItemStack stack) {
      String name = stack.getName().getString();

      for (String target : this.targetItems) {
         if (name.contains(target)) {
            return true;
         }
      }

      return false;
   }

   private boolean hasTargetItems(MinecraftClient mc) {
      for (int i = 0; i < mc.player.getInventory().size(); i++) {
         ItemStack stack = mc.player.getInventory().getStack(i);
         if (!stack.isEmpty() && this.isTargetItem(stack)) {
            return true;
         }
      }

      return false;
   }
}
