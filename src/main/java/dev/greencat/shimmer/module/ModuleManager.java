package dev.greencat.shimmer.module;

import dev.greencat.shimmer.Shimmer;
import dev.greencat.shimmer.event.events.KeyEvent;
import dev.greencat.shimmer.eventbus.ShimmerSubscribe;
import dev.greencat.shimmer.module.modules.combat.AimAssistant;
import dev.greencat.shimmer.module.modules.combat.AutoClicker;
import dev.greencat.shimmer.module.modules.combat.Killaura;
import dev.greencat.shimmer.module.modules.macro.AutoDancer;
import dev.greencat.shimmer.module.modules.macro.AutoFish;
import dev.greencat.shimmer.module.modules.macro.AutoHOTM;
import dev.greencat.shimmer.module.modules.macro.AutoHeal;
import dev.greencat.shimmer.module.modules.macro.AutoJump;
import dev.greencat.shimmer.module.modules.macro.AutoKillWorm;
import dev.greencat.shimmer.module.modules.macro.AutoReel;
import dev.greencat.shimmer.module.modules.macro.AutoSlayer;
import dev.greencat.shimmer.module.modules.macro.AutoTerminal;
import dev.greencat.shimmer.module.modules.macro.BallBot;
import dev.greencat.shimmer.module.modules.macro.ChestBot;
import dev.greencat.shimmer.module.modules.macro.CropBot;
import dev.greencat.shimmer.module.modules.macro.DivanGoldBot;
import dev.greencat.shimmer.module.modules.macro.DojoHelper;
import dev.greencat.shimmer.module.modules.macro.EnderNodeBot;
import dev.greencat.shimmer.module.modules.macro.ExperimentBot;
import dev.greencat.shimmer.module.modules.macro.FullyBallBot;
import dev.greencat.shimmer.module.modules.macro.GhostMacro;
import dev.greencat.shimmer.module.modules.macro.GiftRecipient;
import dev.greencat.shimmer.module.modules.macro.HarpBot;
import dev.greencat.shimmer.module.modules.macro.Im_a_Cat;
import dev.greencat.shimmer.module.modules.macro.InstantSwitch;
import dev.greencat.shimmer.module.modules.macro.KillerBot;
import dev.greencat.shimmer.module.modules.macro.LivingMetalBot;
import dev.greencat.shimmer.module.modules.macro.LockBat;
import dev.greencat.shimmer.module.modules.macro.MacroProtector;
import dev.greencat.shimmer.module.modules.macro.MiningBot;
import dev.greencat.shimmer.module.modules.macro.NPCFlipBot;
import dev.greencat.shimmer.module.modules.macro.Nuker;
import dev.greencat.shimmer.module.modules.macro.PowderBot;
import dev.greencat.shimmer.module.modules.macro.SealAutoStorage;
import dev.greencat.shimmer.module.modules.macro.ShulkerBot;
import dev.greencat.shimmer.module.modules.misc.AutoPurchase;
import dev.greencat.shimmer.module.modules.misc.Cat;
import dev.greencat.shimmer.module.modules.misc.ChestPlacer;
import dev.greencat.shimmer.module.modules.misc.GhostBlock;
import dev.greencat.shimmer.module.modules.misc.GuessTheBot;
import dev.greencat.shimmer.module.modules.misc.HClip;
import dev.greencat.shimmer.module.modules.misc.RainTimer;
import dev.greencat.shimmer.module.modules.movement.Eagle;
import dev.greencat.shimmer.module.modules.movement.Sneak;
import dev.greencat.shimmer.module.modules.movement.Sprint;
import dev.greencat.shimmer.module.modules.movement.TaratulaFly;
import dev.greencat.shimmer.module.modules.player.TargetHUD;
import dev.greencat.shimmer.module.modules.render.BatESP;
import dev.greencat.shimmer.module.modules.render.BerberisTracker;
import dev.greencat.shimmer.module.modules.render.DragonEggESP;
import dev.greencat.shimmer.module.modules.render.ESP;
import dev.greencat.shimmer.module.modules.render.EnderNodeESP;
import dev.greencat.shimmer.module.modules.render.FrozenTreasureESP;
import dev.greencat.shimmer.module.modules.render.HotspotESP;
import dev.greencat.shimmer.module.modules.render.InvisbugESP;
import dev.greencat.shimmer.module.modules.render.Island;
import dev.greencat.shimmer.module.modules.render.LineGlyphs;
import dev.greencat.shimmer.module.modules.render.LivingMetalESP;
import dev.greencat.shimmer.module.modules.render.LushlilacESP;
import dev.greencat.shimmer.module.modules.render.OneGui;
import dev.greencat.shimmer.module.modules.render.RatESP;
import dev.greencat.shimmer.module.modules.render.ShulkerESP;
import dev.greencat.shimmer.module.modules.render.TargetESP;
import dev.greencat.shimmer.module.modules.render.TerminalESP;
import dev.greencat.shimmer.module.modules.render.TurtleESP;
import dev.greencat.shimmer.module.modules.render.WormLavaESP;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public class ModuleManager {
   public final ArrayList<Module> modules;
   public final CopyOnWriteArrayList<String> enabledModules = new CopyOnWriteArrayList();

   public ModuleManager() {
      this.modules = new ArrayList();
      this.modules.add(new Killaura());
      this.modules.add(new AutoClicker());
      this.modules.add(new AimAssistant());
      this.modules.add(new ESP());
      this.modules.add(new HotspotESP());
      this.modules.add(new WormLavaESP());
      this.modules.add(new FrozenTreasureESP());
      this.modules.add(new OneGui());
      this.modules.add(new TerminalESP());
      this.modules.add(new EnderNodeESP());
      this.modules.add(new LushlilacESP());
      this.modules.add(new ShulkerESP());
      this.modules.add(new TurtleESP());
      this.modules.add(new InvisbugESP());
      this.modules.add(new BerberisTracker());
      this.modules.add(new LivingMetalESP());
      this.modules.add(new DragonEggESP());
      this.modules.add(new TargetESP());
      this.modules.add(new RatESP());
      this.modules.add(new BatESP());
      this.modules.add(new LineGlyphs());
      this.modules.add(new Island());
      this.modules.add(new Sprint());
      this.modules.add(new Sneak());
      this.modules.add(new Eagle());
      this.modules.add(new TaratulaFly());
      this.modules.add(new AutoFish());
      this.modules.add(new AutoKillWorm());
      this.modules.add(new AutoSlayer());
      this.modules.add(new Nuker());
      this.modules.add(new KillerBot());
      this.modules.add(new AutoHOTM());
      this.modules.add(new AutoTerminal());
      this.modules.add(new AutoDancer());
      this.modules.add(new AutoHeal());
      this.modules.add(new HarpBot());
      this.modules.add(new ExperimentBot());
      this.modules.add(new DojoHelper());
      this.modules.add(new CropBot());
      this.modules.add(new GiftRecipient());
      this.modules.add(new BallBot());
      this.modules.add(new FullyBallBot());
      this.modules.add(new EnderNodeBot());
      this.modules.add(new NPCFlipBot());
      this.modules.add(new AutoReel());
      this.modules.add(new ShulkerBot());
      this.modules.add(new LivingMetalBot());
      this.modules.add(new GhostMacro());
      this.modules.add(new MacroProtector());
      this.modules.add(new MiningBot());
      this.modules.add(new DivanGoldBot());
      this.modules.add(new ChestBot());
      this.modules.add(new PowderBot());
      this.modules.add(new LockBat());
      this.modules.add(new InstantSwitch());
      this.modules.add(new AutoJump());
      this.modules.add(new SealAutoStorage());
      this.modules.add(new Im_a_Cat());
      this.modules.add(new GhostBlock());
      this.modules.add(new HClip());
      this.modules.add(new ChestPlacer());
      this.modules.add(new AutoPurchase());
      this.modules.add(new Cat());
      this.modules.add(new RainTimer());
      this.modules.add(new GuessTheBot());
      this.modules.add(new TargetHUD());
   }

   public ArrayList<Module> getModules() {
      return this.modules;
   }

   public ArrayList<Module> getEnabledModules() {
      ArrayList<Module> enabledModules = new ArrayList();

      for (Module module : this.modules) {
         if (module.isEnabled()) {
            enabledModules.add(module);
         }
      }

      return enabledModules;
   }

   public Module getModule(String name) {
      return (Module)this.modules.stream().filter(mm -> mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
   }

   public boolean isModuleEnabled(String name) {
      Module mod = (Module)this.modules.stream().filter(mm -> mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
      return mod != null && mod.isEnabled();
   }

   public void refreshEnabled() {
      this.enabledModules.clear();

      for (Module m : this.modules) {
         if (m.isEnabled()) {
            this.enabledModules.add(m.getName());
         }
      }

      if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().textRenderer != null) {
         this.enabledModules.sort((o, o1) -> MinecraftClient.getInstance().textRenderer.getWidth(o1) - MinecraftClient.getInstance().textRenderer.getWidth(o));
      }
   }

   public List<Module> getModulesByCategory(Module.Category category) {
      List<Module> cats = new ArrayList();

      for (Module m : this.modules) {
         if (m.getCategory() == category) {
            cats.add(m);
         }
      }

      return cats;
   }

   @ShimmerSubscribe
   public void onKeyPress(KeyEvent event) {
      if (!InputUtil.isKeyPressed(Shimmer.mc.getWindow(), 292)) {
         this.modules.stream().filter(m -> m.getKey() == event.getKey()).forEach(Module::toggle);
      }
   }
}
