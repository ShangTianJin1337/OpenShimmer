package dev.greencat.shimmer.event.events;

import dev.greencat.shimmer.event.Event;

public class KeyEvent extends Event {
   private final int key;
   private final int code;
   private final KeyEvent.Status status;

   public KeyEvent(int key, int code, KeyEvent.Status status) {
      this.key = key;
      this.code = code;
      this.status = status;
   }

   public int getKey() {
      return this.key;
   }

   public int getCode() {
      return this.code;
   }

   public static enum Status {
      PRESSED,
      RELEASED;
   }
}
