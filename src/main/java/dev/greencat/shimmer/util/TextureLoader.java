package dev.greencat.shimmer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class TextureLoader {
   private static NativeImageBackedTexture dynamicTexture;
   public static Identifier dynamicTextureId;
   private static int i = 0;

   public static void generateTexture(File textureFile) throws IOException {
      if (dynamicTexture != null) {
         dynamicTexture.close();
      }

      NativeImage image = NativeImage.read(new FileInputStream(textureFile));
      dynamicTexture = new NativeImageBackedTexture(textureFile::getName, image);
      dynamicTextureId = Identifier.of("celestium", "skybox_" + i);
      MinecraftClient.getInstance().getTextureManager().registerTexture(dynamicTextureId, dynamicTexture);
      i++;
   }
}
