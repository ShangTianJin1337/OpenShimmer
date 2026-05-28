package dev.greencat.shimmer.util.render;

import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.OptionalDouble;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.client.render.RenderPhase.Texture;
import net.minecraft.util.Identifier;

public class ShimmerRenderLayer {
   private static final Double2ObjectMap<MultiPhase> LINES_LAYERS = new Double2ObjectOpenHashMap();
   private static final Double2ObjectMap<MultiPhase> LINES_THROUGH_WALLS_LAYERS = new Double2ObjectOpenHashMap();
   private static final Object2ObjectMap<Identifier, MultiPhase> TEXTURE_LAYERS = new Object2ObjectOpenHashMap();
   private static final Object2ObjectMap<Identifier, MultiPhase> TEXTURE_THROUGH_WALLS_LAYERS = new Object2ObjectOpenHashMap();
   public static final MultiPhase FILLED = RenderLayer.of(
      "filled", 1536, false, true, RenderPipelines.DEBUG_FILLED_BOX, MultiPhaseParameters.builder().layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
   );
   public static final MultiPhase FILLED_THROUGH_WALLS = RenderLayer.of(
      "filled_through_walls",
      1536,
      false,
      true,
      ShimmerRenderPipelines.FILLED_THROUGH_WALLS,
      MultiPhaseParameters.builder().layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
   );
   private static final DoubleFunction<MultiPhase> LINES = lineWidth -> RenderLayer.of(
         "lines",
         1536,
         false,
         false,
         RenderPipelines.LINES,
         MultiPhaseParameters.builder().lineWidth(new LineWidth(OptionalDouble.of(lineWidth))).layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
      );
   private static final DoubleFunction<MultiPhase> LINES_THROUGH_WALLS = lineWidth -> RenderLayer.of(
         "lines_through_walls",
         1536,
         false,
         false,
         ShimmerRenderPipelines.LINES_THROUGH_WALLS,
         MultiPhaseParameters.builder().lineWidth(new LineWidth(OptionalDouble.of(lineWidth))).layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
      );
   public static final MultiPhase QUADS = RenderLayer.of(
      "quad", 1536, false, true, RenderPipelines.DEBUG_QUADS, MultiPhaseParameters.builder().layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
   );
   public static final MultiPhase QUADS_THROUGH_WALLS = RenderLayer.of(
      "quad_through_walls",
      1536,
      false,
      true,
      ShimmerRenderPipelines.QUADS_THROUGH_WALLS,
      MultiPhaseParameters.builder().layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
   );
   private static final Function<Identifier, MultiPhase> TEXTURE = texture -> RenderLayer.of(
         "texture",
         1536,
         false,
         true,
         ShimmerRenderPipelines.TEXTURE,
         MultiPhaseParameters.builder().texture(new Texture(texture, false)).layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
      );
   private static final Function<Identifier, MultiPhase> TEXTURE_THROUGH_WALLS = texture -> RenderLayer.of(
         "texture_through_walls",
         1536,
         false,
         true,
         ShimmerRenderPipelines.TEXTURE_THROUGH_WALLS,
         MultiPhaseParameters.builder().texture(new Texture(texture, false)).layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
      );
   public static final MultiPhase CYLINDER = RenderLayer.of(
      "cylinder", 1536, false, true, ShimmerRenderPipelines.CYLINDER, MultiPhaseParameters.builder().layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).build(false)
   );

   public static MultiPhase getLines(double lineWidth) {
      return (MultiPhase)LINES_LAYERS.computeIfAbsent(lineWidth, LINES);
   }

   public static MultiPhase getLinesThroughWalls(double lineWidth) {
      return (MultiPhase)LINES_THROUGH_WALLS_LAYERS.computeIfAbsent(lineWidth, LINES_THROUGH_WALLS);
   }

   public static MultiPhase getTexture(Identifier texture) {
      return (MultiPhase)TEXTURE_LAYERS.computeIfAbsent(texture, TEXTURE);
   }

   public static MultiPhase getTextureThroughWalls(Identifier texture) {
      return (MultiPhase)TEXTURE_THROUGH_WALLS_LAYERS.computeIfAbsent(texture, TEXTURE_THROUGH_WALLS);
   }
}
