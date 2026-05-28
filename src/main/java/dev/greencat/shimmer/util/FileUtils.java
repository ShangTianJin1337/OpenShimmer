package dev.greencat.shimmer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
   public static void copyFileFromJar(String path, String newpath) {
      try {
         makeFile(newpath);
         InputStream in = new String().getClass().getClassLoader().getResourceAsStream(path);
         write2File(in, newpath);
      } catch (IOException var3) {
         var3.printStackTrace();
      }
   }

   public static boolean makeFile(String path) {
      File file = new File(path);
      if (file.exists()) {
         return false;
      } else if (path.endsWith(File.separator)) {
         return false;
      } else if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
         return false;
      } else {
         try {
            return file.createNewFile();
         } catch (IOException var3) {
            var3.printStackTrace();
            return false;
         }
      }
   }

   public static void write2File(InputStream is, String filePath) throws IOException {
      OutputStream os = new FileOutputStream(filePath);
      int len = 8192;
      byte[] buffer = new byte[len];

      while ((len = is.read(buffer, 0, len)) != -1) {
         os.write(buffer, 0, len);
      }

      os.close();
      is.close();
   }
}
