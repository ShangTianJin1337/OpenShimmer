package dev.greencat.shimmer.command.commands;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.command.Command;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.util.HaikuLogger;

public class ToggleCmd extends Command {
   public ToggleCmd() {
      super("Toggle", "Toggle a module.", "toggle <mod>", "t");
   }

   @Override
   public void onCommand(String[] args, String command) {
      if (args.length == 0) {
         HaikuLogger.error("Please specify a module.");
      } else {
         String moduleName = args[0];
         Module module = Shimmer.getInstance().getModuleManager().getModule(String.valueOf(moduleName));
         if (module == null) {
            HaikuLogger.error("Module not found.");
         } else {
            module.toggle();
         }
      }
   }
}
