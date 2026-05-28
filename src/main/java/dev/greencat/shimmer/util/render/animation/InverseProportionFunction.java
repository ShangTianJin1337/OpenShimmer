package dev.greencat.shimmer.util.render.animation;

public class InverseProportionFunction {
   private final double k;
   private double offsetX = 0.0;
   private double offsetY = 0.0;

   public InverseProportionFunction(double k) {
      this.k = k;
   }

   public InverseProportionFunction(double x, double y) {
      this.k = x * y;
   }

   public void setOffsetX(double value) {
      this.offsetX = value;
   }

   public void setOffsetY(double value) {
      this.offsetY = value;
   }

   public double getY(double x) {
      return x + this.offsetX != 0.0 ? this.k / (x + this.offsetX) + this.offsetY : 0.0;
   }

   public double getX(double y) {
      return y + this.offsetY != 0.0 ? this.k / (y + this.offsetY) + this.offsetX : 0.0;
   }

   public String toString() {
      return "K: " + this.k + " OffsetX " + this.offsetX + " OffsetY " + this.offsetY;
   }
}
