package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class AutoTerminal extends Module {
   private static final String START_WITH = "What starts with: ";
   private static final String ORDER = "Click in order!";
   private static final String LIGHT_UP = "Correct all the panes!";
   private static final String COLOR = "Select all the ";
   private static final String SAME_COLOR = "Change all to same color!";
   private static final String MELODY = "Click the button on time";
   private static final Item FRAME_ITEM = Items.BLACK_STAINED_GLASS_PANE;
   private static final Map<String, DyeColor> colorFromName = new HashMap();
   private static final Map<Item, DyeColor> itemColor;
   private static long lastClick = 0L;
   private static int randomDelay = 300;
   private static Random random = new Random();

   public AutoTerminal() {
      super("AutoTerminal", "Auto do F7 Terminal", -1, Module.Category.MACRO);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (System.currentTimeMillis() - lastClick >= (long)randomDelay) {
         Screen currentScreen = MinecraftClient.getInstance().currentScreen;
         if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
            if (currentScreen instanceof GenericContainerScreen containerScreen) {
               String TITLE = containerScreen.getTitle().getString();
               if (TITLE.contains("What starts with: ") && TITLE.length() > TITLE.indexOf("'") + 1) {
                  char itemNeeded = TITLE.charAt(TITLE.indexOf("'") + 1);

                  for (Slot slot : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                     if (slot.getStack().getName().getString().charAt(0) == itemNeeded
                        && !slot.getStack().hasGlint()
                        && slot.inventory != MinecraftClient.getInstance().player.getInventory()
                        && slot.id >= 9
                        && slot.id <= 44
                        && slot.id % 9 != 0
                        && slot.id % 9 != 8) {
                        mc.interactionManager
                           .clickSlot(((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slot.id, 0, SlotActionType.CLONE, mc.player);
                        lastClick = System.currentTimeMillis();
                        refreshDelay();
                        break;
                     }
                  }
               }

               if (TITLE.contains("Click in order!")) {
                  int currentMinimum = 100;
                  int minimumSlotID = -1;

                  for (Slot slotx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                     if (slotx.getStack().getItem() != FRAME_ITEM
                        && slotx.inventory != MinecraftClient.getInstance().player.getInventory()
                        && slotx.getStack().getItem() == Items.RED_STAINED_GLASS_PANE
                        && slotx.getStack().getCount() < currentMinimum) {
                        currentMinimum = slotx.getStack().getCount();
                        minimumSlotID = slotx.id;
                     }
                  }

                  if (minimumSlotID != -1) {
                     mc.interactionManager
                        .clickSlot(
                           ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, minimumSlotID, 0, SlotActionType.CLONE, mc.player
                        );
                     lastClick = System.currentTimeMillis();
                     refreshDelay();
                  }
               }

               if (TITLE.contains("Correct all the panes!")) {
                  for (Slot slotxx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                     if (slotxx.getStack().getItem() != FRAME_ITEM
                        && slotxx.inventory != MinecraftClient.getInstance().player.getInventory()
                        && slotxx.getStack().getItem() == Items.RED_STAINED_GLASS_PANE) {
                        mc.interactionManager
                           .clickSlot(((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotxx.id, 0, SlotActionType.CLONE, mc.player);
                        lastClick = System.currentTimeMillis();
                        refreshDelay();
                        break;
                     }
                  }
               }

               if (TITLE.contains("Select all the ")) {
                  String color = TITLE.split("the ")[1].split(" items")[0];
                  DyeColor targetColor = (DyeColor)colorFromName.get(color);
                  if (targetColor != null) {
                     for (Slot slotxxx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                        if (slotxxx.getStack().getItem() != FRAME_ITEM
                           && slotxxx.inventory != MinecraftClient.getInstance().player.getInventory()
                           && !slotxxx.getStack().hasGlint()
                           && targetColor.equals(itemColor.get(slotxxx.getStack().getItem()))) {
                           mc.interactionManager
                              .clickSlot(
                                 ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotxxx.id, 0, SlotActionType.CLONE, mc.player
                              );
                           lastClick = System.currentTimeMillis();
                           refreshDelay();
                           break;
                        }
                     }
                  }
               }

               if (TITLE.contains("Change all to same color!")) {
                  for (Slot slotxxxx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                     if (slotxxxx.getStack().getItem() != FRAME_ITEM
                        && slotxxxx.inventory != MinecraftClient.getInstance().player.getInventory()
                        && slotxxxx.getStack().getItem() != Items.RED_STAINED_GLASS_PANE) {
                        mc.interactionManager
                           .clickSlot(
                              ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotxxxx.id, 0, SlotActionType.CLONE, mc.player
                           );
                        lastClick = System.currentTimeMillis();
                        refreshDelay();
                        break;
                     }
                  }
               }

               if (TITLE.contains("Click the button on time")) {
                  boolean needClick = false;

                  for (Slot slotxxxxx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                     if (slotxxxxx.getStack().getItem() != FRAME_ITEM
                        && slotxxxxx.inventory != MinecraftClient.getInstance().player.getInventory()
                        && slotxxxxx.getStack().getItem() == Items.LIME_STAINED_GLASS_PANE) {
                        for (int i = slotxxxxx.id; i > 0; i -= 9) {
                           if (((GenericContainerScreenHandler)containerScreen.getScreenHandler()).getSlot(i).getStack().getItem()
                              == Items.PURPLE_STAINED_GLASS_PANE) {
                              needClick = true;
                              break;
                           }
                        }
                     }
                  }

                  if (needClick) {
                     for (Slot slotxxxxxx : ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).slots) {
                        if (slotxxxxxx.getStack().getItem() != FRAME_ITEM
                           && slotxxxxxx.inventory != MinecraftClient.getInstance().player.getInventory()
                           && slotxxxxxx.getStack().getItem() == Items.LIME_TERRACOTTA) {
                           mc.interactionManager
                              .clickSlot(
                                 ((GenericContainerScreenHandler)containerScreen.getScreenHandler()).syncId, slotxxxxxx.id, 0, SlotActionType.CLONE, mc.player
                              );
                           lastClick = System.currentTimeMillis();
                           refreshDelay();
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void refreshDelay() {
      randomDelay = random.nextInt(70, 180);
   }

   static {
      for (DyeColor color : DyeColor.values()) {
         colorFromName.put(color.name().toUpperCase(Locale.ENGLISH), color);
      }

      colorFromName.put("SILVER", DyeColor.LIGHT_GRAY);
      colorFromName.put("LIGHT BLUE", DyeColor.LIGHT_BLUE);
      itemColor = new HashMap();

      for (DyeColor color : DyeColor.values()) {
         for (String item : new String[]{"dye", "wool", "stained_glass", "terracotta"}) {
            itemColor.put((Item)Registries.ITEM.get(Identifier.ofVanilla(color.name().toLowerCase() + "_" + item)), color);
         }
      }

      itemColor.put(Items.BONE_MEAL, DyeColor.WHITE);
      itemColor.put(Items.LAPIS_LAZULI, DyeColor.BLUE);
      itemColor.put(Items.COCOA_BEANS, DyeColor.BROWN);
      itemColor.put(Items.INK_SAC, DyeColor.BLACK);
   }
}
