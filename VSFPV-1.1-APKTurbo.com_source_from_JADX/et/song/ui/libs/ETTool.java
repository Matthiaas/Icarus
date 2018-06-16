package et.song.ui.libs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import org.apache.http.conn.util.InetAddressUtils;

@SuppressLint({"DefaultLocale"})
public final class ETTool {
    public static boolean StrIsEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }

    public static String BytesToHexString(byte[] src) throws Exception {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String BytesToHexString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return "";
        }
        int i = 0;
        while (i < src.length && i < len) {
            String hv = Integer.toHexString(src[i] & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            i++;
        }
        return stringBuilder.toString();
    }

    public static String BytesToHexStringEx(byte[] src, String c, int num) throws Exception {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            String hv = Integer.toHexString(src[i] & 255);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(c);
            if (i % num == 0) {
                stringBuilder.append("\r\n");
            }
        }
        return stringBuilder.toString();
    }

    public static byte[] HexStringToBytes(String hexString) throws Exception {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase(Locale.getDefault());
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) ((charToByte(hexChars[pos]) << 4) | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static float getRawSize(Context context, int unit, float size) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) ((pxValue / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static boolean isInternet(Activity activity) {
        NetworkInfo info = ((ConnectivityManager) activity.getSystemService("connectivity")).getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        return info.isRoaming() ? true : true;
    }

    public static String toIP(int i) {
        return (i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public static boolean checkNetworkInfo(Activity activity) {
        ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService("connectivity");
        State mobile = conMan.getNetworkInfo(0).getState();
        State wifi = conMan.getNetworkInfo(1).getState();
        if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
            return true;
        }
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
            return true;
        }
        return false;
    }

    public static boolean checkWifiInfo(Activity activity) {
        State wifi = ((ConnectivityManager) activity.getSystemService("connectivity")).getNetworkInfo(1).getState();
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
            return true;
        }
        return false;
    }

    public static String MD5(String str) {
        try {
            int i;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            char[] charArray = str.toCharArray();
            byte[] byteArray = new byte[charArray.length];
            for (i = 0; i < charArray.length; i++) {
                byteArray[i] = (byte) charArray[i];
            }
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (byte b : md5Bytes) {
                int val = b & 255;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void Vibrate(Activity activity, long milliseconds) {
        ((Vibrator) activity.getSystemService("vibrator")).vibrate(milliseconds);
    }

    public static void Vibrate(Activity activity, long[] pattern, boolean isRepeat) {
        ((Vibrator) activity.getSystemService("vibrator")).vibrate(pattern, isRepeat ? 1 : -1);
    }

    public static void MessageBox(Activity activity, float alpha, String msg, boolean isCancel) {
        AlertDialog alertDialog = new Builder(activity).setMessage(msg).create();
        Window window = alertDialog.getWindow();
        LayoutParams lp = window.getAttributes();
        lp.alpha = alpha;
        window.setAttributes(lp);
        if (isCancel) {
            alertDialog.setCanceledOnTouchOutside(true);
        } else {
            alertDialog.setCancelable(false);
        }
        alertDialog.show();
    }

    public static String getLocalIpAddress(boolean isIPV4) {
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration enumIpAddr = intf.getInetAddresses();
                InetAddress intAddr = (InetAddress) enumIpAddr.nextElement();
                if (isIPV4) {
                    if (!intAddr.isLoopbackAddress() && intf.getName().equals("wlan0") && InetAddressUtils.isIPv4Address(intAddr.getHostAddress())) {
                        return intAddr.getHostAddress().toString();
                    }
                } else if (!intAddr.isLoopbackAddress()) {
                    return intAddr.getHostAddress().toString();
                }
                enumIpAddr.hasMoreElements();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int ETWidth(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        return view.getMeasuredWidth();
    }

    public static int ETHeight(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        return view.getMeasuredHeight();
    }

    public static long m5T() {
        return SystemClock.elapsedRealtime();
    }

    public static double Dice(byte[] d1, byte[] d2) {
        int m = 0;
        int len = d1.length > d2.length ? d2.length : d1.length;
        int i = 0;
        while (i < len) {
            int dif = (int) (((float) d1[i]) * 0.2f);
            byte max = d1[i] + dif;
            if (d1[i] - dif <= d2[i] && max >= d2[i]) {
                m++;
            }
            i++;
        }
        return (double) (m / len);
    }

    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        for (int i = 0; i < bs.length; i++) {
            sb.append(chars[(bs[i] & ETValue.TYPE_CAR) >> 4]);
            sb.append(chars[bs[i] & 15]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[(hexStr.length() / 2)];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (((str.indexOf(hexs[i * 2]) * 16) + str.indexOf(hexs[(i * 2) + 1])) & 255);
        }
        return new String(bytes);
    }

    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (byte b2 : b) {
            String str;
            stmp = Integer.toHexString(b2 & 255);
            if (stmp.length() == 1) {
                str = "0" + stmp;
            } else {
                str = stmp;
            }
            sb.append(str);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    public static String strToUnicode(String strText) throws Exception {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < strText.length(); i++) {
            char intAsc = strText.charAt(i);
            String strHex = Integer.toHexString(intAsc);
            if (intAsc > '') {
                str.append("\\u" + strHex);
            } else {
                str.append("\\u00" + strHex);
            }
        }
        return str.toString();
    }

    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            str.append(new String(Character.toChars(Integer.valueOf(s.substring(2, 4) + "00", 16).intValue() + Integer.valueOf(s.substring(4), 16).intValue())));
        }
        return str.toString();
    }

    public static byte[] intToByte(int i) {
        return new byte[]{(byte) (i & 255), (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & i) >> 8), (byte) ((16711680 & i) >> 16), (byte) ((ViewCompat.MEASURED_STATE_MASK & i) >> 24)};
    }

    public static int bytesToInt(byte[] bytes) {
        return (((bytes[0] & 255) | ((bytes[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | ((bytes[2] << 16) & 16711680)) | ((bytes[3] << 24) & ViewCompat.MEASURED_STATE_MASK);
    }

    public static int bytesTounInt(byte b0, byte b1, byte b2, byte b3) {
        return (int) (((((long) (b0 & 255)) | ((long) ((b1 << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK))) | ((long) ((b2 << 16) & 16711680))) | ((long) ((b3 << 24) & ViewCompat.MEASURED_STATE_MASK)));
    }

    public static String getHour() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()).substring(11, 13);
    }

    public static String getMin() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()).substring(14, 16);
    }

    public static String getSecond() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()).substring(17, 19);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || bitmap == null) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate((float) degrees, (float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2));
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (bitmap == null) {
            return bmp;
        }
        bitmap.recycle();
        return bmp;
    }

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        return ThumbnailUtils.extractThumbnail(ThumbnailUtils.createVideoThumbnail(videoPath, kind), width, height, 2);
    }
}
