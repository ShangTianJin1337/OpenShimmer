package dev.greencat.shimmer.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ClickableWidget.class})
public abstract class MixinClickableWidget {
   @Shadow
   public boolean visible;
   @Shadow
   protected boolean hovered;
   @Shadow
   @Final
   private TooltipState tooltip;
   @Shadow
   protected int width;
   @Shadow
   protected int height;

   @Shadow
   protected abstract void renderWidget(DrawContext var1, int var2, int var3, float var4);

   @Shadow
   public abstract boolean isHovered();

   @Shadow
   public abstract boolean isFocused();

   @Shadow
   public abstract ScreenRect getNavigationFocus();

   @Shadow
   public abstract int getX();

   @Shadow
   public abstract int getY();

   @Overwrite
   public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
      if (this.visible) {
         try {
            this.hovered = context.scissorContains(mouseX, mouseY)
               && mouseX >= this.getX()
               && mouseY >= this.getY()
               && mouseX < this.getX() + this.width
               && mouseY < this.getY() + this.height;
            this.renderWidget(context, mouseX, mouseY, delta);
            this.tooltip.render(context, mouseX, mouseY, this.isHovered(), this.isFocused(), this.getNavigationFocus());
         } catch (Exception var6) {
         }
      }
   }
}
