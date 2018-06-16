package et.song.vspfv;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import com.app.util.ActivtyUtil;
import com.app.util.MyApplication;
import com.app.util.log;
import com.app.view.PreviewView;
import com.fh.lib.FHSDK;
import com.fh.lib.PlayInfo;
import java.io.File;

public class VideoPlayByOpengl extends OpenglActivity {
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
    }

    public void playInit() {
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            if (keyCode == 82) {
                if (this.mPreviewView.getLayoutMenuShow()) {
                    this.mPreviewView.setLayoutMenuShow(false);
                } else {
                    this.mPreviewView.setLayoutMenuShow(true);
                }
            }
            return super.onKeyDown(keyCode, event);
        } else if (PlayInfo.playType == 1 || PlayInfo.playType == 4) {
            return true;
        } else {
            if (System.currentTimeMillis() - this.mExitTime > 2000) {
                ActivtyUtil.openToast(this.mContext, (String) getText(C0127R.string.id_doubleClickToExit));
                this.mExitTime = System.currentTimeMillis();
                return true;
            }
            if (!(PlayInfo.playType == 1 || PlayInfo.playType == 4 || PlayInfo.playType != 3)) {
                finish();
            }
            return true;
        }
    }

    protected void onDestroy() {
        this.mPreviewView.layoutUnInit(mLayout);
        this.mPreviewView = null;
        super.onDestroy();
    }

    protected void onResume() {
        this.mPreviewView.SetActiveCheckSSIDThread(true);
        super.onResume();
    }

    protected void onPause() {
        log.m1e("onPause");
        this.mPreviewView.SetActiveCheckSSIDThread(false);
        super.onPause();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && PlayInfo.playType != 3) {
            this.mPreviewView.leftRockerRefresh();
        }
    }
}
