package dev.greencat.shimmer.module.modules.misc;

import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.util.AuctionItem;
import dev.greencat.shimmer.util.HaikuLogger;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.RunCommand;
import net.minecraft.util.Formatting;

public class AuctionTracker extends Module {
   public static final List<AuctionItem> cowList = new CopyOnWriteArrayList();
   public static final HashMap<String, Formatting> colorMap = new HashMap();
   private static final Random random = new Random();
   private long lastSend = 0L;
   private int lastRandom = 500;
   public static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
   public static final BooleanSetting ignoreDungeonRubbish = new BooleanSetting("Ignore Rubbish", "Ignore the dungeon rubbish", true);
   public static final BooleanSetting ignoreFish = new BooleanSetting("Ignore \"the Fish\"", "Ignore the fish", true);
   public static final BooleanSetting ignore_COMMON = new BooleanSetting("Ignore COMMON", "Ignore COMMON", false);
   public static final BooleanSetting ignore_UNCOMMON = new BooleanSetting("Ignore UNCOMMON", "Ignore UNCOMMON", false);
   public static final BooleanSetting ignore_RARE = new BooleanSetting("Ignore RARE", "Ignore RARE", false);

   public AuctionTracker() {
      super("AuctionTracker", "Auto track the item in auction house", -1, Module.Category.MISC);
      this.addSettings(new Setting[]{ignoreDungeonRubbish, ignoreFish, ignore_COMMON, ignore_UNCOMMON, ignore_RARE});
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
         if (!cowList.isEmpty()) {
            if (System.currentTimeMillis() - this.lastSend > (long)this.lastRandom) {
               this.lastRandom = 500 + random.nextInt(1500) - 750;
               this.lastSend = System.currentTimeMillis();
               AuctionItem item = (AuctionItem)cowList.getFirst();
               Formatting itemColor = (Formatting)colorMap.getOrDefault(item.tier, Formatting.YELLOW);
               MutableText text = Text.literal(itemColor + item.name + Formatting.WHITE + " | " + Formatting.GOLD + numberFormat.format(item.price));
               Style clickStyle = Style.EMPTY.withClickEvent(new RunCommand("viewauction " + item.uid));
               MutableText clickable = Text.literal(Formatting.YELLOW + " CLICK").fillStyle(clickStyle);
               text.append(clickable);
               cowList.remove(item);
               if (isAvailable(item)) {
                  HaikuLogger.auctionTracker(text);
               }
            }
         }
      }
   }

   private static boolean isAvailable(AuctionItem item) {
      if (ignoreFish.isEnabled() && item.name.contains("the Fish")) {
         return false;
      } else if (ignore_COMMON.isEnabled() && item.tier.equals("COMMON")) {
         return false;
      } else if (ignore_UNCOMMON.isEnabled() && item.tier.equals("UNCOMMON")) {
         return false;
      } else {
         return ignore_RARE.isEnabled() && item.tier.equals("RARE")
            ? false
            : !ignoreDungeonRubbish.isEnabled()
               || !item.name.contains(" Heavy ")
                  && !item.name.contains("Skeleton Grunt")
                  && !item.name.contains("Rotten")
                  && !item.name.contains("Soldier")
                  && !item.name.contains("Skeleton Master")
                  && !item.name.contains("Zombie Knight")
                  && !item.name.contains("Dreadlord")
                  && !item.name.contains("Machine Gun")
                  && !item.name.contains("Soulstealer")
                  && !item.name.contains("Conjuring");
      }
   }

   static {
      colorMap.put("COMMON", Formatting.WHITE);
      colorMap.put("UNCOMMON", Formatting.GREEN);
      colorMap.put("RARE", Formatting.BLUE);
      colorMap.put("EPIC", Formatting.DARK_PURPLE);
      colorMap.put("LEGENDARY", Formatting.GOLD);
      colorMap.put("MYTHIC", Formatting.LIGHT_PURPLE);
      colorMap.put("DIVINE", Formatting.AQUA);
      colorMap.put("SPECIAL", Formatting.RED);
      colorMap.put("VERY_SPECIAL", Formatting.RED);
      colorMap.put("ULTIMATE", Formatting.DARK_RED);
   }
}
