package dev.greencat.shimmer.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.greencat.shimmer.mixin.DrawContextAccessor;
import java.awt.Color;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;

public class Rect {
   private static final int STEP = 10;

   public static void draw2DRoundedRect(float x, float y, float width, float height, float radius, float z, Color color, DrawContext context) {
      draw2DRoundedRect(x, y, width, height, radius, z, color, context, true, true, true, true);
   }

   public static void draw2DRoundedRect(
      float x, float y, float width, float height, float radius, float z, Color color, DrawContext context, boolean... control
   ) {
      int colorInt = color.getRGB();
      Matrix3x2f pose = new Matrix3x2f(((DrawContextAccessor)context).matrices());
      ScreenRect scissor = context.scissorStack.peekLast();
      TextureSetup texture = TextureSetup.empty();
      RenderPipeline pipeline = RenderPipelines.DEBUG_QUADS;
      boolean tl = control.length > 0 ? control[0] : true;
      boolean tr = control.length > 1 ? control[1] : true;
      boolean br = control.length > 2 ? control[2] : true;
      boolean bl = control.length > 3 ? control[3] : true;
      context.state
         .addSimpleElement(new FloatQuadGuiElementRenderState(pipeline, texture, pose, x + radius, y, width - 2.0F * radius, height, colorInt, scissor));
      context.state
         .addSimpleElement(new FloatQuadGuiElementRenderState(pipeline, texture, pose, x, y + radius, radius, height - 2.0F * radius, colorInt, scissor));
      context.state
         .addSimpleElement(
            new FloatQuadGuiElementRenderState(pipeline, texture, pose, x + width - radius, y + radius, radius, height - 2.0F * radius, colorInt, scissor)
         );
      if (tl) {
         addCorner(context, pipeline, texture, pose, x + radius, y + radius, radius, 0, colorInt, scissor);
      } else {
         context.state.addSimpleElement(new FloatQuadGuiElementRenderState(pipeline, texture, pose, x, y, radius, radius, colorInt, scissor));
      }

      if (tr) {
         addCorner(context, pipeline, texture, pose, x + width - radius, y + radius, radius, 1, colorInt, scissor);
      } else {
         context.state.addSimpleElement(new FloatQuadGuiElementRenderState(pipeline, texture, pose, x + width - radius, y, radius, radius, colorInt, scissor));
      }

      if (bl) {
         addCorner(context, pipeline, texture, pose, x + width - radius, y + height - radius, radius, 2, colorInt, scissor);
      } else {
         context.state
            .addSimpleElement(
               new FloatQuadGuiElementRenderState(pipeline, texture, pose, x + width - radius, y + height - radius, radius, radius, colorInt, scissor)
            );
      }

      if (br) {
         addCorner(context, pipeline, texture, pose, x + radius, y + height - radius, radius, 3, colorInt, scissor);
      } else {
         context.state.addSimpleElement(new FloatQuadGuiElementRenderState(pipeline, texture, pose, x, y + height - radius, radius, radius, colorInt, scissor));
      }
   }

   private static void addCorner(
      DrawContext context,
      RenderPipeline pipeline,
      TextureSetup texture,
      Matrix3x2f pose,
      float cx,
      float cy,
      float r,
      int corner,
      int color,
      ScreenRect scissor
   ) {
      int startAngle;
      int endAngle;
      switch (corner) {
         case 0:
            startAngle = 180;
            endAngle = 270;
            break;
         case 1:
            startAngle = 270;
            endAngle = 360;
            break;
         case 2:
            startAngle = 0;
            endAngle = 90;
            break;
         case 3:
            startAngle = 90;
            endAngle = 180;
            break;
         default:
            return;
      }

      for (int i = startAngle; i < endAngle; i += 10) {
         double rad1 = Math.toRadians((double)(i - 10));
         double rad2 = Math.toRadians((double)(i + 10));
         float x1 = (float)((double)cx + Math.cos(rad1) * (double)r);
         float y1 = (float)((double)cy + Math.sin(rad1) * (double)r);
         float x2 = (float)((double)cx + Math.cos(rad2) * (double)r);
         float y2 = (float)((double)cy + Math.sin(rad2) * (double)r);
         context.state.addSimpleElement(new SliceGuiElementRenderState(pipeline, texture, pose, cx, cy, x1, y1, x2, y2, color, scissor));
      }
   }

   public static void drawOutlinedRoundedRect(
      float x, float y, float width, float height, float radius, float z, Color color, Color color2, float thickness, DrawContext context
   ) {
      drawOutlinedRoundedRect(x, y, width, height, radius, z, color, color2, thickness, context, true, true, true, true);
   }

   public static void drawOutlinedRoundedRect(
      float x1,
      float y1,
      float width1,
      float height1,
      float radius1,
      float z,
      Color color,
      Color color2,
      float thickness,
      DrawContext context,
      boolean... control
   ) {
      draw2DRoundedRect(x1, y1, width1, height1, radius1, z, color, context, control);
      draw2DRoundedRect(x1 + thickness, y1 + thickness, width1 - thickness * 2.0F, height1 - thickness * 2.0F, radius1 - thickness, z, color2, context, control);
   }
}
