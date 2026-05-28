package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.ModeSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;
import dev.greencat.shimmer.util.world.MiningBot.isTargetCallback;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;

public class Nuker extends Module {
   public final BooleanSetting randomMoveEnable = new BooleanSetting("Random Move", "Make some move randomly when nuking", true);
   public final BooleanSetting sneakEnable = new BooleanSetting("Sneak", "Make you keep sneak", true);
   public final BooleanSetting upperEnable = new BooleanSetting("Only Upper Block", "Only mine the Block upper than You", false);
   public final BooleanSetting realNuker = new BooleanSetting("Real Nuker", "Use packet sending instead of invoke vanilla mining method", false);
   public final BooleanSetting serverRotation = new BooleanSetting(
      "Server Rotation", "Use server rotation instead of normal rotation (Only available in real nuker)", false
   );
   public final BooleanSetting oneTick = new BooleanSetting("One Tick Mode", "Don't wait when sending packet", false);
   public final NumberSetting timedOutTime = new NumberSetting("TimeOut MillSeconds", "", 20000.0, 0.0, 100000.0, 1.0);
   public isTargetCallback callback = pos -> true;
   public boolean allowReset = true;
   public final ModeSetting mode = new ModeSetting(
      "Nuker Mode",
      "Change your nuker mode",
      "Stone",
      "Stone",
      "Stone With Cobblestone",
      "Mithril",
      "Mithril With Titanium",
      "Gold",
      "Gemstone",
      "Gemstone With Pane",
      "Tungsten",
      "Umber",
      "Glacite",
      "Diamond",
      "Netherrack",
      "Sand",
      "Red Sand",
      "Mycelium",
      "Obsidian",
      "End Stone",
      "Sugar Cane",
      "Pumpkin",
      "Wheat",
      "Melon",
      "Carrot",
      "EnderNode",
      "RedstoneBlock",
      "IronBlock",
      "DiamondBlock",
      "EmeraldBlock",
      "CoalBlock",
      "Fig Log",
      "Mangrove Wood",
      "Caducous Stem",
      "Red Mushroom"
   );

   public Nuker() {
      super("Nuker", "Mining Block Automatically", -1, Module.Category.MACRO, true);
      this.addSettings(
         new Setting[]{
            this.randomMoveEnable, this.sneakEnable, this.upperEnable, this.mode, this.realNuker, this.serverRotation, this.oneTick, this.timedOutTime
         }
      );
      this.needDisable = true;
   }

