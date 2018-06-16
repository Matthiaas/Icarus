package com.bwin.airtoplay;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import et.song.vspfv.SysApp;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utilities {
    private static final String HOME_PATH_NAME = "AirToPlay";
    private static final String PHOTO_FILE_EXTENSION = "png";
    private static final String PHOTO_PATH_NAME = "image";
    private static final String VIDEO_FILE_EXTENSION = "mp4";
    private static final String VIDEO_PATH_NAME = "movie";

    class C00801 implements FileFilter {
        C00801() {
        }

        public boolean accept(File file) {
            try {
                String filePath = file.getCanonicalPath();
                if (filePath.substring(filePath.lastIndexOf(".") + 1).equalsIgnoreCase(Utilities.PHOTO_FILE_EXTENSION)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    class C00812 implements FileFilter {
        C00812() {
        }

        public boolean accept(File file) {
            try {
                String filePath = file.getCanonicalPath();
                if (filePath.substring(filePath.lastIndexOf(".") + 1).equalsIgnoreCase(Utilities.VIDEO_FILE_EXTENSION)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static String getHomePath() {
        String homePath = null;
        try {
            homePath = new File(Environment.getExternalStorageDirectory().getCanonicalPath(), HOME_PATH_NAME).getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return homePath;
    }

    public static String getSubDir(String dir) {
        String homePath = getHomePath();
        if (homePath == null) {
            return null;
        }
        String subDirPath = null;
        try {
            return new File(homePath, dir).getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
            return subDirPath;
        }
    }

    public static String getPhotoPath() {
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath()));
        SysApp.getMe();
        return stringBuilder.append(SysApp.SAVE_DATA_PATH).toString();
    }

    public static String getVideoPath() {
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath()));
        SysApp.getMe();
        return stringBuilder.append(SysApp.SAVE_DATA_PATH).toString();
    }

    public static List<String> loadPhotoList() {
        File[] photoFiles = new File(getPhotoPath()).listFiles(new C00801());
        List<String> photoFileNameList = null;
        if (photoFiles != null) {
            photoFileNameList = new ArrayList();
            for (File file : photoFiles) {
                photoFileNameList.add(file.getPath());
            }
            Collections.reverse(photoFileNameList);
        }
        return photoFileNameList;
    }

    public static List<String> loadVideoList() {
        File[] videoFiles = new File(getVideoPath()).listFiles(new C00812());
        List<String> videoFileNameList = null;
        if (videoFiles != null) {
            videoFileNameList = new ArrayList();
            for (File file : videoFiles) {
                videoFileNameList.add(file.getPath());
            }
            Collections.reverse(videoFileNameList);
        }
        return videoFileNameList;
    }

    public static String getRandomPhotoFilePath() {
        String str = null;
        String photoPath = getPhotoPath();
        if (photoPath != null) {
            File photoDir = new File(photoPath);
            if (photoDir.exists() || photoDir.mkdirs()) {
                str = null;
                try {
                    str = new File(photoPath, new StringBuilder(String.valueOf(new SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.getDefault()).format(new Date()))).append(".").append(PHOTO_FILE_EXTENSION).toString()).getCanonicalPath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    public static String getRandomVideoFilePath() {
        String str = null;
        String videoPath = getVideoPath();
        if (videoPath != null) {
            File videoDir = new File(videoPath);
            if (videoDir.exists() || videoDir.mkdirs()) {
                str = null;
                try {
                    str = new File(videoPath, new StringBuilder(String.valueOf(new SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.getDefault()).format(new Date()))).append(".").append(VIDEO_FILE_EXTENSION).toString()).getCanonicalPath();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return str;
    }

    public static Bitmap addBorderToBitmap(Bitmap bmp, int color, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + (borderSize * 2), bmp.getHeight() + (borderSize * 2), bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawLine(0.0f, 0.0f, (float) (bmp.getWidth() + (borderSize * 2)), (float) (borderSize * 2), paint);
        canvas.drawLine(0.0f, (float) (bmp.getHeight() + borderSize), (float) (bmp.getWidth() + (borderSize * 2)), (float) (bmp.getHeight() + (borderSize * 2)), paint);
        canvas.drawLine(0.0f, 0.0f, (float) borderSize, (float) (bmp.getHeight() + (borderSize * 2)), paint);
        canvas.drawLine((float) (bmp.getWidth() + borderSize), 0.0f, (float) (bmp.getWidth() + (borderSize * 2)), (float) (bmp.getHeight() + (borderSize * 2)), paint);
        canvas.drawBitmap(bmp, (float) borderSize, (float) borderSize, null);
        return bmpWithBorder;
    }
}
