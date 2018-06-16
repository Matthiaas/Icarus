package et.song.ui.libs;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Environment;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public final class ETEnv {
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static int getSDKVersion() {
        try {
            return VERSION.SDK_INT;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String getUpdataVerJSON(String serverPath) throws Exception {
        StringBuilder newVerJSON = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpParams httpParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        HttpEntity entity = client.execute(new HttpGet(serverPath)).getEntity();
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8192);
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                newVerJSON.append(new StringBuilder(String.valueOf(line)).append("\n").toString());
            }
            reader.close();
        }
        return newVerJSON.toString();
    }

    public static boolean isForeground(Context context) {
        String packageName = getPackageName(context);
        String topActivityClassName = getTopActivityName(context);
        System.out.println("packageName=" + packageName + ",topActivityClassName=" + topActivityClassName);
        if (packageName == null || topActivityClassName == null || !topActivityClassName.startsWith(packageName)) {
            return false;
        }
        return true;
    }

    private static String getTopActivityName(Context context) {
        List runningTaskInfos = ((ActivityManager) context.getSystemService("activity")).getRunningTasks(1);
        if (runningTaskInfos != null) {
            return ((RunningTaskInfo) runningTaskInfos.get(0)).topActivity.getClassName();
        }
        return null;
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static int getVerCode(Context context, String packName) throws NameNotFoundException {
        int verCode = -1;
        try {
            return context.getPackageManager().getPackageInfo(packName, 0).versionCode;
        } catch (Exception e) {
            return verCode;
        }
    }

    public static String getVerName(Context context, String packName) {
        String verName = "";
        try {
            return context.getPackageManager().getPackageInfo(packName, 0).versionName;
        } catch (Exception e) {
            return verName;
        }
    }

    public static String getFilePath(Context context) {
        return context.getFilesDir().getPath();
    }
}
