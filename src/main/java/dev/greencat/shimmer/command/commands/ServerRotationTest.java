package dev.greencat.shimmer.command.commands;

import dev.greencat.shimmer.command.Command;
import dev.greencat.shimmer.util.HaikuLogger;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;

public class ServerRotationTest extends Command {
   public ServerRotationTest() {
      super("ServerRotationTest", "Test ServerRotation", "sr", "sr");
   }

   @Override
   public void onCommand(String[] args, String command) {
      if (args.length == 2) {
         ServerRotation.useServerRotation = !ServerRotation.useServerRotation;
         HaikuLogger.info(ServerRotation.useServerRotation + "");
         ServerRotation.serverYaw = Float.parseFloat(args[0]);
         ServerRotation.serverPitch = Float.parseFloat(args[1]);
      }
   }
}
