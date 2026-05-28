package dev.greencat.shimmer.command.commands;

import dev.greencat.shimmer.command.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class RayCastCommand extends Command {
   public RayCastCommand() {
      super("Ray", "test ray cast function", "ray", "ray");
   }

   @Override
   public void onCommand(String[] args, String command) {
      if (args.length == 3) {
         BlockPos pos = new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
         BlockPos start = MinecraftClient.getInstance().player.getBlockPos().down();
         RayCastTestClass.allPos.clear();
         RayCastTestClass.debugStart = start;
         RayCastTestClass.debugEnd = pos;
         RayCastTestClass.nextStep();
      }
   }
}
