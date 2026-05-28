package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.module.modules.macro.MacroProtector;
import dev.greencat.shimmer.module.modules.misc.ChatSuffix;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CommandExecutionC2SPacket.class})
public class CommandExecutionC2SPacketMixin {
   @Mutable
   @Shadow
   @Final
   private String command;

   @Inject(
      method = {"write"},
      at = {@At("HEAD")}
   )
   public void onWrite(PacketByteBuf buf, CallbackInfo ci) {
      if (Shimmer.getInstance().getModuleManager().getModule("ChatSuffix") != null
         && Shimmer.getInstance().getModuleManager().getModule("ChatSuffix").isEnabled()
         && (
            this.command.startsWith("pc ")
               || this.command.startsWith("gc ")
               || this.command.startsWith("ac ")
               || this.command.startsWith("r ")
               || this.command.startsWith("say ")
         )) {
         this.command = this.command + ChatSuffix.suffix.getString();
      }

      if (this.command.startsWith("warp")) {
         MacroProtector.lastWarp = System.currentTimeMillis();
      }
   }
}
