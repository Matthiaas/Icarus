package et.song.vspfv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.app.util.MyApplication;
import com.app.util.log;
import com.fh.lib.FHSDK;
import com.fh.lib.PlayInfo;
import et.song.ui.libs.ETGlobal;
import et.song.ui.libs.ETImage;
import et.song.ui.libs.ETWindow;
import et.song.ui.widgets.ETButton;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity implements OnClickListener {
    private static Handler mHandler = null;
    public Context mContext;

    class C01471 implements Runnable {
        C01471() {
        }

        public void run() {
            try {
                if (ETGlobal.bitmapAnimReco == null) {
                    ETGlobal.bitmapAnimReco = new Bitmap[]{ETImage.readBitmap(WelcomeActivity.this.getResources(), (int) C0127R.drawable.ic_reco_off, (ETGlobal.f6W - 320) / 10, ETGlobal.f5H / 10, true), ETImage.readBitmap(WelcomeActivity.this.getResources(), (int) C0127R.drawable.ic_reco_on, (ETGlobal.f6W - 320) / 10, ETGlobal.f5H / 10, true)};
                }
                if (ETGlobal.bitmapHelpBg == null) {
                    ETGlobal.bitmapHelpBg = ETImage.readBitmap(WelcomeActivity.this.getResources(), (int) C0127R.drawable.helpbg, ETGlobal.f6W, ETGlobal.f5H, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            WelcomeActivity.mHandler.sendEmptyMessage(5);
        }
    }

    static class MsgHandler extends Handler {
        List<TextView> TextViews = new ArrayList();
        WeakReference<Activity> mActivity;

        MsgHandler(Activity activity) {
            this.mActivity = new WeakReference(activity);
        }

        public void handleMessage(Message msg) {
            Activity activity = (Activity) this.mActivity.get();
            super.handleMessage(msg);
            switch (msg.what) {
                case 5:
                    ((ETButton) activity.findViewById(C0127R.id.button_start)).setOnClickListener((OnClickListener) activity);
                    ((ETButton) activity.findViewById(C0127R.id.button_help)).setOnClickListener((OnClickListener) activity);
                    ETButton setting = (ETButton) activity.findViewById(C0127R.id.button_setting);
                    setting.setOnClickListener((OnClickListener) activity);
                    setting.setVisibility(8);
                    ((ETButton) activity.findViewById(C0127R.id.button_url)).setOnClickListener((OnClickListener) activity);
                    return;
                default:
                    return;
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.e("WelcomeActivity", "onCreate()");
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        this.mContext = this;
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        ETGlobal.f6W = ETWindow.GetWindowWidth(this);
        ETGlobal.f5H = ETWindow.GetWindowHeight(this);
        setContentView(C0127R.layout.activity_welcome);
        mHandler = new MsgHandler(this);
        new Thread(new C01471()).start();
        FHSDK.apiInit();
        PlayInfo.decodeType = 0;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        SysApp.getMe().sendUDPEndCmd();
        MyApplication.getInstance().exit();
        return true;
    }

    public void onStart() {
        super.onStart();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        if (v.getId() == C0127R.id.button_start) {
            if (PlayInfo.udpDevType == 4 || PlayInfo.udpDevType == 5) {
                PlayInfo.playType = 4;
            } else {
                PlayInfo.playType = 1;
                SysApp.getMe().StartActive(true);
            }
            SysApp.getMe().StartCheckWork(true);
            if (7 == PlayInfo.udpDevType) {
                log.m1e("intent = new Intent(this, RTSPActivity.class);");
                startActivity(new Intent(this, RTSPActivity.class));
            } else if (PlayInfo.decodeType == 2 || PlayInfo.decodeType == 3) {
                startActivity(new Intent(this, VideoPlayBySDL.class));
            } else {
                startActivity(new Intent(this, VideoPlayByOpengl.class));
            }
        } else if (v.getId() == C0127R.id.button_help) {
            startActivity(new Intent(this, HelpActivity.class));
        } else if (v.getId() == C0127R.id.button_url) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.youtube.com/channel/UCZIE6EbY7wL75m-hWbx-_1g")));
        }
    }

    private void ConnectWifi(String ssid) {
        WifiManager mWifiManager = (WifiManager) getSystemService("wifi");
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        config.allowedKeyManagement.set(0);
        config.wepTxKeyIndex = 0;
        mWifiManager.enableNetwork(mWifiManager.addNetwork(config), true);
        mWifiManager.reconnect();
    }
}
