package dev.greencat.shimmer.module.modules.player;

import dev.greencat.shimmer.event.events.RenderInGameHudEvent;
import dev.greencat.shimmer.event.events.TickEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.util.render.Rect;
import dev.greencat.shimmer.util.render.RenderUtil;
import dev.greencat.shimmer.util.render.TextRenderUtil;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;

public class TargetHUD extends Module {
   public LivingEntity target = null;
   public long targetOutSpace = 0L;

   public TargetHUD() {
      super("TargetHUD", "", -1, Module.Category.PLAYER);
   }

   @ShimmerSubscribe
   public void onTick(TickEvent event) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         if (MinecraftClient.getInstance().crosshairTarget instanceof EntityHitResult entityHitResult
            && entityHitResult.getEntity() instanceof LivingEntity entity
            && !entity.isInvisible()) {
            this.target = entity;
            this.targetOutSpace = System.currentTimeMillis();
         }

         if (this.target != null && this.target.getEntityPos().distanceTo(MinecraftClient.getInstance().player.getEntityPos()) <= 3.0) {
            this.targetOutSpace = System.currentTimeMillis();
         }

         if (System.currentTimeMillis() - this.targetOutSpace >= 2500L || !this.target.isAlive()) {
            this.target = null;
         }
      }
   }

   @ShimmerSubscribe
   public void onRender(RenderInGameHudEvent event) {
      if (mc.world != null && mc.player != null) {
         if (this.target != null) {
            int windowWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int windowHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
            int hudHeight = 50;
            int x = windowWidth / 2 + 20;
            int y = windowHeight / 2 + 20;
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;
            String name = this.target.getDisplayName().getString();
            int nameWidth = tr.getWidth(name);
            int hudWidth = Math.max(120, nameWidth + 50);
            Color bgColor = new Color(0, 0, 0, 160);
            Color healthBgColor = new Color(60, 60, 60, 180);
            float radius = 6.0F;
            Rect.draw2DRoundedRect((float)x, (float)y, (float)hudWidth, (float)hudHeight, radius, 0.0F, bgColor, event.getContext());
            int avatarSize = hudHeight - 10;
            int calculatedSize = (int)((float)avatarSize / Math.max(this.target.getHeight(), 0.5F) * 0.8F);
            if (calculatedSize > 60) {
               calculatedSize = 60;
            }

            if (calculatedSize < 15) {
               calculatedSize = 15;
            }

            int x1 = x + 6;
            int y1 = y + 6;
            int x2 = x1 + avatarSize;
            int y2 = y1 + avatarSize;
            RenderUtil.drawEntity(event.getContext(), x1, y1, x2, y2, calculatedSize, 0.05F, (float)(x1 + x2) / 2.0F, (float)(y1 + y2) / 2.0F, this.target);
            float textScale = 1.0F;
            int textX = x + avatarSize + 10;
            int textY = y + 8;
            TextRenderUtil.drawScaledText(event.getContext(), this.target.getDisplayName(), textX, textY, textScale, Color.WHITE.getRGB());
            float health = this.target.getHealth();
            float maxHealth = this.target.getMaxHealth();
            float healthPercent = MathHelper.clamp(health / maxHealth, 0.0F, 1.0F);
            int barX = x + avatarSize + 10;
            int barY = y + hudHeight - 15;
            int barWidth = hudWidth - avatarSize - 15;
            int barHeight = 8;
            Rect.draw2DRoundedRect((float)barX, (float)barY, (float)barWidth, (float)barHeight, 4.0F, 0.0F, healthBgColor, event.getContext());
            Color healthColor = Color.getHSBColor(healthPercent * 0.33F, 0.9F, 1.0F);
            int currentBarWidth = (int)((float)barWidth * healthPercent);
            if (currentBarWidth > 0) {
               Rect.draw2DRoundedRect((float)barX, (float)barY, (float)currentBarWidth, (float)barHeight, 4.0F, 0.0F, healthColor, event.getContext());
            }

            String hpText = String.format("%.1f", health);
            TextRenderUtil.drawScaledText(
               event.getContext(), Text.of(hpText), (int)((float)(barX + barWidth) - (float)tr.getWidth(hpText) * 0.8F), barY - 8, 0.8F, Color.WHITE.getRGB()
            );
         }
      }
   }
}
