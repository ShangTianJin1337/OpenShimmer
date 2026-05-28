package dev.greencat.shimmer.util;

import java.io.File;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class FilePicker {
   public static File chooseImageFile() {
      MemoryStack stack = MemoryStack.stackPush();

      File var3;
      label40: {
         try {
            PointerBuffer filterPatterns = stack.pointers(stack.ASCII("*"));
            String filePath = TinyFileDialogs.tinyfd_openFileDialog("请选择一张图片(Choose a Image)", (String)null, filterPatterns, "(PNG/JPG)", false);
            if (filePath != null) {
               var3 = new File(filePath);
               break label40;
            }
         } catch (Throwable var5) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (stack != null) {
            stack.close();
         }

         return null;
      }

      if (stack != null) {
         stack.close();
      }

      return var3;
   }
}
