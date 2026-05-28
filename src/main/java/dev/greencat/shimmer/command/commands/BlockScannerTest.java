package dev.greencat.shimmer.command.commands;

import dev.greencat.shimmer.command.Command;
import dev.greencat.shimmer.util.HaikuLogger;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class BlockScannerTest extends Command {
   public BlockScannerTest() {
      super("BlockScannerTest", "Test BlockScanner", "bs", "bs");
   }

   @Override
   public void onCommand(String[] args, String command) {
      if (args.length == 6) {
         BlockPos.iterate(
               new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])),
               new BlockPos(Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]))
            )
            .forEach(it -> {
               if (MinecraftClient.getInstance().world.getBlockState(it).getBlock() == Blocks.LAVA) {
                  HaikuLogger.info(it.toString());
               } else {
                  System.out.println(it.toString() + " " + MinecraftClient.getInstance().world.getBlockState(it).toString());
               }
            });
      }
   }
}
