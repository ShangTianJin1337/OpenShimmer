package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.RenderUtil;
import dev.greencat.shimmer.util.render.animation.AnimationEngine;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Vec3d;

public class LineGlyphs extends Module {
   private final List<LineGlyphs.GlyphWalker> walkers = new ArrayList();
   private final Random random = new Random();
   private final int MAX_WALKERS = 80;
   private final double SPAWN_RANGE = 20.0;
   private final int TRAIL_LENGTH = 20;
   private final double ANIM_SPEED = 0.4;
   private final int R = 255;
   private final int G = 180;
   private final int B = 215;

   public LineGlyphs() {
      super("LineGlyphs", "Renders cybernetic lines moving on a grid with trails", -1, Module.Category.RENDER);
   }

   @Override
   public void onEnable() {
      this.walkers.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      for (LineGlyphs.GlyphWalker walker : this.walkers) {
         walker.cleanup();
      }

      this.walkers.clear();
      super.onDisable();
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (mc.player != null && mc.world != null) {
         Iterator<LineGlyphs.GlyphWalker> iterator = this.walkers.iterator();

         while (iterator.hasNext()) {
            LineGlyphs.GlyphWalker walker = (LineGlyphs.GlyphWalker)iterator.next();
            boolean tooFar = walker.currentHeadPos.distanceTo(mc.player.getEntityPos()) > 35.0;
            boolean dead = walker.ticksExisted > walker.maxLife;
            if (!dead && !tooFar) {
               walker.onTick();
            } else {
               walker.cleanup();
               iterator.remove();
            }
         }

         if (this.walkers.size() < 80) {
            Vec3d playerPos = mc.player.getEntityPos();
            double x = Math.floor(playerPos.x + (this.random.nextDouble() * 20.0 * 2.0 - 20.0));
            double y = Math.floor(playerPos.y + this.random.nextDouble() * 20.0 / 2.0);
            double z = Math.floor(playerPos.z + (this.random.nextDouble() * 20.0 * 2.0 - 20.0));
            this.walkers.add(new LineGlyphs.GlyphWalker(new Vec3d(x, y, z)));
         }
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderEvent.Post event) {
      if (mc.world != null && mc.player != null) {
         for (LineGlyphs.GlyphWalker walker : this.walkers) {
            Vec3d activeHeadPos = walker.getRenderPos();
            List<Vec3d> trail = walker.trailPoints;
            float maxIndex = (float)Math.max(1, trail.size());

            for (int i = 0; i < trail.size() - 1; i++) {
               Vec3d p1 = (Vec3d)trail.get(i);
               Vec3d p2 = (Vec3d)trail.get(i + 1);
               float progress = (float)i / maxIndex;
               int alpha = 60 + (int)(progress * 195.0F);
               if (alpha > 255) {
                  alpha = 255;
               }

               RenderUtil.drawLineNoESP(p1, p2, new Color(255, 180, 215, alpha), event.storage.getEntityVertexConsumers());
            }

            if (!trail.isEmpty()) {
               Vec3d lastStaticPoint = (Vec3d)trail.get(trail.size() - 1);
               RenderUtil.drawLineNoESP(lastStaticPoint, activeHeadPos, new Color(255, 180, 215, 255), event.storage.getEntityVertexConsumers());
            }
         }
      }
   }

   private class GlyphWalker {
      Vec3d currentHeadPos;
      Vec3d targetGridPos;
      LinkedList<Vec3d> trailPoints = new LinkedList();
      int ticksExisted = 0;
      int maxLife;
      AnimationEngine animXY;
      AnimationEngine animZ;

      public GlyphWalker(Vec3d startPos) {
         this.currentHeadPos = startPos;
         this.targetGridPos = startPos;
         this.maxLife = 300 + LineGlyphs.this.random.nextInt(400);
         this.trailPoints.add(startPos);
         this.animXY = new AnimationEngine((int)startPos.x, (int)startPos.y);
         this.animZ = new AnimationEngine((int)startPos.z, 0);
         this.pickNewTarget();
      }

      public void onTick() {
         this.ticksExisted++;
         if (!this.animXY.isRunning && !this.animZ.isRunning) {
            this.trailPoints.add(this.targetGridPos);
            if (this.trailPoints.size() > 20) {
               this.trailPoints.removeFirst();
            }

            this.pickNewTarget();
         }
      }

      public Vec3d getRenderPos() {
         this.animXY.RenderTick();
         this.animZ.RenderTick();
         this.currentHeadPos = new Vec3d(this.animXY.xCoord, this.animXY.yCoord, this.animZ.xCoord);
         return this.currentHeadPos;
      }

      public void cleanup() {
         if (this.animXY != null) {
            this.animXY.destroy();
         }

         if (this.animZ != null) {
            this.animZ.destroy();
         }
      }

      private void pickNewTarget() {
         Vec3d currentGrid = (Vec3d)this.trailPoints.getLast();
         int axis = LineGlyphs.this.random.nextInt(3);
         int direction = LineGlyphs.this.random.nextBoolean() ? 1 : -1;
         double tx = currentGrid.x;
         double ty = currentGrid.y;
         double tz = currentGrid.z;
         switch (axis) {
            case 0:
               tx += (double)direction;
               break;
            case 1:
               ty += (double)direction;
               break;
            case 2:
               tz += (double)direction;
         }

         this.targetGridPos = new Vec3d(tx, ty, tz);
         int easeType = 1;
         this.animXY.moveTo((float)tx, (float)ty, 0.4, easeType);
         this.animZ.moveTo((float)tz, 0.0F, 0.4, easeType);
      }
   }
}
