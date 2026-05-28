package dev.greencat.shimmer.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class IconLoader {
   private static final List<ByteBuffer> loadedIconPixels = new ArrayList();

   public static void setWindowIcons(long windowHandle, String... iconPaths) throws IOException {
      try {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            Buffer icons = GLFWImage.malloc(iconPaths.length, stack);

            for (int i = 0; i < iconPaths.length; i++) {
               String iconPath = iconPaths[i];
               ByteBuffer imageBuffer = null;
               ByteBuffer imageBytes = null;
               InputStream is = IconLoader.class.getResourceAsStream(iconPath);

               try {
                  if (is == null) {
                     throw new IOException("Icon file not found: " + iconPath);
                  }

                  byte[] bytes = is.readAllBytes();
                  imageBytes = BufferUtils.createByteBuffer(bytes.length);
                  imageBytes.put(bytes).flip();
                  IntBuffer w = stack.mallocInt(1);
                  IntBuffer h = stack.mallocInt(1);
                  IntBuffer channels = stack.mallocInt(1);
                  imageBuffer = STBImage.stbi_load_from_memory(imageBytes, w, h, channels, 4);
                  if (imageBuffer == null) {
                     throw new IOException("Failed to load image " + iconPath + ": " + STBImage.stbi_failure_reason());
                  }

                  loadedIconPixels.add(imageBuffer);
                  int width = w.get(0);
                  int height = h.get(0);
                  ((GLFWImage)icons.get(i)).width(width).height(height).pixels(imageBuffer);
               } catch (Throwable var24) {
                  if (is != null) {
                     try {
                        is.close();
                     } catch (Throwable var23) {
                        var24.addSuppressed(var23);
                     }
                  }

                  throw var24;
               }

               if (is != null) {
                  is.close();
               }
            }

            GLFW.glfwSetWindowIcon(windowHandle, icons);
         } catch (Throwable var25) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var22) {
                  var25.addSuppressed(var22);
               }
            }

            throw var25;
         }

         if (stack != null) {
            stack.close();
         }
      } finally {
         cleanUpIcons();
      }
   }

   public static void cleanUpIcons() {
      for (ByteBuffer byteBuffer : loadedIconPixels) {
         STBImage.stbi_image_free(byteBuffer);
      }

      loadedIconPixels.clear();
   }
}
