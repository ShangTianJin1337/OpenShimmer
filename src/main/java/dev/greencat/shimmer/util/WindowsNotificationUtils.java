package dev.greencat.shimmer.util;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public class WindowsNotificationUtils {
   private static final int NIM_ADD = 0;
   private static final int NIM_DELETE = 2;
   private static final int NIF_ICON = 2;
   private static final int NIF_TIP = 4;
   private static final int NIF_INFO = 16;
   private static final long IDI_INFORMATION = 32516L;
   private static final int NIIF_INFO = 1;
   private static final int NIIF_WARNING = 2;
   private static final int NIIF_ERROR = 3;

   public static void sendNotification(String title, String content, int type) {
      if (Platform.isWindows()) {
         new Thread(() -> {
            com.sun.jna.platform.win32.WinDef.HWND hwnd = WindowsNotificationUtils.User32.INSTANCE.GetForegroundWindow();
            if (hwnd == null) {
               hwnd = WindowsNotificationUtils.User32.INSTANCE.GetDesktopWindow();
            }

            WindowsNotificationUtils.NOTIFYICONDATA data = new WindowsNotificationUtils.NOTIFYICONDATA();
            data.hWnd = hwnd;
            data.uID = (int)System.currentTimeMillis();
            data.uFlags = 22;
            data.hIcon = WindowsNotificationUtils.User32.INSTANCE.LoadIcon(null, 32516L);
            copyTo(title, data.szInfoTitle);
            copyTo(content, data.szInfo);
            copyTo("Notification", data.szTip);
            switch (type) {
               case 2:
                  data.dwInfoFlags = 2;
                  break;
               case 3:
                  data.dwInfoFlags = 3;
                  break;
               default:
                  data.dwInfoFlags = 1;
            }

            WindowsNotificationUtils.Shell32.INSTANCE.Shell_NotifyIcon(0, data);

            try {
               Thread.sleep(3000L);
            } catch (InterruptedException var6) {
            }

            WindowsNotificationUtils.Shell32.INSTANCE.Shell_NotifyIcon(2, data);
         }).start();
      }
   }

   private static void copyTo(String src, char[] dest) {
      if (src != null) {
         int len = Math.min(src.length(), dest.length - 1);
         System.arraycopy(src.toCharArray(), 0, dest, 0, len);
         dest[len] = 0;
      }
   }

   @FieldOrder({"cbSize", "hWnd", "uID", "uFlags", "uCallbackMessage", "hIcon", "szTip", "dwState", "dwStateMask", "szInfo", "uTimeoutOrVersion", "szInfoTitle", "dwInfoFlags", "guidItem", "hBalloonIcon"})
   public static class NOTIFYICONDATA extends Structure {
      public int cbSize;
      public com.sun.jna.platform.win32.WinDef.HWND hWnd;
      public int uID;
      public int uFlags;
      public int uCallbackMessage;
      public com.sun.jna.platform.win32.WinDef.HICON hIcon;
      public char[] szTip = new char[128];
      public int dwState;
      public int dwStateMask;
      public char[] szInfo = new char[256];
      public int uTimeoutOrVersion;
      public char[] szInfoTitle = new char[64];
      public int dwInfoFlags;
      public byte[] guidItem = new byte[16];
      public com.sun.jna.platform.win32.WinDef.HICON hBalloonIcon;

      public NOTIFYICONDATA() {
         this.cbSize = this.size();
      }
   }

   public interface Shell32 extends StdCallLibrary {
      WindowsNotificationUtils.Shell32 INSTANCE = (WindowsNotificationUtils.Shell32)Native.load(
         "shell32", WindowsNotificationUtils.Shell32.class, W32APIOptions.DEFAULT_OPTIONS
      );

      boolean Shell_NotifyIcon(int var1, WindowsNotificationUtils.NOTIFYICONDATA var2);
   }

   public interface User32 extends StdCallLibrary {
      WindowsNotificationUtils.User32 INSTANCE = (WindowsNotificationUtils.User32)Native.load(
         "user32", WindowsNotificationUtils.User32.class, W32APIOptions.DEFAULT_OPTIONS
      );

      WindowsNotificationUtils.User32.HWND GetForegroundWindow();

      WindowsNotificationUtils.User32.HWND GetDesktopWindow();

      WindowsNotificationUtils.User32.HICON LoadIcon(Object var1, Object var2);

      public static class HICON extends com.sun.jna.platform.win32.WinDef.HICON {
      }

      public static class HWND extends com.sun.jna.platform.win32.WinDef.HWND {
      }
   }
}
