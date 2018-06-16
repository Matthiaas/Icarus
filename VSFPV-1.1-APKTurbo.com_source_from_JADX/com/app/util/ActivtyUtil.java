package com.app.util;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ActivtyUtil {

    class C00691 implements OnClickListener {
        C00691() {
        }

        public void onClick(DialogInterface dialog, int which) {
        }
    }

    public static void showAlert(Context context, CharSequence title, CharSequence message, CharSequence btnTitle) {
        new Builder(context).setTitle(title).setMessage(message).setPositiveButton(btnTitle, new C00691()).show();
    }

    public static void openToast(Context context, String str) {
        Toast.makeText(context, str, 0).show();
    }

    public static String getCurSysDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
    }

    public static String formatTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(Long.valueOf(time));
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
}
