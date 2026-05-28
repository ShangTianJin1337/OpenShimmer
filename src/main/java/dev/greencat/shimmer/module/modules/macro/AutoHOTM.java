package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.PlayerListHudAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.player.rotation.Rotation;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.player.rotation.SmoothRotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

public class AutoHOTM extends Module {
   private static final Random random = new Random();
   private static final Timer timer = new Timer();
   private static BlockPos pos = new BlockPos(0, 0, 0);
   private static final String CLIFFSIDE_VEINS = "Cliffside Veins";
   private static final String LAVA_SPRINGS = "Lava Springs";
   private static final String RAMPARTS_QUARRY = "Rampart's Quarry";
   private static final String ROYAL_MINES = "Royal Mines";
   private static final String UPPER_MINES = "Upper Mines";
   private static final String SLAYER_GLACIAL = "Glacite Walker Slayer";
   private static final String SLAYER_GOBLIN = "Goblin Slayer";
   private static final String TITANIUM_COMMISSION = "Titanium";
   private static final String MITHRIL_COMMISSION = "Mithril";
   private static final BlockPos[] CLIFFSIDE_VEINS_POS = new BlockPos[]{
      new BlockPos(47, 135, 11), new BlockPos(30, 133, 22), new BlockPos(47, 126, 23), new BlockPos(11, 127, 36)
   };
   private static final BlockPos[] LAVA_SPRINGS_POS = new BlockPos[]{new BlockPos(46, 210, 17), new BlockPos(51, 206, 17)};
   private static final BlockPos[] RAMPARTS_QUARRY_POS = new BlockPos[]{
      new BlockPos(-42, 136, 3), new BlockPos(-48, 135, 24), new BlockPos(-90, 145, -4), new BlockPos(-90, 147, -14)
   };
   private static final BlockPos[] ROYAL_MINES_POS = new BlockPos[]{new BlockPos(140, 152, 24), new BlockPos(117, 153, 44)};
   private static final BlockPos[] UPPER_MINES_POS = new BlockPos[]{new BlockPos(-126, 172, -76)};
   private static final BlockPos GREAT_ICE_WALL_POS = new BlockPos(0, 128, 160);
   private static final BlockPos GOBLIN_POS = new BlockPos(-138, 144, 134);
   private static final BlockPos SUBMIT_POS = new BlockPos(42, 133, 21);
   private static int currentStatus = 0;
   private static long lastSwitchStatus = 0L;
   private static int clickTick = 0;
   private static int READY_GO_SPOT = 0;
   private static int READY_MINE = 1;
   private static int READY_GO_FORGE = 2;
   private static int READY_GO_SUBMIT = 3;
   private static int SUBMIT_FINISH = 4;
   private static final List<Pattern> COMMISSIONS = Stream.of(
         "(?:Titanium|Mithril|Hard Stone) Miner",
         "(?:Glacite Walker|Golden Goblin|(?<!Golden )Goblin|Goblin Raid|Treasure Hoarder|Automaton|Sludge|Team Treasurite Member|Yog|Boss Corleone|Thyst|Maniac|Mines) Slayer",
         "(?:Lava Springs|Cliffside Veins|Rampart's Quarry|Upper Mines|Royal Mines) Mithril",
         "(?:Lava Springs|Cliffside Veins|Rampart's Quarry|Upper Mines|Royal Mines) Titanium"
      )
      .map(s -> Pattern.compile("(" + s + "): (\\d+\\.?\\d*%|DONE)"))
      .toList();

   public AutoHOTM() {
      super("AutoHOTM", "Do Heart of the Mountains mission automaticly", -1, Module.Category.MACRO);
      this.needDisable = true;
   }

