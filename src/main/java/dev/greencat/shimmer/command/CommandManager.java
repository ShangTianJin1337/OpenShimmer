package dev.greencat.shimmer.command;

import dev.greencat.shimmer.command.commands.BlockScannerTest;
import dev.greencat.shimmer.command.commands.EtherwrapTestCommand;
import dev.greencat.shimmer.command.commands.HelpCmd;
import dev.greencat.shimmer.command.commands.ServerRotationTest;
import dev.greencat.shimmer.command.commands.ToggleCmd;
import dev.greencat.shimmer.command.commands.Travel;
import dev.greencat.shimmer.util.HaikuLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
   public String prefix = "-";
   public List<Command> commands = new ArrayList();
   boolean commandFound;

   public CommandManager() {
      this.commands.add(new HelpCmd());
      this.commands.add(new ToggleCmd());
      this.commands.add(new BlockScannerTest());
      this.commands.add(new ServerRotationTest());
      this.commands.add(new Travel());
      this.commands.add(new EtherwrapTestCommand());
   }

   public void execute(String command) {
      if (command.startsWith(this.prefix)) {
         String message = command.substring(this.prefix.length());
         if (message.split(" ").length > 0) {
            this.commandFound = false;
            String commandName = message.split(" ")[0];

            for (Command c : this.commands) {
               if (c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
                  c.onCommand((String[])Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
                  this.commandFound = true;
                  break;
               }
            }

            if (!this.commandFound) {
               HaikuLogger.error("Command not found, use " + this.prefix + "help");
            }
         }
      }
   }

   public Command getCommand(String name) {
      for (Command command : this.commands) {
         if (command.name.equalsIgnoreCase(name)) {
            return command;
         }
      }

      return null;
   }
}
