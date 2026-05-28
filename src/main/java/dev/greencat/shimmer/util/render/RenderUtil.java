package dev.greencat.shimmer.util.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import dev.greencat.shimmer.Shimmer;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
   public static final MinecraftClient mc = MinecraftClient.getInstance();

   public static void draw3DBox(Box boxO, Color color, MatrixStack stack, Immediate immediate) {
      Box box = new Box(boxO.minX, boxO.minY, boxO.minZ, boxO.maxX, boxO.maxY, boxO.maxZ);
      Camera camera = mc.gameRenderer.getCamera();
      stack.push();
      stack.translate(-camera.getCameraPos().getX(), -camera.getCameraPos().getY(), -camera.getCameraPos().getZ());
      RenderLayer layer = ShimmerRenderLayer.getLinesThroughWalls(10.0);
      VertexConsumer buffer = immediate.getBuffer(layer);
      setup3D();

      for (double i = 0.0; i <= box.maxX - box.minX; i += 0.5) {
         for (double j = 0.0; j <= box.maxY - box.minY; j += 0.5) {
            VertexRendering.drawBox(
               stack.peek(),
               buffer,
               (box.minX + box.maxX - i) / 2.0,
               (box.minY + box.maxY - j) / 2.0,
               box.minZ,
               (box.minX + box.maxX + i) / 2.0,
               (box.minY + box.maxY + j) / 2.0,
               box.maxZ,
               (float)color.getRed() / 255.0F,
               (float)color.getGreen() / 255.0F,
               (float)color.getBlue() / 255.0F,
               (float)color.getAlpha() / 255.0F
            );
         }
      }

      clean3D();
      stack.pop();
   }

   public static void draw3DOutline(Box box, Color color, MatrixStack stack, Immediate immediate) {
      draw3DBox(box, color, stack, immediate);
   }

   public static void draw2DOutlinePlayer(Entity entity, Color color, MatrixStack stack, Immediate immediate) {
      Camera c = mc.gameRenderer.getCamera();
      Vec3d camPos = c.getCameraPos();
      Vec3d start = entity.getEntityPos().subtract(camPos);
      float x = (float)start.x;
      float y = (float)start.y;
      float z = (float)start.z;
      double r = Math.toRadians((double)(-c.getYaw() + 90.0F));
      float sin = (float)(Math.sin(r) * ((double)entity.getWidth() / 1.5));
      float cos = (float)(Math.cos(r) * ((double)entity.getWidth() / 1.5));
      stack.push();
      Identifier texture = Identifier.of("shimmer:player_box.png");
      RenderLayer layer = RenderLayer.getBlockScreenEffect(texture);
      VertexConsumer buffer = immediate.getBuffer(layer);
      GL11.glDepthFunc(519);
      GlStateManager._enableBlend();
      buffer.vertex(stack.peek(), x + sin, y, z + cos)
         .texture(0.0F, 0.0F)
         .color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F)
         .light(15);
      buffer.vertex(stack.peek(), x - sin, y, z - cos)
         .texture(0.0F, 1.0F)
         .color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F)
         .light(15);
      buffer.vertex(stack.peek(), x - sin, y + entity.getHeight(), z - cos)
         .texture(1.0F, 1.0F)
         .color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F)
         .light(15);
      buffer.vertex(stack.peek(), x + sin, y + entity.getHeight(), z + cos)
         .texture(1.0F, 0.0F)
         .color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F)
         .light(15);
      GL11.glDepthFunc(515);
      GlStateManager._disableBlend();
      stack.pop();
   }

   public static void drawTracer(Vec3d vec3d, Color color, Immediate immediate) {
      Camera camera = mc.gameRenderer.getCamera();
      MatrixStack stack = Shimmer.matrixStack;
      setup3D();
      stack.push();
      stack.translate(-camera.getCameraPos().getX(), -camera.getCameraPos().getY(), -camera.getCameraPos().getZ());
      RenderLayer layer = RenderLayer.getDebugLineStrip(2.5);
      VertexConsumer buffer = immediate.getBuffer(layer);
      Vec3d targetPos = MinecraftClient.getInstance().crosshairTarget.getPos();
      buffer.vertex(stack.peek(), (float)targetPos.x, (float)targetPos.y, (float)targetPos.z).color(color.getRGB()).normal(1.0F, 1.0F, 1.0F);
      buffer.vertex(stack.peek(), (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).color(color.getRGB()).normal(1.0F, 1.0F, 1.0F);
      stack.pop();
      clean3D();
   }

   public static void drawLineNoESP(Vec3d vec3d, Vec3d vec3d2, Color color, Immediate immediate) {
      Camera camera = mc.gameRenderer.getCamera();
      MatrixStack stack = Shimmer.matrixStack;
      setup3D();
      stack.push();
      stack.translate(-camera.getCameraPos().getX(), -camera.getCameraPos().getY(), -camera.getCameraPos().getZ());
      RenderLayer layer = ShimmerRenderLayer.getLines(10.0);
      VertexConsumer buffer = immediate.getBuffer(layer);
      buffer.vertex(stack.peek(), (float)vec3d2.x, (float)vec3d2.y, (float)vec3d2.z).color(color.getRGB()).normal(1.0F, 1.0F, 1.0F);
      buffer.vertex(stack.peek(), (float)vec3d.x, (float)vec3d.y, (float)vec3d.z).color(color.getRGB()).normal(1.0F, 1.0F, 1.0F);
      stack.pop();
      clean3D();
   }

   public static void draw2DLine(
      Matrix3x2fStack stack, float startX, float startY, float endX, float endY, float z, Color color, float width, DrawContext context
   ) {
   }

   public static void draw2DCircle(Matrix3x2fStack matrixStack, float x, float y, float z, float radius, Color color, DrawContext context) {
      context.fill((int)(x - radius), (int)(y - radius), (int)(x + radius), (int)(y + radius), color.getRGB());
   }

   public static Vec3d getInterpolationOffset(Entity e, float tickDelta) {
      return MinecraftClient.getInstance().isPaused()
         ? Vec3d.ZERO
         : new Vec3d(
            e.getX() - MathHelper.lerp((double)tickDelta, e.lastRenderX, e.getX()),
            e.getY() - MathHelper.lerp((double)tickDelta, e.lastRenderY, e.getY()),
            e.getZ() - MathHelper.lerp((double)tickDelta, e.lastRenderZ, e.getZ())
         );
   }

   public static Vec3d smoothen(Entity e, float tickDelta) {
      return e.getEntityPos().subtract(getInterpolationOffset(e, tickDelta));
   }

   public static Box smoothen(Entity e, Box b, float tickDelta) {
      return Box.of(smoothen(e, tickDelta), b.getLengthX(), b.getLengthY(), b.getLengthZ()).offset(0.0, (double)(e.getHeight() / 2.0F), 0.0);
   }

   public static void setup() {
      GlStateManager._deleteTexture(0);
      GlStateManager._enableBlend();
   }

   public static void setup3D() {
      setup();
      GlStateManager._disableDepthTest();
      GlStateManager._depthMask(false);
      GlStateManager._disableCull();
   }

   public static void clean() {
      GlStateManager._disableBlend();
      GlStateManager._bindTexture(0);
   }

   public static void clean3D() {
      clean();
      GlStateManager._enableDepthTest();
      GlStateManager._depthMask(true);
      GlStateManager._enableCull();
   }

   public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float scale, float mouseX, float mouseY, LivingEntity entity) {
      float f = (float)(x1 + x2) / 2.0F;
      float g = (float)(y1 + y2) / 2.0F;
      context.enableScissor(x1, y1, x2, y2);
      float h = (float)Math.atan((double)((f - mouseX) / 40.0F));
      float i = (float)Math.atan((double)((g - mouseY) / 40.0F));
      Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
      Quaternionf quaternionf2 = new Quaternionf().rotateX(i * 20.0F * (float) (Math.PI / 180.0));
      quaternionf.mul(quaternionf2);
      float j = entity.bodyYaw;
      float k = entity.getYaw();
      float l = entity.getPitch();
      float m = entity.lastHeadYaw;
      float n = entity.headYaw;
      entity.bodyYaw = 180.0F + h * 20.0F;
      entity.setYaw(180.0F + h * 40.0F);
      entity.setPitch(-i * 20.0F);
      entity.headYaw = entity.getYaw();
      entity.lastHeadYaw = entity.getYaw();
      float o = entity.getScale();
      Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + scale * o, 0.0F);
      float p = (float)size / o;
      drawEntity(context, x1, y1, x2, y2, p, vector3f, quaternionf, quaternionf2, entity);
      entity.bodyYaw = j;
      entity.setYaw(k);
      entity.setPitch(l);
      entity.lastHeadYaw = m;
      entity.headYaw = n;
      context.disableScissor();
   }

   public static void drawEntity(
      DrawContext drawer,
      int x1,
      int y1,
      int x2,
      int y2,
      float scale,
      Vector3f translation,
      Quaternionf rotation,
      @Nullable Quaternionf overrideCameraAngle,
      LivingEntity entity
   ) {
      EntityRenderManager entityRenderManager = MinecraftClient.getInstance().getEntityRenderDispatcher();
      EntityRenderer<? super LivingEntity, ?> entityRenderer = entityRenderManager.getRenderer(entity);
      EntityRenderState entityRenderState = entityRenderer.getAndUpdateRenderState(entity, 1.0F);
      entityRenderState.light = 15728880;
      entityRenderState.hitbox = null;
      entityRenderState.shadowPieces.clear();
      entityRenderState.outlineColor = 0;
      drawer.addEntity(entityRenderState, scale, translation, rotation, overrideCameraAngle, x1, y1, x2, y2);
   }
}
