package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.ArmRenderEvent;
import dev.greencat.shimmer.event.events.HeldItemRendererEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3d;

public class Animation extends Module {
   public static final NumberSetting mainHandSwingProgress = new NumberSetting("Swing Progress(Main)", "Your main hand swing progress", 0.0, 0.0, 1.0, 0.1);
   public static final NumberSetting offHandSwingProgress = new NumberSetting("Swing Progress(Off)", "Your offhand swing progress", 0.0, 0.0, 1.0, 0.1);
   public static final BooleanSetting oldAnimation = new BooleanSetting("Old Animation", "Switch attack animation to 1.8.9 style", true);
   public static final BooleanSetting noFoodAnimation = new BooleanSetting("No Food Animation", "Disable food eating animation", false);
   public static final NumberSetting ArmRotateX = new NumberSetting("Arm Rotate X", "Your hand rotate on X axis", 0.0, -180.0, 180.0, 0.1);
   public static final NumberSetting ArmRotateY = new NumberSetting("Arm Rotate Y", "Your hand rotate on Y axis", 0.0, -180.0, 180.0, 0.1);
   public static final NumberSetting ArmRotateZ = new NumberSetting("Arm Rotate Z", "Your hand rotate on Z axis", 0.0, -180.0, 180.0, 0.1);
   public static final NumberSetting ArmScaleX = new NumberSetting("Arm Scale X", "Your hand scale on X axis", 1.0, 0.0, 5.0, 0.1);
   public static final NumberSetting ArmScaleY = new NumberSetting("Arm Scale Y", "Your hand scale on Y axis", 1.0, 0.0, 5.0, 0.1);
   public static final NumberSetting ArmScaleZ = new NumberSetting("Arm Scale Z", "Your hand scale on Z axis", 1.0, 0.0, 5.0, 0.1);
   public static final NumberSetting ArmTranslateX = new NumberSetting("Arm Translate X", "Your hand translate on X axis", 0.0, -3.0, 3.0, 0.1);
   public static final NumberSetting ArmTranslateY = new NumberSetting("Arm Translate Y", "Your hand translate on Y axis", 0.0, -3.0, 3.0, 0.1);
   public static final NumberSetting ArmTranslateZ = new NumberSetting("Arm Translate Z", "Your hand translate on Z axis", 0.0, -3.0, 3.0, 0.1);
   public static final NumberSetting ItemRotateX = new NumberSetting("Item Rotate X", "Your item rotate on X axis", 0.0, -180.0, 180.0, 0.1);
   public static final NumberSetting ItemRotateY = new NumberSetting("Item Rotate Y", "Your item rotate on Y axis", 0.0, -180.0, 180.0, 0.1);
   public static final NumberSetting ItemRotateZ = new NumberSetting("Item Rotate Z", "Your item rotate on Z axis", 0.0, -180.0, 180.0, 0.1);
   public static final NumberSetting ItemScaleX = new NumberSetting("Item Scale X", "Your item scale on X axis", 1.0, 0.0, 5.0, 0.1);
   public static final NumberSetting ItemScaleY = new NumberSetting("Item Scale Y", "Your item scale on Y axis", 1.0, 0.0, 5.0, 0.1);
   public static final NumberSetting ItemScaleZ = new NumberSetting("Item Scale Z", "Your item scale on Z axis", 1.0, 0.0, 5.0, 0.1);
   public static final NumberSetting ItemTranslateX = new NumberSetting("Item Translate X", "Your item translate on X axis", 0.0, -3.0, 3.0, 0.1);
   public static final NumberSetting ItemTranslateY = new NumberSetting("Item Translate Y", "Your item translate on Y axis", 0.0, -3.0, 3.0, 0.1);
   public static final NumberSetting ItemTranslateZ = new NumberSetting("Item Translate Z", "Your item translate on Z axis", 0.0, -3.0, 3.0, 0.1);
   public static final NumberSetting swingSpeed = new NumberSetting("Swing Speed", "Your swing speed", 6.0, 0.0, 50.0, 0.1);

   public Animation() {
      super("Animation", "Allow you change your animation", -1, Module.Category.RENDER);
      this.addSettings(
         new Setting[]{
            swingSpeed,
            mainHandSwingProgress,
            offHandSwingProgress,
            oldAnimation,
            noFoodAnimation,
            ArmRotateX,
            ArmRotateY,
            ArmRotateZ,
            ArmScaleX,
            ArmScaleY,
            ArmScaleZ,
            ArmTranslateX,
            ArmTranslateY,
            ArmTranslateZ,
            ItemRotateX,
            ItemRotateY,
            ItemRotateZ,
            ItemScaleX,
            ItemScaleY,
            ItemScaleZ,
            ItemTranslateX,
            ItemTranslateY,
            ItemTranslateZ
         }
      );
   }

   @ShimmerSubscribe
   public void onRenderArm(ArmRenderEvent event) {
      this.rotate(event.matrices, new Vector3d(ArmRotateX.getValue(), ArmRotateY.getValue(), ArmRotateZ.getValue()));
      this.scale(event.matrices, new Vector3d(ArmScaleX.getValue(), ArmScaleY.getValue(), ArmScaleZ.getValue()));
      this.translate(event.matrices, new Vector3d(ArmTranslateX.getValue(), ArmTranslateY.getValue(), ArmTranslateZ.getValue()));
   }

   @ShimmerSubscribe
   public void onRenderItem(HeldItemRendererEvent event) {
      this.rotate(event.matrices, new Vector3d(ItemRotateX.getValue(), ItemRotateY.getValue(), ItemRotateZ.getValue()));
      this.scale(event.matrices, new Vector3d(ItemScaleX.getValue(), ItemScaleY.getValue(), ItemScaleZ.getValue()));
      this.translate(event.matrices, new Vector3d(ItemTranslateX.getValue(), ItemTranslateY.getValue(), ItemTranslateZ.getValue()));
   }

   private void rotate(MatrixStack matrix, Vector3d rotation) {
      matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)rotation.x));
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)rotation.y));
      matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)rotation.z));
   }

   private void scale(MatrixStack matrix, Vector3d scale) {
      matrix.scale((float)scale.x, (float)scale.y, (float)scale.z);
   }

   private void translate(MatrixStack matrix, Vector3d translation) {
      matrix.translate((float)translation.x, (float)translation.y, (float)translation.z);
   }
}
