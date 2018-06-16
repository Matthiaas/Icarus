package et.song.ui.libs;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.util.DisplayMetrics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ETWindow {

    class C00911 extends Thread {
        private final /* synthetic */ int val$keyCode;

        C00911(int i) {
            this.val$keyCode = i;
        }

        public void run() {
            new Instrumentation().sendKeyDownUpSync(this.val$keyCode);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void ScreenON(Activity activity) {
        activity.getWindow().addFlags(128);
    }

    public static void ScreenOFF(Activity activity) {
        activity.getWindow().clearFlags(128);
    }

    public static void FullWindow(Activity activity, int requestedOrientation) {
        activity.requestWindowFeature(1);
        activity.getWindow().setFlags(1024, 1024);
        activity.getWindow().setFormat(-3);
        activity.setRequestedOrientation(requestedOrientation);
        activity.getWindow().setFlags(512, 512);
    }

    public static void CutTitle(Activity activity, int requestedOrientation) {
        activity.requestWindowFeature(1);
        activity.getWindow().setFormat(-3);
        activity.setRequestedOrientation(requestedOrientation);
    }

    public static int GetWindowWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int GetWindowHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static void SendKeyCode(int keyCode) throws Exception {
        new C00911(keyCode).start();
    }

    public static String run(String[] cmd) {
        String line = "";
        try {
            InputStream is = Runtime.getRuntime().exec(cmd).getInputStream();
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            do {
            } while (!buf.readLine().startsWith("User"));
            line = buf.readLine();
            if (is != null) {
                buf.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int dip2px(Context context, float px) {
        return (int) (((double) (px * getScreenDensity(context))) + 0.5d);
    }
}
