package et.song.vspfv;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import et.song.ui.libs.ETTool;

public class AppConfig {
    public static final int PLAYER_BUFFER_END = 10002;
    public static final int PLAYER_BUFFER_GOING = 10004;
    public static final int PLAYER_BUFFER_START = 10001;
    public static final int PLAYER_EN_REACED = 10005;
    public static final int PLAYER_SURFACE_SIZE = 10003;
    private static final String TAG = "AppConfig";
    public static final int TCP_CONNECT_ERROR = 11003;
    public static final int TCP_CONNECT_SUCCEED = 11001;
    public static final int TCP_RECEIVE_DATA = 11002;
    private final String DESKEY = "ecardkey";
    public float Density = 0.0f;
    public int DensityDpi = 0;
    public int DesktopH = 0;
    public int DesktopW = 0;
    public String DeviceMAC = null;
    public float Xdpi = 0.0f;
    public float Ydip = 0.0f;
    private Context mContext;
    private SharedPreferences mSharePre;

    public AppConfig(Context context) {
        this.mContext = context;
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        this.mSharePre = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        this.DesktopW = dm.widthPixels;
        this.DesktopH = dm.heightPixels;
        this.Density = dm.density;
        this.DensityDpi = dm.densityDpi;
        this.Xdpi = dm.xdpi;
        this.Ydip = dm.ydpi;
        this.DeviceMAC = getLocalMacAddress();
        Log.i(TAG, "DesktopW:" + this.DesktopW + "  DesktopH:" + this.DesktopH + "  Density:" + this.Density + "  DensityDpi:" + this.DensityDpi + "  Xdpi:" + this.Xdpi + "  Ydip:" + this.Ydip);
    }

    public void saveData(String key, String val) {
        if (this.mSharePre != null) {
            Editor editor = this.mSharePre.edit();
            editor.putString(key, val);
            editor.commit();
        }
    }

    public void saveData(String key, int val) {
        saveData(key, String.valueOf(val));
    }

    public void saveData(String key, boolean val) {
        saveData(key, String.valueOf(val));
    }

    public void saveData(String key, float val) {
        saveData(key, String.valueOf(val));
    }

    public String getData(String key) {
        String reStr = "";
        if (ETTool.StrIsEmpty(key) || this.mSharePre == null) {
            return reStr;
        }
        return this.mSharePre.getString(key, "");
    }

    public String getData(String key, String defVal) {
        String reStr = getData(key);
        if (ETTool.StrIsEmpty(reStr)) {
            return defVal;
        }
        return reStr;
    }

    public String getLocalMacAddress() {
        return ((WifiManager) this.mContext.getSystemService("wifi")).getConnectionInfo().getMacAddress();
    }
}
