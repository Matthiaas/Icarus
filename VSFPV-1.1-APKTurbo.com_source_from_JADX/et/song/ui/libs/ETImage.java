package et.song.ui.libs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import et.song.ui.libs.ETAsync.ImageCallback;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ETImage {
    private static final long MB = 1048576;

    class C01751 implements ImageCallback {
        private final /* synthetic */ int val$height;
        private final /* synthetic */ ImageView val$view;
        private final /* synthetic */ int val$width;

        C01751(int i, int i2, ImageView imageView) {
            this.val$width = i;
            this.val$height = i2;
            this.val$view = imageView;
        }

        public void imageLoaded(byte[] data) {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = ETImage.computeSampleSize(options, -1, this.val$width * this.val$height);
            bmp = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeByteArray(data, 0, data.length, options), this.val$width, this.val$height, 2);
            if (this.val$view != null) {
                this.val$view.setImageBitmap(bmp);
            }
        }
    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        double w = (double) options.outWidth;
        double h = (double) options.outHeight;
        int lowerBound = maxNumOfPixels == -1 ? 1 : (int) Math.ceil(Math.sqrt((w * h) / ((double) maxNumOfPixels)));
        int upperBound = minSideLength == -1 ? 128 : (int) Math.min(Math.floor(w / ((double) minSideLength)), Math.floor(h / ((double) minSideLength)));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if (maxNumOfPixels == -1 && minSideLength == -1) {
            return 1;
        }
        if (minSideLength != -1) {
            return upperBound;
        }
        return lowerBound;
    }

    private static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        if (initialSize > 8) {
            return ((initialSize + 7) / 8) * 8;
        }
        int roundedSize = 1;
        while (roundedSize < initialSize) {
            roundedSize <<= 1;
        }
        return roundedSize;
    }

    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = computeSampleSize(options, -1, width * height);
        return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath, options), width, height, 2);
    }

    public static Bitmap getNetImageThumbnail(InputStream input, ImageView view, int width, int height) {
        return new ETAsync().loadDrawable(input, (String) view.getTag(), new C01751(width, height, view));
    }

    public static Bitmap clipBitmap(Bitmap bitmap, int x, int y, int w, int h) {
        return Bitmap.createBitmap(bitmap, x, y, w, h);
    }

    public static Bitmap readBitmap(Context context, int resId, int width, int height) throws Exception {
        InputStream is = context.getResources().openRawResource(resId);
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        opt.inSampleSize = computeSampleSize(opt, -1, width * height);
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap readBitmap(Resources res, int resId, int width, int height) throws Exception {
        InputStream is = res.openRawResource(resId);
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        opt.inSampleSize = computeSampleSize(opt, -1, width * height);
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap readBitmap(Resources res, int resId, int width, int height, boolean isMatrixed) throws Exception {
        if (isMatrixed) {
            InputStream is = res.openRawResource(resId);
            Options opt = new Options();
            opt.inPreferredConfig = Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, opt);
            opt.inSampleSize = computeSampleSize(opt, -1, width * height);
            opt.inJustDecodeBounds = false;
            return matrixBitmap(BitmapFactory.decodeStream(is, null, opt), width, height);
        }
        is = res.openRawResource(resId);
        opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        opt.inSampleSize = computeSampleSize(opt, -1, width * height);
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap readBitmap(Context context, int resId, int width, int height, boolean isMatrixed) throws Exception {
        if (isMatrixed) {
            InputStream is = context.getResources().openRawResource(resId);
            Options opt = new Options();
            opt.inPreferredConfig = Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, opt);
            opt.inSampleSize = computeSampleSize(opt, -1, width * height);
            opt.inJustDecodeBounds = false;
            return matrixBitmap(BitmapFactory.decodeStream(is, null, opt), width, height);
        }
        is = context.getResources().openRawResource(resId);
        opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, opt);
        opt.inSampleSize = computeSampleSize(opt, -1, width * height);
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap readBitmap(Context context, int resId) throws Exception {
        InputStream is = context.getResources().openRawResource(resId);
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap readBitmap(Resources res, int resId) throws Exception {
        InputStream is = res.openRawResource(resId);
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap matrixBitmap(Bitmap bitmap, int width, int height) throws Exception {
        Matrix matrixLayout = new Matrix();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        matrixLayout.postScale(1.0f * ((float) ((((double) width) * 1.0d) / ((double) w))), 1.0f * ((float) ((((double) height) * 1.0d) / ((double) h))));
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrixLayout, true);
    }

    public static Bitmap readBitmap(String path, int width, int height) throws Exception {
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        opt.inSampleSize = computeSampleSize(opt, -1, width * height);
        opt.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(new FileInputStream(path), new Rect(0, 0, 0, 0), opt);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bitmap(Intent intent) {
        byte[] buff = intent.getByteArrayExtra("bitmap");
        return BitmapFactory.decodeByteArray(buff, 0, buff.length);
    }

    public static Bitmap shot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Display display = activity.getWindowManager().getDefaultDisplay();
        view.layout(0, 500, display.getWidth() - 200, display.getHeight() - 250);
        return Bitmap.createBitmap(view.getDrawingCache());
    }

    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    public static void save(String path, String name, Bitmap bitmap) throws IOException {
        File file = new File(path, name);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(file));
    }

    public static Bitmap getBitmap(String path, String name) throws IOException {
        Options options = new Options();
        File file = new File(path, name);
        if (file.exists() && file.length() / MB > 1) {
            options.inSampleSize = 2;
        }
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static Bitmap getBitmap(String path) {
        Bitmap imageBitmap = null;
        try {
            Options options = new Options();
            File file = new File(path);
            if (file.exists() && file.length() / MB > 1) {
                options.inSampleSize = 2;
            }
            imageBitmap = BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageBitmap;
    }

    public static Bitmap zoomImage(Bitmap bm, double newWidth, double newHeight) {
        float width = (float) bm.getWidth();
        float height = (float) bm.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(((float) newWidth) / width, ((float) newHeight) / height);
        return Bitmap.createBitmap(bm, 0, 0, (int) width, (int) height, matrix, true);
    }
}
