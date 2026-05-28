package dev.greencat.shimmer.util.render.animation;

import java.util.LinkedList;
import java.util.Queue;

public class QueueAnimationEngine extends AnimationEngine {
   public Queue<QueueAnimationEngine.PositionWrapper> animationQueue = new LinkedList();

   public QueueAnimationEngine() {
   }

   public QueueAnimationEngine(int x, int y) {
      super(x, y);
   }

   public boolean isWorking() {
      return AnimationManager.animations.containsKey(this);
   }

   public void QueueMoveTo(int x, int y, double second, int type) {
      if (!this.isWorking()) {
         super.moveTo((float)x, (float)y, second, type);
      } else {
         this.animationQueue.offer(new QueueAnimationEngine.PositionWrapper(x, y, second, type));
      }
   }

   @Override
   public void callback() {
      if (!this.isWorking() && !this.animationQueue.isEmpty()) {
         QueueAnimationEngine.PositionWrapper positionWrapper = (QueueAnimationEngine.PositionWrapper)this.animationQueue.poll();
         super.moveTo((float)positionWrapper.x, (float)positionWrapper.y, positionWrapper.speed, positionWrapper.type);
      }
   }

   static class PositionWrapper {
      public int x;
      public int y;
      public double speed;
      public int type;

      public PositionWrapper(int x, int y, double speed, int type) {
         this.x = x;
         this.y = y;
         this.speed = speed;
         this.type = type;
      }
   }
}
