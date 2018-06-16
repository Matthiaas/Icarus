package et.song.vspfv;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import com.app.Interface.MyMediaCodec;
import com.app.util.ActivtyUtil;
import com.app.util.MyApplication;
import com.app.view.PreviewView;
import com.fh.lib.FHSDK;
import com.fh.lib.PlayInfo;
import com.fh.lib.SDLActivity;
import java.io.File;

public class VideoPlayBySDL extends SDLActivity {
    public Context mContext;
    private long mExitTime;
    private PreviewView mPreviewView;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        this.mContext = this;
        MyApplication.getInstance().addActivity(this);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        playInit();
        this.mPreviewView = new PreviewView(this);
        this.mPreviewView.layoutInit(mLayout);
        MyMediaCodec.getInstance().init(null);
    }

    public void playInit() {
        setVideoShowRect();
        FHSDK.setPlayInfo(new PlayInfo());
    }

    private void createFilePath() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            File file = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append(PreviewView.getSettingPath()).toString());
            if (!file.exists()) {
                file.mkdir();
            }
            FHSDK.setShotPath(file.getAbsolutePath());
            return;
        }
        ActivtyUtil.openToast(this.mContext, getString(C0127R.string.str_nofoundSDCard));
    }

    public void setVideoShowRect() {
        Rect rect = new Rect();
        rect.left = 0;
        rect.right = getWindowManager().getDefaultDisplay().getWidth();
        rect.top = 0;
        rect.bottom = getWindowManager().getDefaultDisplay().getHeight();
        FHSDK.setShowRect(rect, true);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation != 2) {
            int i = getResources().getConfiguration().orientation;
        }
        setVideoShowRect();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (PlayInfo.playType == 1 || PlayInfo.playType == 4) {
                return true;
            }
        } else if (keyCode == 82) {
            if (this.mPreviewView.getLayoutMenuShow()) {
                this.mPreviewView.setLayoutMenuShow(false);
            } else {
                this.mPreviewView.setLayoutMenuShow(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onDestroy() {
        MyMediaCodec.getInstance().unInit();
        this.mPreviewView.layoutUnInit(mLayout);
        this.mPreviewView = null;
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && PlayInfo.playType != 3) {
            this.mPreviewView.leftRockerRefresh();
        }
    }
}
