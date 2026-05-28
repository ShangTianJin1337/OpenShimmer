package dev.greencat.shimmer.command.commands;

import dev.greencat.shimmer.command.Command;
import dev.greencat.shimmer.util.HaikuLogger;
import dev.greencat.shimmer.util.player.WalkerUtils;
import net.minecraft.util.math.BlockPos;

public class Travel extends Command {
   public Travel() {
      super("Travel", "test baritone,accept <x> <y> <z> args", "travel", "travel");
   }

   @Override
   public void onCommand(String[] args, String command) {
      if (args.length == 3) {
         BlockPos pos = new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
         if (!WalkerUtils.walkTo(pos)) {
            HaikuLogger.info("无法找到路径");
         }
      }
   }
}
