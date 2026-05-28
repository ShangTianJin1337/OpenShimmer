package dev.greencat.shimmer.util.render.animation;

public class LinearFunction {
   private final double k;
   private final double b;
   private double offsetX = 0.0;
   private double offsetY = 0.0;

   public LinearFunction(double k, double b) {
      this.k = k;
      this.b = b;
   }

   public LinearFunction(double x1, double y1, double x2, double y2) {
      this.k = (y2 - y1) / (x2 - x1);
      this.b = y1 - this.k * x1;
   }

   public void setOffsetX(double value) {
      this.offsetX = value;
   }

   public void setOffsetY(double value) {
      this.offsetY = value;
   }

   public double getY(double x) {
      return (x + this.offsetX) * this.k + this.b + this.offsetY;
   }

   public double getX(double y) {
      return this.k + this.offsetX != 0.0 ? (y + this.offsetY - this.b) / this.k + this.offsetX : 0.0;
   }

   public String toString() {
      return "K: " + this.k + " B: " + this.b + " OffsetX " + this.offsetX + " OffsetY " + this.offsetY;
   }
}
