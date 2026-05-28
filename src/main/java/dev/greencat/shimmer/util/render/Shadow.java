package dev.greencat.shimmer.util.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class Shadow {
   public static void drawShadow(int x, int y, int w, int h, DrawContext context) {
      GlStateManager._enableBlend();
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelleft.png"), x - 19, y, 0.0F, 0.0F, 19, h, 19, h);
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelright.png"), x + w, y, 0.0F, 0.0F, 19, h, 19, h);
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/paneltop.png"), x, y - 19, 0.0F, 0.0F, w, 19, w, 19);
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelbottom.png"), x, y + h, 0.0F, 0.0F, w, 19, w, 19);
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/paneltopleft.png"), x - 18, y - 18, 0.0F, 0.0F, 18, 18, 18, 18);
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/paneltopright.png"), x + w, y - 18, 0.0F, 0.0F, 18, 18, 18, 18);
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelbottomleft.png"), x - 18, y + h, 0.0F, 0.0F, 18, 18, 18, 18);
      context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelbottomright.png"), x + w, y + h, 0.0F, 0.0F, 18, 18, 18, 18);
   }

   public static void drawShadow(int x, int y, int w, int h, DrawContext context, Shadow.ShadowLocation location) {
      GlStateManager._enableBlend();
      if (location == Shadow.ShadowLocation.TOP) {
         context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/paneltop.png"), x, y - 19, 0.0F, 0.0F, w, 19, w, 19);
      } else if (location == Shadow.ShadowLocation.BOTTOM) {
         context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelbottom.png"), x, y + h, 0.0F, 0.0F, w, 19, w, 19);
      } else if (location == Shadow.ShadowLocation.LEFT) {
         context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelleft.png"), x - 19, y, 0.0F, 0.0F, 19, h, 19, h);
      } else if (location == Shadow.ShadowLocation.RIGHT) {
         context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelright.png"), x + w, y, 0.0F, 0.0F, 19, h, 19, h);
      } else if (location == Shadow.ShadowLocation.TOP_LEFT) {
         context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/paneltopleft.png"), x - 18, y - 18, 0.0F, 0.0F, 18, 18, 18, 18);
      } else if (location == Shadow.ShadowLocation.TOP_RIGHT) {
         context.drawTexture(RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/paneltopright.png"), x + w, y - 18, 0.0F, 0.0F, 18, 18, 18, 18);
      } else if (location == Shadow.ShadowLocation.BOTTOM_LEFT) {
         context.drawTexture(
            RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelbottomleft.png"), x - 18, y + h, 0.0F, 0.0F, 18, 18, 18, 18
         );
      } else if (location == Shadow.ShadowLocation.BOTTOM_RIGHT) {
         context.drawTexture(
            RenderPipelines.BLOCK_SCREEN_EFFECT, Identifier.of("shimmer:shadow/panelbottomright.png"), x + w, y + h, 0.0F, 0.0F, 18, 18, 18, 18
         );
      }
   }

   public static enum ShadowLocation {
      TOP,
      BOTTOM,
      LEFT,
      RIGHT,
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_RIGHT;
   }
}
