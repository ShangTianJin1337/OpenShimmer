package dev.greencat.shimmer.module.modules.render;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.RenderInGameHudEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.Module.Category;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.util.render.Shadow;
import dev.greencat.shimmer.util.render.Shadow.ShadowLocation;
import dev.greencat.shimmer.util.render.animation.AnimationEngine;
import java.awt.Color;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.text.Text;

public class Hud extends Module {
   public final BooleanSetting arraylist = new BooleanSetting("Arraylist", "Renders the Shimmer arraylist.", true);
   private final AnimationEngine rectAnimation = new AnimationEngine(0, 0);
   private long colorCounter = 0L;
   private long colorDelay = 0L;
   private final AnimationEngine normalInformationAnimation = new AnimationEngine(0, 0);
   private final int arrayListColor = new Color(0, 0, 0, 120).getRGB();
   private final Color centerHUDColor = new Color(0, 0, 0, 120);
   private static CopyOnWriteArrayList<Text> noticeList = new CopyOnWriteArrayList();
   private static int arrayListHeight = 0;

   public Hud() {
      super("HUD", "Renders the Shimmer hud.", -1, Module.Category.RENDER);
      this.addSettings(new Setting[]{this.arraylist});
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   public static void onMessage(Text... texts) {
      Island.addMessage(texts);
   }

   public AnimationEngine getRectAnimation() {
      return this.rectAnimation;
   }

   public int getArrayListHeight() {
      return arrayListHeight;
   }

   @ShimmerSubscribe
   public void onRender(RenderInGameHudEvent event) {
      if (mc.world != null && mc.player != null) {
         if (!mc.getDebugHud().shouldShowDebugHud()) {
            if (!Shimmer.getInstance().isSorted) {
               Shimmer.getInstance().isSorted = true;
               Shimmer.getInstance().getModuleManager().refreshEnabled();
            }

            int screenWidth = mc.getWindow().getScaledWidth();
            if (System.currentTimeMillis() - this.colorDelay >= 30L) {
               this.colorDelay = System.currentTimeMillis();
               this.colorCounter++;
            }

            int y = 2;
            int lastWidth = mc.textRenderer.getWidth((String)Shimmer.getInstance().getModuleManager().enabledModules.getFirst()) + 8;
            arrayListHeight = 0;
            if (this.arraylist.isEnabled()) {
               for (String str : Shimmer.getInstance().getModuleManager().enabledModules) {
                  boolean swap = (int)((float)((long)y + this.colorCounter) / 3.0F / 30.0F) % 2 == 0;
                  Color currentColor = Color.getHSBColor(
                     0.925F,
                     ((swap ? 30.0F - (float)((long)y + this.colorCounter) / 3.0F % 30.0F : (float)((long)y + this.colorCounter) / 3.0F % 30.0F) + 35.0F)
                        / 100.0F,
                     1.0F
                  );
                  event.getContext().fill(screenWidth - mc.textRenderer.getWidth(str) - 3 - 5, y - 2, screenWidth, y + 10, this.arrayListColor);
                  event.getContext().fill(screenWidth - 3, y - 2, screenWidth, y + 10, currentColor.getRGB());
                  int widthDiffer = lastWidth - mc.textRenderer.getWidth(str) - 8;
                  Shadow.drawShadow(
                     screenWidth - mc.textRenderer.getWidth(str) - 3 - 5 - widthDiffer,
                     y - 14,
                     widthDiffer,
                     12,
                     event.getContext(),
                     Shadow.ShadowLocation.BOTTOM
                  );
                  Shadow.drawShadow(screenWidth - mc.textRenderer.getWidth(str) - 3 - 5, y - 2, 0, 12, event.getContext(), Shadow.ShadowLocation.LEFT);
                  Shadow.drawShadow(screenWidth - mc.textRenderer.getWidth(str) - 3 - 5, y - 2, 0, 12, event.getContext(), Shadow.ShadowLocation.BOTTOM_LEFT);
                  event.getContext().drawTextWithShadow(mc.textRenderer, str, screenWidth - mc.textRenderer.getWidth(str) - 5, y, currentColor.getRGB());
                  lastWidth = mc.textRenderer.getWidth(str) + 8;
                  y += 12;
               }
            }

            arrayListHeight = y - 2;
            Shadow.drawShadow(screenWidth - lastWidth, y - 14, lastWidth, 12, event.getContext(), Shadow.ShadowLocation.BOTTOM);
         }
      }
   }
}
