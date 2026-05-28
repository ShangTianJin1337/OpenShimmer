package dev.greencat.shimmer.util.render.animation;

import java.util.concurrent.ConcurrentHashMap;

public class AnimationManager {
   public static final ConcurrentHashMap<AnimationEngine, Integer> animations = new ConcurrentHashMap();

   public AnimationManager() {
      Thread animationThread = new Thread(() -> {
         while (true) {
            try {
               this.onRenderTick();
               Thread.sleep(10L);
            } catch (InterruptedException var2) {
               var2.printStackTrace();
            }
         }
      }, "Client Animation Thread");
      animationThread.start();
   }

   public static void add(AnimationEngine animation) {
      animations.put(animation, 0);
   }

   public static void destroy(AnimationEngine animation) {
      animations.remove(animation);
   }

   public void onRenderTick() {
      animations.keySet().forEach(AnimationEngine::RenderTick);
   }
}
