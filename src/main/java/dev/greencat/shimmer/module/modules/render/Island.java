package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.event.events.RenderInGameHudEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.mixin.BossBarHudAccessor;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.Rect;
import dev.greencat.shimmer.util.world.TPSUtil;
import java.awt.Color;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;

public class Island extends Module {
   private static Island INSTANCE;
   private float currentWidth = 100.0F;
   private float currentHeight = 24.0F;
   private float targetWidth = 100.0F;
   private float targetHeight = 24.0F;
   private float currentY = 5.0F;
   private float targetY = 5.0F;
   private boolean isMessageActive = false;
   private long messageStartTime = 0L;
   private static final long MESSAGE_DURATION = 3500L;
   private Text messageTitle = null;
   private Text[] messageLines = null;
   private final Color backgroundColor = Color.BLACK;
   private final float cornerRadius = 12.0F;

   public Island() {
      super("Island", "Dynamic Island HUD", -1, Module.Category.RENDER);
      INSTANCE = this;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.currentHeight = 24.0F;
      this.currentY = 5.0F;
      this.isMessageActive = false;
   }

   public static void addMessage(Text... texts) {
      if (INSTANCE != null && INSTANCE.isEnabled()) {
         INSTANCE.triggerMessage(texts);
      }
   }

   private void triggerMessage(Text... texts) {
      if (texts != null && texts.length != 0) {
         this.messageTitle = texts[0];
         if (texts.length > 1) {
            this.messageLines = new Text[texts.length - 1];
            System.arraycopy(texts, 1, this.messageLines, 0, texts.length - 1);
         } else {
            this.messageLines = new Text[0];
         }

         this.isMessageActive = true;
         this.messageStartTime = System.currentTimeMillis();
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderInGameHudEvent event) {
      if (mc.world != null && mc.player != null) {
         if (!mc.getDebugHud().shouldShowDebugHud()) {
            DrawContext context = event.getContext();
            int screenWidth = context.getScaledWindowWidth();
            if (this.isMessageActive && System.currentTimeMillis() - this.messageStartTime > 3500L) {
               this.isMessageActive = false;
            }

            this.calculateTargets();
            this.calculatePosition();
            float lerpSpeed = 0.15F;
            this.currentWidth = this.lerp(this.currentWidth, this.targetWidth, lerpSpeed);
            this.currentHeight = this.lerp(this.currentHeight, this.targetHeight, lerpSpeed);
            this.currentY = this.lerp(this.currentY, this.targetY, lerpSpeed);
            float x = ((float)screenWidth - this.currentWidth) / 2.0F;
            float y = this.currentY;
            Rect.draw2DRoundedRect(x, y, this.currentWidth, this.currentHeight, 12.0F, 0.0F, this.backgroundColor, context);
            if (this.isMessageActive) {
               this.renderMessageText(context, screenWidth, y);
            } else {
               this.renderIdleText(context, screenWidth, y);
            }
         }
      }
   }

   private void calculatePosition() {
      int bossBarCount = this.getBossBarCount();
      int bossBarHeight = 19;
      int baseY = 5;
      this.targetY = (float)(baseY + bossBarCount * bossBarHeight);
   }

   private void calculateTargets() {
      if (this.isMessageActive) {
         int maxTextWidth = mc.textRenderer.getWidth(this.messageTitle);

         for (Text line : this.messageLines) {
            maxTextWidth = Math.max(maxTextWidth, mc.textRenderer.getWidth(line));
         }

         this.targetWidth = (float)(maxTextWidth + 30);
         this.targetHeight = (float)(19 + this.messageLines.length * 10 + 6);
         if (this.targetHeight < 24.0F) {
            this.targetHeight = 24.0F;
         }
      } else {
         String info = this.getIdleString();
         this.targetWidth = (float)(mc.textRenderer.getWidth(info) + 24);
         this.targetHeight = 24.0F;
      }
   }

   private void renderIdleText(DrawContext context, int screenWidth, float y) {
      if (!(Math.abs(this.currentWidth - this.targetWidth) > 20.0F)) {
         String text = this.getIdleString();
         int textWidth = mc.textRenderer.getWidth(text);
         float textY = y + (this.currentHeight - 8.0F) / 2.0F;
         context.drawText(mc.textRenderer, text, (screenWidth - textWidth) / 2, (int)textY, Color.WHITE.getRGB(), true);
      }
   }

   private void renderMessageText(DrawContext context, int screenWidth, float y) {
      if (!(this.currentHeight < this.targetHeight * 0.8F)) {
         int titleWidth = mc.textRenderer.getWidth(this.messageTitle);
         context.drawText(mc.textRenderer, this.messageTitle, (screenWidth - titleWidth) / 2, (int)(y + 6.0F), Color.WHITE.getRGB(), true);
         int currentY = (int)(y + 19.0F);

         for (Text line : this.messageLines) {
            int lineWidth = mc.textRenderer.getWidth(line);
            context.drawText(mc.textRenderer, line, (screenWidth - lineWidth) / 2, currentY, Color.GRAY.getRGB(), true);
            currentY += 10;
         }
      }
   }

   private String getIdleString() {
      String tps = String.format("%.1f", TPSUtil.INSTANCE.getTPS());
      int fps = mc.getCurrentFps();
      return "Shimmer | FPS: " + fps + " | TPS: " + tps;
   }

   private float lerp(float start, float end, float delta) {
      return start + (end - start) * delta;
   }

   private int getBossBarCount() {
      if (mc.inGameHud == null) {
         return 0;
      } else {
         BossBarHud hud = mc.inGameHud.getBossBarHud();
         if (hud == null) {
            return 0;
         } else {
            Map<UUID, ClientBossBar> map = ((BossBarHudAccessor)hud).getBossBars();
            return map != null ? map.size() : 0;
         }
      }
   }
}
