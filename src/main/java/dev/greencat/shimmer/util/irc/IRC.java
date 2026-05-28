package dev.greencat.shimmer.util.irc;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.util.HaikuLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.MinecraftClient;

public class IRC extends Thread {
   public Socket socket;
   public BufferedReader reader;
   public PrintWriter writer;
   public boolean closed = false;

   public IRC() throws IOException {
      try {
         this.socket = new Socket("frp-rib.com", 12783);
         this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
         this.writer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
         MinecraftClient.getInstance().execute(() -> HaikuLogger.info("[IRC] IRC已链接,输入!disconnect断开链接,!{消息}发送消息"));
      } catch (IOException var4) {
         IOException e = var4;

         try {
            this.socket.close();
            this.reader.close();
            this.writer.close();
            e.printStackTrace();
         } catch (Exception var3) {
         }
      }
   }

   public void run() {
      String message = "";
      if (this.socket != null) {
         while (true) {
            try {
               if ((message = this.reader.readLine()) == null) {
                  break;
               }
            } catch (IOException var5) {
               try {
                  if (!this.closed) {
                     if (!Shimmer.getInstance().IRC.socket.isClosed()) {
                        Shimmer.getInstance().IRC.socket.shutdownInput();
                     }

                     if (!Shimmer.getInstance().IRC.socket.isClosed()) {
                        Shimmer.getInstance().IRC.socket.shutdownOutput();
                     }

                     if (!this.socket.isClosed()) {
                        this.socket.close();
                     }

                     MinecraftClient.getInstance().execute(() -> HaikuLogger.info("[IRC] IRC已断开链接,输入!reconnect重新链接"));
                     Shimmer.getInstance().IRC = null;
                  }
               } catch (Exception var4) {
                  var4.printStackTrace();
               }
               break;
            }

            this.received(message);
         }
      }
   }

   public void received(String message) {
      if (message.equals("#requestUsername")) {
         this.write("#Username " + MinecraftClient.getInstance().getSession().getUsername());
      }

      if (message.equals("#noVerify")) {
         MinecraftClient.getInstance().execute(() -> HaikuLogger.info("[IRC] You was kicked from IRC channel because of verify too late"));
      }

      if (message.equals("#CatGirl")) {
         this.write("#CatGirl");
      }

      if (message.startsWith("#newPlayerJoin")) {
         String username = message.split("\\|")[1];
         MinecraftClient.getInstance().execute(() -> HaikuLogger.ircJoin(username));
      }

      if (message.startsWith("#playerLeave")) {
         String username = message.split("\\|")[1];
         MinecraftClient.getInstance().execute(() -> HaikuLogger.ircLeave(username));
      }

      if (message.startsWith("#playerList")) {
         String[] username = message.split("\\|");
         MinecraftClient.getInstance().execute(() -> HaikuLogger.ircChatSplitter());
         MinecraftClient.getInstance().execute(() -> HaikuLogger.ircOnlinePlayerNumber(username.length - 1));
         MinecraftClient.getInstance().execute(() -> HaikuLogger.ircChatSplitter());
         StringBuilder sb = new StringBuilder();

         for (int i = 0; i < username.length; i++) {
            if (i != 0) {
               if (i % 3 == 0) {
                  sb.append(username[i] + " ");
                  StringBuilder finalSb1 = sb;
                  MinecraftClient.getInstance().execute(() -> HaikuLogger.ircSystemMessage(finalSb1.toString()));
                  sb = new StringBuilder();
               } else {
                  sb.append(username[i] + " ");
               }
            }
         }

         if ((username.length - 1) % 3 != 0) {
            StringBuilder finalSb = sb;
            MinecraftClient.getInstance().execute(() -> HaikuLogger.ircSystemMessage(finalSb.toString()));
         }

         MinecraftClient.getInstance().execute(HaikuLogger::ircChatSplitter);
      }

      if (message.startsWith("#ChatMessage")) {
         String username = message.split("\\|")[1];
         String chatMessage = message.split("\\|")[2];
         MinecraftClient.getInstance().execute(() -> HaikuLogger.irc(username, chatMessage));
      }
   }

   public void write(String message) {
      if (this.writer != null) {
         this.writer.println(message);
         this.writer.flush();
      }
   }
}
