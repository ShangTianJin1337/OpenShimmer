package dev.greencat.shimmer.module.modules.macro;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;

public class GiftRecipient extends Module {
   private int tick = 0;
   Queue<ArmorStandEntity> giftQueue = new LinkedList();
   private final NumberSetting delay = new NumberSetting("Delay", "Click Delay", 5.0, 5.0, 20.0, 1.0);
   private final BooleanSetting onlyName = new BooleanSetting("Only Name", "Only check gift's display name", true);

   public GiftRecipient() {
      super("GiftRecipient", "Auto open gift", -1, Module.Category.MACRO);
      this.addSettings(new Setting[]{this.delay, this.onlyName});
   }

   @ShimmerSubscribe
   public void onClientTick(TickEvent event) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         if (this.tick % (int)this.delay.getValue() == 0) {
            if (!this.giftQueue.isEmpty()) {
               ArmorStandEntity gift = (ArmorStandEntity)this.giftQueue.poll();
               PlayerInteractEntityC2SPacket c02 = PlayerInteractEntityC2SPacket.interact(gift, false, Hand.MAIN_HAND);
               MinecraftClient.getInstance().getNetworkHandler().getConnection().send(c02);
            } else {
               ArrayList<Entity> currentEntities = new ArrayList();
               MinecraftClient.getInstance().world.getEntities().forEach(currentEntities::add);
               currentEntities.forEach(
                  entity -> {
                     if (entity.distanceTo(MinecraftClient.getInstance().player) <= 5.0F
                        && entity.hasCustomName()
                        && entity instanceof ArmorStandEntity
                        && entity.getCustomName().getString().contains("CLICK TO OPEN")) {
                        if (this.onlyName.enabled) {
                           for (Entity entity1 : currentEntities) {
                              if (entity1 instanceof ArmorStandEntity) {
                                 ArmorStandEntity armorStand = (ArmorStandEntity)entity1;
                                 if ((double)entity1.distanceTo(entity) <= 0.5 && !armorStand.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
                                    this.giftQueue.offer(armorStand);
                                 }
                              }
                           }
                        } else {
                           this.giftQueue.offer((ArmorStandEntity)entity);
                        }
                     }
                  }
               );
            }
         }

         this.tick++;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.tick = 0;
   }
}
