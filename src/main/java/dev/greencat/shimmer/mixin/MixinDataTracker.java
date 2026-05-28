package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.util.HaikuLogger;
import java.util.List;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Entry;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({DataTracker.class})
public abstract class MixinDataTracker {
   @Unique
   private long lastMessaged = 0L;
   @Shadow
   @Final
   private Entry<?>[] entries;
   @Shadow
   @Final
   private DataTracked trackedEntity;

   @Shadow
   protected abstract <T> void copyToFrom(Entry<T> var1, SerializedEntry<?> var2);

   @Overwrite
   public void writeUpdatedEntries(List<SerializedEntry<?>> entries) {
      try {
         for (SerializedEntry<?> serializedEntry : entries) {
            Entry<?> entry = this.entries[serializedEntry.id()];
            this.copyToFrom(entry, serializedEntry);
            this.trackedEntity.onTrackedDataSet(entry.getData());
         }

         this.trackedEntity.onDataTrackerUpdate(entries);
      } catch (Exception var5) {
         if (System.currentTimeMillis() - this.lastMessaged >= 600000L) {
            HaikuLogger.info("发生网络协议错误,已终止对发生错误项目的数据更新");
            HaikuLogger.info("发生异常的原因为:" + var5.getMessage());
            this.lastMessaged = System.currentTimeMillis();
         }
      }
   }
}
