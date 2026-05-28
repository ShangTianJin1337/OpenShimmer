package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class EnderNodeESP extends Module {
   public final NumberSetting refreshDelay = new NumberSetting("Refresh Delay", "Change Refresh Delay", 10.0, 0.1, 30.0, 0.25);
   private final CopyOnWriteArrayList<Box> nodePosition = new CopyOnWriteArrayList();
   private long lastSearch = 0L;

   public EnderNodeESP() {
      super("EnderNodeESP", "Auto Find Ender Node", -1, Module.Category.RENDER);
      this.addSettings(new Setting[]{this.refreshDelay});
   }

   @Override
   public void onEnable() {
      this.nodePosition.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (mc.world != null && mc.player != null) {
         if ((double)(System.currentTimeMillis() - this.lastSearch) >= 1000.0 * this.refreshDelay.getValue()) {
            this.lastSearch = System.currentTimeMillis();
            this.nodePosition.clear();
            Hud.onMessage(Text.literal(Formatting.BOLD + "EnderNodeESP"), Text.literal("正在刷新EnderNode点位"));
            Thread thread = new Thread(
               () -> {
                  if (MinecraftClient.getInstance().player != null) {
                     for (BlockPos pos : BlockPos.iterate(
                        new BlockPos(MinecraftClient.getInstance().player.getBlockX() + 128, 130, MinecraftClient.getInstance().player.getBlockZ() + 128),
                        new BlockPos(MinecraftClient.getInstance().player.getBlockX() - 128, 0, MinecraftClient.getInstance().player.getBlockZ() - 128)
                     )) {
                        if (MinecraftClient.getInstance().world.getBlockState(pos).getBlock() == Blocks.PURPLE_TERRACOTTA) {
                           this.nodePosition.add(new Box(pos));
                        }
                     }

                     try {
                        Thread.sleep(1000L);
                     } catch (InterruptedException var3) {
                        throw new RuntimeException(var3);
                     }

                     Hud.onMessage(Text.literal(Formatting.BOLD + "EnderNodeESP"), Text.literal("发现" + this.nodePosition.size() + "个点位"));
                  }
               }
            );
            thread.start();
         }
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (MinecraftClient.getInstance().world != null) {
         for (Box pos : this.nodePosition) {
            RenderUtil.draw3DBox(pos, new Color(0, 255, 255), Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
         }
      }
   }
}
