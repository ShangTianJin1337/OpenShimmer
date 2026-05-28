package dev.greencat.shimmer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.greencat.shimmer.Shimmer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class WorldRendererMixin {
   @Shadow
   @Final
   private BufferBuilderStorage bufferBuilders;

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/WorldRenderer;fillEntityRenderStates(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/client/render/state/WorldRenderState;)V"
      )}
   )
   public void onRenderPre(
      ObjectAllocator allocator,
      RenderTickCounter tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      Matrix4f positionMatrix,
      Matrix4f matrix4f,
      Matrix4f projectionMatrix,
      GpuBufferSlice fogBuffer,
      Vector4f fogColor,
      boolean renderSky,
      CallbackInfo ci
   ) {
      Shimmer.onWorldRenderPre((WorldRenderer)(Object)this, this.bufferBuilders);
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/WorldRenderer;fillEntityRenderStates(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/client/render/state/WorldRenderState;)V",
         shift = Shift.AFTER
      )}
   )
   public void onRenderAfterEntity(
      ObjectAllocator allocator,
      RenderTickCounter tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      Matrix4f positionMatrix,
      Matrix4f matrix4f,
      Matrix4f projectionMatrix,
      GpuBufferSlice fogBuffer,
      Vector4f fogColor,
      boolean renderSky,
      CallbackInfo ci
   ) {
      Shimmer.onWorldRenderAfterEntity((WorldRenderer)(Object)this, this.bufferBuilders);
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   public void onRenderPost(
      ObjectAllocator allocator,
      RenderTickCounter tickCounter,
      boolean renderBlockOutline,
      Camera camera,
      Matrix4f positionMatrix,
      Matrix4f matrix4f,
      Matrix4f projectionMatrix,
      GpuBufferSlice fogBuffer,
      Vector4f fogColor,
      boolean renderSky,
      CallbackInfo ci
   ) {
      Shimmer.onWorldRenderPost((WorldRenderer)(Object)this, this.bufferBuilders);
   }

   @ModifyExpressionValue(
      method = {"method_62214"},
      at = {@At(
         value = "NEW",
         target = "net/minecraft/client/util/math/MatrixStack"
      )}
   )
   private MatrixStack setMatrixStack(MatrixStack matrixStack) {
      Shimmer.matrixStack = matrixStack;
      return matrixStack;
   }
}
