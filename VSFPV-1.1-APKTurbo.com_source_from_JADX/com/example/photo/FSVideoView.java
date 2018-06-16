package com.example.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FSVideoView extends VideoView {
    public FSVideoView(Context context) {
        super(context);
    }

    public FSVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FSVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
    }
}
