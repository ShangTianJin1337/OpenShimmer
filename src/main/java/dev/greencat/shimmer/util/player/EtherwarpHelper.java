package dev.greencat.shimmer.util.player;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class EtherwarpHelper {
   private static final String CONFIG_FILE_NAME = "etherwarp_points.properties";
   private final MinecraftClient client;
   private final List<BlockPos> points = new ArrayList();
   Timer timer = new Timer();
   private int currentTargetIndex = 0;
   private KeyBinding addKey;
   private KeyBinding deleteKey;

   public EtherwarpHelper() {
      this.client = MinecraftClient.getInstance();
      this.load();
      this.registerKeys();
      this.registerEvents();
   }

   private void registerKeys() {
      this.addKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.etherwarp.add", Type.KEYSYM, 296, new Category(Identifier.of("shimmer"))));
      this.deleteKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.etherwarp.delete", Type.KEYSYM, 297, new Category(Identifier.of("shimmer"))));
   }

   private void registerEvents() {
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         while (this.addKey.wasPressed()) {
            this.add();
         }

         while (this.deleteKey.wasPressed()) {
            this.delete();
         }
      });
   }

   public void add() {
      if (this.client.player != null) {
         BlockPos pos = this.client.player.getBlockPos().down();
         this.points.add(pos);
         this.sendChat("Added point: " + pos.toShortString() + " (Total: " + this.points.size() + ")");
         this.save();
      }
   }

   public void delete() {
      if (!this.points.isEmpty()) {
         BlockPos removed = (BlockPos)this.points.remove(this.points.size() - 1);
         this.sendChat("Removed last point: " + removed.toShortString());
         if (this.currentTargetIndex >= this.points.size()) {
            this.currentTargetIndex = 0;
         }

         this.save();
      } else {
         this.sendChat("List is empty.");
      }
   }

   public void next() {
      if (this.client.player != null && this.client.interactionManager != null && !this.points.isEmpty()) {
         if (this.currentTargetIndex >= this.points.size()) {
            this.currentTargetIndex = 0;
         }

         BlockPos targetPos = (BlockPos)this.points.get(this.currentTargetIndex);
         this.lookAtBlockSmart(targetPos);
         Shimmer.getInstance().getModuleManager().getModule("Sneak").setEnabled(true);
         TimerTask timerTask = new TimerTask() {
            public void run() {
               final int originalSlot = EtherwarpHelper.this.client.player.getInventory().getSelectedSlot();
               int toolSlot = EtherwarpHelper.this.findEtherwarpToolSlot();
               if (toolSlot != -1) {
                  EtherwarpHelper.this.client.player.getInventory().setSelectedSlot(toolSlot);
                  EtherwarpHelper.this.client.player.getInventory().markDirty();
                  EtherwarpHelper.this.client.interactionManager.interactItem(EtherwarpHelper.this.client.player, Hand.MAIN_HAND);
                  TimerTask timerTask1 = new TimerTask() {
                     public void run() {
                        EtherwarpHelper.this.client.player.getInventory().setSelectedSlot(originalSlot);
                        EtherwarpHelper.this.client.player.getInventory().markDirty();
                     }
                  };
                  EtherwarpHelper.this.timer.schedule(timerTask1, 200L);
               } else {
                  EtherwarpHelper.this.sendChat("No Diamond Shovel or Sword found in hotbar!");
               }

               TimerTask timerTask2 = new TimerTask() {
                  public void run() {
                     Shimmer.getInstance().getModuleManager().getModule("Sneak").setEnabled(false);
                  }
               };
               EtherwarpHelper.this.timer.schedule(timerTask2, 200L);
            }
         };
         this.timer.schedule(timerTask, 200L);
         this.currentTargetIndex++;
         if (this.currentTargetIndex >= this.points.size()) {
            this.currentTargetIndex = 0;
         }
      }
   }

   private void lookAtBlockSmart(BlockPos target) {
      ClientPlayerEntity player = this.client.player;
      if (player != null) {
         double targetX = (double)target.getX() + 0.5;
         double targetZ = (double)target.getZ() + 0.5;
         double targetY;
         if (player.getY() > (double)target.getY()) {
            targetY = (double)target.getY() + 1.0;
         } else {
            targetY = (double)target.getY() + 0.5;
         }

         Vec3d targetVec = new Vec3d(targetX, targetY, targetZ);
         this.lookAt(targetVec);
      }
   }

   private void lookAt(Vec3d target) {
      this.client.player.setYaw((float)RotationUtil.getYaw(target));
      this.client.player.setPitch((float)RotationUtil.getPitch(target));
   }

   private int findEtherwarpToolSlot() {
      for (int i = 0; i < 9; i++) {
         Item item = this.client.player.getInventory().getStack(i).getItem();
         if (item == Items.DIAMOND_SHOVEL || item == Items.DIAMOND_SWORD) {
            return i;
         }
      }

      return -1;
   }

   public void save() {
      Properties props = new Properties();
      String data = (String)this.points.stream().map(p -> p.getX() + "," + p.getY() + "," + p.getZ()).collect(Collectors.joining(";"));
      props.setProperty("points", data);
      File file = new File(this.client.runDirectory, "etherwarp_points.properties");

      try {
         FileOutputStream fos = new FileOutputStream(file);

         try {
            props.store(fos, "Etherwarp Helper Points");
         } catch (Throwable var8) {
            try {
               fos.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         fos.close();
      } catch (IOException var9) {
         var9.printStackTrace();
      }
   }

   public void load() {
      File file = new File(this.client.runDirectory, "etherwarp_points.properties");
      if (file.exists()) {
         Properties props = new Properties();

         try {
            FileInputStream fis = new FileInputStream(file);

            try {
               props.load(fis);
               String data = props.getProperty("points", "");
               this.points.clear();
               if (!data.isEmpty()) {
                  String[] parts = data.split(";");

                  for (String part : parts) {
                     String[] coords = part.split(",");
                     if (coords.length == 3) {
                        this.points.add(new BlockPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2])));
                     }
                  }
               }
            } catch (Throwable var12) {
               try {
                  fis.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }

               throw var12;
            }

            fis.close();
         } catch (Exception var13) {
            var13.printStackTrace();
            this.sendChat("Failed to load points.");
         }
      }
   }

   public void renderPoints(RenderEvent event) {
      if (!this.points.isEmpty()) {
         if (this.currentTargetIndex >= this.points.size()) {
            this.currentTargetIndex = 0;
         }

         int prevIndex = this.currentTargetIndex - 1;
         if (prevIndex < 0) {
            prevIndex = this.points.size() - 1;
         }

         for (int i = 0; i < this.points.size(); i++) {
            BlockPos pos = (BlockPos)this.points.get(i);
            Color color;
            if (i == prevIndex) {
               color = Color.RED;
            } else {
               color = Color.ORANGE;
            }

            RenderUtil.draw3DBox(new Box(pos), color, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
         }
      }
   }

   private void sendChat(String msg) {
      if (this.client.player != null) {
         this.client.player.sendMessage(Text.of("§b[Etherwarp] §f" + msg), false);
      }
   }
}
