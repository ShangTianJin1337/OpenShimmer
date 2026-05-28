package dev.greencat.shimmer.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(EnvType.CLIENT)
public record SliceGuiElementRenderState(
   RenderPipeline pipeline,
   TextureSetup textureSetup,
   Matrix3x2f pose,
   float x,
   float y,
   float x1,
   float y1,
   float x2,
   float y2,
   int color,
   @Nullable ScreenRect scissorArea,
   @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState {
   public SliceGuiElementRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      float cx,
      float cy,
      float p1x,
      float p1y,
      float p2x,
      float p2y,
      int color,
      @Nullable ScreenRect scissorArea
   ) {
      this(pipeline, textureSetup, pose, cx, cy, p1x, p1y, p2x, p2y, color, scissorArea, createBounds(cx, cy, p1x, p1y, p2x, p2y, pose, scissorArea));
   }

   public void setupVertices(VertexConsumer vertices) {
      vertices.vertex(this.pose, this.x, this.y).color(this.color);
      vertices.vertex(this.pose, this.x1, this.y1).color(this.color);
      vertices.vertex(this.pose, this.x2, this.y2).color(this.color);
      vertices.vertex(this.pose, this.x, this.y).color(this.color);
   }

   @Nullable
   private static ScreenRect createBounds(float x, float y, float x1, float y1, float x2, float y2, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
      float minX = Math.min(x, Math.min(x1, x2));
      float minY = Math.min(y, Math.min(y1, y2));
      float maxX = Math.max(x, Math.max(x1, x2));
      float maxY = Math.max(y, Math.max(y1, y2));
      ScreenRect screenRect = new ScreenRect((int)minX, (int)minY, (int)(maxX - minX), (int)(maxY - minY)).transformEachVertex(pose);
      return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
   }

   public RenderPipeline pipeline() {
      return this.pipeline;
   }

   public TextureSetup textureSetup() {
      return this.textureSetup;
   }

   @Nullable
   public ScreenRect scissorArea() {
      return this.scissorArea;
   }

   @Nullable
   public ScreenRect bounds() {
      return this.bounds;
   }
}