   @Override
   public void onEnable() {
      List<Block> list = new ArrayList();
      if (this.mode.getMode().equals("Stone")) {
         list.add(Blocks.STONE);
      }

      if (this.mode.getMode().equals("Stone With Cobblestone")) {
         list.add(Blocks.STONE);
         list.add(Blocks.COBBLESTONE);
      }

      if (this.mode.getMode().equals("Mithril")) {
         list.add(Blocks.PRISMARINE);
         list.add(Blocks.PRISMARINE_BRICKS);
         list.add(Blocks.DARK_PRISMARINE);
         list.add(Blocks.LIGHT_BLUE_WOOL);
         list.add(Blocks.GRAY_WOOL);
         list.add(Blocks.GRAY_CONCRETE);
      }

      if (this.mode.getMode().equals("Mithril With Titanium")) {
         list.add(Blocks.PRISMARINE);
         list.add(Blocks.PRISMARINE_BRICKS);
         list.add(Blocks.DARK_PRISMARINE);
         list.add(Blocks.LIGHT_BLUE_WOOL);
         list.add(Blocks.GRAY_WOOL);
         list.add(Blocks.GRAY_CONCRETE);
         list.add(Blocks.POLISHED_DIORITE);
      }

      if (this.mode.getMode().equals("Gold")) {
         list.add(Blocks.GOLD_BLOCK);
      }

      if (this.mode.getMode().equals("Gemstone")) {
         list.add(Blocks.RED_STAINED_GLASS);
         list.add(Blocks.ORANGE_STAINED_GLASS);
         list.add(Blocks.LIGHT_BLUE_STAINED_GLASS);
         list.add(Blocks.LIME_STAINED_GLASS);
         list.add(Blocks.PURPLE_STAINED_GLASS);
         list.add(Blocks.WHITE_STAINED_GLASS);
         list.add(Blocks.YELLOW_STAINED_GLASS);
         list.add(Blocks.MAGENTA_STAINED_GLASS);
         list.add(Blocks.BLACK_STAINED_GLASS);
         list.add(Blocks.BLUE_STAINED_GLASS);
         list.add(Blocks.BROWN_STAINED_GLASS);
         list.add(Blocks.GREEN_STAINED_GLASS);
      }

      if (this.mode.getMode().equals("Gemstone With Pane")) {
         list.add(Blocks.RED_STAINED_GLASS);
         list.add(Blocks.RED_STAINED_GLASS_PANE);
         list.add(Blocks.ORANGE_STAINED_GLASS);
         list.add(Blocks.ORANGE_STAINED_GLASS_PANE);
         list.add(Blocks.LIGHT_BLUE_STAINED_GLASS);
         list.add(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
         list.add(Blocks.LIME_STAINED_GLASS);
         list.add(Blocks.LIME_STAINED_GLASS_PANE);
         list.add(Blocks.PURPLE_STAINED_GLASS);
         list.add(Blocks.PURPLE_STAINED_GLASS_PANE);
         list.add(Blocks.WHITE_STAINED_GLASS);
         list.add(Blocks.WHITE_STAINED_GLASS_PANE);
         list.add(Blocks.YELLOW_STAINED_GLASS);
         list.add(Blocks.YELLOW_STAINED_GLASS_PANE);
         list.add(Blocks.MAGENTA_STAINED_GLASS);
         list.add(Blocks.MAGENTA_STAINED_GLASS_PANE);
         list.add(Blocks.BLACK_STAINED_GLASS);
         list.add(Blocks.BLACK_STAINED_GLASS_PANE);
         list.add(Blocks.BLUE_STAINED_GLASS);
         list.add(Blocks.BLUE_STAINED_GLASS_PANE);
         list.add(Blocks.BROWN_STAINED_GLASS);
         list.add(Blocks.BROWN_STAINED_GLASS_PANE);
         list.add(Blocks.GREEN_STAINED_GLASS);
         list.add(Blocks.GREEN_STAINED_GLASS_PANE);
      }

      if (this.mode.getMode().equals("Tungsten")) {
         list.add(Blocks.CLAY);
      }

      if (this.mode.getMode().equals("Umber")) {
         list.add(Blocks.RED_SAND);
      }

      if (this.mode.getMode().equals("Glacite")) {
         list.add(Blocks.PACKED_ICE);
      }

      if (this.mode.getMode().equals("Diamond")) {
         list.add(Blocks.DIAMOND_BLOCK);
      }

      if (this.mode.getMode().equals("Netherrack")) {
         list.add(Blocks.NETHERRACK);
      }

      if (this.mode.getMode().equals("Sand")) {
         list.add(Blocks.SAND);
      }

      if (this.mode.getMode().equals("Red Sand")) {
         list.add(Blocks.RED_SAND);
      }

      if (this.mode.getMode().equals("Mycelium")) {
         list.add(Blocks.MYCELIUM);
      }

      if (this.mode.getMode().equals("Obsidian")) {
         list.add(Blocks.OBSIDIAN);
      }

      if (this.mode.getMode().equals("End Stone")) {
         list.add(Blocks.END_STONE);
      }

      if (this.mode.getMode().equals("Sugar Cane")) {
         list.add(Blocks.SUGAR_CANE);
      }

      if (this.mode.getMode().equals("Pumpkin")) {
         list.add(Blocks.PUMPKIN);
         list.add(Blocks.CARVED_PUMPKIN);
      }

      if (this.mode.getMode().equals("Wheat")) {
         list.add(Blocks.WHEAT);
      }

      if (this.mode.getMode().equals("Melon")) {
         list.add(Blocks.MELON);
      }

      if (this.mode.getMode().equals("Carrot")) {
         list.add(Blocks.CARROTS);
      }

      if (this.mode.getMode().equals("EnderNode")) {
         list.add(Blocks.PURPLE_TERRACOTTA);
      }

      if (this.mode.getMode().equals("RedstoneBlock")) {
         list.add(Blocks.REDSTONE_BLOCK);
      }

      if (this.mode.getMode().equals("IronBlock")) {
         list.add(Blocks.IRON_BLOCK);
      }

      if (this.mode.getMode().equals("DiamondBlock")) {
         list.add(Blocks.DIAMOND_BLOCK);
      }

      if (this.mode.getMode().equals("EmeraldBlock")) {
         list.add(Blocks.EMERALD_BLOCK);
      }

      if (this.mode.getMode().equals("CoalBlock")) {
         list.add(Blocks.COAL_BLOCK);
      }

      if (this.mode.getMode().equals("Fig Log")) {
         list.add(Blocks.STRIPPED_SPRUCE_WOOD);
      }

      if (this.mode.getMode().equals("Mangrove Wood")) {
         list.add(Blocks.MANGROVE_WOOD);
      }

      if (this.mode.getMode().equals("Caducous Stem")) {
         list.add(Blocks.ROSE_BUSH);
      }

      if (this.mode.getMode().equals("Red Mushroom")) {
         list.add(Blocks.RED_MUSHROOM);
      }

      super.onEnable();
      if (!dev.greencat.shimmer.util.world.MiningBot.setup(
         "Nuker",
         list,
         this.randomMoveEnable.isEnabled(),
         this.sneakEnable.isEnabled(),
         this.allowReset,
         this.upperEnable.isEnabled(),
         this.realNuker.isEnabled(),
         this.serverRotation.isEnabled(),
         this.oneTick.isEnabled(),
         (int)this.timedOutTime.getValue(),
         this.callback
      )) {
         Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
      }

      if (this.serverRotation.isEnabled()) {
         ServerRotation.useServerRotation = true;
      }
   }

   @Override
   public void onDisable() {
      dev.greencat.shimmer.util.world.MiningBot.release("Nuker");
      if (this.serverRotation.isEnabled()) {
         ServerRotation.useServerRotation = false;
      }

      super.onDisable();
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      dev.greencat.shimmer.util.world.MiningBot.onTickPre();
      if (dev.greencat.shimmer.util.world.MiningBot.target == null && this.serverRotation.isEnabled()) {
         ServerRotation.useServerRotation = false;
      }

      if (dev.greencat.shimmer.util.world.MiningBot.target == null && !this.realNuker.isEnabled()) {
         MinecraftClient.getInstance().options.attackKey.setPressed(false);
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      dev.greencat.shimmer.util.world.MiningBot.onRender(event);
   }
}
