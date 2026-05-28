package dev.greencat.shimmer.mixin;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.PacketEvent;
import dev.greencat.shimmer.event.events.PacketEvent.Type;
import dev.greencat.shimmer.util.HaikuLogger;
import dev.greencat.shimmer.util.irc.IRC;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientConnection.class})
public class ClientConnectionMixin {
   @Inject(
      method = {"channelRead0*"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void packetReceive(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
      PacketEvent event = new PacketEvent(packet, PacketEvent.Type.RECEIVE);
      Shimmer.getInstance().getEventBus().post(event);
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"send(Lnet/minecraft/network/packet/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void packetSend(Packet<?> packet, CallbackInfo ci) {
      if (packet instanceof ChatMessageC2SPacket pack) {
         if (pack.chatMessage().startsWith("!")) {
            if (pack.chatMessage().startsWith("!reconnect")) {
               if (Shimmer.getInstance().IRC == null) {
                  try {
                     Shimmer.getInstance().IRC = new IRC();
                     Shimmer.getInstance().IRC.start();
                  } catch (Exception var5) {
                     HaikuLogger.info("[IRC] IRC链接失败");
                  }
               } else {
                  HaikuLogger.info("[IRC] IRC已经处于链接状态");
               }

               ci.cancel();
            } else if (pack.chatMessage().startsWith("!disconnect")) {
               Thread thread = new Thread(() -> {
                  if (Shimmer.getInstance().IRC != null) {
                     try {
                        Shimmer.getInstance().IRC.closed = true;
                        Shimmer.getInstance().IRC.interrupt();
                        if (!Shimmer.getInstance().IRC.socket.isClosed()) {
                           Shimmer.getInstance().IRC.socket.shutdownInput();
                        }

                        if (!Shimmer.getInstance().IRC.socket.isClosed()) {
                           Shimmer.getInstance().IRC.socket.shutdownOutput();
                        }

                        if (!Shimmer.getInstance().IRC.socket.isClosed()) {
                           Shimmer.getInstance().IRC.socket.close();
                        }

                        HaikuLogger.info("[IRC] IRC已断开链接,输入!reconnect重新链接");
                        Shimmer.getInstance().IRC = null;
                     } catch (Exception var1) {
                        var1.printStackTrace();
                     }
                  } else {
                     HaikuLogger.info("[IRC] IRC已经处于断开链接状态");
                  }
               });
               thread.start();
               ci.cancel();
            } else if (pack.chatMessage().startsWith("!toggle")) {
               if (!Shimmer.getInstance().isIRCToggled) {
                  HaikuLogger.info("[IRC] IRC现在已经设为默认聊天频道");
                  Shimmer.getInstance().isIRCToggled = true;
               } else {
                  HaikuLogger.info("[IRC] IRC现在已经不再是默认聊天频道");
                  Shimmer.getInstance().isIRCToggled = false;
               }

               ci.cancel();
            } else if (pack.chatMessage().startsWith("!list")) {
               if (Shimmer.getInstance().IRC != null) {
                  Shimmer.getInstance().IRC.write("#requestPlayerList");
                  ci.cancel();
               }
            } else if (Shimmer.getInstance().IRC != null) {
               if (!pack.chatMessage().equals("!")) {
                  Shimmer.getInstance().IRC.write(pack.chatMessage().substring(1));
               }
            } else {
               HaikuLogger.info("[IRC] IRC已断开链接,输入!reconnect重新链接");
            }

            ci.cancel();
         }

         if (pack.chatMessage().startsWith(Shimmer.getInstance().getCommandManager().prefix)) {
            Shimmer.getInstance().getCommandManager().execute(pack.chatMessage());
            ci.cancel();
         }

         if (Shimmer.getInstance().isIRCToggled && !pack.chatMessage().startsWith("!")) {
            if (Shimmer.getInstance().IRC != null) {
               Shimmer.getInstance().IRC.write(pack.chatMessage());
            } else {
               HaikuLogger.info("[IRC] IRC已断开链接,输入!reconnect重新链接");
            }

            ci.cancel();
         }
      }

      PacketEvent event = new PacketEvent(packet, PacketEvent.Type.SEND);
      Shimmer.getInstance().getEventBus().post(event);
      if (event.isCancelled()) {
         ci.cancel();
      }

      if (!ServerRotation.onPacketSent(packet)) {
         ci.cancel();
      }
   }
}
