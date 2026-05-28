package dev.greencat.shimmer.util.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

public class TextRenderUtil {
   private static long colorIndex = 0L;
   private static long colorCounter = 0L;
   private static long colorDelay = 0L;
   public static final int backgroundColor = new Color(0, 0, 0, 100).getRGB();

   public static void renderText(Text text, Vec3d pos, float scale, boolean seeThrough, VertexConsumerProvider consumerProvider) {
      renderText(text, pos, scale, 0.0F, seeThrough, consumerProvider);
   }

   public static void renderText(Text text, Vec3d pos, float scale, float yOffset, boolean seeThrough, VertexConsumerProvider consumerProvider) {
      renderText(text.asOrderedText(), pos, scale, yOffset, seeThrough, consumerProvider);
   }

   public static void renderText(OrderedText text, Vec3d pos, float scale, float yOffset, boolean throughWalls, VertexConsumerProvider consumerProvider) {
      Matrix4f positionMatrix = new Matrix4f();
      Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
      Vec3d cameraPos = camera.getPos();
      TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
      scale *= 0.025F;
      positionMatrix.translate(
            (float)(pos.getX() - cameraPos.getX()),
            (float)(pos.getY() - cameraPos.getY()),
            (float)(pos.getZ() - cameraPos.getZ())
         )
         .rotate(camera.getRotation())
         .scale(scale, -scale, scale);
      float xOffset = (float)(-textRenderer.getWidth(text)) / 2.0F;
      GlStateManager._depthFunc(throughWalls ? 519 : 515);
      textRenderer.draw(text, xOffset, yOffset, -1, false, positionMatrix, consumerProvider, TextLayerType.SEE_THROUGH, 0, 15728880);
      GlStateManager._depthFunc(515);
   }

   public static void renderHUDText(DrawContext context, int x, int y, Text... text) {
      int height = text.length * 10;
      int width = 0;

      for (Text t : text) {
         int length = MinecraftClient.getInstance().textRenderer.getWidth(t.asOrderedText());
         if (length > width) {
            width = length;
         }
      }

      context.fill(x - 3, y - 3, x + width + 3, y + height + 3, backgroundColor);

      for (int i = 0; i < text.length; i++) {
         context.drawText(MinecraftClient.getInstance().textRenderer, text[i], x, y + i * 10, Color.WHITE.getRGB(), false);
      }
   }

   public static void colorText(DrawContext context, int x, int y, String text) {
      colorText(context, x, y, text, false);
   }

   public static void colorText(DrawContext context, int x, int y, String text, boolean cleanIndex) {
      int currentShift = 0;

      for (int i = 1; i < text.length() + 1; i++) {
         boolean swap = (int)((float)(colorIndex + colorCounter) / 3.0F / 30.0F) % 2 == 0;
         Color currentColor = Color.getHSBColor(
            0.925F,
            ((swap ? 30.0F - (float)(colorIndex + colorCounter) / 3.0F % 30.0F : (float)(colorIndex + colorCounter) / 3.0F % 30.0F) + 35.0F) / 100.0F,
            1.0F
         );
         String currentString = text.substring(i - 1, i);
         context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, currentString, x + currentShift, y, currentColor.getRGB());
         currentShift += MinecraftClient.getInstance().textRenderer.getWidth(currentString);
         colorIndex += 12L;
      }

      if (cleanIndex) {
         colorIndex = 0L;
      }
   }

   public static void cleanIndex() {
      colorIndex = 0L;
   }

   public static void onRenderHUD(DrawContext context, RenderTickCounter tickCounter) {
      if (System.currentTimeMillis() - colorDelay >= 5L) {
         colorDelay = System.currentTimeMillis();
         colorCounter++;
      }

      colorIndex = 0L;
   }

   public static void drawScaledText(DrawContext context, Text text, int x, int y, float scale, int color) {
      Matrix3x2fStack stack = context.getMatrices();
      stack.scale(scale, scale);
      context.drawText(MinecraftClient.getInstance().textRenderer, text, (int)((float)x / scale), (int)((float)y / scale), color, false);
      stack.scale(1.0F / scale, 1.0F / scale);
   }
}
