package dev.greencat.shimmer.util;

public class AuctionItem {
   public final String name;
   public final String uid;
   public final long price;
   public final String tier;

   public AuctionItem(String name, String uid, long price, String tier) {
      this.name = name;
      this.uid = uid;
      this.price = price;
      this.tier = tier;
   }
}
