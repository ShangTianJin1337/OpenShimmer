package dev.greencat.shimmer.util.render.animation;

public class AnimationEngine {
   public double xCoord;
   public double yCoord;
   public double targetX;
   public double targetY;
   public boolean isRunning = false;
   public int type;
   public long endTime = 0L;
   public long startTime = 0L;
   public LinearFunction xLinearFunction;
   public LinearFunction yLinearFunction;
   public InverseProportionFunction xInverseProportionFunction;
   public InverseProportionFunction yInverseProportionFunction;
   public static final int EASE_OUT = 1;
   public static final int EASE_IN = 2;
   public static final int LINEAR = 3;

   public AnimationEngine(int x, int y) {
      this.xCoord = (double)x;
      this.yCoord = (double)y;
      this.targetX = (double)x;
      this.targetY = (double)y;
   }

   public AnimationEngine() {
      this.xCoord = 0.0;
      this.yCoord = 0.0;
      this.targetX = 0.0;
      this.targetY = 0.0;
   }

   public void setPosition(int x, int y) {
      this.xCoord = (double)x;
      this.yCoord = (double)y;
      this.targetX = (double)x;
      this.targetY = (double)y;
   }

   public void register() {
      AnimationManager.add(this);
      this.isRunning = true;
   }

   public void moveTo(float x, float y, double second, int type) {
      if (this.targetX != (double)x || this.targetY != (double)y) {
         this.startTime = System.currentTimeMillis();
         this.endTime = (long)((double)System.currentTimeMillis() + 1000.0 * second);
         this.targetX = (double)x;
         this.targetY = (double)y;
         this.xLinearFunction = null;
         this.yLinearFunction = null;
         this.xInverseProportionFunction = null;
         this.yInverseProportionFunction = null;
         this.type = type;
         if (type == 3) {
            this.xLinearFunction = new LinearFunction(1.0, this.xCoord, 100.0, this.targetX);
            this.yLinearFunction = new LinearFunction(1.0, this.yCoord, 100.0, this.targetY);
         }

         if (type == 1) {
            this.xInverseProportionFunction = new InverseProportionFunction(2500.0);
            this.xInverseProportionFunction.setOffsetX(20.0);
            this.yInverseProportionFunction = new InverseProportionFunction(2500.0);
            this.yInverseProportionFunction.setOffsetX(20.0);
            double XFunctionYPositionAt1 = this.xInverseProportionFunction.getY(1.0);
            double XFunctionYPositionAt100 = this.xInverseProportionFunction.getY(100.0);
            this.xLinearFunction = new LinearFunction(XFunctionYPositionAt1, this.xCoord, XFunctionYPositionAt100, this.targetX);
            double YFunctionYPositionAt1 = this.yInverseProportionFunction.getY(1.0);
            double YFunctionYPositionAt100 = this.yInverseProportionFunction.getY(100.0);
            this.yLinearFunction = new LinearFunction(YFunctionYPositionAt1, this.yCoord, YFunctionYPositionAt100, this.targetY);
         }

         if (type == 2) {
            this.xInverseProportionFunction = new InverseProportionFunction(-2500.0);
            this.xInverseProportionFunction.setOffsetX(-121.0);
            this.yInverseProportionFunction = new InverseProportionFunction(-2500.0);
            this.yInverseProportionFunction.setOffsetX(-121.0);
            double XFunctionYPositionAt1 = this.xInverseProportionFunction.getY(1.0);
            double XFunctionYPositionAt100 = this.xInverseProportionFunction.getY(100.0);
            this.xLinearFunction = new LinearFunction(XFunctionYPositionAt1, this.xCoord, XFunctionYPositionAt100, this.targetX);
            double YFunctionYPositionAt1 = this.yInverseProportionFunction.getY(1.0);
            double YFunctionYPositionAt100 = this.yInverseProportionFunction.getY(100.0);
            this.yLinearFunction = new LinearFunction(YFunctionYPositionAt1, this.yCoord, YFunctionYPositionAt100, this.targetY);
         }

         this.register();
      }
   }

   public void RenderTick() {
      if (System.currentTimeMillis() > this.endTime) {
         this.destroy();
         this.xCoord = this.targetX;
         this.yCoord = this.targetY;
      }

      if (this.xCoord != this.targetX) {
         this.xCoord = this.getXPosition();
      }

      if (this.yCoord != this.targetY) {
         this.yCoord = this.getYPosition();
      }

      if (this.getXPosition() > this.xCoord && this.xCoord > this.targetX) {
         this.xCoord = this.targetX;
      }

      if (this.getYPosition() > this.yCoord && this.yCoord > this.targetY) {
         this.yCoord = this.targetY;
      }

      if (this.getXPosition() < this.xCoord && this.xCoord < this.targetX) {
         this.xCoord = this.targetX;
      }

      if (this.getYPosition() < this.yCoord && this.yCoord < this.targetY) {
         this.yCoord = this.targetY;
      }

      if (this.xCoord == this.targetX && this.yCoord == this.targetY) {
         this.destroy();
      }

      this.callback();
   }

   public void callback() {
   }

   public void destroy() {
      AnimationManager.destroy(this);
      this.isRunning = false;
   }

   public double getXPosition() {
      long currentTime = System.currentTimeMillis() - this.startTime;
      long allTime = this.endTime - this.startTime;
      double progress = (double)currentTime / (double)allTime * 100.0;
      if (this.type == 3) {
         return this.xLinearFunction.getY(progress);
      } else if (progress == 0.0) {
         return this.xCoord;
      } else if (progress >= 100.0) {
         return this.targetX;
      } else {
         double numberInInverseProportion = this.xInverseProportionFunction.getY(progress);
         return this.xLinearFunction.getY(numberInInverseProportion);
      }
   }

   public double getYPosition() {
      long currentTime = System.currentTimeMillis() - this.startTime;
      long allTime = this.endTime - this.startTime;
      double progress = (double)currentTime / (double)allTime * 100.0;
      if (this.type == 3) {
         return this.yLinearFunction.getY(progress);
      } else if (progress == 0.0) {
         return this.yCoord;
      } else if (progress >= 100.0) {
         return this.targetY;
      } else {
         double numberInInverseProportion = this.yInverseProportionFunction.getY(progress);
         return this.yLinearFunction.getY(numberInInverseProportion);
      }
   }
}
