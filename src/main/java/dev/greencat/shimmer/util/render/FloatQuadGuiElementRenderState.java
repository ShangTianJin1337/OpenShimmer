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
public record FloatQuadGuiElementRenderState(
   RenderPipeline pipeline,
   TextureSetup textureSetup,
   Matrix3x2f pose,
   float x,
   float y,
   float width,
   float height,
   int color,
   @Nullable ScreenRect scissorArea,
   @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState {
   public FloatQuadGuiElementRenderState(
      RenderPipeline pipeline,
      TextureSetup textureSetup,
      Matrix3x2f pose,
      float x,
      float y,
      float width,
      float height,
      int color,
      @Nullable ScreenRect scissorArea
   ) {
      this(pipeline, textureSetup, pose, x, y, width, height, color, scissorArea, createBounds((int)x, (int)y, (int)width, (int)height, pose, scissorArea));
   }

   @Nullable
   private static ScreenRect createBounds(int x, int y, int width, int height, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
      ScreenRect screenRect = new ScreenRect(x, y, width, height).transformEachVertex(pose);
      return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
   }

   public void setupVertices(VertexConsumer vertices) {
      vertices.vertex(this.pose, this.x, this.y).color(this.color);
      vertices.vertex(this.pose, this.x, this.y + this.height).color(this.color);
      vertices.vertex(this.pose, this.x + this.width, this.y + this.height).color(this.color);
      vertices.vertex(this.pose, this.x + this.width, this.y).color(this.color);
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
