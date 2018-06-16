package com.example.photo;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Gallery;

public class PhotosGallery extends Gallery implements OnTouchListener {
    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH;
    private float baseValue;
    private PhotosImageView imageView;
    private float originalScale;

    public PhotosGallery(Context context) {
        super(context);
        init();
    }

    public PhotosGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhotosGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnTouchListener(this);
        this.SCREEN_WIDTH = PlayPhotoActivity.SCREEN_WIDTH;
        this.SCREEN_HEIGHT = PlayPhotoActivity.SCREEN_HEIGHT;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        View view = getSelectedView();
        if (view instanceof PhotosImageView) {
            this.imageView = (PhotosImageView) view;
            float[] v = new float[9];
            this.imageView.getImageMatrix().getValues(v);
            int width = (int) (this.imageView.getScale() * ((float) this.imageView.getImageWidth()));
            int height = (int) (this.imageView.getScale() * ((float) this.imageView.getImageHeight()));
            if (width > this.SCREEN_WIDTH || height > this.SCREEN_HEIGHT) {
                float left = v[2];
                float right = left + ((float) width);
                Rect rect = new Rect();
                this.imageView.getGlobalVisibleRect(rect);
                if (distanceX > 0.0f && rect.left <= 0 && right >= ((float) this.SCREEN_WIDTH)) {
                    this.imageView.postTranslate(-distanceX, -distanceY);
                    return false;
                } else if (distanceX < 0.0f && rect.right >= this.SCREEN_WIDTH && left <= 0.0f) {
                    this.imageView.postTranslate(-distanceX, -distanceY);
                    return false;
                }
            }
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        View view = getSelectedView();
        if (view instanceof PhotosImageView) {
            this.imageView = (PhotosImageView) view;
            switch (event.getAction()) {
                case 1:
                    int height = (int) (this.imageView.getScale() * ((float) this.imageView.getImageHeight()));
                    if (((int) (this.imageView.getScale() * ((float) this.imageView.getImageWidth()))) <= this.SCREEN_WIDTH && height <= this.SCREEN_HEIGHT) {
                        if (this.imageView.getScale() < 1.0f) {
                            this.imageView.zoomTo(1.0f, 0.0f, 0.0f);
                            break;
                        }
                    }
                    float[] v = new float[9];
                    this.imageView.getImageMatrix().getValues(v);
                    float top = v[5];
                    float bottom = top + ((float) height);
                    if (top > 0.0f) {
                        this.imageView.postTranslateDur(-top, 200.0f);
                    }
                    if (bottom < ((float) this.SCREEN_HEIGHT)) {
                        this.imageView.postTranslateDur(((float) this.SCREEN_HEIGHT) - bottom, 200.0f);
                        break;
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean onTouch(View v, MotionEvent event) {
        View view = getSelectedView();
        if (view instanceof PhotosImageView) {
            this.imageView = (PhotosImageView) view;
            switch (event.getAction()) {
                case 0:
                    this.originalScale = this.imageView.getScale();
                    this.baseValue = 0.0f;
                    break;
                case 2:
                    if (event.getPointerCount() == 2) {
                        float x2 = event.getX(1);
                        float y2 = event.getY(1);
                        float x = event.getX(0) - x2;
                        float y = event.getY(0) - y2;
                        float value = (float) Math.sqrt((double) ((x * x) + (y * y)));
                        if (this.baseValue != 0.0f) {
                            this.imageView.zoomTo(this.originalScale * (value / this.baseValue), x + x2, y + y2);
                            break;
                        }
                        this.baseValue = value;
                        break;
                    }
                    break;
            }
        }
        return false;
    }
}
