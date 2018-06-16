package et.song.vspfv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.app.util.MyApplication;
import com.app.view.PreviewView;
import com.bwin.airtoplay.RTSPPlayerView;
import com.fh.lib.PlayInfo;

public class RTSPActivity extends Activity {
    protected static ViewGroup mLayout;
    private String TAG = "rtspActivity";
    public Context mContext;
    private PreviewView mPreviewView;
    private RTSPPlayerView rtspSurface;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        this.mContext = this;
        DisplayMetrics dm = new DisplayMetrics();
        dm = getApplicationContext().getResources().getDisplayMetrics();
        this.rtspSurface = new RTSPPlayerView(this);
        mLayout = new FrameLayout(this);
        mLayout.addView(this.rtspSurface);
        setContentView(mLayout);
        MyApplication.getInstance().addActivity(this);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        this.mPreviewView = new PreviewView(this);
        this.mPreviewView.layoutInit(mLayout);
    }

    protected void onDestroy() {
        this.mPreviewView.layoutUnInit(mLayout);
        this.mPreviewView = null;
        super.onDestroy();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && PlayInfo.playType != 3) {
            this.mPreviewView.leftRockerRefresh();
        }
    }
}
