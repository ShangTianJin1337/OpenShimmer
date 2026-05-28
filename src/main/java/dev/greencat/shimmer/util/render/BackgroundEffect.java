package dev.greencat.shimmer.util.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import dev.greencat.shimmer.module.modules.render.OneGui;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;

public class BackgroundEffect {
   public static final List<BackgroundEffect.Point> pointList = new ArrayList();
   private static final Random random = new Random();
   public static boolean renderBackground = false;

   public static void onRender(DrawContext context, int mouseX, int mouseY, float delta) {
      if (MinecraftClient.getInstance().currentScreen == null || !MinecraftClient.getInstance().currentScreen.getClass().getSimpleName().equals("MaterialGui")) {
         if (OneGui.backgroundEffect.isEnabled()) {
            if (MinecraftClient.getInstance() != null) {
               if (MinecraftClient.getInstance().getWindow() != null) {
                  if (context.getMatrices() != null) {
                     if (renderBackground) {
                        renderBackground = false;
                     }

                     GlStateManager._disableDepthTest();
                     if (MinecraftClient.getInstance().world == null) {
                        context.drawText(MinecraftClient.getInstance().textRenderer, "", 0, 0, Color.white.getRGB(), true);
                     }

                     int scaledWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
                     int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
                     int size = (int)((float)scaledWidth / 50.0F * ((float)scaledHeight / 50.0F));
                     if (pointList.size() < size) {
                        addPoint();
                     }

                     List<BackgroundEffect.Point> removalList = new ArrayList();

                     for (BackgroundEffect.Point point : pointList) {
                        float vectorX = (float)mouseX - point.x;
                        float vectorY = (float)mouseY - point.y;
                        float distance = (float)Math.sqrt((double)(vectorX * vectorX + vectorY * vectorY));
                        float targetDistance = Math.min((float)scaledWidth / 7.0F, (float)scaledHeight / 4.0F);
                        if (distance <= targetDistance) {
                           RenderUtil.draw2DLine(
                              context.getMatrices(),
                              (float)mouseX,
                              (float)mouseY,
                              point.x,
                              point.y,
                              MinecraftClient.getInstance().world == null && !(MinecraftClient.getInstance().currentScreen instanceof TitleScreen)
                                 ? 0.0F
                                 : -0.0025F,
                              Color.WHITE,
                              2.5F,
                              context
                           );
                        }

                        RenderUtil.draw2DCircle(
                           context.getMatrices(),
                           point.x,
                           point.y,
                           MinecraftClient.getInstance().world == null && !(MinecraftClient.getInstance().currentScreen instanceof TitleScreen)
                              ? 0.0F
                              : -0.0025F,
                           1.0F,
                           Color.WHITE,
                           context
                        );
                        point.update();
                        if (point.x < -10.0F || point.y < -10.0F || point.x > (float)(scaledWidth + 10) || point.y > (float)(scaledHeight + 10)) {
                           removalList.add(point);
                        }
                     }

                     pointList.removeAll(removalList);
                     GlStateManager._enableBlend();
                  }
               }
            }
         }
      }
   }

   public static void addPoint() {
      int scaledWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
      int scaledHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
      pointList.add(
         new BackgroundEffect.Point(
            (float)(random.nextBoolean() ? 0 : scaledWidth),
            (float)(random.nextBoolean() ? 0 : scaledHeight),
            random.nextFloat() - 0.5F,
            random.nextFloat() - 0.5F
         )
      );
   }

   public static class Point {
      public float x;
      public float y;
      public final float velocityX;
      public final float velocityY;

      public Point(float x, float y, float velocityX, float velocityY) {
         this.x = x;
         this.y = y;
         this.velocityX = velocityX;
         this.velocityY = velocityY;
      }

      public void update() {
         this.x = this.x + this.velocityX;
         this.y = this.y + this.velocityY;
      }
   }
}