   @ShimmerSubscribe
   public void onClientTick(TickEvent events) {
      String currentCommission = null;

      for (PlayerListEntry playerListEntry : MinecraftClient.getInstance()
         .getNetworkHandler()
         .getPlayerList()
         .stream()
         .sorted(PlayerListHudAccessor.getOrdering())
         .toList()) {
         if (playerListEntry.getDisplayName() != null) {
            String name = playerListEntry.getDisplayName().getString().strip();

            for (Pattern pattern : COMMISSIONS) {
               Matcher matcher = pattern.matcher(name);
               if (matcher.matches()) {
                  String temp = matcher.group(0);
                  if (!temp.contains("Raid") && !temp.contains("Golden") && !temp.contains("Powder")) {
                     currentCommission = temp;
                  }
               }
            }
         }
      }

      for (BlockPos pos : BlockPos.iterate(
         new BlockPos(
            MinecraftClient.getInstance().player.getBlockX() + 10,
            MinecraftClient.getInstance().player.getBlockY() + 10,
            MinecraftClient.getInstance().player.getBlockZ() + 10
         ),
         new BlockPos(
            MinecraftClient.getInstance().player.getBlockX() - 10,
            MinecraftClient.getInstance().player.getBlockY() - 10,
            MinecraftClient.getInstance().player.getBlockZ() - 10
         )
      )) {
         if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.SLIME_BLOCK) {
            MinecraftClient.getInstance().world.setBlockState(pos, Blocks.STONE.getDefaultState(), 18);
            MinecraftClient.getInstance().world.setBlockState(pos.up(), Blocks.LAVA.getDefaultState(), 18);
            MinecraftClient.getInstance().world.setBlockState(pos.up(2), Blocks.STONE.getDefaultState(), 18);
         }

         if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() instanceof FenceGateBlock) {
            MinecraftClient.getInstance().world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 18);
         }

         if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() instanceof LadderBlock) {
            MinecraftClient.getInstance().world.setBlockState(pos, Blocks.LAVA.getDefaultState(), 18);
         }
      }

      if (currentCommission != null && currentStatus == READY_GO_SPOT && !WalkerUtils.isActive() && System.currentTimeMillis() - lastSwitchStatus >= 500L) {
         Shimmer.getInstance().getModuleManager().getModule("KillerBot").setEnabled(false);
         Shimmer.getInstance().getModuleManager().getModule("Killaura").setEnabled(false);
         Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
         ((Nuker)Shimmer.getInstance().getModuleManager().getModule("Nuker")).mode.setMode("Mithril With Titanium");
         if (currentCommission.contains("Cliffside Veins")) {
            AutoHOTM.pos = CLIFFSIDE_VEINS_POS[random.nextInt(CLIFFSIDE_VEINS_POS.length)];
         }

         if (currentCommission.contains("Lava Springs")) {
            AutoHOTM.pos = LAVA_SPRINGS_POS[random.nextInt(LAVA_SPRINGS_POS.length)];
         }

         if (currentCommission.contains("Rampart's Quarry")) {
            AutoHOTM.pos = RAMPARTS_QUARRY_POS[random.nextInt(RAMPARTS_QUARRY_POS.length)];
         }

         if (currentCommission.contains("Royal Mines")) {
            AutoHOTM.pos = ROYAL_MINES_POS[random.nextInt(ROYAL_MINES_POS.length)];
         }

         if (currentCommission.contains("Upper Mines")) {
            AutoHOTM.pos = UPPER_MINES_POS[random.nextInt(UPPER_MINES_POS.length)];
         }

         if (currentCommission.contains("Glacite Walker Slayer")) {
            AutoHOTM.pos = GREAT_ICE_WALL_POS;
         }

         if (currentCommission.contains("Goblin Slayer")) {
            AutoHOTM.pos = GOBLIN_POS;
         }

         if ((currentCommission.contains("Titanium") || currentCommission.contains("Mithril")) && currentCommission.contains("Miner")) {
            List<BlockPos> posArrayList = new ArrayList();
            posArrayList.addAll(List.of(ROYAL_MINES_POS));
            posArrayList.addAll(List.of(RAMPARTS_QUARRY_POS));
            posArrayList.addAll(List.of(CLIFFSIDE_VEINS_POS));
            AutoHOTM.pos = (BlockPos)posArrayList.get(random.nextInt(posArrayList.size()));
         }

         AutoHOTM.pos = AutoHOTM.pos.down();
         WalkerUtils.walkToBaritoneRotation(AutoHOTM.pos);
         MinecraftClient.getInstance().player.setYaw(MinecraftClient.getInstance().player.getYaw() + (float)(random.nextInt(100) - 50));
         lastSwitchStatus = System.currentTimeMillis();
         currentStatus = READY_MINE;
      }

      if (currentStatus == READY_MINE
         && !WalkerUtils.isActive()
         && System.currentTimeMillis() - lastSwitchStatus >= 500L
         && AutoHOTM.pos != null
         && MinecraftClient.getInstance().player.getBlockPos().getSquaredDistance(AutoHOTM.pos) > 5.0
         && !currentCommission.contains("DONE")) {
         WalkerUtils.walkToBaritoneRotation(AutoHOTM.pos);
         MinecraftClient.getInstance().player.setYaw(MinecraftClient.getInstance().player.getYaw() + (float)(random.nextInt(100) - 50));
      }

      if (currentStatus == READY_MINE
         && !WalkerUtils.isActive()
         && System.currentTimeMillis() - lastSwitchStatus >= 500L
         && AutoHOTM.pos != null
         && MinecraftClient.getInstance().player.getBlockPos().getSquaredDistance(AutoHOTM.pos) <= 5.0
         && !currentCommission.contains("DONE")
         && !currentCommission.contains("Slayer")) {
         Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(true);
      }

      if (currentStatus == READY_MINE && !WalkerUtils.isActive() && currentCommission != null && currentCommission.contains("DONE")) {
         if (!currentCommission.contains("Slayer")) {
            Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
         }

         MinecraftClient.getInstance().player.networkHandler.sendChatCommand("warp forge");
         lastSwitchStatus = System.currentTimeMillis();
         currentStatus = READY_GO_FORGE;
      }

      if (currentStatus == READY_GO_FORGE
         && !Shimmer.getInstance().getModuleManager().getModule("Nuker").isEnabled()
         && !Shimmer.getInstance().getModuleManager().getModule("KillerBot").isEnabled()
         && System.currentTimeMillis() - lastSwitchStatus >= 500L) {
         WalkerUtils.walkToBaritoneRotation(SUBMIT_POS);
         MinecraftClient.getInstance().player.setYaw(MinecraftClient.getInstance().player.getYaw() + (float)(random.nextInt(100) - 50));
         lastSwitchStatus = System.currentTimeMillis();
         currentStatus = READY_GO_SUBMIT;
      }

      if (currentStatus == READY_GO_SUBMIT
         && !WalkerUtils.isActive()
         && System.currentTimeMillis() - lastSwitchStatus >= 500L
         && MinecraftClient.getInstance().player.getBlockPos().getSquaredDistance(SUBMIT_POS) > 25.0) {
         SmoothRotation.smoothLook(new Rotation(MinecraftClient.getInstance().player.getYaw() + (float)(random.nextInt(100) - 50), 0.0F), 300, () -> {
         });
         timer.schedule(new TimerTask() {
            public void run() {
               WalkerUtils.walkToBaritoneRotation(AutoHOTM.SUBMIT_POS);
            }
         }, 300L);
      }

      if (currentStatus == READY_GO_SUBMIT
         && !WalkerUtils.isActive()
         && System.currentTimeMillis() - lastSwitchStatus >= 500L
         && MinecraftClient.getInstance().player.getBlockPos().getSquaredDistance(SUBMIT_POS) <= 25.0) {
         if (!(MinecraftClient.getInstance().currentScreen instanceof HandledScreen)) {
            Entity submitEntity = null;

            for (Entity entity : mc.world.getEntities()) {
               if (!entity.getUuidAsString().equals(mc.player.getUuidAsString())
                  && entity instanceof ArmorStandEntity
                  && !(entity.distanceTo(MinecraftClient.getInstance().player) >= 3.0F)
                  && entity.hasCustomName()
                  && entity.getCustomName().getString().contains("CLICK")) {
                  submitEntity = entity;
               }
            }

            if (submitEntity != null) {
               MinecraftClient.getInstance().player.setYaw((float)RotationUtil.getYaw(submitEntity));
               MinecraftClient.getInstance().player.setPitch(0.0F);
               lastSwitchStatus = System.currentTimeMillis();
               MinecraftClient.getInstance().options.useKey.setPressed(true);
               timer.schedule(new TimerTask() {
                  public void run() {
                     MinecraftClient.getInstance().options.useKey.setPressed(false);
                  }
               }, 300L);
            }

            clickTick = 0;
         } else {
            HandledScreen<?> screen = (HandledScreen<?>)mc.currentScreen;
            if (screen.getTitle().getString().contains("Commission")) {
               clickTick++;
               if (clickTick == 5) {
                  mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 11, 0, SlotActionType.CLONE, mc.player);
               }

               if (clickTick == 10) {
                  mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 12, 0, SlotActionType.CLONE, mc.player);
               }

               if (clickTick == 15) {
                  mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 13, 0, SlotActionType.CLONE, mc.player);
               }

               if (clickTick == 20) {
                  mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 14, 0, SlotActionType.CLONE, mc.player);
               }

               if (clickTick == 25) {
                  mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 15, 0, SlotActionType.CLONE, mc.player);
               }

               if (clickTick > 30) {
                  mc.setScreen(null);
                  lastSwitchStatus = System.currentTimeMillis();
                  currentStatus = SUBMIT_FINISH;
               }
            }
         }
      }

      if (currentStatus == SUBMIT_FINISH
         && System.currentTimeMillis() - lastSwitchStatus >= 500L
         && !(MinecraftClient.getInstance().currentScreen instanceof HandledScreen)) {
         MinecraftClient.getInstance().player.networkHandler.sendChatCommand("warp forge");
         lastSwitchStatus = System.currentTimeMillis() + 2000L;
         currentStatus = READY_GO_SPOT;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      pos = new BlockPos(0, 0, 0);
      Shimmer.getInstance().getModuleManager().getModule("Nuker").setEnabled(false);
      Shimmer.getInstance().getModuleManager().getModule("KillerBot").setEnabled(false);
      Shimmer.getInstance().getModuleManager().getModule("Killaura").setEnabled(false);
      WalkerUtils.cancel();
      clickTick = 0;
      currentStatus = READY_GO_SPOT;
   }
}
