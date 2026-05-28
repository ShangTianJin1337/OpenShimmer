package dev.greencat.shimmer.eventbus;

public abstract class ShimmerEvent {
   private boolean cancelled;
   private ShimmerEvent.Era era;

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public void cancel() {
      this.setCancelled(true);
   }

   public ShimmerEvent.Era getEra() {
      return this.era;
   }

   public void setEra(ShimmerEvent.Era era) {
      this.era = era;
   }

   public static enum Era {
      PRE,
      POST;
   }
}
