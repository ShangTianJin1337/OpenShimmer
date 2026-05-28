package dev.greencat.shimmer;

import dev.greencat.MaterialConfigFactory;
import dev.greencat.core.type.Category;
import dev.greencat.core.type.settings.BooleanSettings;
import dev.greencat.core.type.settings.KeybindSettings;
import dev.greencat.core.type.settings.ModeSettings;
import dev.greencat.core.type.settings.NumberSettings;
import dev.greencat.core.type.settings.StringSettings;
import dev.greencat.shimmer.command.CommandManager;
import dev.greencat.shimmer.event.events.ActionBarRenderEvent;
import dev.greencat.shimmer.event.events.RenderEvent;
import dev.greencat.shimmer.event.events.WorldChangeEvent;
import dev.greencat.shimmer.eventbus.EventBus;
import dev.greencat.shimmer.keybind.VanillaKeyBind;
import dev.greencat.shimmer.module.Module;
import dev.greencat.shimmer.module.ModuleManager;
import dev.greencat.shimmer.module.modules.misc.Cat;
import dev.greencat.shimmer.module.modules.misc.RainTimer;
import dev.greencat.shimmer.setting.Setting;
import dev.greencat.shimmer.setting.SettingManager;
import dev.greencat.shimmer.setting.settings.BooleanSetting;
import dev.greencat.shimmer.setting.settings.KeybindSetting;
import dev.greencat.shimmer.setting.settings.ModeSetting;
import dev.greencat.shimmer.setting.settings.NumberSetting;
import dev.greencat.shimmer.setting.settings.StringSetting;
import dev.greencat.shimmer.util.HaikuLogger;
import dev.greencat.shimmer.util.IconLoader;
import dev.greencat.shimmer.util.RankingOverrider;
import dev.greencat.shimmer.util.irc.IRC;
import dev.greencat.shimmer.util.player.EtherwarpHelper;
import dev.greencat.shimmer.util.player.WalkerUtils;
import dev.greencat.shimmer.util.player.rotation.SmoothRotation;
import dev.greencat.shimmer.util.render.ShimmerRenderPipelines;
import dev.greencat.shimmer.util.render.TextRenderUtil;
import dev.greencat.shimmer.util.render.animation.AnimationManager;
import dev.greencat.shimmer.util.world.LocationUtils;
import dev.greencat.shimmer.util.world.TPSUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents.AfterClientWorldChange;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents.AllowGame;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BlockStateManagers;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Shimmer implements ModInitializer {
   public static final String MOD_NAME = "Shimmer Noble";
   public static final String MOD_VERSION = "21.1";
   public static final MinecraftClient mc = MinecraftClient.getInstance();
   public HashMap<String, BooleanSettings> moduleEnabledMap = new HashMap();
   public boolean lastHasWorld = false;
   public boolean isIRCToggled = false;
   private static Shimmer INSTANCE;
   private final VanillaKeyBind keyBind = new VanillaKeyBind();
   private final EventBus EVENT_BUS = new EventBus();
   private final ModuleManager MODULE_MANAGER = new ModuleManager();
   private final CommandManager COMMAND_MANAGER = new CommandManager();
   private final SettingManager SETTING_MANAGER = new SettingManager();
   private final RankingOverrider RANKING_OVERRIDER = new RankingOverrider();
   public static MatrixStack matrixStack = new MatrixStack();
   public static EtherwarpHelper etherwarpHelper = new EtherwarpHelper();
   public final MaterialConfigFactory factory = new MaterialConfigFactory("Shimmer");
   public IRC IRC = null;
   private String title = "Shimmer Noble v21.1";
   public boolean isSorted = false;
   private final AnimationManager ANIMATION_MANAGER = new AnimationManager();

   public Shimmer() {
      INSTANCE = this;
   }

   public static Shimmer getInstance() {
      return INSTANCE;
   }

   public static void onWorldRenderPre(WorldRenderer renderer, BufferBuilderStorage bufferBuilderStorage) {
   }

   public static void onWorldRenderAfterEntity(WorldRenderer renderer, BufferBuilderStorage bufferBuilderStorage) {
      RenderEvent.Pre event1 = new RenderEvent.Pre(renderer, bufferBuilderStorage);
      getInstance().getEventBus().post(event1);
      RenderEvent.Post event = new RenderEvent.Post(renderer, bufferBuilderStorage);
      getInstance().getEventBus().post(event);
      RenderEvent.AfterEntities event2 = new RenderEvent.AfterEntities(renderer, bufferBuilderStorage);
      etherwarpHelper.renderPoints(new RenderEvent(renderer, bufferBuilderStorage));
      getInstance().getEventBus().post(event2);
      if (LocationUtils.sideBarString.toLowerCase().contains("village")) {
         BlockState blockState = BlockStateManagers.getStateForItemFrame(false, true);
         BlockStateModel blockStateModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState);
         VertexConsumerProvider vertexConsumerProvider = bufferBuilderStorage.getEntityVertexConsumers();
         MatrixStack matrixStack = Shimmer.matrixStack;
         if (matrixStack != null && vertexConsumerProvider != null) {
            matrixStack.push();
            Vec3d pos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
            matrixStack.translate(-pos.x + Cat.RENDER_POSITION.x + 1.0, -pos.y + Cat.RENDER_POSITION.y, -pos.z + Cat.RENDER_POSITION.z + 1.0 + 0.0125);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            Entry peek = matrixStack.peek();
            BlockModelRenderer.render(
               peek,
               vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolidZOffsetForward(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)),
               blockStateModel,
               1.0F,
               1.0F,
               1.0F,
               15,
               OverlayTexture.DEFAULT_UV
            );
            matrixStack.translate(1.0F, 1.0F, 0.0F);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            Matrix4f matrix4f = peek.getPositionMatrix();
            Identifier TEXTURE = Identifier.of("shimmer", Cat.TEXTURE);
            VertexConsumer cat = vertexConsumerProvider.getBuffer(RenderLayer.getText(TEXTURE));
            float z = 0.9370117F;
            cat.vertex(matrix4f, 0.0F, 1.0F, z).color(-1).texture(0.0F, 1.0F).light(15);
            cat.vertex(matrix4f, 1.0F, 1.0F, z).color(-1).texture(1.0F, 1.0F).light(15);
            cat.vertex(matrix4f, 1.0F, 0.0F, z).color(-1).texture(1.0F, 0.0F).light(15);
            cat.vertex(matrix4f, 0.0F, 0.0F, z).color(-1).texture(0.0F, 0.0F).light(15);
            matrixStack.pop();
            matrixStack.push();
            matrixStack.translate(
               -pos.x + Cat.BIG_RENDER_POSITION.x + 1.0, -pos.y + Cat.BIG_RENDER_POSITION.y, -pos.z + Cat.BIG_RENDER_POSITION.z + 1.0 + 0.0125
            );
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            matrixStack.scale(7.0F, 7.0F, 1.0F);
            Entry peek1 = matrixStack.peek();
            BlockModelRenderer.render(
               peek1,
               vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolidZOffsetForward(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)),
               blockStateModel,
               1.0F,
               1.0F,
               1.0F,
               15,
               OverlayTexture.DEFAULT_UV
            );
            matrixStack.translate(1.0F, 1.0F, 0.0F);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            Matrix4f matrix4f1 = peek1.getPositionMatrix();
            VertexConsumer cat1 = vertexConsumerProvider.getBuffer(RenderLayer.getText(TEXTURE));
            cat1.vertex(matrix4f1, 0.0F, 1.0F, z).color(-1).texture(0.0F, 1.0F).light(15);
            cat1.vertex(matrix4f1, 1.0F, 1.0F, z).color(-1).texture(1.0F, 1.0F).light(15);
            cat1.vertex(matrix4f1, 1.0F, 0.0F, z).color(-1).texture(1.0F, 0.0F).light(15);
            cat1.vertex(matrix4f1, 0.0F, 0.0F, z).color(-1).texture(0.0F, 0.0F).light(15);
            matrixStack.pop();
         }
      }
   }

   public static void onWorldRenderPost(WorldRenderer renderer, BufferBuilderStorage bufferBuilderStorage) {
   }

   public void onInitialize() {
      HaikuLogger.logger.info("Shimmer Noble v21.1 (phase 1) has initialized!");
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         if (MinecraftClient.getInstance().world != null) {
            LocationUtils.update();
         }
      });
      ClientReceiveMessageEvents.ALLOW_GAME.register((AllowGame)(text, overlay) -> {
         ActionBarRenderEvent event = new ActionBarRenderEvent(text);
         getInstance().getEventBus().post(event);
         return true;
      });
      ClientTickEvents.START_CLIENT_TICK.register(SmoothRotation::onTick);
      ClientTickEvents.START_CLIENT_TICK.register(WalkerUtils::handleRotation);
      ClientTickEvents.START_CLIENT_TICK.register((StartTick)mc -> {
         if (!this.lastHasWorld && mc.world != null) {
            HaikuLogger.info("[IRC] IRC已链接,输入\"!disconnect\"断开链接,\"!消息\"发送消息");
            HaikuLogger.info("[IRC] 输入\"!list\"查看当前玩家列表,\"!toggle\"来将IRC设为默认聊天频道");
         }

         this.lastHasWorld = mc.world != null;
      });
      ClientTickEvents.START_CLIENT_TICK
         .register(
            (StartTick)mc -> {
               if (MinecraftClient.getInstance().player != null
                  && MinecraftClient.getInstance().world != null
                  && WalkerUtils.isActive()
                  && MinecraftClient.getInstance().player.getMovement().y <= -0.08) {
               }
            }
         );
      ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((AfterClientWorldChange)(client, world) -> {
         WorldChangeEvent event = new WorldChangeEvent();
         getInstance().getEventBus().post(event);
         this.MODULE_MANAGER.getModule("Cat").setEnabled(true);
         File file = new File(MinecraftClient.getInstance().runDirectory, "21.1");
         if (!file.exists()) {
            try {
               file.createNewFile();
            } catch (Exception var6) {
               var6.printStackTrace();
            }

            this.MODULE_MANAGER.getModule("LineGlyphs").setEnabled(true);
            this.MODULE_MANAGER.getModule("Island").setEnabled(true);
         }
      });
      HudRenderCallback.EVENT.register(TextRenderUtil::onRenderHUD);
      new ShimmerRenderPipelines();
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         if (getInstance().IRC != null) {
            try {
               getInstance().IRC.socket.close();
               getInstance().IRC.reader.close();
               getInstance().IRC.writer.close();
            } catch (IOException var1) {
               throw new RuntimeException(var1);
            }
         }
      }));
   }

   public void postInitialize() {
      this.EVENT_BUS.register(TPSUtil.INSTANCE);
      HaikuLogger.logger.info("Registered TickRateUtil!");
      HaikuLogger.logger.info("Shimmer Noble v21.1 (phase 2) has initialized!");
      this.MODULE_MANAGER.getModule("AutoFish").setEnabled(false);

      try {
         IconLoader.setWindowIcons(MinecraftClient.getInstance().getWindow().getHandle(), "/assets/shimmer/logo.png");
      } catch (Exception var21) {
         var21.printStackTrace();
         System.err.println("Cannot load icon!");
      }

      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         if (System.currentTimeMillis() - Cat.lastChange >= 500L) {
            this.title = "Shimmer Noble v21.1";
            String tpsInfo = "TPS: " + TPSUtil.INSTANCE.getTPS();
            String fpsInfo = String.valueOf(mc.getCurrentFps());
            String fpsString = "FPS:" + fpsInfo;
            this.title = this.title + "  | " + fpsString + " | " + tpsInfo;
            if (getInstance().getModuleManager().isModuleEnabled("RainTimer")) {
               this.title = this.title + " | " + RainTimer.getStringForRenderAndUpdate().getString();
            }

            Cat.lastChange = System.currentTimeMillis();
         }

         MinecraftClient.getInstance().getWindow().setTitle(this.title + " (Minecraft " + SharedConstants.getGameVersion().name() + ")");
      });

      try {
         this.IRC = new IRC();
         this.IRC.start();
      } catch (IOException var20) {
         var20.printStackTrace();
      }

      for (Module.Category category : Module.Category.values()) {
         this.factory.addCategory(new Category(category.name()));
      }

      for (Module module : getInstance().getModuleManager().getModules()) {
         String moduleName = module.getName();
         this.factory.addModule(new dev.greencat.core.type.Module(module.getCategory().name(), moduleName));
         BooleanSettings booleanSettings = new BooleanSettings(this.factory, moduleName, "isEnabled", false);
         this.factory.addSettings(booleanSettings, value -> module.setEnabled((Boolean)value.getValue()));
         this.factory.addCallback((settingBase, value) -> {
            if (settingBase.module.equals(booleanSettings.module) && settingBase.name.equals(booleanSettings.name)) {
               module.setEnabled((Boolean)value);
            }
         });
         this.moduleEnabledMap.put(moduleName, booleanSettings);

         for (Setting setting : module.settings) {
            String className = setting.getClass().getSimpleName();
            switch (className) {
               case "BooleanSetting":
                  BooleanSetting originalSetting = (BooleanSetting)setting;
                  BooleanSettings wrapperSetting = new BooleanSettings(this.factory, moduleName, originalSetting.name, originalSetting.isEnabled());
                  this.factory.addSettings(wrapperSetting, value -> originalSetting.setEnabled((Boolean)value.getValue()));
                  this.factory.addCallback((settingBase, value) -> {
                     if (settingBase.name.equals(originalSetting.name) && moduleName.equals(settingBase.module)) {
                        originalSetting.setEnabled((Boolean)value);
                     }
                  });
                  break;
               case "NumberSetting":
                  NumberSetting originalSetting1 = (NumberSetting)setting;
                  NumberSettings wrapperSetting1 = new NumberSettings(
                     this.factory, moduleName, originalSetting1.name, originalSetting1.getValue(), originalSetting1.maximum, originalSetting1.minimum
                  );
                  this.factory.addSettings(wrapperSetting1, value -> originalSetting1.setValue((Double)value.getValue()));
                  this.factory.addCallback((settingBase, value) -> {
                     if (settingBase.name.equals(originalSetting1.name) && moduleName.equals(settingBase.module)) {
                        originalSetting1.setValue((Double)value);
                     }
                  });
                  break;
               case "StringSetting":
                  StringSetting originalSetting2 = (StringSetting)setting;
                  StringSettings wrapperSetting2 = new StringSettings(this.factory, moduleName, originalSetting2.name, originalSetting2.getString());
                  this.factory.addSettings(wrapperSetting2, value -> originalSetting2.setString((String)value.getValue()));
                  this.factory.addCallback((settingBase, value) -> {
                     if (settingBase.name.equals(originalSetting2.name) && moduleName.equals(settingBase.module)) {
                        originalSetting2.setString((String)value);
                     }
                  });
                  break;
               case "ModeSetting":
                  ModeSetting originalSetting3 = (ModeSetting)setting;
                  ModeSettings wrapperSetting3 = new ModeSettings(
                     this.factory, moduleName, originalSetting3.name, originalSetting3.getMode(), (String[])originalSetting3.getModes().toArray(new String[0])
                  );
                  this.factory.addSettings(wrapperSetting3, value -> originalSetting3.setMode((String)value.getValue()));
                  this.factory.addCallback((settingBase, value) -> {
                     if (settingBase.name.equals(originalSetting3.name) && moduleName.equals(settingBase.module)) {
                        originalSetting3.setMode((String)value);
                     }
                  });
                  break;
               case "KeybindSetting":
                  KeybindSetting originalSetting4 = (KeybindSetting)setting;
                  KeybindSettings wrapperSetting4 = new KeybindSettings(this.factory, moduleName, originalSetting4.name);
                  this.factory.addSettings(wrapperSetting4, value -> originalSetting4.setKeyCode((Integer)value.getValue()));
                  this.factory.addCallback((settingBase, value) -> {
                     if (settingBase.name.equals(originalSetting4.name) && moduleName.equals(settingBase.module)) {
                        originalSetting4.setKeyCode((Integer)value);
                     }
                  });
                  if (module.name.equals("OneGui")) {
                     wrapperSetting4.setValue(344);
                  }
                  break;
               default:
                  HaikuLogger.logger.error("Unknown setting type: " + className);
            }
         }
      }
   }

   public EventBus getEventBus() {
      return this.EVENT_BUS;
   }

   public ModuleManager getModuleManager() {
      return this.MODULE_MANAGER;
   }

   public CommandManager getCommandManager() {
      return this.COMMAND_MANAGER;
   }

   public SettingManager getSettingManager() {
      return this.SETTING_MANAGER;
   }

   public VanillaKeyBind getKeyBindManager() {
      return this.keyBind;
   }
}
