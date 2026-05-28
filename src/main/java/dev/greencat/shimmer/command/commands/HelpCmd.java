package dev.greencat.shimmer.command.commands;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.command.Command;
import dev.greencat.shimmer.util.HaikuLogger;
import java.util.stream.Collectors;

public class HelpCmd extends Command {
   public HelpCmd() {
      super("Help", "Shows a list of commands", "help", "h");
   }

   @Override
   public void onCommand(String[] args, String command) {
      if (args.length == 0) {
         HaikuLogger.info(
            "Commands: " + (String)Shimmer.getInstance().getCommandManager().commands.stream().map(Command::getName).collect(Collectors.joining(", "))
         );
      } else {
         for (Command cmd : Shimmer.getInstance().getCommandManager().commands) {
            if (cmd.getName().equalsIgnoreCase(args[0])) {
               HaikuLogger.info(cmd.getSyntax());
               return;
            }
         }

         HaikuLogger.error("Command not found.");
      }
   }
}
