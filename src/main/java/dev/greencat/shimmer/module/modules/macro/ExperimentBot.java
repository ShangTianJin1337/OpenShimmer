package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.BackgroundDrawnEvent;
import dev.greencat.shimmer.event.events.OnScreenOpenEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

public class ExperimentBot extends Module {
   MinecraftClient mc = MinecraftClient.getInstance();
   private final NumberSetting delay = new NumberSetting("Delay", "Click Delay", 200.0, 0.0, 1000.0, 0.1);
   private final BooleanSetting autoExit = new BooleanSetting("Auto Exit", "Auto exit experiment table when got all superpair clicks", true);
   private ExperimentBot.ExperimentType currentExperiment = ExperimentBot.ExperimentType.NONE;
   private boolean hasAdded = false;
   private int clicks = 0;
   private long lastClickTime = 0L;
   private final List<Entry<Integer, String>> chronomatronOrder = new ArrayList(28);
   private int lastAdded = 0;
   private final HashMap<Integer, Integer> ultrasequencerOrder = new HashMap();

   public ExperimentBot() {
      super("ExperimentsBot", "Auto finish experiment table", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{this.delay, this.autoExit});
   }

   @ShimmerSubscribe
   public void onGuiOpen(OnScreenOpenEvent event) {
      this.currentExperiment = ExperimentBot.ExperimentType.NONE;
      this.hasAdded = false;
      this.chronomatronOrder.clear();
      this.lastAdded = 0;
      this.ultrasequencerOrder.clear();
      if (event.getScreen() instanceof GenericContainerScreen container) {
         String chestName = container.getTitle().getString();
         if (chestName.contains("Chronomatron") && !chestName.contains("Sta")) {
            this.currentExperiment = ExperimentBot.ExperimentType.CHRONOMATRON;
         } else if (chestName.contains("Ultrasequencer") && !chestName.contains("Sta")) {
            this.currentExperiment = ExperimentBot.ExperimentType.ULTRASEQUENCER;
         } else if (chestName.startsWith("Superpairs (")) {
            this.currentExperiment = ExperimentBot.ExperimentType.SUPERPAIRS;
         }
      }
   }

   @ShimmerSubscribe
   public void onGuiDraw(BackgroundDrawnEvent event) {
      if (event.getScreen() instanceof GenericContainerScreen container) {
         DefaultedList<Slot> var6 = ((GenericContainerScreenHandler)container.getScreenHandler()).slots;
         switch (this.currentExperiment) {
            case CHRONOMATRON:
               if (((Slot)var6.get(49)).getStack() != null
                  && ((Slot)var6.get(49)).getStack().getItem() == Item.fromBlock(Blocks.GLOWSTONE)
                  && !((Slot)var6.get(this.lastAdded)).getStack().hasGlint()) {
                  this.hasAdded = false;
                  if (this.chronomatronOrder.size() > 12 && this.autoExit.isEnabled()) {
                     MinecraftClient.getInstance().setScreen(null);
                  }
               }

               if (!this.hasAdded && ((Slot)var6.get(49)).getStack() != null && ((Slot)var6.get(49)).getStack().getItem() == Items.CLOCK) {
                  Optional<Slot> optional = var6.stream().filter(it -> it.id >= 10 && it.id <= 43).filter(item -> item.getStack().hasGlint()).findFirst();
                  if (optional.isPresent()) {
                     Slot slot = (Slot)optional.get();
                     this.chronomatronOrder.add(new SimpleEntry<>(slot.id, slot.getStack().getName().getString()));
                     this.lastAdded = slot.id;
                     this.hasAdded = true;
                     this.clicks = 0;
                  }
               }

               if (this.hasAdded
                  && ((Slot)var6.get(49)).getStack().getItem() == Items.CLOCK
                  && this.chronomatronOrder.size() > this.clicks
                  && (double)(System.currentTimeMillis() - this.lastClickTime) > this.delay.getValue()) {
                  this.mc
                     .interactionManager
                     .clickSlot(
                        ((GenericContainerScreenHandler)container.getScreenHandler()).syncId,
                        (Integer)((Entry)this.chronomatronOrder.get(this.clicks)).getKey(),
                        0,
                        SlotActionType.CLONE,
                        this.mc.player
                     );
                  this.lastClickTime = System.currentTimeMillis();
                  this.clicks++;
               }
               break;
            case ULTRASEQUENCER:
               if (((Slot)var6.get(49)).getStack().getItem() == Items.CLOCK) {
                  this.hasAdded = false;
               }

               if (!this.hasAdded && ((Slot)var6.get(49)).getStack().getItem() == Item.fromBlock(Blocks.GLOWSTONE)) {
                  if (!((Slot)var6.get(44)).hasStack()) {
                     return;
                  }

                  this.ultrasequencerOrder.clear();
                  var6.stream().filter(it -> it.id >= 9 && it.id <= 44).forEach(this::setUltraSequencerOrder);
                  this.hasAdded = true;
                  this.clicks = 0;
                  if (this.ultrasequencerOrder.size() > 10 && this.autoExit.isEnabled()) {
                     MinecraftClient.getInstance().setScreen(null);
                  }
               }

               if (((Slot)var6.get(49)).getStack() != null
                  && ((Slot)var6.get(49)).getStack().getItem() == Items.CLOCK
                  && this.ultrasequencerOrder.containsKey(this.clicks)
                  && (double)(System.currentTimeMillis() - this.lastClickTime) > this.delay.getValue()) {
                  Integer slot = (Integer)this.ultrasequencerOrder.get(this.clicks);
                  if (slot != null) {
                     this.mc
                        .interactionManager
                        .clickSlot(((GenericContainerScreenHandler)container.getScreenHandler()).syncId, slot, 0, SlotActionType.CLONE, this.mc.player);
                  }

                  this.lastClickTime = System.currentTimeMillis();
                  this.clicks++;
               }
         }
      }
   }

   public void setUltraSequencerOrder(Slot slot) {
      if (slot.getStack() != null
         && (slot.getStack().getItem() instanceof DyeItem || slot.getStack().getItem() == Items.LAPIS_LAZULI || slot.getStack().getItem() == Items.BONE_MEAL)) {
         this.ultrasequencerOrder.put(slot.getStack().getCount() - 1, slot.id);
      }
   }

   static enum ExperimentType {
      CHRONOMATRON,
      ULTRASEQUENCER,
      SUPERPAIRS,
      NONE;
   }
}
