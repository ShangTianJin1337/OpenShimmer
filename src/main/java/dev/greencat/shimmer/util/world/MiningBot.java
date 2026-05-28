package dev.greencat.shimmer.util.world;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.util.HaikuLogger;
import dev.greencat.shimmer.util.math.MathUtil;
import dev.greencat.shimmer.util.player.rotation.RotationUtil;
import dev.greencat.shimmer.util.player.rotation.ServerRotation;
import dev.greencat.shimmer.util.player.rotation.SmoothRotation;
import dev.greencat.shimmer.util.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class MiningBot {
   private static String currentModule = null;
   private static boolean isWorking = false;
   private static List<MiningBot.BlockData> pos = new ArrayList();
   private static List<Block> targetBlocks;
   private static boolean randomMoveEnable = false;
   private static boolean sneakEnable = false;
   private static boolean autoReset = false;
   private static boolean realNuker = false;
   private static boolean serverRotation = false;
   private static MiningBot.isTargetCallback callback;
   private static boolean onlyUpperThanPlayer = false;
   private static boolean oneTick = false;
   private static int timedOutMills = 20000;
   private static List<BlockPos> blacklist = new ArrayList();
   public static BlockPos target;
   public static BlockPos prevTarget;
   public static Block lastMined;
   public static long blacklistCleanTimer = 0L;
   public static long miningDelay = 0L;
   private static long lastBreakTime = 0L;
   private static boolean lastRotate = false;
   private static int moveTick = 0;
   private static boolean prevStatus = false;
   private static Timer timer = new Timer();
   private static final ArrayList<Vec3d> SHIFTING = new ArrayList();
   static Block prevBlock = Blocks.AIR;

   public static boolean isWorking() {
      return isWorking;
   }

   public static boolean setup(
      String name,
      List<Block> target,
      boolean randomMoveEnable,
      boolean sneakEnable,
      boolean autoReset,
      boolean onlyUpperThanPlayer,
      boolean realNuker,
      boolean serverRotation,
      boolean oneTick,
      MiningBot.isTargetCallback callback
   ) {
      return setup(name, target, randomMoveEnable, sneakEnable, autoReset, onlyUpperThanPlayer, realNuker, serverRotation, oneTick, 20000, callback);
   }

   public static boolean setup(
      String name,
      List<Block> target,
      boolean randomMoveEnable,
      boolean sneakEnable,
      boolean autoReset,
      boolean onlyUpperThanPlayer,
      boolean realNuker,
      boolean serverRotation,
      boolean oneTick,
      int timedOutMills,
      MiningBot.isTargetCallback callback
   ) {
      if (isWorking) {
         return false;
      } else {
         isWorking = true;
         currentModule = name;
         targetBlocks = target;
         prevTarget = null;
         lastMined = null;
         MiningBot.timedOutMills = timedOutMills;
         MiningBot.randomMoveEnable = randomMoveEnable;
         MiningBot.sneakEnable = sneakEnable;
         MiningBot.autoReset = autoReset;
         MiningBot.target = null;
         MiningBot.onlyUpperThanPlayer = onlyUpperThanPlayer;
         MiningBot.realNuker = realNuker;
         MiningBot.callback = callback;
         prevStatus = false;
         MiningBot.serverRotation = serverRotation;
         MiningBot.oneTick = oneTick;
         lastBreakTime = System.currentTimeMillis();
         moveTick = 0;
         if (sneakEnable && MinecraftClient.getInstance().options != null) {
            MinecraftClient.getInstance().options.sneakKey.setPressed(true);
         }

         return true;
      }
   }

   public static void onTickPre() {
      if (isWorking) {
         if (System.currentTimeMillis() - blacklistCleanTimer >= 30000L) {
            blacklistCleanTimer = System.currentTimeMillis();
            blacklist.clear();
         }

         pos.clear();
         int range = 10;
         if (target == null && prevTarget != null || target != null && !target.equals(prevTarget)) {
            prevTarget = target;
            lastBreakTime = System.currentTimeMillis();
         }

         if (autoReset && System.currentTimeMillis() - lastBreakTime >= (long)timedOutMills && !realNuker) {
            HaikuLogger.info("Nuker超时，自动重置");
            new Thread(() -> {
               try {
                  Thread.sleep(1000L);
                  String moduleName = currentModule;
                  MinecraftClient.getInstance().options.attackKey.setPressed(false);
                  blacklist.add(target);
                  if (Shimmer.getInstance().getModuleManager().getModule(moduleName) != null) {
                     Shimmer.getInstance().getModuleManager().getModule(moduleName).setEnabled(false);
                     Thread.sleep(2500L);
                     blacklistCleanTimer = System.currentTimeMillis();
                     Shimmer.getInstance().getModuleManager().getModule(moduleName).setEnabled(true);
                  }
               } catch (InterruptedException var1x) {
                  throw new RuntimeException(var1x);
               }
            }).start();
         }

         if (MinecraftClient.getInstance().player != null) {
            BlockPos blockPos1 = MinecraftClient.getInstance().player.getBlockPos();
            CopyOnWriteArrayList<MiningBot.BlockData> blockPos = new CopyOnWriteArrayList();

            for (int i = 0; i < range; i++) {
               for (int j = 0; j < range; j++) {
                  for (int k = 0; k < range; k++) {
                     BlockPos cp = blockPos1.add(i - range / 2, j - range / 2, k - range / 2);
                     if (isTarget(cp)) {
                        blockPos.add(new MiningBot.BlockData(cp, null));
                     }
                  }
               }
            }

            if (target != null && MinecraftClient.getInstance().world.getBlockState(target).isAir()) {
               target = null;
            }

            Vec3d start = Vec3d.fromPolar(MinecraftClient.getInstance().player.getPitch(), MinecraftClient.getInstance().player.getYaw());
            if (serverRotation && realNuker) {
               for (MiningBot.BlockData b : blockPos) {
                  b.lookPos = Vec3d.of(b.blockPos);
                  pos.add(b);
               }

               pos.sort(
                  (pos, pos1) -> (int)(
                        pos.blockPos.getSquaredDistance(MinecraftClient.getInstance().player.getEyePos())
                           - pos1.blockPos.getSquaredDistance(MinecraftClient.getInstance().player.getEyePos())
                     )
               );
            } else {
               while (!blockPos.isEmpty()) {
                  double lastAngle = Double.MAX_VALUE;
                  MiningBot.BlockData blockPos2 = null;
                  Vec3d vc = null;

                  for (MiningBot.BlockData b : blockPos) {
                     if (blockPos2 == null) {
                        blockPos2 = b;
                     }

                     Vec3d v = traversal(b.blockPos, start);
                     if (v != null) {
                        Vec3d vv = v.subtract(MinecraftClient.getInstance().player.getEyePos());
                        double angle = MathUtil.calculateAngle(vv, start);
                        if (angle < lastAngle) {
                           lastAngle = angle;
                           blockPos2 = b;
                           b.lookPos = v;
                           vc = v;
                        }
                     } else {
                        blockPos.remove(b);
                     }
                  }

                  if (vc != null) {
                     start = vc;
                     blockPos.remove(blockPos2);
                     pos.add(blockPos2);
                  }
               }
            }

            if (!pos.isEmpty()) {
               if (target == null || !isTarget(target)) {
                  if (!targetBlocks.contains(Blocks.ROSE_BUSH)) {
                     target = ((MiningBot.BlockData)pos.getFirst()).blockPos;
                  } else {
                     if (MinecraftClient.getInstance().world.getBlockState(((MiningBot.BlockData)pos.getFirst()).blockPos.up(2)).getBlock()
                        != Blocks.AIR) {
                        target = ((MiningBot.BlockData)pos.getFirst()).blockPos.up(2);
                     }

                     if (MinecraftClient.getInstance().world.getBlockState(((MiningBot.BlockData)pos.getFirst()).blockPos.up(2)).getBlock()
                           == Blocks.AIR
                        && MinecraftClient.getInstance().world.getBlockState(((MiningBot.BlockData)pos.getFirst()).blockPos.up()).getBlock()
                           != Blocks.AIR) {
                        target = ((MiningBot.BlockData)pos.getFirst()).blockPos.up();
                     }

                     if (MinecraftClient.getInstance().world.getBlockState(((MiningBot.BlockData)pos.getFirst()).blockPos.up(2)).getBlock()
                           == Blocks.AIR
                        && MinecraftClient.getInstance().world.getBlockState(((MiningBot.BlockData)pos.getFirst()).blockPos.up()).getBlock()
                           == Blocks.AIR) {
                        target = ((MiningBot.BlockData)pos.getFirst()).blockPos;
                     }
                  }
               }

               HitResult hitResult1 = MinecraftClient.getInstance().crosshairTarget;
               if (!realNuker) {
                  if (hitResult1 instanceof BlockHitResult) {
                     breakBlock(((BlockHitResult)hitResult1).getBlockPos().add(0, 0, 0).equals(target) && hitResult1.getType() == Type.BLOCK);
                  }
               } else {
                  breakBlock(target != null && (prevTarget == null || !prevTarget.equals(target)));
               }

               if (sneakEnable) {
                  MinecraftClient.getInstance().options.sneakKey.setPressed(true);
               }

               if (randomMoveEnable) {
                  if (moveTick % 160 == 0) {
                     if (new Random().nextBoolean()) {
                        MinecraftClient.getInstance().options.forwardKey.setPressed(true);
                     }

                     if (new Random().nextBoolean() && new Random().nextBoolean() && new Random().nextBoolean()) {
                        MinecraftClient.getInstance().options.backKey.setPressed(true);
                     }

                     if (new Random().nextBoolean()) {
                        MinecraftClient.getInstance().options.leftKey.setPressed(true);
                     }

                     if (new Random().nextBoolean()) {
                        MinecraftClient.getInstance().options.rightKey.setPressed(true);
                     }

                     new Thread(() -> {
                        try {
                           Thread.sleep(500L);
                           MinecraftClient.getInstance().options.forwardKey.setPressed(false);
                           MinecraftClient.getInstance().options.backKey.setPressed(false);
                           MinecraftClient.getInstance().options.leftKey.setPressed(false);
                           MinecraftClient.getInstance().options.rightKey.setPressed(false);
                        } catch (InterruptedException var1x) {
                           throw new RuntimeException(var1x);
                        }
                     }).start();
                  }

                  moveTick++;
               }

               if (serverRotation) {
                  MinecraftClient.getInstance().player.setPitch(MinecraftClient.getInstance().player.getPitch() + (lastRotate ? 0.1F : -0.1F));
                  lastRotate = !lastRotate;
               }

               if (serverRotation && realNuker) {
                  ServerRotation.serverYaw = RotationUtil.toRotation(((MiningBot.BlockData)pos.getFirst()).lookPos).getYaw();
                  ServerRotation.serverPitch = RotationUtil.toRotation(((MiningBot.BlockData)pos.getFirst()).lookPos).getPitch();
               } else {
                  SmoothRotation.smoothLook(RotationUtil.toRotation(((MiningBot.BlockData)pos.getFirst()).lookPos), 100, () -> {
                  });
               }
            } else {
               target = null;
            }
         }
      }
   }

   public static void onRender(RenderEvent event) {
      if (isWorking) {
         if (target != null) {
            RenderUtil.draw3DOutline(new Box(target), Color.CYAN, Shimmer.matrixStack, event.storage.getEntityVertexConsumers());
         }
      }
   }

   public static void release(String name) {
      if (currentModule != null && currentModule.equals(name)) {
         currentModule = null;
         isWorking = false;
         breakBlock(false);
         target = null;
         MinecraftClient.getInstance().options.sneakKey.setPressed(false);
         pos.clear();
      }
   }

   public static boolean isTarget(BlockPos bp) {
      if (MinecraftClient.getInstance().world == null) {
         return false;
      } else if (blacklist.contains(bp)) {
         return false;
      } else if (!callback.isTarget(bp)) {
         return false;
      } else if (onlyUpperThanPlayer
         && MinecraftClient.getInstance().player != null
         && (double)bp.getY() < MinecraftClient.getInstance().player.getY()) {
         return false;
      } else if (MinecraftClient.getInstance().world.getBlockState(bp).getBlock() instanceof CropBlock
         && (Integer)MinecraftClient.getInstance().world.getBlockState(bp).get(CropBlock.AGE) != 7) {
         return false;
      } else if (MinecraftClient.getInstance().world.getBlockState(bp).getBlock() != Blocks.SUGAR_CANE
         || MinecraftClient.getInstance().world.getBlockState(bp.down()).getBlock() == Blocks.SUGAR_CANE
            && !MinecraftClient.getInstance().world.getBlockState(bp.up()).isAir()) {
         if (bp != null && bp.equals(target) && MinecraftClient.getInstance().world.getBlockState(bp).getBlock() != lastMined) {
            return false;
         } else if (MinecraftClient.getInstance().world.isAir(bp)) {
            return false;
         } else if (MinecraftClient.getInstance().world.isWater(bp)) {
            return false;
         } else {
            Block currentBlock = MinecraftClient.getInstance().world.getBlockState(bp).getBlock();

            for (Block b : targetBlocks) {
               if (currentBlock.getName().equals(b.getName())) {
                  if (!realNuker && bp != null) {
                     SmoothRotation.smoothLook(RotationUtil.toRotation(Vec3d.of(bp)), 300, () -> {
                     });
                  }

                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static void breakBlock(boolean enableBreak) {
      if (System.currentTimeMillis() - miningDelay > 500L) {
         if (!realNuker) {
            if (MinecraftClient.getInstance().crosshairTarget != null && MinecraftClient.getInstance().crosshairTarget.getType() == Type.BLOCK) {
               MinecraftClient.getInstance().options.attackKey.setPressed(enableBreak);
            }

            if (MinecraftClient.getInstance().world != null
               && (
                  target == null
                     || prevBlock != MinecraftClient.getInstance().world.getBlockState(target).getBlock()
                        && MinecraftClient.getInstance().world.getBlockState(target).getBlock() == Blocks.POLISHED_DIORITE
               )) {
               MinecraftClient.getInstance().options.attackKey.setPressed(false);
               miningDelay = System.currentTimeMillis();
            }

            if (MinecraftClient.getInstance().world != null) {
               prevBlock = MinecraftClient.getInstance().world.getBlockState(target).getBlock();
            }
         } else {
            if (enableBreak && !prevStatus && target != null) {
               MinecraftClient.getInstance()
                  .getNetworkHandler()
                  .getConnection()
                  .send(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, target, Direction.DOWN, 0));
            }

            if (target != null) {
               MinecraftClient.getInstance().player.swingHand(Hand.MAIN_HAND);
            }
         }

         if (target != null && (lastMined == null || lastMined != MinecraftClient.getInstance().world.getBlockState(target).getBlock())) {
            lastMined = MinecraftClient.getInstance().world.getBlockState(target).getBlock();
         }

         prevStatus = enableBreak;
         if (target == null || !target.equals(prevTarget)) {
            prevStatus = false;
         }

         if (oneTick) {
            blacklist.add(target);
            target = null;
            blacklistCleanTimer = System.currentTimeMillis() - 29000L;
         }
      }
   }

   public static Direction getDirection(BlockPos pos) {
      Vec3d eyesPos = new Vec3d(
         MinecraftClient.getInstance().player.getX(),
         MinecraftClient.getInstance().player.getY()
            + (double)MinecraftClient.getInstance().player.getEyeHeight(MinecraftClient.getInstance().player.getPose()),
         MinecraftClient.getInstance().player.getZ()
      );
      if ((double)pos.getY() > eyesPos.y) {
         return MinecraftClient.getInstance().world.getBlockState(pos.add(0, -1, 0)).isReplaceable()
            ? Direction.DOWN
            : MinecraftClient.getInstance().player.getHorizontalFacing().getOpposite();
      } else {
         return !MinecraftClient.getInstance().world.getBlockState(pos.add(0, 1, 0)).isReplaceable()
            ? MinecraftClient.getInstance().player.getHorizontalFacing().getOpposite()
            : Direction.UP;
      }
   }

   public static Vec3d traversal(BlockPos blockPos, Vec3d viewVec1) {
      if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
         Vec3d viewVec = Vec3d.fromPolar(MinecraftClient.getInstance().player.getPitch(), MinecraftClient.getInstance().player.getYaw());
         Vec3d playerPos = MinecraftClient.getInstance().player.getCameraPosVec(1.0F);
         if (viewVec1 != null) {
            viewVec = viewVec1;
         }

         double lastAngle = Double.MAX_VALUE;
         Vec3d rtv = null;

         for (Vec3d vec3dv : SHIFTING) {
            Vec3d cpos = blockPos.toCenterPos().add(vec3dv);
            if (!(cpos.distanceTo(playerPos) > 4.5)) {
               BlockHitResult hitResult = MinecraftClient.getInstance()
                  .world
                  .raycast(new RaycastContext(playerPos, cpos, ShapeType.OUTLINE, FluidHandling.NONE, MinecraftClient.getInstance().player));
               if (hitResult instanceof BlockHitResult && hitResult.getBlockPos().equals(blockPos) && hitResult.getType() == Type.BLOCK) {
                  if (rtv == null) {
                     rtv = cpos;
                     lastAngle = MathUtil.calculateAngle(cpos.subtract(playerPos), viewVec);
                  } else {
                     Vec3d vec3d = cpos.subtract(playerPos);
                     double angle = MathUtil.calculateAngle(vec3d, viewVec);
                     if (angle < lastAngle) {
                        lastAngle = angle;
                        rtv = cpos;
                     }
                  }
               }
            }
         }

         return rtv;
      } else {
         return viewVec1;
      }
   }

   static {
      SHIFTING.add(new Vec3d(0.5, 0.0, 0.0));
      SHIFTING.add(new Vec3d(0.0, 0.5, 0.0));
      SHIFTING.add(new Vec3d(0.0, 0.0, 0.5));
      SHIFTING.add(new Vec3d(-0.5, 0.0, 0.0));
      SHIFTING.add(new Vec3d(0.0, -0.5, 0.0));
      SHIFTING.add(new Vec3d(0.0, 0.0, -0.5));
      SHIFTING.add(new Vec3d(0.5, 0.5, 0.0));
      SHIFTING.add(new Vec3d(0.0, 0.5, 0.5));
      SHIFTING.add(new Vec3d(0.5, 0.0, 0.5));
      SHIFTING.add(new Vec3d(-0.5, 0.5, 0.0));
      SHIFTING.add(new Vec3d(0.0, -0.5, 0.5));
      SHIFTING.add(new Vec3d(0.5, 0.0, -0.5));
      SHIFTING.add(new Vec3d(-0.5, 0.0, 0.5));
      SHIFTING.add(new Vec3d(0.0, 0.5, -0.5));
      SHIFTING.add(new Vec3d(-0.5, 0.0, -0.5));
      SHIFTING.add(new Vec3d(0.0, -0.5, -0.5));
      SHIFTING.add(new Vec3d(-0.5, -0.5, 0.0));
      SHIFTING.add(new Vec3d(0.0, -0.5, -0.5));
      SHIFTING.add(new Vec3d(-0.5, 0.0, -0.5));
      SHIFTING.add(new Vec3d(-0.5, -0.5, -0.5));
      SHIFTING.add(new Vec3d(0.5, 0.5, 0.5));
      SHIFTING.add(new Vec3d(0.5, 0.5, -0.5));
      SHIFTING.add(new Vec3d(0.5, -0.5, 0.5));
      SHIFTING.add(new Vec3d(-0.5, 0.5, 0.5));
      SHIFTING.add(new Vec3d(-0.5, -0.5, 0.5));
      SHIFTING.add(new Vec3d(0.0, 0.0, 0.0));
   }

   private static class BlockData {
      public BlockPos blockPos;
      public Vec3d lookPos;

      public BlockData(BlockPos blockPos, Vec3d lookPos) {
         this.blockPos = blockPos;
         this.lookPos = lookPos;
      }
   }

   public interface isTargetCallback {
      boolean isTarget(BlockPos var1);
   }
}
