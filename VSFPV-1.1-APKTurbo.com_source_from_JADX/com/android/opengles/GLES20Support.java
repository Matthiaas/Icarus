package com.android.opengles;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class GLES20Support {

    class C00641 implements OnClickListener {
        private final /* synthetic */ Activity val$activity;

        C00641(Activity activity) {
            this.val$activity = activity;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$activity.finish();
        }
    }

    public static boolean detectOpenGLES20(Context context) {
        return ((ActivityManager) context.getSystemService("activity")).getDeviceConfigurationInfo().reqGlEsVersion >= 131072;
    }

    public static Dialog getNoSupportGLES20Dialog(Activity activity) {
        Builder b = new Builder(activity);
        b.setCancelable(false);
        b.setTitle("不支持");
        b.setMessage("不支持GLES20");
        b.setNegativeButton("退出", new C00641(activity));
        return b.create();
    }
}
