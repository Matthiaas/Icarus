package et.song.vspfv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.opengles.GLFrameRenderer;
import com.android.opengles.GLFrameSurface;
import com.app.Interface.MyMediaCodec;
import com.app.util.MyApplication;
import com.fh.lib.PlayInfo;

public class OpenglActivity extends Activity {
    protected static ViewGroup mLayout;
    private String TAG = "MyMediaCodec";
    private GLFrameSurface glFrameSurface;
    public Context mContext;
    public GLFrameRenderer mFrameRender;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        this.mContext = this;
        DisplayMetrics dm = new DisplayMetrics();
        dm = getApplicationContext().getResources().getDisplayMetrics();
        if (PlayInfo.decodeType != 4) {
            this.glFrameSurface = new GLFrameSurface(this);
            this.glFrameSurface.setEGLContextClientVersion(2);
            this.mFrameRender = new GLFrameRenderer(this.mContext, this.glFrameSurface, dm);
            this.glFrameSurface.setRenderer(this.mFrameRender);
            mLayout = new FrameLayout(this);
            mLayout.addView(this.glFrameSurface);
        }
        setContentView(mLayout);
        MyApplication.getInstance().addActivity(this);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        init();
    }

    private void init() {
        if (PlayInfo.decodeType == 4) {
            MyMediaCodec.getInstance().init(null);
        } else {
            MyMediaCodec.getInstance().init(this.mFrameRender);
        }
    }

    protected void onResume() {
        Log.v(this.TAG, "onResume()");
        super.onResume();
        if (this.glFrameSurface != null) {
            this.glFrameSurface.rigisterListener();
        }
    }

    protected void onPause() {
        Log.v(this.TAG, "onPause()");
        super.onPause();
        if (this.glFrameSurface != null) {
            this.glFrameSurface.unRigisterListener();
        }
    }

    protected void onDestroy() {
        Log.v(this.TAG, "onDestroy()");
        MyMediaCodec.getInstance().unInit();
        super.onDestroy();
    }
}
