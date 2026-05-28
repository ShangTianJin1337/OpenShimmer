package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.module.modules.misc.ChatSuffix;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ChatCommandSignedC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChatCommandSignedC2SPacket.class})
public class ChatCommandSignedC2SPacketMixin {
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
         && this.command.startsWith("say ")) {
         this.command = this.command + ChatSuffix.suffix.getString();
      }
   }
}
