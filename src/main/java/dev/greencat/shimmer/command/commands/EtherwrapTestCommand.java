package dev.greencat.shimmer.command.commands;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.command.Command;

public class EtherwrapTestCommand extends Command {
   public EtherwrapTestCommand() {
      super("ether", "", "ether", "ether");
   }

   @Override
   public void onCommand(String[] args, String command) {
      Shimmer.etherwarpHelper.next();
   }
}
