package dev.greencat.shimmer.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ShimmerRenderPipelines {
   static final RenderPipeline FILLED_THROUGH_WALLS = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("shimmer", "pipeline/debug_filled_box_through_walls"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withCull(false)
         .build()
   );
   static final RenderPipeline LINES_THROUGH_WALLS = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.RENDERTYPE_LINES_SNIPPET})
         .withLocation(Identifier.of("shimmer", "pipeline/lines_through_walls"))
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .build()
   );
   static final RenderPipeline QUADS_THROUGH_WALLS = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("shimmer", "pipeline/debug_quads_through_walls"))
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withCull(false)
         .build()
   );
   static final RenderPipeline TEXTURE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("shimmer", "pipeline/texture"))
         .withCull(false)
         .build()
   );
   static final RenderPipeline TEXTURE_THROUGH_WALLS = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_TEX_COLOR_SNIPPET})
         .withLocation(Identifier.of("shimmer", "pipeline/texture_through_walls"))
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withCull(false)
         .build()
   );
   static final RenderPipeline CYLINDER = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
         .withLocation(Identifier.of("shimmer", "pipeline/cylinder"))
         .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLE_STRIP)
         .withCull(false)
         .build()
   );
}
