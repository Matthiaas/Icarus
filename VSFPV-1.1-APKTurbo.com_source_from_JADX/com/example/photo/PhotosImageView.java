package com.example.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PhotosImageView extends ImageView {
    private static final float MAX_ZOOM = 2.0f;
    private static final float MIN_ZOOM = 0.8f;
    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH;
    private int imageHeight;
    private int imageWidth;
    private final Matrix mBaseMatrix;
    private Bitmap mBitmap;
    private final Matrix mDisplayMatrix;
    private final Handler mHandler;
    private final float[] mMatrixValues;
    private final Matrix mSuppMatrix;
    private float tag;

    public PhotosImageView(Context context) {
        super(context);
        this.mMatrixValues = new float[9];
        this.mDisplayMatrix = new Matrix();
        this.mBaseMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mHandler = new Handler();
        this.mBitmap = null;
        this.tag = 0.0f;
        init();
    }

    public PhotosImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMatrixValues = new float[9];
        this.mDisplayMatrix = new Matrix();
        this.mBaseMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mHandler = new Handler();
        this.mBitmap = null;
        this.tag = 0.0f;
        init();
    }

    public PhotosImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mMatrixValues = new float[9];
        this.mDisplayMatrix = new Matrix();
        this.mBaseMatrix = new Matrix();
        this.mSuppMatrix = new Matrix();
        this.mHandler = new Handler();
        this.mBitmap = null;
        this.tag = 0.0f;
        init();
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
        this.SCREEN_WIDTH = PlayPhotoActivity.SCREEN_WIDTH;
        this.SCREEN_HEIGHT = PlayPhotoActivity.SCREEN_HEIGHT;
    }

    public int getImageWidth() {
        return this.imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return this.imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
        this.mBitmap = bitmap;
        layoutToCenter();
    }

    private void layoutToCenter() {
        float fill_width = (float) (this.SCREEN_WIDTH - this.imageWidth);
        float fill_height = (float) (this.SCREEN_HEIGHT - this.imageHeight);
        float tran_width = 0.0f;
        float tran_height = 0.0f;
        if (fill_width > 0.0f) {
            tran_width = fill_width / MAX_ZOOM;
        }
        if (fill_height > 0.0f) {
            tran_height = fill_height / MAX_ZOOM;
        }
        postTranslate(tran_width, tran_height);
        setImageMatrix(getImageViewMatrix());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getRepeatCount() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        event.startTracking();
        return true;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !event.isTracking() || event.isCanceled() || getScale() <= 1.0f) {
            return super.onKeyUp(keyCode, event);
        }
        zoomTo(1.0f, ((float) getWidth()) / MAX_ZOOM, ((float) getHeight()) / MAX_ZOOM);
        return true;
    }

    public float getScale() {
        return getValue(this.mSuppMatrix);
    }

    private float getValue(Matrix matrix) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[0];
    }

    public void postTranslateDur(float dy, float durationMs) {
        this.tag = 0.0f;
        final float incrementPerMs = dy / durationMs;
        final long startTime = System.currentTimeMillis();
        final float f = durationMs;
        this.mHandler.post(new Runnable() {
            public void run() {
                float currentMs = Math.min(f, (float) (System.currentTimeMillis() - startTime));
                PhotosImageView.this.postTranslate(0.0f, (incrementPerMs * currentMs) - PhotosImageView.this.tag);
                PhotosImageView.this.tag = incrementPerMs * currentMs;
                if (currentMs < f) {
                    PhotosImageView.this.mHandler.post(this);
                }
            }
        });
    }

    public void postTranslate(float dx, float dy) {
        this.mSuppMatrix.postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }

    public void zoomTo(float scale, float centerX, float centerY) {
        if (scale > MAX_ZOOM) {
            scale = MAX_ZOOM;
        } else if (scale < MIN_ZOOM) {
            scale = MIN_ZOOM;
        }
        float deltaScale = scale / getScale();
        this.mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
        center();
    }

    private void center() {
        if (this.mBitmap != null) {
            RectF rect = new RectF(0.0f, 0.0f, (float) this.mBitmap.getWidth(), (float) this.mBitmap.getHeight());
            getImageViewMatrix().mapRect(rect);
            float deltaX = 0.0f;
            float deltaY = 0.0f;
            float height = rect.height();
            float width = rect.width();
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            if (height < ((float) viewHeight)) {
                deltaY = ((((float) viewHeight) - height) / MAX_ZOOM) - rect.top;
            } else if (rect.top > 0.0f) {
                deltaY = -rect.top;
            } else if (rect.bottom < ((float) viewHeight)) {
                deltaY = ((float) getHeight()) - rect.bottom;
            }
            if (width < ((float) viewWidth)) {
                deltaX = ((((float) viewWidth) - width) / MAX_ZOOM) - rect.left;
            } else if (rect.left > 0.0f) {
                deltaX = -rect.left;
            } else if (rect.right < ((float) viewWidth)) {
                deltaX = ((float) viewWidth) - rect.right;
            }
            postTranslate(deltaX, deltaY);
            setImageMatrix(getImageViewMatrix());
        }
    }

    private Matrix getImageViewMatrix() {
        this.mDisplayMatrix.set(this.mBaseMatrix);
        this.mDisplayMatrix.postConcat(this.mSuppMatrix);
        return this.mDisplayMatrix;
    }
}
