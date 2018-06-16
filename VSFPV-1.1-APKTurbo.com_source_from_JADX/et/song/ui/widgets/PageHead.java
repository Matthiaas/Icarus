package et.song.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import et.song.vspfv.C0127R;

public class PageHead extends RelativeLayout {
    public ImageView LeftBnt;
    public ImageView RightBnt;
    public TextView TitleView;
    private Context mContext;
    private String title;

    public PageHead(Context context) {
        super(context);
        InitView();
    }

    public PageHead(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        InitView();
    }

    private void InitView() {
        View layout = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C0127R.layout.view_pagehead, this);
        this.LeftBnt = (ImageView) findViewById(C0127R.id.HeadLeftimageView);
        this.RightBnt = (ImageView) findViewById(C0127R.id.HeadRightimageView);
        this.TitleView = (TextView) findViewById(C0127R.id.HeadTitletextView);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.TitleView.setText(title);
    }
}
