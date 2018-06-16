package com.app.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import com.fh.lib.FHSDK;
import et.song.vspfv.SysApp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/* compiled from: PreviewView */
class ConvertRecThread implements Runnable {
    private String filePath;
    private Context mContext;
    private List<String> mRecData = new ArrayList();

    public ConvertRecThread(Context mContext, String filePath) {
        this.mContext = mContext;
        this.filePath = filePath;
    }

    private static void copyfile(File fromFile, File toFile, Boolean rewrite) {
        if (fromFile.exists() && fromFile.isFile() && fromFile.canRead()) {
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            if (toFile.exists() && rewrite.booleanValue()) {
                toFile.delete();
            }
            try {
                FileInputStream fosfrom = new FileInputStream(fromFile);
                FileOutputStream fosto = new FileOutputStream(toFile);
                byte[] bt = new byte[1024];
                while (true) {
                    int c = fosfrom.read(bt);
                    if (c <= 0) {
                        fosfrom.close();
                        fosto.close();
                        return;
                    }
                    fosto.write(bt, 0, c);
                }
            } catch (Exception ex) {
                Log.e("readfile", ex.getMessage());
            }
        }
    }

    private void RefreshListData() {
        this.mRecData.clear();
        File[] subFile = new File(SysApp.SAVE_PATH).listFiles();
        for (int ii = 0; ii < subFile.length; ii++) {
            if (!subFile[ii].isDirectory()) {
                String filename = subFile[ii].getName().trim();
                String str = filename.toLowerCase();
                if (str.endsWith(".h264") || str.endsWith(".jpg") || str.endsWith(".png") || str.endsWith(".bmp")) {
                    this.mRecData.add(filename);
                }
            }
        }
    }

    private void output(String srcFilePath) {
        File sd = Environment.getExternalStorageDirectory();
        String[] m = srcFilePath.split("/");
        String recName = m[m.length - 1];
        StringBuilder append = new StringBuilder(String.valueOf(sd.getPath())).append("/DCIM");
        SysApp.getMe();
        String dstFilePath = append.append(SysApp.SAVE_DATA_PATH).append("/").append(recName).toString();
        String str = srcFilePath.substring(srcFilePath.lastIndexOf(46), srcFilePath.length());
        if (".h264".equalsIgnoreCase(str)) {
            dstFilePath = dstFilePath.substring(0, dstFilePath.lastIndexOf(46)) + ".avi";
            File dstFile = new File(dstFilePath);
            if (!dstFile.getParentFile().exists()) {
                dstFile.getParentFile().mkdirs();
            }
            if (!dstFile.exists()) {
                int handle = FHSDK.startConvertRecFormat(srcFilePath, dstFilePath);
                if (handle != 0) {
                    while (100 != FHSDK.getConvertProgress(handle)) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    FHSDK.stopConvertRecFormat(handle);
                    try {
                        Media.insertImage(this.mContext.getContentResolver(), dstFile.getAbsolutePath(), recName, null);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    this.mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + dstFile.getAbsoluteFile())));
                }
            }
        } else if (".jpg".equalsIgnoreCase(str) || ".bmp".equalsIgnoreCase(str)) {
            copyfile(new File(srcFilePath), new File(dstFilePath), Boolean.valueOf(false));
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void run() {
        if (this.filePath != null) {
            output(this.filePath);
        }
    }
}
