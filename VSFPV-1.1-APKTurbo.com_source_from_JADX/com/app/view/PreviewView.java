package com.app.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.android.opengles.GLFrameRenderer;
import com.android.opengles.GLFrameSurface;
import com.app.Interface.MyMediaCodec;
import com.app.util.ActivtyUtil;
import com.app.util.WLANCfg;
import com.app.util.log;
import com.bwin.airtoplay.RTSPPlayerView;
import com.bwin.airtoplay.RTSPPlayerView.Callback;
import com.bwin.airtoplay.RTSPPlayerView.RTSPPlayerStatus;
import com.bwin.airtoplay.Utilities;
import com.fh.lib.Define;
import com.fh.lib.Define.BCSS;
import com.fh.lib.Define.CbDataInterface;
import com.fh.lib.Define.PBRecTime;
import com.fh.lib.Define.SerialDataCallBackInterface;
import com.fh.lib.FHSDK;
import com.fh.lib.PlayInfo;
import et.song.ui.widgets.ETButton;
import et.song.ui.widgets.ETLeftRocker;
import et.song.ui.widgets.ETPlaneRocker;
import et.song.ui.widgets.ETRightRocker;
import et.song.ui.widgets.ETTBTrim;
import et.song.ui.widgets.ETTrim;
import et.song.vspfv.C0127R;
import et.song.vspfv.SnapshotActivity;
import et.song.vspfv.SysApp;
import et.song.vspfv.WelcomeActivity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PreviewView {
    private static int DEVICE_TYPE_FH8610 = 1;
    private static int DEVICE_TYPE_FH8620 = 2;
    private static int DEVICE_TYPE_FH8810 = 3;
    private static final int MENU_GONE = 2;
    private static final int MUNE_AUTO_HIDE_TIME = 3000;
    private static final int MUNE_FLY_WAITING_TIME = 3000;
    private static final int MUNE_LAND_WAITING_TIME = 3000;
    private static final int MUNE_REV_WAITING_TIME = 5000;
    private static final int NOTIFY_BACKGROUND_HIDE = 1;
    private static final int NOTIFY_HIDE_BG = 2004;
    private static final int NOTIFY_QUIT = 0;
    private static final int NOTIFY_REQUEST_I = 2003;
    private static final int NOTIFY_REV_WAITING = 2;
    private static final int NOTIFY_SHOW_STRING = 2005;
    private static final int NOTIFY_TO_360RollClean = 5;
    private static final int NOTIFY_TO_FLY = 3;
    private static final int NOTIFY_TO_GRight = 6;
    private static final int NOTIFY_TO_LAND = 4;
    private static final int NOTIFY_TYPE_HIDE_BACKGROUND = 5;
    private static final int NOTIFY_TYPE_PTS = 3;
    private static final int NOTIFY_TYPE_REQUEST_I = 4;
    private static final int NOTIFY_TYPE_SHOT_Fail = 1;
    private static final int NOTIFY_TYPE_SHOT_FileName = 0;
    private static final int NOTIFY_TYPE_STREAM_INFO = 2;
    private static final int NOTIFY_UPDATE_STREAM_INFO = 3;
    private static final int NOTIFY_VIEW_GONE = 0;
    public static long PBStartTime = 0;
    public static long PBStopTime = 0;
    private static final String REC_PATH = "/FHVideo/";
    private static final int SEEKBAR_MAX_NUM = 10000;
    private static final int SHOT_Fail = 2002;
    private static final int SHOT_FileName = 2001;
    private static final int SPEED_1 = 0;
    private static final int SPEED_16 = 4;
    private static final int SPEED_1_16 = 10;
    private static final int SPEED_1_2 = 7;
    private static final int SPEED_1_4 = 8;
    private static final int SPEED_1_8 = 9;
    private static final int SPEED_2 = 1;
    private static final int SPEED_4 = 2;
    private static final int SPEED_8 = 3;
    private static final String TAG = "PreviewView";
    private static final int UPDATE_PROGRESS = 1;
    private static final int[] audioType;
    private static ImageView imageBg;
    private static PreviewView instance;
    private static TalkThread mTalk = null;
    private static String[] strPBSpeed = new String[]{"", "x2", "x4", "x8", "x16", "", "", "1/2", "1/4", "1/8", "1/16"};
    public static int talkFormat = 0;
    public static int talkSample = 0;
    private Runnable CheckTouchTime;
    private long CurrentTime;
    private OnClickListener OnCancelClickLister;
    private OnClickListener OnSaveClickLister;
    private OnClickListener OnSerialCancelClickLister;
    private View.OnClickListener OnSerialSendClickLister;
    private int PBCurrentSpeed;
    private long PBVideoCurLen;
    private long PBVideoLen;
    private Runnable ShowAllLayout;
    private boolean b360RollValueClean;
    private boolean bGetCurSensorBase;
    private ProgressBar bar;
    private float baseX;
    private float baseY;
    private BCSS bcssDefObj;
    private BCSS bcssObj;
    private View.OnClickListener bntClickListener;
    private ImageButton btPBAudio;
    private ImageButton btPBFrame;
    private ImageButton btPBPlay;
    private ImageButton btPBSpeedDown;
    private ImageButton btPBSpeedUp;
    private ImageView btn3D;
    private View.OnClickListener btn3DListener;
    private Button btnAudio;
    private View.OnClickListener btnAudioSetListener;
    private Button btnBCSS;
    private View.OnClickListener btnBCSSListener;
    private ImageView btnBack;
    private View.OnClickListener btnBackListener;
    private Button btnLocateRec;
    private View.OnClickListener btnLocateRecSetListener;
    private Button btnLocateShot;
    private View.OnClickListener btnLocateShotSetListener;
    private ImageView btnPBPlay;
    private ImageView btnPBSpeedDown;
    private ImageView btnPBSpeedUp;
    private Button btnRemoteRec;
    private View.OnClickListener btnRemoteRecSetListener;
    private Button btnRemoteShot;
    private View.OnClickListener btnRemoteShotSetListener;
    private Button btnSerial;
    private View.OnClickListener btnSerialSetListener;
    private Button btnStreamChange;
    private Button btnTalk;
    private View.OnClickListener btnTalkSetListener;
    Runnable checkSSIDThread;
    private String connectSSID;
    private RelativeLayout controlLayout;
    private CbDataInterface dataFun;
    Runnable disConnectWifiThread;
    private EditText edtSerialInput;
    private int[] gravity_bg;
    private Handler hCheckSSID;
    private Handler hDevNotify;
    private Handler hDisConnectWifi;
    private Handler hRecTime;
    private Handler handler;
    private boolean haveDoneDisConnectWifi;
    private LayoutInflater inflater;
    private boolean isAudioOpened;
    private boolean isGuijiMode;
    private boolean isPBAudioOn;
    private boolean isPBPause;
    private boolean isRecOn;
    private boolean isStopSendMsg;
    private boolean isTalkOpened;
    private long lastGetFrameTime;
    private RelativeLayout layoutMenu;
    private View.OnClickListener locatePBPlayListener;
    private View.OnClickListener locatePBSpeedDownListener;
    private View.OnClickListener locatePBSpeedUpListener;
    private OnSeekBarChangeListener locateSeekBarChangeListener;
    private int m360RollValue;
    private int m360RollValueCount;
    private int mAirnValue;
    private String[] mBitrateCmds;
    private ETButton mButton360Roll;
    private ETButton mButton3D;
    private ETButton mButtonAirn;
    private ETButton mButtonAlbum;
    private ETButton mButtonBack;
    private ETButton mButtonGRight;
    private ETButton mButtonGravity;
    private ETButton mButtonGuiJi;
    private ETButton mButtonHide;
    private ETButton mButtonMore;
    private ETButton mButtonNohead;
    private ETButton mButtonPhoto;
    private ETButton mButtonReco;
    private ETButton mButtonReco2;
    private ETButton mButtonRev;
    private ETButton mButtonSpeed;
    private ETButton mButtonStop;
    private ETButton mButtonToLand;
    private ETButton mButtonTofly;
    public Context mContext;
    private MyControlThread mControlThread;
    private ConvertRecThread mConvertRecThread;
    private long mExitTime;
    private GLFrameRenderer mFrameRender;
    private int mGRightValue;
    private int mGravityValue;
    Handler mHandler;
    private int mHeadLessValue;
    private long mLastTime;
    private long mLastTouchTime;
    private ETLeftRocker mLeftRocker;
    private int mMusic;
    private TextView mPBCurrentTime;
    private TextView mPBStopTime;
    private String[] mResolutionCmds;
    private ETPlaneRocker mRightPlane;
    private ETRightRocker mRightRocker;
    private int mRockerFBValue;
    private int mRockerRLValue;
    private SeekBar mSeekBar;
    private SendThread mSendThread;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private SoundPool mSoundPool;
    private int mSpeedValue;
    private int mStopValue;
    private int mToLandValue;
    private int mToflyValue;
    private ETTrim mTrimLeft;
    private ETTrim mTrimRight;
    private ETTBTrim mTrimSide;
    private WLANCfg mWifiCfg;
    private BroadcastReceiver myBroadcastReciver;
    private View.OnClickListener pbFrameListener;
    private View.OnClickListener pbPlayListener;
    private View.OnClickListener pbSpeedDownListener;
    private View.OnClickListener pbSpeedUpListener;
    private String recFilePath;
    Runnable recTimeThread;
    private Rect rightRockerRect;
    private RTSPPlayerView rtspSurface;
    private int secondCount;
    private SeekBar seekBarBrightness;
    private OnSeekBarChangeListener seekBarBrightnessChangeListener;
    private OnSeekBarChangeListener seekBarChangeListener;
    private SeekBar seekBarContrast;
    private OnSeekBarChangeListener seekBarContrastChangeListener;
    private SeekBar seekBarSaturation;
    private OnSeekBarChangeListener seekBarSaturationChangeListener;
    private SeekBar seekBarSharpness;
    private OnSeekBarChangeListener seekBarSharpnessChangeListener;
    private SensorEventListener sensorListener;
    private SerialDataCallBackInterface serialFun;
    private int serialHandle;
    private int[] showctr_bg;
    private int[] speed_bg;
    public OnTouchListener surfaceTouchListener;
    private TextView tvBrightnessVal;
    private TextView tvContrastVal;
    private TextView tvPBSpeed;
    private TextView tvPBStatus;
    private TextView tvRecTime;
    private TextView tvSaturationVal;
    private TextView tvSharpnessVal;
    private TextView tvStreamInfo;
    private View view;
    public boolean wifiActiveStatus;

    class C00702 implements OnSeekBarChangeListener {
        int progress = 0;

        C00702() {
        }

        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
            this.progress = arg1;
        }

        public void onStartTrackingTouch(SeekBar arg0) {
            PreviewView.this.isStopSendMsg = true;
        }

        public void onStopTrackingTouch(SeekBar arg0) {
            FHSDK.jumpPlayBack(1000 * (PreviewView.PBStartTime + (((PreviewView.PBStopTime - PreviewView.PBStartTime) * ((long) this.progress)) / 10000)));
        }
    }

    class C00723 implements View.OnClickListener {
        C00723() {
        }

        public void onClick(View v) {
            if (PreviewView.this.isPBPause && FHSDK.continuePBPlay()) {
                PreviewView.this.isPBPause = false;
                PreviewView.this.tvPBStatus.setText("");
                PreviewView.this.btPBPlay.setImageResource(C0127R.drawable.pb_play);
            } else if (!PreviewView.this.isPBPause && FHSDK.pausePBPlay()) {
                PreviewView.this.isPBPause = true;
                PreviewView.this.tvPBStatus.setText((String) PreviewView.this.mContext.getText(C0127R.string.id_pause));
                PreviewView.this.btPBPlay.setImageResource(C0127R.drawable.pb_pause);
            }
        }
    }

    class C00734 implements View.OnClickListener {
        C00734() {
        }

        public void onClick(View v) {
            if (!PreviewView.this.isPBPause) {
                PreviewView.this.isPBPause = true;
                PreviewView.this.tvPBStatus.setText((String) PreviewView.this.mContext.getText(C0127R.string.id_framePlay));
                PreviewView.this.btPBPlay.setImageResource(C0127R.drawable.pb_pause);
            }
            FHSDK.playFrame();
        }
    }

    class C00745 implements View.OnClickListener {
        C00745() {
        }

        public void onClick(View v) {
            PreviewView previewView;
            if (1 == PreviewView.this.PBCurrentSpeed || 2 == PreviewView.this.PBCurrentSpeed || 3 == PreviewView.this.PBCurrentSpeed || 4 == PreviewView.this.PBCurrentSpeed) {
                previewView = PreviewView.this;
                previewView.PBCurrentSpeed = previewView.PBCurrentSpeed - 1;
            } else if (PreviewView.this.PBCurrentSpeed == 0) {
                PreviewView.this.PBCurrentSpeed = 7;
            } else if (7 == PreviewView.this.PBCurrentSpeed || 8 == PreviewView.this.PBCurrentSpeed || 9 == PreviewView.this.PBCurrentSpeed) {
                previewView = PreviewView.this;
                previewView.PBCurrentSpeed = previewView.PBCurrentSpeed + 1;
            } else if (10 == PreviewView.this.PBCurrentSpeed) {
                return;
            }
            PreviewView.this.tvPBSpeed.setText(PreviewView.strPBSpeed[PreviewView.this.PBCurrentSpeed]);
            FHSDK.setPBSpeed(PreviewView.this.PBCurrentSpeed);
        }
    }

    class C00756 implements View.OnClickListener {
        C00756() {
        }

        public void onClick(View v) {
            PreviewView previewView;
            if (PreviewView.this.PBCurrentSpeed == 0 || 1 == PreviewView.this.PBCurrentSpeed || 2 == PreviewView.this.PBCurrentSpeed || 3 == PreviewView.this.PBCurrentSpeed) {
                previewView = PreviewView.this;
                previewView.PBCurrentSpeed = previewView.PBCurrentSpeed + 1;
            } else if (4 == PreviewView.this.PBCurrentSpeed) {
                return;
            } else {
                if (8 == PreviewView.this.PBCurrentSpeed || 9 == PreviewView.this.PBCurrentSpeed || 10 == PreviewView.this.PBCurrentSpeed) {
                    previewView = PreviewView.this;
                    previewView.PBCurrentSpeed = previewView.PBCurrentSpeed - 1;
                } else if (7 == PreviewView.this.PBCurrentSpeed) {
                    PreviewView.this.PBCurrentSpeed = 0;
                }
            }
            PreviewView.this.tvPBSpeed.setText(PreviewView.strPBSpeed[PreviewView.this.PBCurrentSpeed]);
            FHSDK.setPBSpeed(PreviewView.this.PBCurrentSpeed);
        }
    }

    class C00767 implements View.OnClickListener {
        C00767() {
        }

        public void onClick(View v) {
            String str;
            if (FHSDK.getRemoteRecordState(PlayInfo.userID)) {
                str = new StringBuilder(String.valueOf((String) PreviewView.this.mContext.getText(C0127R.string.id_remoteRec))).append((String) PreviewView.this.mContext.getText(C0127R.string.id_stop)).toString();
                FHSDK.stopRemoteRecord(PlayInfo.userID);
                PreviewView.this.btnRemoteRec.setText(PreviewView.this.mContext.getText(C0127R.string.id_remoteRec));
            } else if (FHSDK.startRemoteRecord(PlayInfo.userID)) {
                str = new StringBuilder(String.valueOf((String) PreviewView.this.mContext.getText(C0127R.string.id_remoteRec))).append((String) PreviewView.this.mContext.getText(C0127R.string.id_start)).toString();
                PreviewView.this.btnRemoteRec.setText(PreviewView.this.mContext.getText(C0127R.string.id_stopRec));
            } else {
                str = new StringBuilder(String.valueOf((String) PreviewView.this.mContext.getText(C0127R.string.id_remoteRec))).append((String) PreviewView.this.mContext.getText(C0127R.string.id_fail)).toString();
            }
            ActivtyUtil.openToast(PreviewView.this.mContext, str);
        }
    }

    class C00778 implements View.OnClickListener {
        C00778() {
        }

        public void onClick(View v) {
            String str = (String) PreviewView.this.mContext.getText(C0127R.string.id_remoteShot);
            if (FHSDK.shot(PlayInfo.userID, null, false)) {
                str = new StringBuilder(String.valueOf(str)).append((String) PreviewView.this.mContext.getText(C0127R.string.id_success)).toString();
            } else {
                str = new StringBuilder(String.valueOf(str)).append((String) PreviewView.this.mContext.getText(C0127R.string.id_fail)).toString();
            }
            ActivtyUtil.openToast(PreviewView.this.mContext, str);
        }
    }

    class C00789 implements View.OnClickListener {
        C00789() {
        }

        public void onClick(View v) {
            PreviewView.getSDCardPath();
            if (PreviewView.this.isRecOn) {
                FHSDK.stopLocalRecord();
                PreviewView.this.isRecOn = false;
                PreviewView.this.btnLocateRec.setText((String) PreviewView.this.mContext.getText(C0127R.string.id_locateRec));
                PreviewView.this.tvRecTime.setVisibility(4);
                PreviewView.this.hRecTime.removeCallbacks(PreviewView.this.recTimeThread);
                PreviewView.this.secondCount = 0;
            } else if (Environment.getExternalStorageState().equals("mounted")) {
                String path = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append(PreviewView.getSettingPath()).toString();
                String fileName = "VIDEO_" + ActivtyUtil.getCurSysDate();
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdir();
                }
                if (FHSDK.startLocalRecord(new StringBuilder(String.valueOf(path)).append("/").append(fileName).append(".H264").toString())) {
                    PreviewView.this.isRecOn = true;
                    PreviewView.this.btnLocateRec.setText((String) PreviewView.this.mContext.getText(C0127R.string.id_stopRec));
                    PreviewView.this.tvRecTime.setVisibility(0);
                    PreviewView.this.hRecTime.post(PreviewView.this.recTimeThread);
                    return;
                }
                ActivtyUtil.openToast(PreviewView.this.mContext, "录像失败");
            } else {
                ActivtyUtil.openToast(PreviewView.this.mContext, "have no SD card, record failed");
            }
        }
    }

    class MyControlThread implements Runnable {
        private Context mContext;
        private boolean startF = false;

        public MyControlThread(Context mContext) {
            this.mContext = mContext;
        }

        public boolean isStart() {
            return this.startF;
        }

        public void start() {
            this.startF = true;
            new Thread(this).start();
        }

        public void stop() {
            this.startF = false;
        }

        public void run() {
            while (this.startF) {
                if (PreviewView.this.connectSSID == null && PreviewView.this.mWifiCfg != null) {
                    PreviewView.this.connectSSID = PreviewView.this.mWifiCfg.getSSID();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SendThread extends Thread {
        private boolean isRun = true;

        public void run() {
            while (this.isRun) {
                if (!(PreviewView.this.mLeftRocker == null || PreviewView.this.mTrimLeft == null || PreviewView.this.mTrimRight == null)) {
                    byte[] data = new byte[11];
                    data[0] = (byte) -1;
                    data[1] = (byte) 8;
                    int ck = 0 + data[1];
                    data[2] = (byte) ((int) (PreviewView.this.mLeftRocker.getYV() * 2.0f));
                    ck += data[2];
                    data[3] = (byte) ((int) PreviewView.this.mLeftRocker.getXV());
                    ck += data[3];
                    if (PreviewView.this.isGuijiMode) {
                        data[4] = (byte) ((int) (((float) PreviewView.this.mRightPlane.getMaxY()) - PreviewView.this.mRightPlane.getYV()));
                    } else {
                        data[4] = (byte) ((int) (((float) PreviewView.this.mRightRocker.getMaxY()) - PreviewView.this.mRightRocker.getYV()));
                    }
                    ck += data[4];
                    if (PreviewView.this.isGuijiMode) {
                        data[5] = (byte) ((int) PreviewView.this.mRightPlane.getXV());
                    } else {
                        data[5] = (byte) ((int) PreviewView.this.mRightRocker.getXV());
                    }
                    data[5] = (byte) ((int) PreviewView.this.mRightRocker.getXV());
                    if ((data[4] >= (byte) 96 || data[4] <= (byte) 32 || data[5] >= (byte) 96 || data[5] <= (byte) 32) && 1 == ((Integer) PreviewView.this.mButton360Roll.getTag()).intValue()) {
                        PreviewView.this.m360RollValue = 1;
                        PreviewView previewView = PreviewView.this;
                        previewView.m360RollValueCount = previewView.m360RollValueCount + 1;
                        if (PreviewView.this.m360RollValueCount > 4) {
                            PreviewView.this.sendMsg360RollClean();
                        }
                    } else {
                        PreviewView.this.m360RollValueCount = 0;
                    }
                    ck += data[5];
                    data[6] = (byte) PreviewView.this.mTrimLeft.getPos();
                    if (PreviewView.this.mAirnValue == 1) {
                        data[6] = (byte) (data[6] | 128);
                    } else {
                        data[6] = (byte) (data[6] & TransportMediator.KEYCODE_MEDIA_PAUSE);
                    }
                    data[6] = (byte) (data[6] | (PreviewView.this.mGRightValue << 6));
                    ck += data[6];
                    data[7] = (byte) (PreviewView.this.mTrimSide.getMax() - PreviewView.this.mTrimSide.getPos());
                    ck += data[7];
                    data[8] = (byte) PreviewView.this.mTrimRight.getPos();
                    ck += data[8];
                    data[9] = (byte) PreviewView.this.mSpeedValue;
                    data[9] = (byte) (data[9] | (PreviewView.this.m360RollValue << 2));
                    data[9] = (byte) (data[9] | (PreviewView.this.mHeadLessValue << 4));
                    data[9] = (byte) (data[9] | (PreviewView.this.mStopValue << 5));
                    data[9] = (byte) (data[9] | (PreviewView.this.mToflyValue << 6));
                    data[9] = (byte) (data[9] | (PreviewView.this.mToLandValue << 7));
                    data[10] = (byte) (255 - ((byte) (ck + data[9])));
                    SysApp.getMe().writeUDPCmd(data);
                    try {
                        Thread.sleep(25);
                    } catch (Exception e) {
                    }
                }
            }
        }

        public void cancel() {
            this.isRun = false;
            interrupt();
        }

        public boolean getRun() {
            return this.isRun;
        }
    }

    class C01741 implements CbDataInterface {
        C01741() {
        }

        public void cb_data(int type, byte[] data, int len) {
            Message msg;
            switch (type) {
                case 0:
                    String filePath = new String(data, 0, len);
                    String[] m = filePath.split("/");
                    String fileName = m[m.length - 1];
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(filePath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    if (PreviewView.this.mWifiCfg == null || PreviewView.this.mWifiCfg.getSSID() == null || !(PreviewView.this.mWifiCfg.getSSID().startsWith("DFD") || PreviewView.this.mWifiCfg.getSSID().startsWith("ZC"))) {
                        PreviewView.saveImageToGallery(PreviewView.this.mContext, fileName, bitmap);
                    } else {
                        PreviewView.saveImageToGallery(PreviewView.this.mContext, fileName, ThumbnailUtils.extractThumbnail(bitmap, 1280, 1024));
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("filePath", filePath);
                    msg = PreviewView.this.handler.obtainMessage();
                    msg.what = PreviewView.SHOT_FileName;
                    msg.setData(bundle);
                    PreviewView.this.handler.sendMessage(msg);
                    return;
                case 1:
                    msg = PreviewView.this.handler.obtainMessage();
                    msg.what = PreviewView.SHOT_Fail;
                    PreviewView.this.handler.sendMessage(msg);
                    return;
                case 2:
                    String s = null;
                    try {
                        s = new String(data, "GB2312");
                    } catch (UnsupportedEncodingException e2) {
                        e2.printStackTrace();
                    }
                    msg = PreviewView.this.handler.obtainMessage();
                    msg.what = 3;
                    msg.obj = s;
                    PreviewView.this.handler.sendMessage(msg);
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    msg = PreviewView.this.handler.obtainMessage();
                    msg.what = PreviewView.NOTIFY_HIDE_BG;
                    PreviewView.this.handler.sendMessage(msg);
                    return;
                default:
                    return;
            }
            if (PlayInfo.playType == 3) {
                if (!PreviewView.this.isStopSendMsg) {
                    msg = PreviewView.this.handler.obtainMessage();
                    msg.what = 1;
                    PreviewView.this.handler.sendMessage(msg);
                }
                msg = PreviewView.this.handler.obtainMessage();
                msg.what = PreviewView.NOTIFY_REQUEST_I;
                PreviewView.this.handler.sendMessage(msg);
            }
        }
    }

    static {
        int[] iArr = new int[2];
        iArr[1] = 1;
        audioType = iArr;
    }

    public static PreviewView getInstance() {
        if (instance == null) {
            instance = new PreviewView();
        }
        return instance;
    }

    public PreviewView() {
        this.PBCurrentSpeed = 0;
        this.isPBPause = false;
        this.isStopSendMsg = false;
        this.isPBAudioOn = false;
        this.isRecOn = false;
        this.isAudioOpened = false;
        this.isTalkOpened = false;
        this.secondCount = 0;
        this.PBVideoLen = 0;
        this.PBVideoCurLen = 0;
        this.CurrentTime = PBStartTime;
        this.isGuijiMode = false;
        this.speed_bg = new int[]{C0127R.drawable.ic_speed_0, C0127R.drawable.ic_speed_1, C0127R.drawable.ic_speed_2};
        this.gravity_bg = new int[]{C0127R.drawable.ic_g_off, C0127R.drawable.ic_g_on};
        this.showctr_bg = new int[]{C0127R.drawable.ic_hide, C0127R.drawable.ic_show};
        this.mButtonBack = null;
        this.mButtonPhoto = null;
        this.mButtonReco = null;
        this.mButtonReco2 = null;
        this.mButtonAlbum = null;
        this.mButtonSpeed = null;
        this.mButtonAirn = null;
        this.mButtonGravity = null;
        this.mButtonHide = null;
        this.mButtonMore = null;
        this.mButtonRev = null;
        this.mButton3D = null;
        this.mButtonStop = null;
        this.mButton360Roll = null;
        this.mButtonTofly = null;
        this.mButtonToLand = null;
        this.mButtonNohead = null;
        this.mButtonGRight = null;
        this.mButtonGuiJi = null;
        this.mRockerFBValue = 0;
        this.mRockerRLValue = 0;
        this.mSpeedValue = 0;
        this.mAirnValue = 0;
        this.mGravityValue = 0;
        this.mStopValue = 0;
        this.m360RollValue = 0;
        this.mToflyValue = 0;
        this.mToLandValue = 0;
        this.mHeadLessValue = 0;
        this.mGRightValue = 0;
        this.m360RollValueCount = 0;
        this.b360RollValueClean = false;
        this.mLeftRocker = null;
        this.mRightRocker = null;
        this.mRightPlane = null;
        this.mTrimLeft = null;
        this.mTrimRight = null;
        this.mTrimSide = null;
        this.mLastTime = 0;
        this.mBitrateCmds = new String[]{"VideoEncoder0.RateControl.Bitrate=u:2000\r\n", "VideoEncoder0.RateControl.Bitrate=u:1500\r\n", "VideoEncoder0.RateControl.Bitrate=u:1000\r\n"};
        this.mResolutionCmds = new String[]{"VideoEncoder0.Resolution=s:720P\r\n", "VideoEncoder0.Resolution=s:VGA\r\n"};
        this.mSendThread = null;
        this.mLastTouchTime = 0;
        this.mWifiCfg = null;
        this.mConvertRecThread = null;
        this.connectSSID = null;
        this.mControlThread = null;
        this.recFilePath = null;
        this.lastGetFrameTime = 0;
        this.haveDoneDisConnectWifi = false;
        this.rightRockerRect = new Rect();
        this.bGetCurSensorBase = false;
        this.baseX = 0.0f;
        this.baseY = 0.0f;
        this.dataFun = new C01741();
        this.seekBarChangeListener = new C00702();
        this.pbPlayListener = new C00723();
        this.pbFrameListener = new C00734();
        this.pbSpeedDownListener = new C00745();
        this.pbSpeedUpListener = new C00756();
        this.btnRemoteRecSetListener = new C00767();
        this.btnRemoteShotSetListener = new C00778();
        this.btnLocateRecSetListener = new C00789();
        this.btnLocateShotSetListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (Environment.getExternalStorageState().equals("mounted")) {
                    String path = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append(PreviewView.getSettingPath()).toString();
                    String fileName = "IMG_" + ActivtyUtil.getCurSysDate();
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    if (FHSDK.shot(PlayInfo.userID, new StringBuilder(String.valueOf(path)).append("/").append(fileName).append(".jpg").toString(), true)) {
                        ActivtyUtil.openToast(PreviewView.this.mContext, new StringBuilder(String.valueOf(fileName)).append(".jpg").toString());
                        return;
                    } else {
                        ActivtyUtil.openToast(PreviewView.this.mContext, "shot failed");
                        return;
                    }
                }
                ActivtyUtil.openToast(PreviewView.this.mContext, "have no SD card, shot failed");
            }
        };
        this.btnAudioSetListener = new View.OnClickListener() {
            public void onClick(View v) {
                PreviewView.this.isAudioOpened = FHSDK.getRealAudioState();
                if (PreviewView.this.isAudioOpened) {
                    String str = new StringBuilder(String.valueOf((String) PreviewView.this.mContext.getText(C0127R.string.id_start))).append((String) PreviewView.this.mContext.getText(C0127R.string.id_audio)).toString();
                    FHSDK.closeAudio(PreviewView.audioType[0]);
                    PreviewView.this.btnAudio.setText(str);
                    return;
                }
                str = new StringBuilder(String.valueOf((String) PreviewView.this.mContext.getText(C0127R.string.id_stop))).append((String) PreviewView.this.mContext.getText(C0127R.string.id_audio)).toString();
                FHSDK.openAudio(PreviewView.audioType[0]);
                PreviewView.this.btnAudio.setText(str);
            }
        };
        this.btnTalkSetListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (PreviewView.this.isTalkOpened) {
                    PreviewView.this.isTalkOpened = false;
                    PreviewView.stopTalkThread();
                    PreviewView.this.btnTalk.setText(new StringBuilder(String.valueOf((String) PreviewView.this.mContext.getText(C0127R.string.id_start))).append((String) PreviewView.this.mContext.getText(C0127R.string.id_talk)).toString());
                    return;
                }
                PreviewView.this.isTalkOpened = true;
                PreviewView.startTalkThread();
                PreviewView.this.btnTalk.setText(new StringBuilder(String.valueOf((String) PreviewView.this.mContext.getText(C0127R.string.id_stop))).append((String) PreviewView.this.mContext.getText(C0127R.string.id_talk)).toString());
            }
        };
        this.btnSerialSetListener = new View.OnClickListener() {
            public void onClick(View v) {
                View myInputView = LayoutInflater.from(PreviewView.this.mContext).inflate(C0127R.layout.serial_input, null);
                PreviewView.this.edtSerialInput = (EditText) myInputView.findViewById(C0127R.id.edtSerialInput);
                Builder builder = new Builder(PreviewView.this.mContext);
                builder.setTitle("数据输入");
                builder.setView(myInputView).setPositiveButton("发送", null);
                builder.setView(myInputView).setNegativeButton("取消", PreviewView.this.OnSerialCancelClickLister);
                builder.setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(-1).setOnClickListener(PreviewView.this.OnSerialSendClickLister);
            }
        };
        this.serialFun = new SerialDataCallBackInterface() {
            public int SerialDataCallBack(int serialHandle, byte[] buffer, int bufferLen) {
                Log.e("xxx", "serialHandle = " + serialHandle + "| len = " + bufferLen);
                return 0;
            }
        };
        this.OnSerialSendClickLister = new View.OnClickListener() {
            public void onClick(View v) {
                byte[] sendData = PreviewView.this.edtSerialInput.getText().toString().getBytes();
                if (sendData == null || sendData.length <= 0) {
                    ActivtyUtil.openToast(PreviewView.this.mContext, "数据不可为空");
                    return;
                }
                PreviewView.this.serialHandle = FHSDK.startSerial(PlayInfo.userID, 1, 1, PreviewView.this.serialFun);
                if (PreviewView.this.serialHandle == 0) {
                    ActivtyUtil.openToast(PreviewView.this.mContext, "创建句柄失败");
                    return;
                }
                if (FHSDK.sendSerial(PreviewView.this.serialHandle, sendData, sendData.length)) {
                    ActivtyUtil.openToast(PreviewView.this.mContext, "发送成功");
                } else {
                    ActivtyUtil.openToast(PreviewView.this.mContext, "发送失败");
                }
                FHSDK.stopSerial(PreviewView.this.serialHandle);
            }
        };
        this.OnSerialCancelClickLister = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        };
        this.btnBCSSListener = new View.OnClickListener() {
            public void onClick(View v) {
                View myInputView = LayoutInflater.from(PreviewView.this.mContext).inflate(C0127R.layout.bcss, null);
                PreviewView.this.seekBarBrightness = (SeekBar) myInputView.findViewById(C0127R.id.SeekBarBrightNess);
                PreviewView.this.seekBarContrast = (SeekBar) myInputView.findViewById(C0127R.id.SeekBarContrast);
                PreviewView.this.seekBarSaturation = (SeekBar) myInputView.findViewById(C0127R.id.SeekBarSaturation);
                PreviewView.this.seekBarSharpness = (SeekBar) myInputView.findViewById(C0127R.id.SeekBarSharpness);
                PreviewView.this.tvBrightnessVal = (TextView) myInputView.findViewById(C0127R.id.tvBrightnessVal);
                PreviewView.this.tvContrastVal = (TextView) myInputView.findViewById(C0127R.id.tvContrastVal);
                PreviewView.this.tvSaturationVal = (TextView) myInputView.findViewById(C0127R.id.tvSaturationVal);
                PreviewView.this.tvSharpnessVal = (TextView) myInputView.findViewById(C0127R.id.tvSharpnessVal);
                if (FHSDK.getVideoBCSS(PlayInfo.userID, PreviewView.this.bcssObj)) {
                    PreviewView.this.bcssDefObj.brightness = PreviewView.this.bcssObj.brightness;
                    PreviewView.this.bcssDefObj.contrast = PreviewView.this.bcssObj.contrast;
                    PreviewView.this.bcssDefObj.saturation = PreviewView.this.bcssObj.saturation;
                    PreviewView.this.bcssDefObj.sharpness = PreviewView.this.bcssObj.sharpness;
                    int devFlag = FHSDK.getDeviceFlag(PlayInfo.userID);
                    if (PreviewView.DEVICE_TYPE_FH8610 == devFlag) {
                        PreviewView.this.seekBarBrightness.setMax(255);
                        PreviewView.this.seekBarContrast.setMax(63);
                        PreviewView.this.seekBarSaturation.setMax(63);
                        PreviewView.this.seekBarSharpness.setMax(15);
                    } else if (PreviewView.DEVICE_TYPE_FH8620 == devFlag || PreviewView.DEVICE_TYPE_FH8810 == devFlag) {
                        PreviewView.this.seekBarBrightness.setMax(255);
                        PreviewView.this.seekBarContrast.setMax(255);
                        PreviewView.this.seekBarSaturation.setMax(255);
                        PreviewView.this.seekBarSharpness.setMax(255);
                    }
                    PreviewView.this.seekBarBrightness.setProgress(PreviewView.this.bcssObj.brightness);
                    PreviewView.this.seekBarBrightness.setOnSeekBarChangeListener(PreviewView.this.seekBarBrightnessChangeListener);
                    PreviewView.this.tvBrightnessVal.setText(PreviewView.this.bcssObj.brightness);
                    PreviewView.this.seekBarContrast.setProgress(PreviewView.this.bcssObj.contrast);
                    PreviewView.this.seekBarContrast.setOnSeekBarChangeListener(PreviewView.this.seekBarContrastChangeListener);
                    PreviewView.this.tvContrastVal.setText(PreviewView.this.bcssObj.contrast);
                    PreviewView.this.seekBarSaturation.setProgress(PreviewView.this.bcssObj.saturation);
                    PreviewView.this.seekBarSaturation.setOnSeekBarChangeListener(PreviewView.this.seekBarSaturationChangeListener);
                    PreviewView.this.tvSaturationVal.setText(PreviewView.this.bcssObj.saturation);
                    PreviewView.this.seekBarSharpness.setProgress(PreviewView.this.bcssObj.sharpness);
                    PreviewView.this.seekBarSharpness.setOnSeekBarChangeListener(PreviewView.this.seekBarSharpnessChangeListener);
                    PreviewView.this.tvSharpnessVal.setText(PreviewView.this.bcssObj.sharpness);
                } else {
                    ActivtyUtil.openToast(PreviewView.this.mContext, "获取失败");
                }
                Builder builder = new Builder(PreviewView.this.mContext);
                builder.setTitle("色彩调节");
                builder.setView(myInputView).setPositiveButton("保存", PreviewView.this.OnSaveClickLister);
                builder.setView(myInputView).setNegativeButton("取消", PreviewView.this.OnCancelClickLister);
                builder.create().show();
            }
        };
        this.OnSaveClickLister = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (FHSDK.setVideoBCSS(PlayInfo.userID, PreviewView.this.bcssObj)) {
                    FHSDK.saveDevConfig(PlayInfo.userID);
                } else {
                    ActivtyUtil.openToast(PreviewView.this.mContext, "保存失败");
                }
            }
        };
        this.OnCancelClickLister = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                FHSDK.setVideoBCSS(PlayInfo.userID, PreviewView.this.bcssDefObj);
            }
        };
        this.seekBarBrightnessChangeListener = new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                PreviewView.this.bcssObj.brightness = arg1;
                PreviewView.this.tvBrightnessVal.setText(arg1);
                FHSDK.setVideoBCSS(PlayInfo.userID, PreviewView.this.bcssObj);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
            }

            public void onStopTrackingTouch(SeekBar arg0) {
            }
        };
        this.seekBarContrastChangeListener = new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                PreviewView.this.bcssObj.contrast = arg1;
                PreviewView.this.tvContrastVal.setText(arg1);
                FHSDK.setVideoBCSS(PlayInfo.userID, PreviewView.this.bcssObj);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
            }

            public void onStopTrackingTouch(SeekBar arg0) {
            }
        };
        this.seekBarSaturationChangeListener = new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                PreviewView.this.bcssObj.saturation = arg1;
                PreviewView.this.tvSaturationVal.setText(arg1);
                FHSDK.setVideoBCSS(PlayInfo.userID, PreviewView.this.bcssObj);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
            }

            public void onStopTrackingTouch(SeekBar arg0) {
            }
        };
        this.seekBarSharpnessChangeListener = new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                PreviewView.this.bcssObj.sharpness = arg1;
                PreviewView.this.tvSharpnessVal.setText(arg1);
                FHSDK.setVideoBCSS(PlayInfo.userID, PreviewView.this.bcssObj);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
            }

            public void onStopTrackingTouch(SeekBar arg0) {
            }
        };
        this.locateSeekBarChangeListener = new OnSeekBarChangeListener() {
            int progress = 0;

            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                this.progress = arg1;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                PreviewView.this.isStopSendMsg = true;
            }

            public void onStopTrackingTouch(SeekBar arg0) {
                FHSDK.locateJumpPlayBack((this.progress * 100) / PreviewView.SEEKBAR_MAX_NUM);
                if (!PreviewView.this.isPBPause) {
                    PreviewView.this.isStopSendMsg = false;
                }
            }
        };
        this.locatePBPlayListener = new View.OnClickListener() {
            public void onClick(View v) {
                boolean z = true;
                boolean z2 = false;
                PreviewView previewView;
                if (PreviewView.this.isPBPause && FHSDK.locateContinuePBPlay()) {
                    previewView = PreviewView.this;
                    if (PreviewView.this.isPBPause) {
                        z = false;
                    }
                    previewView.isPBPause = z;
                    PreviewView.this.btnPBPlay.setImageResource(17301540);
                    PreviewView.this.isStopSendMsg = false;
                } else if (!PreviewView.this.isPBPause && FHSDK.locatePausePBPlay()) {
                    previewView = PreviewView.this;
                    if (!PreviewView.this.isPBPause) {
                        z2 = true;
                    }
                    previewView.isPBPause = z2;
                    PreviewView.this.btnPBPlay.setImageResource(17301539);
                }
            }
        };
        this.locatePBSpeedDownListener = new View.OnClickListener() {
            public void onClick(View v) {
                PreviewView previewView;
                if (1 == PreviewView.this.PBCurrentSpeed || 2 == PreviewView.this.PBCurrentSpeed) {
                    previewView = PreviewView.this;
                    previewView.PBCurrentSpeed = previewView.PBCurrentSpeed - 1;
                } else if (PreviewView.this.PBCurrentSpeed == 0) {
                    PreviewView.this.PBCurrentSpeed = 7;
                } else if (7 == PreviewView.this.PBCurrentSpeed) {
                    previewView = PreviewView.this;
                    previewView.PBCurrentSpeed = previewView.PBCurrentSpeed + 1;
                } else if (8 == PreviewView.this.PBCurrentSpeed) {
                    return;
                }
                if (FHSDK.setLocatePBSpeed(PreviewView.this.PBCurrentSpeed)) {
                    PreviewView.this.tvPBSpeed.setText(PreviewView.strPBSpeed[PreviewView.this.PBCurrentSpeed]);
                }
            }
        };
        this.locatePBSpeedUpListener = new View.OnClickListener() {
            public void onClick(View v) {
                PreviewView previewView;
                if (PreviewView.this.PBCurrentSpeed == 0 || 1 == PreviewView.this.PBCurrentSpeed) {
                    previewView = PreviewView.this;
                    previewView.PBCurrentSpeed = previewView.PBCurrentSpeed + 1;
                } else if (2 == PreviewView.this.PBCurrentSpeed) {
                    return;
                } else {
                    if (8 == PreviewView.this.PBCurrentSpeed) {
                        previewView = PreviewView.this;
                        previewView.PBCurrentSpeed = previewView.PBCurrentSpeed - 1;
                    } else if (7 == PreviewView.this.PBCurrentSpeed) {
                        PreviewView.this.PBCurrentSpeed = 0;
                    }
                }
                if (FHSDK.setLocatePBSpeed(PreviewView.this.PBCurrentSpeed)) {
                    PreviewView.this.tvPBSpeed.setText(PreviewView.strPBSpeed[PreviewView.this.PBCurrentSpeed]);
                }
            }
        };
        this.btnBackListener = new View.OnClickListener() {
            public void onClick(View v) {
                PreviewView.this.mContext.finish();
            }
        };
        this.btn3DListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (PlayInfo.udpDevType == 6) {
                    int mode = ((Integer) v.getTag()).intValue() + 1;
                    if (mode > 3) {
                        mode = 0;
                    }
                    v.setTag(Integer.valueOf(mode));
                    GLFrameRenderer.ctrlIndex = mode;
                    GLFrameRenderer.bMixMode = false;
                    FHSDK.setImagingType(GLFrameRenderer.hWin, 0);
                    FHSDK.setStandardCircle(GLFrameRenderer.hWin, 0.0f, 0.0f, 0.0f);
                    GLFrameRenderer.eyeMode = 0;
                    GLFrameRenderer.vDegrees = 0.0f;
                    GLFrameRenderer.hDegrees = 0.0f;
                    switch (mode) {
                        case 0:
                            GLFrameRenderer.displayMode = 0;
                            GLFrameRenderer.depth = FHSDK.getMaxZDepth(GLFrameRenderer.hWin);
                            return;
                        case 1:
                            GLFrameRenderer.displayMode = 0;
                            GLFrameRenderer.eyeMode = 3;
                            GLFrameRenderer.vDegrees = FHSDK.getMaxVDegress(GLFrameRenderer.hWin);
                            return;
                        case 2:
                            GLFrameRenderer.displayMode = 3;
                            return;
                        case 3:
                            GLFrameRenderer.displayMode = 5;
                            return;
                        default:
                            return;
                    }
                } else if (((Integer) v.getTag()).intValue() == 0) {
                    v.setTag(Integer.valueOf(1));
                    if (PlayInfo.decodeType == 2) {
                        FHSDK.set3DMode(true);
                    } else {
                        MyMediaCodec.getInstance().setShowMode(MyMediaCodec.SHOW_MODE_3D);
                    }
                    v.setBackgroundResource(C0127R.drawable.ic_3d_on);
                } else {
                    v.setTag(Integer.valueOf(0));
                    if (PlayInfo.decodeType == 2) {
                        FHSDK.set3DMode(false);
                    } else {
                        MyMediaCodec.getInstance().setShowMode(MyMediaCodec.SHOW_MODE_FULLSCREEN);
                    }
                    v.setBackgroundResource(C0127R.drawable.ic_3d_off);
                }
            }
        };
        this.wifiActiveStatus = false;
        this.disConnectWifiThread = new Runnable() {
            public void run() {
                WLANCfg wlan = new WLANCfg(PreviewView.this.mContext);
                String s2 = "wlan=" + wlan + "/connectSSID=" + PreviewView.this.connectSSID + "/equals=" + wlan.getSSID().equals(PreviewView.this.connectSSID);
                Message msg2 = PreviewView.this.handler.obtainMessage();
                msg2.what = PreviewView.NOTIFY_SHOW_STRING;
                msg2.obj = s2;
                PreviewView.this.handler.sendMessage(msg2);
                if (wlan != null && PreviewView.this.connectSSID != null && wlan.getSSID().equals(PreviewView.this.connectSSID)) {
                    wlan.disConnectionWifi(wlan.getNetWordId());
                    Message msg = PreviewView.this.handler.obtainMessage();
                    msg.what = PreviewView.NOTIFY_SHOW_STRING;
                    msg.obj = " 5s未获取到数据, 主动断开WIFI, 5s后自动重连";
                    PreviewView.this.handler.sendMessage(msg);
                    if (PreviewView.this.hCheckSSID != null) {
                        PreviewView.this.hCheckSSID.postDelayed(PreviewView.this.checkSSIDThread, 7000);
                    }
                }
            }
        };
        this.checkSSIDThread = new Runnable() {
            public void run() {
                if (PreviewView.this.connectSSID == null && PreviewView.this.mWifiCfg != null) {
                    PreviewView.this.connectSSID = new WLANCfg(PreviewView.this.mContext).getSSID();
                }
                if (PreviewView.this.Connect(PreviewView.this.connectSSID, 1)) {
                    Message msg = PreviewView.this.handler.obtainMessage();
                    msg.what = PreviewView.NOTIFY_SHOW_STRING;
                    msg.obj = "已重新连接";
                    PreviewView.this.handler.sendMessage(msg);
                    return;
                }
                PreviewView.this.hCheckSSID.postDelayed(PreviewView.this.checkSSIDThread, 5000);
            }
        };
        this.recTimeThread = new Runnable() {
            public void run() {
                PreviewView previewView = PreviewView.this;
                int access$65 = previewView.secondCount;
                previewView.secondCount = access$65 + 1;
                long ms = (long) (access$65 * 1000);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                PreviewView.this.tvRecTime.setText(formatter.format(Long.valueOf(ms)));
                PreviewView.this.hRecTime.postDelayed(PreviewView.this.recTimeThread, 1000);
            }
        };
        this.surfaceTouchListener = new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent event) {
                if (PlayInfo.playType == 1 || PlayInfo.playType == 4) {
                    if (PlayInfo.decodeType == 2 || PlayInfo.decodeType == 3) {
                        if (FHSDK.is3DMode()) {
                            PreviewView.this.resetMsgSendTime();
                        }
                    } else if ((PlayInfo.decodeType == 0 || PlayInfo.decodeType == 1) && MyMediaCodec.SHOW_MODE_3D == MyMediaCodec.getInstance().getShowMode()) {
                        PreviewView.this.resetMsgSendTime();
                    }
                    if (PreviewView.this.view.getVisibility() == 8) {
                        PreviewView.this.view.setVisibility(0);
                        if (8 != PreviewView.this.mButtonReco2.getVisibility()) {
                            PreviewView.this.mButtonReco2.setVisibility(8);
                        }
                    }
                    PreviewView.this.mLastTouchTime = System.currentTimeMillis();
                    if (event.getAction() == 0 && 1 == PreviewView.this.mGravityValue) {
                        if (event.getRawX() >= ((float) (PreviewView.this.rightRockerRect.left - 100)) && event.getRawX() < ((float) (PreviewView.this.rightRockerRect.right + 100)) && event.getRawY() >= ((float) (PreviewView.this.rightRockerRect.top - 100)) && event.getRawY() < ((float) (PreviewView.this.rightRockerRect.bottom + 100))) {
                            PreviewView.this.bGetCurSensorBase = true;
                        }
                    } else if (event.getAction() == 1) {
                        PreviewView.this.baseX = 0.0f;
                        PreviewView.this.baseY = 0.0f;
                        PreviewView.this.mRightRocker.setLock(true);
                        PreviewView.this.mRightRocker.Refresh();
                    }
                } else if (PlayInfo.playType == 3) {
                    if (PreviewView.this.view.getVisibility() == 8) {
                        PreviewView.this.view.setVisibility(0);
                    } else {
                        PreviewView.this.view.setVisibility(8);
                    }
                }
                return true;
            }
        };
        this.sensorListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor != null && event.sensor.getType() == 1 && PreviewView.this.mGravityValue == 1) {
                    if (PreviewView.this.bGetCurSensorBase) {
                        PreviewView.this.bGetCurSensorBase = false;
                    }
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];
                    float offsetX = x - PreviewView.this.baseX;
                    PreviewView.this.mRockerFBValue = (int) (((((y - PreviewView.this.baseY) + 9.0f) * ((float) PreviewView.this.mRightRocker.getMaxX())) * 2.0f) / 9.0f);
                    PreviewView.this.mRockerRLValue = (int) ((((-offsetX) + 9.0f) * ((float) PreviewView.this.mRightRocker.getMaxY())) / 9.0f);
                    PreviewView.this.mRightRocker.setXV((PreviewView.this.mRockerFBValue / 2) - (PreviewView.this.mRightRocker.getMaxX() / 2));
                    PreviewView.this.mRightRocker.setYV(PreviewView.this.mRockerRLValue - (PreviewView.this.mRightRocker.getMaxX() / 2));
                    PreviewView.this.mRightRocker.Refresh();
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        this.myBroadcastReciver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(SysApp.REMOTE_RECORD)) {
                    PreviewView.this.mButtonReco.performClick();
                } else if (action.equals(SysApp.RECORD_START)) {
                    PreviewView.this.RecordClick(0);
                } else if (action.equals(SysApp.RECORD_END)) {
                    PreviewView.this.RecordClick(1);
                } else if (action.equals(SysApp.REMOTE_PHOTO)) {
                    PreviewView.this.PhotoClick();
                }
            }
        };
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (PreviewView.this.view != null) {
                            if (1 == ((Integer) PreviewView.this.mButtonReco.getTag()).intValue()) {
                                PreviewView.this.mButtonReco2.setVisibility(0);
                                PreviewView.this.mButtonReco2.setBackgroundResource(C0127R.anim.anim_reco);
                                ((AnimationDrawable) PreviewView.this.mButtonReco2.getBackground()).start();
                            }
                            PreviewView.this.view.setVisibility(8);
                            return;
                        }
                        return;
                    case 2:
                        if (PreviewView.this.mButtonRev != null) {
                            PreviewView.this.mButtonRev.setEnabled(true);
                            PreviewView.this.mButtonRev.setBackgroundResource(C0127R.drawable.ic_rev);
                            return;
                        }
                        return;
                    case 3:
                        PreviewView.this.mButtonTofly.setEnabled(true);
                        PreviewView.this.mToflyValue = 0;
                        return;
                    case 4:
                        PreviewView.this.mButtonToLand.setEnabled(true);
                        PreviewView.this.mToLandValue = 0;
                        return;
                    case 5:
                        PreviewView.this.mButton360Roll.setBackgroundResource(C0127R.drawable.ic_reload_1);
                        PreviewView.this.mButton360Roll.setTag(Integer.valueOf(0));
                        PreviewView.this.m360RollValue = 0;
                        return;
                    case 6:
                        PreviewView.this.mButtonGRight.setEnabled(true);
                        PreviewView.this.mGRightValue = 0;
                        return;
                    default:
                        return;
                }
            }
        };
        this.ShowAllLayout = new Runnable() {
            public void run() {
                PreviewView.this.view.setVisibility(0);
            }
        };
        this.CheckTouchTime = new Runnable() {
            public void run() {
                if (((Integer) PreviewView.this.mButton3D.getTag()).intValue() != 1) {
                    return;
                }
                if (PreviewView.this.view.getVisibility() != 0 || (System.currentTimeMillis() - PreviewView.this.mLastTouchTime) / 1000 <= 3) {
                    PreviewView.this.mHandler.postDelayed(PreviewView.this.CheckTouchTime, 1000);
                } else {
                    PreviewView.this.view.setVisibility(8);
                }
            }
        };
        this.bntClickListener = new View.OnClickListener() {

            class C00711 implements Runnable {
                C00711() {
                }

                public void run() {
                    PreviewView.this.mToLandValue = 0;
                    PreviewView.this.mButtonStop.setBackgroundResource(C0127R.drawable.ic_stop_off);
                    PreviewView.this.mButtonStop.setTag(Integer.valueOf(0));
                    PreviewView.this.mStopValue = 0;
                    PreviewView.this.mButtonStop.setClickable(true);
                }
            }

            public void onClick(View v) {
                if (v.getId() == C0127R.id.button_back) {
                    Activity mActivity = (Activity) PreviewView.this.mContext;
                    PreviewView.this.mContext.startActivity(new Intent(PreviewView.this.mContext, WelcomeActivity.class));
                    mActivity.finish();
                } else if (v.getId() == C0127R.id.button_hide) {
                    PreviewView.this.HideClick(((Integer) v.getTag()).intValue());
                } else if (v.getId() == C0127R.id.button_hidemore) {
                    PreviewView.this.HideMoreClick(((Integer) v.getTag()).intValue());
                } else if (v.getId() == C0127R.id.button_album) {
                    PreviewView.this.mContext.finish();
                    PreviewView.this.mContext.startActivity(new Intent(PreviewView.this.mContext, SnapshotActivity.class));
                } else if (v.getId() == C0127R.id.button_reco) {
                    PreviewView.this.RecordClick(((Integer) v.getTag()).intValue());
                } else if (v.getId() == C0127R.id.button_photo) {
                    PreviewView.this.PhotoClick();
                } else if (v.getId() == C0127R.id.button_speed) {
                    Integer state = Integer.valueOf(((Integer) v.getTag()).intValue() + 1);
                    state = Integer.valueOf(state.intValue() > 2 ? 0 : state.intValue());
                    v.setTag(state);
                    v.setBackgroundResource(PreviewView.this.speed_bg[state.intValue()]);
                    PreviewView.this.mSpeedValue = state.intValue();
                } else if (v.getId() == C0127R.id.button_atmospheric) {
                    if (((Integer) v.getTag()).intValue() == 1) {
                        v.setBackgroundResource(C0127R.drawable.atmospheric_off);
                        v.setTag(Integer.valueOf(0));
                        PreviewView.this.mAirnValue = 0;
                        PreviewView.this.mToflyValue = 0;
                        PreviewView.this.mToLandValue = 0;
                        PreviewView.this.mLeftRocker.setLock(false);
                        PreviewView.this.mLeftRocker.setYV(0);
                        PreviewView.this.mLeftRocker.Refresh();
                        PreviewView.this.view.findViewById(C0127R.id.layout_player_midbnt).setVisibility(8);
                        PreviewView.this.view.findViewById(C0127R.id.layout_flyland).setVisibility(8);
                        PreviewView.this.mButtonStop.setVisibility(4);
                        PreviewView.this.mButtonStop.setClickable(false);
                        return;
                    }
                    v.setBackgroundResource(C0127R.drawable.atmospheric_on);
                    v.setTag(Integer.valueOf(1));
                    PreviewView.this.mAirnValue = 1;
                    PreviewView.this.mLeftRocker.setLock(true);
                    PreviewView.this.mLeftRocker.setXV(PreviewView.this.mLeftRocker.getMaxX() / 2);
                    PreviewView.this.mLeftRocker.setYV(PreviewView.this.mLeftRocker.getMaxY() / 2);
                    PreviewView.this.mLeftRocker.Refresh();
                    PreviewView.this.mButtonTofly.setStatus(true);
                    PreviewView.this.view.findViewById(C0127R.id.layout_player_midbnt).setVisibility(0);
                    PreviewView.this.view.findViewById(C0127R.id.layout_flyland).setVisibility(0);
                    PreviewView.this.mButtonStop.setVisibility(0);
                    PreviewView.this.mButtonStop.setClickable(true);
                } else if (v.getId() == C0127R.id.button_g) {
                    PreviewView.this.mGravityValue = ((Integer) v.getTag()).intValue() == 1 ? 0 : 1;
                    v.setTag(Integer.valueOf(PreviewView.this.mGravityValue));
                    v.setBackgroundResource(PreviewView.this.gravity_bg[PreviewView.this.mGravityValue]);
                    int t;
                    int l;
                    int r;
                    int b;
                    if (PreviewView.this.mGravityValue == 1) {
                        PreviewView.this.mRightRocker.setLock(false);
                        PreviewView.this.mRightRocker.setManual(false);
                        PreviewView.this.mRockerFBValue = 0;
                        PreviewView.this.mRockerFBValue = 0;
                        t = PreviewView.this.mRightRocker.getTop();
                        l = PreviewView.this.mRightRocker.getLeft();
                        r = PreviewView.this.mRightRocker.getRight();
                        b = PreviewView.this.mRightRocker.getBottom();
                        int[] location = new int[4];
                        PreviewView.this.mRightRocker.getLocationOnScreen(location);
                        PreviewView.this.rightRockerRect.top = location[1];
                        PreviewView.this.rightRockerRect.left = location[0];
                        PreviewView.this.rightRockerRect.right = PreviewView.this.rightRockerRect.left + (r - l);
                        PreviewView.this.rightRockerRect.bottom = PreviewView.this.rightRockerRect.top + (b - t);
                        return;
                    }
                    if (1 == ((Integer) PreviewView.this.mButtonHide.getTag()).intValue()) {
                        PreviewView.this.mRightRocker.setVisibility(0);
                    }
                    t = PreviewView.this.mRightRocker.getTop();
                    l = PreviewView.this.mRightRocker.getLeft();
                    r = PreviewView.this.mRightRocker.getRight();
                    b = PreviewView.this.mRightRocker.getBottom();
                    PreviewView.this.mRightRocker.getLocationOnScreen(new int[4]);
                    PreviewView.this.mRightRocker.setLock(true);
                    PreviewView.this.mRightRocker.setManual(true);
                } else if (v.getId() == C0127R.id.button_rev) {
                    String cmdStrMirror;
                    String cmdStrFlip;
                    if (SysApp.getMe().isMirror()) {
                        GLFrameRenderer.circleX = 454.0f;
                        GLFrameRenderer.circleY = 462.0f;
                        GLFrameRenderer.circleR = 444.0f;
                        v.setTag(Integer.valueOf(0));
                        if (PlayInfo.udpDevType == 1 || PlayInfo.udpDevType == 2 || PlayInfo.udpDevType == 6 || PlayInfo.udpDevType == 7) {
                            SysApp.getMe().writeUDPCmd(new byte[]{(byte) 2});
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else if (PlayInfo.udpDevType == 4) {
                            v.setEnabled(false);
                            PreviewView.this.mButtonRev.setBackgroundResource(C0127R.drawable.ic_rev_off);
                            PreviewView.this.sendMsgWaitRev();
                            cmdStrMirror = "VideoSource0.Imaging.Mirror=b:1\r\n";
                            cmdStrFlip = "VideoSource0.Imaging.Flip=b:1\r\n";
                            SysApp.getMe().writeTCPCmd(cmdStrMirror);
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                            try {
                                SysApp.getMe().writeTCPCmd(cmdStrFlip);
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e22) {
                                    e22.printStackTrace();
                                }
                                SysApp.getMe().writeTCPCmd(cmdStrMirror);
                                try {
                                    Thread.sleep(30);
                                } catch (InterruptedException e222) {
                                    e222.printStackTrace();
                                }
                                SysApp.getMe().writeTCPCmd(cmdStrFlip);
                                try {
                                    Thread.sleep(30);
                                } catch (InterruptedException e2222) {
                                    e2222.printStackTrace();
                                }
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        } else if (PlayInfo.udpDevType == 5) {
                            SysApp.getMe().writeTCPCmd("MIRROR=1");
                        }
                        SysApp.getMe().setMirror(false);
                        return;
                    }
                    GLFrameRenderer.circleX = 506.0f;
                    GLFrameRenderer.circleY = 498.0f;
                    GLFrameRenderer.circleR = 444.0f;
                    v.setTag(Integer.valueOf(1));
                    if (PlayInfo.udpDevType == 1 || PlayInfo.udpDevType == 2 || PlayInfo.udpDevType == 6 || PlayInfo.udpDevType == 7) {
                        SysApp.getMe().writeUDPCmd(new byte[]{(byte) 1});
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e22222) {
                            e22222.printStackTrace();
                        }
                    } else if (PlayInfo.udpDevType == 4) {
                        v.setEnabled(false);
                        PreviewView.this.mButtonRev.setBackgroundResource(C0127R.drawable.ic_rev_off);
                        PreviewView.this.sendMsgWaitRev();
                        cmdStrMirror = "VideoSource0.Imaging.Mirror=b:0\r\n";
                        cmdStrFlip = "VideoSource0.Imaging.Flip=b:0\r\n";
                        SysApp.getMe().writeTCPCmd(cmdStrMirror);
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e222222) {
                            e222222.printStackTrace();
                        }
                        try {
                            SysApp.getMe().writeTCPCmd(cmdStrFlip);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e2222222) {
                                e2222222.printStackTrace();
                            }
                            SysApp.getMe().writeTCPCmd(cmdStrMirror);
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e22222222) {
                                e22222222.printStackTrace();
                            }
                            SysApp.getMe().writeTCPCmd(cmdStrFlip);
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e222222222) {
                                e222222222.printStackTrace();
                            }
                        } catch (Exception e32) {
                            e32.printStackTrace();
                        }
                    } else if (PlayInfo.udpDevType == 5) {
                        SysApp.getMe().writeTCPCmd("MIRROR=0");
                    }
                    SysApp.getMe().setMirror(true);
                } else if (v.getId() == C0127R.id.button_3d) {
                    if (PlayInfo.udpDevType == 6) {
                        int mode = ((Integer) v.getTag()).intValue() + 1;
                        if (mode > 3) {
                            mode = 0;
                        }
                        v.setTag(Integer.valueOf(mode));
                        GLFrameRenderer.ctrlIndex = mode;
                        GLFrameRenderer.bMixMode = false;
                        FHSDK.setImagingType(GLFrameRenderer.hWin, 0);
                        FHSDK.setStandardCircle(GLFrameRenderer.hWin, 0.0f, 0.0f, 0.0f);
                        GLFrameRenderer.eyeMode = 0;
                        GLFrameRenderer.vDegrees = 0.0f;
                        GLFrameRenderer.hDegrees = 0.0f;
                        switch (mode) {
                            case 0:
                                GLFrameRenderer.displayMode = 0;
                                GLFrameRenderer.depth = FHSDK.getMaxZDepth(GLFrameRenderer.hWin);
                                return;
                            case 1:
                                GLFrameRenderer.displayMode = 0;
                                GLFrameRenderer.eyeMode = 3;
                                GLFrameRenderer.vDegrees = FHSDK.getMaxVDegress(GLFrameRenderer.hWin);
                                return;
                            case 2:
                                GLFrameRenderer.displayMode = 3;
                                return;
                            case 3:
                                GLFrameRenderer.displayMode = 5;
                                return;
                            default:
                                return;
                        }
                    }
                    int is3D = ((Integer) v.getTag()).intValue();
                    if (7 == PlayInfo.udpDevType) {
                        if (is3D == 0) {
                            v.setTag(Integer.valueOf(1));
                            PreviewView.this.rtspSurface.set3DView(true);
                            v.setBackgroundResource(C0127R.drawable.ic_3d_on);
                            PreviewView.this.resetMsgSendTime();
                            return;
                        }
                        v.setTag(Integer.valueOf(0));
                        PreviewView.this.rtspSurface.set3DView(false);
                        v.setBackgroundResource(C0127R.drawable.ic_3d_off);
                        PreviewView.this.mHandler.removeMessages(0);
                    } else if (is3D == 0) {
                        v.setTag(Integer.valueOf(1));
                        if (PlayInfo.decodeType == 2) {
                            FHSDK.set3DMode(true);
                        } else {
                            MyMediaCodec.getInstance().setShowMode(MyMediaCodec.SHOW_MODE_3D);
                        }
                        v.setBackgroundResource(C0127R.drawable.ic_3d_on);
                        PreviewView.this.resetMsgSendTime();
                    } else {
                        v.setTag(Integer.valueOf(0));
                        if (PlayInfo.decodeType == 2) {
                            FHSDK.set3DMode(false);
                        } else {
                            MyMediaCodec.getInstance().setShowMode(MyMediaCodec.SHOW_MODE_FULLSCREEN);
                        }
                        v.setBackgroundResource(C0127R.drawable.ic_3d_off);
                        PreviewView.this.mHandler.removeMessages(0);
                    }
                } else if (v.getId() == C0127R.id.button_stop) {
                    if (((Integer) v.getTag()).intValue() == 1) {
                        PreviewView.this.mToLandValue = 0;
                        v.setBackgroundResource(C0127R.drawable.ic_stop_off);
                        v.setTag(Integer.valueOf(0));
                        PreviewView.this.mStopValue = 0;
                        return;
                    }
                    PreviewView.this.mToflyValue = 0;
                    PreviewView.this.mToLandValue = 1;
                    v.setBackgroundResource(C0127R.drawable.ic_stop_on);
                    v.setTag(Integer.valueOf(1));
                    PreviewView.this.mStopValue = 1;
                    PreviewView.this.mButtonStop.setClickable(false);
                    new Handler().postDelayed(new C00711(), 1000);
                } else if (v.getId() == C0127R.id.button_switch_video) {
                    if (((Integer) v.getTag()).intValue() == 1) {
                        v.setBackgroundResource(C0127R.drawable.ic_reload_1);
                        v.setTag(Integer.valueOf(0));
                        PreviewView.this.m360RollValue = 0;
                        return;
                    }
                    v.setBackgroundResource(C0127R.drawable.ic_reload_2);
                    v.setTag(Integer.valueOf(1));
                } else if (v.getId() == C0127R.id.button_tofly) {
                    if (1 != ((Integer) PreviewView.this.mButtonStop.getTag()).intValue()) {
                        PreviewView.this.mToLandValue = 1;
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e2222222222) {
                            e2222222222.printStackTrace();
                        }
                        PreviewView.this.mToflyValue = 1;
                        PreviewView.this.mToLandValue = 0;
                        v.setEnabled(false);
                        PreviewView.this.sendMsgToFlyWait();
                    }
                } else if (v.getId() == C0127R.id.button_toland) {
                    PreviewView.this.mToLandValue = 1;
                    PreviewView.this.mToflyValue = 0;
                    v.setEnabled(false);
                    PreviewView.this.sendMsgToLandWait();
                } else if (v.getId() == C0127R.id.button_nohead) {
                    if (((Integer) v.getTag()).intValue() == 1) {
                        v.setBackgroundResource(C0127R.drawable.ic_nohead);
                        v.setTag(Integer.valueOf(0));
                        PreviewView.this.mHeadLessValue = 0;
                        return;
                    }
                    v.setBackgroundResource(C0127R.drawable.ic_nohead_selected);
                    v.setTag(Integer.valueOf(1));
                    PreviewView.this.mHeadLessValue = 1;
                } else if (v.getId() == C0127R.id.button_g_right) {
                    PreviewView.this.mGRightValue = 1;
                    v.setEnabled(false);
                    PreviewView.this.sendMsgGRightWait();
                } else if (v.getId() != C0127R.id.button_guiji) {
                } else {
                    if (((Integer) v.getTag()).intValue() == 1) {
                        PreviewView.this.isGuijiMode = false;
                        v.setBackgroundResource(C0127R.drawable.ic_guiji_off);
                        v.setTag(Integer.valueOf(0));
                        PreviewView.this.mRightRocker.setMode(0);
                        PreviewView.this.mRightRocker.setVisibility(0);
                        PreviewView.this.mRightPlane.setVisibility(4);
                        PreviewView.this.mRightPlane.setMaxX(TransportMediator.KEYCODE_MEDIA_PAUSE);
                        PreviewView.this.mRightPlane.setMaxY(TransportMediator.KEYCODE_MEDIA_PAUSE);
                        PreviewView.this.mRightPlane.setMode(0);
                        PreviewView.this.mTrimSide.setVisibility(0);
                        PreviewView.this.mTrimRight.setVisibility(0);
                        PreviewView.this.mButtonSpeed.setEnabled(true);
                        PreviewView.this.mButtonSpeed.setStatus(true);
                        PreviewView.this.mButtonAirn.setEnabled(true);
                        PreviewView.this.mButtonAirn.setStatus(true);
                        PreviewView.this.mButtonGravity.setEnabled(true);
                        PreviewView.this.mButtonGravity.setStatus(true);
                        return;
                    }
                    PreviewView.this.isGuijiMode = true;
                    PreviewView.this.mButtonSpeed.setTag(Integer.valueOf(0));
                    PreviewView.this.mButtonSpeed.setBackgroundResource(PreviewView.this.speed_bg[0]);
                    PreviewView.this.mButtonSpeed.setEnabled(false);
                    PreviewView.this.mButtonSpeed.setStatus(false);
                    PreviewView.this.mSpeedValue = 0;
                    v.setBackgroundResource(C0127R.drawable.ic_guiji_on);
                    v.setTag(Integer.valueOf(1));
                    PreviewView.this.mRightRocker.setMode(1);
                    PreviewView.this.mRightRocker.setVisibility(4);
                    PreviewView.this.mRightPlane.setVisibility(0);
                    PreviewView.this.mRightPlane.setMode(1);
                    PreviewView.this.mTrimSide.setVisibility(8);
                    PreviewView.this.mTrimRight.setVisibility(4);
                    PreviewView.this.mButtonAirn.setEnabled(false);
                    PreviewView.this.mButtonAirn.setStatus(false);
                    PreviewView.this.mButtonGravity.setEnabled(false);
                    PreviewView.this.mButtonGravity.setStatus(false);
                }
            }
        };
    }

    public PreviewView(Context mContext) {
        this.PBCurrentSpeed = 0;
        this.isPBPause = false;
        this.isStopSendMsg = false;
        this.isPBAudioOn = false;
        this.isRecOn = false;
        this.isAudioOpened = false;
        this.isTalkOpened = false;
        this.secondCount = 0;
        this.PBVideoLen = 0;
        this.PBVideoCurLen = 0;
        this.CurrentTime = PBStartTime;
        this.isGuijiMode = false;
        this.speed_bg = new int[]{C0127R.drawable.ic_speed_0, C0127R.drawable.ic_speed_1, C0127R.drawable.ic_speed_2};
        this.gravity_bg = new int[]{C0127R.drawable.ic_g_off, C0127R.drawable.ic_g_on};
        this.showctr_bg = new int[]{C0127R.drawable.ic_hide, C0127R.drawable.ic_show};
        this.mButtonBack = null;
        this.mButtonPhoto = null;
        this.mButtonReco = null;
        this.mButtonReco2 = null;
        this.mButtonAlbum = null;
        this.mButtonSpeed = null;
        this.mButtonAirn = null;
        this.mButtonGravity = null;
        this.mButtonHide = null;
        this.mButtonMore = null;
        this.mButtonRev = null;
        this.mButton3D = null;
        this.mButtonStop = null;
        this.mButton360Roll = null;
        this.mButtonTofly = null;
        this.mButtonToLand = null;
        this.mButtonNohead = null;
        this.mButtonGRight = null;
        this.mButtonGuiJi = null;
        this.mRockerFBValue = 0;
        this.mRockerRLValue = 0;
        this.mSpeedValue = 0;
        this.mAirnValue = 0;
        this.mGravityValue = 0;
        this.mStopValue = 0;
        this.m360RollValue = 0;
        this.mToflyValue = 0;
        this.mToLandValue = 0;
        this.mHeadLessValue = 0;
        this.mGRightValue = 0;
        this.m360RollValueCount = 0;
        this.b360RollValueClean = false;
        this.mLeftRocker = null;
        this.mRightRocker = null;
        this.mRightPlane = null;
        this.mTrimLeft = null;
        this.mTrimRight = null;
        this.mTrimSide = null;
        this.mLastTime = 0;
        this.mBitrateCmds = new String[]{"VideoEncoder0.RateControl.Bitrate=u:2000\r\n", "VideoEncoder0.RateControl.Bitrate=u:1500\r\n", "VideoEncoder0.RateControl.Bitrate=u:1000\r\n"};
        this.mResolutionCmds = new String[]{"VideoEncoder0.Resolution=s:720P\r\n", "VideoEncoder0.Resolution=s:VGA\r\n"};
        this.mSendThread = null;
        this.mLastTouchTime = 0;
        this.mWifiCfg = null;
        this.mConvertRecThread = null;
        this.connectSSID = null;
        this.mControlThread = null;
        this.recFilePath = null;
        this.lastGetFrameTime = 0;
        this.haveDoneDisConnectWifi = false;
        this.rightRockerRect = new Rect();
        this.bGetCurSensorBase = false;
        this.baseX = 0.0f;
        this.baseY = 0.0f;
        this.dataFun = new C01741();
        this.seekBarChangeListener = new C00702();
        this.pbPlayListener = new C00723();
        this.pbFrameListener = new C00734();
        this.pbSpeedDownListener = new C00745();
        this.pbSpeedUpListener = new C00756();
        this.btnRemoteRecSetListener = new C00767();
        this.btnRemoteShotSetListener = new C00778();
        this.btnLocateRecSetListener = new C00789();
        this.btnLocateShotSetListener = /* anonymous class already generated */;
        this.btnAudioSetListener = /* anonymous class already generated */;
        this.btnTalkSetListener = /* anonymous class already generated */;
        this.btnSerialSetListener = /* anonymous class already generated */;
        this.serialFun = /* anonymous class already generated */;
        this.OnSerialSendClickLister = /* anonymous class already generated */;
        this.OnSerialCancelClickLister = /* anonymous class already generated */;
        this.btnBCSSListener = /* anonymous class already generated */;
        this.OnSaveClickLister = /* anonymous class already generated */;
        this.OnCancelClickLister = /* anonymous class already generated */;
        this.seekBarBrightnessChangeListener = /* anonymous class already generated */;
        this.seekBarContrastChangeListener = /* anonymous class already generated */;
        this.seekBarSaturationChangeListener = /* anonymous class already generated */;
        this.seekBarSharpnessChangeListener = /* anonymous class already generated */;
        this.locateSeekBarChangeListener = /* anonymous class already generated */;
        this.locatePBPlayListener = /* anonymous class already generated */;
        this.locatePBSpeedDownListener = /* anonymous class already generated */;
        this.locatePBSpeedUpListener = /* anonymous class already generated */;
        this.btnBackListener = /* anonymous class already generated */;
        this.btn3DListener = /* anonymous class already generated */;
        this.wifiActiveStatus = false;
        this.disConnectWifiThread = /* anonymous class already generated */;
        this.checkSSIDThread = /* anonymous class already generated */;
        this.recTimeThread = /* anonymous class already generated */;
        this.surfaceTouchListener = /* anonymous class already generated */;
        this.sensorListener = /* anonymous class already generated */;
        this.myBroadcastReciver = /* anonymous class already generated */;
        this.mHandler = /* anonymous class already generated */;
        this.ShowAllLayout = /* anonymous class already generated */;
        this.CheckTouchTime = /* anonymous class already generated */;
        this.bntClickListener = /* anonymous class already generated */;
        this.mContext = mContext;
    }

    public void layoutUnInit(ViewGroup mLayout) {
        stopTalkThread();
        if (1 == PlayInfo.playType || 4 == PlayInfo.playType || 5 == PlayInfo.playType) {
            RecordClick(1);
        }
        if (this.mSendThread != null) {
            this.mSendThread.cancel();
            this.mSendThread = null;
        }
        SysApp.getMe().StartActive(false);
        SysApp.getMe().StartCheckWork(false);
        unregisterReceiver();
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SysApp.RECORD_START);
        intentFilter.addAction(SysApp.RECORD_END);
        intentFilter.addAction(SysApp.REMOTE_PHOTO);
        intentFilter.addAction(SysApp.REMOTE_RECORD);
        this.mContext.registerReceiver(this.myBroadcastReciver, intentFilter);
    }

    public void unregisterReceiver() {
        try {
            this.mContext.unregisterReceiver(this.myBroadcastReciver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void layoutInit(ViewGroup mLayout) {
        this.inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        if (!(2 == PlayInfo.playType || PlayInfo.playType == 0)) {
            if (1 == PlayInfo.playType || 4 == PlayInfo.playType) {
                this.view = this.inflater.inflate(C0127R.layout.activity_player_main, null);
                mLayout.addView(this.view);
                this.mButtonReco2 = new ETButton(this.mContext);
                this.mButtonReco2.setLayoutParams(new LayoutParams(-2, -2));
                mLayout.addView(this.mButtonReco2);
                this.mButtonReco2.setVisibility(8);
                this.tvStreamInfo = new TextView(this.mContext);
                mLayout.addView(this.tvStreamInfo);
                this.mButtonBack = (ETButton) this.view.findViewById(C0127R.id.button_back);
                this.mButtonBack.setOnClickListener(this.bntClickListener);
                this.mButtonPhoto = (ETButton) this.view.findViewById(C0127R.id.button_photo);
                this.mButtonPhoto.setOnClickListener(this.bntClickListener);
                this.mButtonReco = (ETButton) this.view.findViewById(C0127R.id.button_reco);
                this.mButtonReco.setOnClickListener(this.bntClickListener);
                this.mButtonReco.setTag(Integer.valueOf(0));
                this.tvRecTime = (TextView) this.view.findViewById(C0127R.id.tvRecTime);
                this.mButtonAlbum = (ETButton) this.view.findViewById(C0127R.id.button_album);
                this.mButtonAlbum.setOnClickListener(this.bntClickListener);
                this.mButtonHide = (ETButton) this.view.findViewById(C0127R.id.button_hide);
                this.mButtonHide.setOnClickListener(this.bntClickListener);
                this.mButtonHide.setTag(Integer.valueOf(1));
                this.mButtonMore = (ETButton) this.view.findViewById(C0127R.id.button_hidemore);
                this.mButtonMore.setOnClickListener(this.bntClickListener);
                this.mButtonMore.setTag(Integer.valueOf(1));
                imageBg = (ImageView) this.view.findViewById(C0127R.id.imageBg);
                imageBg.setAlpha(1.0f);
                this.bar = (ProgressBar) this.view.findViewById(C0127R.id.progressBar1);
                this.mButtonSpeed = (ETButton) this.view.findViewById(C0127R.id.button_speed);
                this.mButtonSpeed.setOnClickListener(this.bntClickListener);
                this.mButtonSpeed.setTag(Integer.valueOf(0));
                this.mButtonAirn = (ETButton) this.view.findViewById(C0127R.id.button_atmospheric);
                this.mButtonAirn.setOnClickListener(this.bntClickListener);
                this.mButtonAirn.setTag(Integer.valueOf(0));
                this.mButtonGravity = (ETButton) this.view.findViewById(C0127R.id.button_g);
                this.mButtonGravity.setOnClickListener(this.bntClickListener);
                this.mButtonGravity.setTag(Integer.valueOf(0));
                this.mButtonRev = (ETButton) this.view.findViewById(C0127R.id.button_rev);
                this.mButtonRev.setOnClickListener(this.bntClickListener);
                this.mButtonRev.setTag(Integer.valueOf(0));
                this.mButton3D = (ETButton) this.view.findViewById(C0127R.id.button_3d);
                this.mButton3D.setOnClickListener(this.bntClickListener);
                this.mButton3D.setBackgroundResource(C0127R.drawable.ic_3d_off);
                this.mButton3D.setTag(Integer.valueOf(0));
                this.mButtonStop = (ETButton) this.view.findViewById(C0127R.id.button_stop);
                this.mButtonStop.setOnClickListener(this.bntClickListener);
                this.mButtonStop.setTag(Integer.valueOf(0));
                this.mButton360Roll = (ETButton) this.view.findViewById(C0127R.id.button_switch_video);
                this.mButton360Roll.setOnClickListener(this.bntClickListener);
                this.mButton360Roll.setTag(Integer.valueOf(0));
                this.mButtonGuiJi = (ETButton) this.view.findViewById(C0127R.id.button_guiji);
                this.mButtonGuiJi.setOnClickListener(this.bntClickListener);
                this.mButtonGuiJi.setTag(Integer.valueOf(0));
                this.mButtonGuiJi.setVisibility(8);
                this.mButtonTofly = (ETButton) this.view.findViewById(C0127R.id.button_tofly);
                this.mButtonTofly.setOnClickListener(this.bntClickListener);
                this.mButtonTofly.setTag(Integer.valueOf(0));
                this.mButtonToLand = (ETButton) this.view.findViewById(C0127R.id.button_toland);
                this.mButtonToLand.setOnClickListener(this.bntClickListener);
                this.mButtonToLand.setTag(Integer.valueOf(0));
                this.mButtonNohead = (ETButton) this.view.findViewById(C0127R.id.button_nohead);
                this.mButtonNohead.setOnClickListener(this.bntClickListener);
                this.mButtonNohead.setTag(Integer.valueOf(0));
                this.mButtonGRight = (ETButton) this.view.findViewById(C0127R.id.button_g_right);
                this.mButtonGRight.setOnClickListener(this.bntClickListener);
                this.mButtonGRight.setTag(Integer.valueOf(0));
                this.mLeftRocker = (ETLeftRocker) this.view.findViewById(C0127R.id.rocker_left);
                this.mLeftRocker.setLock(false);
                this.mLeftRocker.setMaxX(TransportMediator.KEYCODE_MEDIA_PAUSE);
                this.mLeftRocker.setMaxY(TransportMediator.KEYCODE_MEDIA_PAUSE);
                this.mRightRocker = (ETRightRocker) this.view.findViewById(C0127R.id.rocker_right);
                this.mRightRocker.setMaxX(TransportMediator.KEYCODE_MEDIA_PAUSE);
                this.mRightRocker.setMaxY(TransportMediator.KEYCODE_MEDIA_PAUSE);
                this.mRightPlane = (ETPlaneRocker) this.view.findViewById(C0127R.id.rocker_right_plane);
                this.mRightPlane.setMaxX(TransportMediator.KEYCODE_MEDIA_PAUSE);
                this.mRightPlane.setMaxY(TransportMediator.KEYCODE_MEDIA_PAUSE);
                this.mTrimLeft = (ETTrim) this.view.findViewById(C0127R.id.trim_left);
                this.mTrimRight = (ETTrim) this.view.findViewById(C0127R.id.trim_right);
                this.mTrimSide = (ETTBTrim) this.view.findViewById(C0127R.id.trim_side);
                this.view.findViewById(C0127R.id.layout_player_midbnt).setVisibility(8);
                this.view.findViewById(C0127R.id.layout_more).setVisibility(8);
                this.mLastTime = System.currentTimeMillis();
                this.mSoundPool = new SoundPool(10, 1, 5);
                this.mMusic = this.mSoundPool.load(this.mContext, C0127R.raw.photo, 1);
                this.mSensorManager = (SensorManager) this.mContext.getSystemService("sensor");
                if (this.mSensorManager == null) {
                    Log.d(TAG, "deveice not support SensorManager");
                } else {
                    this.mSensor = this.mSensorManager.getDefaultSensor(1);
                    this.mSensorManager.registerListener(this.sensorListener, this.mSensor, 1);
                }
                this.mWifiCfg = new WLANCfg(this.mContext);
                if (!(this.mWifiCfg == null || this.mWifiCfg.getSSID() == null || !this.mWifiCfg.getSSID().contains("ZC"))) {
                    this.mButtonGuiJi.setVisibility(0);
                }
                this.hCheckSSID = new Handler();
                registerReceiver();
                this.hRecTime = new Handler();
            } else if (3 == PlayInfo.playType) {
                this.view = this.inflater.inflate(C0127R.layout.playback_locate_video, null);
                mLayout.addView(this.view);
                this.mSeekBar = (SeekBar) this.view.findViewById(C0127R.id.seekBarProgress);
                this.mPBCurrentTime = (TextView) this.view.findViewById(C0127R.id.pbCurrentTime);
                this.mPBStopTime = (TextView) this.view.findViewById(C0127R.id.pbStopTime);
                this.tvPBSpeed = (TextView) this.view.findViewById(C0127R.id.tvPBSpeed);
                this.btnPBPlay = (ImageView) this.view.findViewById(C0127R.id.btnPBPlay);
                this.btnPBSpeedDown = (ImageView) this.view.findViewById(C0127R.id.btnPBSpeedDown);
                this.btnPBSpeedDown.setVisibility(8);
                this.btnPBSpeedUp = (ImageView) this.view.findViewById(C0127R.id.btnPBSpeedUp);
                this.btnPBSpeedUp.setVisibility(8);
                this.btn3D = (ImageView) this.view.findViewById(C0127R.id.btn3D);
                this.btnBack = (ImageView) this.view.findViewById(C0127R.id.btnBack);
                this.mSeekBar.setMax(SEEKBAR_MAX_NUM);
                this.mSeekBar.setOnSeekBarChangeListener(this.locateSeekBarChangeListener);
                this.btnPBPlay.setOnClickListener(this.locatePBPlayListener);
                this.btnPBSpeedDown.setOnClickListener(this.locatePBSpeedDownListener);
                this.btnPBSpeedUp.setOnClickListener(this.locatePBSpeedUpListener);
                this.layoutMenu = (RelativeLayout) this.view.findViewById(C0127R.id.control);
                this.tvStreamInfo = (TextView) this.view.findViewById(C0127R.id.tvPBStreamInfo);
                this.btn3D.setOnClickListener(this.btn3DListener);
                this.btn3D.setTag(Integer.valueOf(0));
                this.btnBack.setOnClickListener(this.btnBackListener);
            }
        }
        createHandler();
        FHSDK.registerNotifyCallBack(this.dataFun);
        this.mFrameRender = GLFrameRenderer.getInstance();
        if (7 == PlayInfo.udpDevType) {
            this.rtspSurface = (RTSPPlayerView) mLayout.getChildAt(0);
            this.rtspSurface.setOnTouchListener(this.surfaceTouchListener);
            this.rtspSurface.addCallback(new Callback() {
                public void rtspPlayerStatusChanged(int status) {
                    switch (status) {
                        case 1:
                            log.m1e("rtspSurface.isShown()= " + PreviewView.this.rtspSurface.isShown());
                            if (PreviewView.this.rtspSurface.isShown()) {
                                PreviewView.this.rtspSurface.startPlaying();
                                if (PreviewView.imageBg != null) {
                                    PreviewView.imageBg.setVisibility(4);
                                }
                                if (PreviewView.this.bar != null) {
                                    PreviewView.this.bar.setVisibility(4);
                                    return;
                                }
                                return;
                            }
                            PreviewView.this.rtspSurface.stopPlaying();
                            return;
                        case 2:
                        case 4:
                            return;
                        case 3:
                            log.m1e("rtspSurface.initPlaying();");
                            PreviewView.this.rtspSurface.initPlaying();
                            return;
                        default:
                            Log.i(PreviewView.TAG, "RTSPPlayerView Callback: Why run into here?");
                            return;
                    }
                }
            });
            this.rtspSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    Log.i(PreviewView.TAG, "surfaceCreated");
                    PreviewView.this.rtspSurface.initPlaying();
                }

                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                    Log.i(PreviewView.TAG, "surfaceChanged:(" + i + ": " + i1 + "," + i2 + ")");
                }

                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    Log.i(PreviewView.TAG, "surfaceDestroyed");
                    PreviewView.this.rtspSurface.stopPlaying();
                }
            });
        } else if (PlayInfo.decodeType == 2) {
            ((SurfaceView) mLayout.getChildAt(0)).setOnTouchListener(this.surfaceTouchListener);
        } else if (PlayInfo.udpDevType != 6) {
            ((GLFrameSurface) mLayout.getChildAt(0)).setOnTouchListener(this.surfaceTouchListener);
        }
    }

    public void setLayoutMenuShow(boolean isShow) {
        if (this.layoutMenu != null) {
            if (isShow) {
                this.layoutMenu.setVisibility(0);
            } else {
                this.layoutMenu.setVisibility(8);
            }
        }
    }

    public boolean getLayoutMenuShow() {
        if (this.layoutMenu == null || this.layoutMenu.getVisibility() == 8) {
            return false;
        }
        return true;
    }

    public void createHandler() {
        if (PlayInfo.playType == 0) {
            this.hRecTime = new Handler();
        }
        this.handler = new Handler() {
            boolean bGetRecInfo = true;

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        int progress;
                        if (2 != PlayInfo.playType) {
                            if (3 == PlayInfo.playType) {
                                PreviewView.this.CurrentTime = FHSDK.getCurrentPts();
                                if (0 != PreviewView.this.CurrentTime) {
                                    if (this.bGetRecInfo) {
                                        Define define = new Define();
                                        define.getClass();
                                        PBRecTime recTime = new PBRecTime();
                                        FHSDK.getRecPlayTimeInfo(recTime);
                                        PreviewView.PBStopTime = recTime.pbStopTime;
                                        PreviewView.PBStartTime = recTime.pbStartTime;
                                        PreviewView.this.PBVideoLen = (PreviewView.PBStopTime - PreviewView.PBStartTime) / 1000;
                                        if (PreviewView.this.mPBStopTime != null) {
                                            PreviewView.this.mPBStopTime.setText(ActivtyUtil.formatTime(PreviewView.this.PBVideoLen));
                                        }
                                        this.bGetRecInfo = false;
                                    }
                                    progress = 0;
                                    PreviewView.this.PBVideoCurLen = PreviewView.this.CurrentTime - (PreviewView.PBStartTime / 1000);
                                    if (PreviewView.this.PBVideoLen > 0) {
                                        progress = (int) ((PreviewView.this.PBVideoCurLen * 10000) / PreviewView.this.PBVideoLen);
                                    }
                                    if (PreviewView.this.mPBCurrentTime != null) {
                                        PreviewView.this.mPBCurrentTime.setText(ActivtyUtil.formatTime(PreviewView.this.PBVideoCurLen));
                                    }
                                    if (PreviewView.this.mSeekBar != null) {
                                        PreviewView.this.mSeekBar.setProgress(progress);
                                    }
                                    if (100 == FHSDK.getRecPlayProgress()) {
                                        PreviewView.this.mContext.finish();
                                        break;
                                    }
                                }
                            }
                        }
                        PreviewView.this.CurrentTime = FHSDK.getCurrentPts();
                        if (PreviewView.this.mPBCurrentTime != null) {
                            PreviewView.this.mPBCurrentTime.setText(FHSDK.timeConvert(PlayInfo.userID, PreviewView.this.CurrentTime));
                        }
                        progress = (int) (((PreviewView.this.CurrentTime - PreviewView.PBStartTime) * 10000) / (PreviewView.PBStopTime - PreviewView.PBStartTime));
                        if (PreviewView.this.mSeekBar != null) {
                            PreviewView.this.mSeekBar.setProgress(progress);
                            break;
                        }
                        break;
                    case 3:
                        if (PreviewView.this.connectSSID == null && PreviewView.this.mWifiCfg != null) {
                            PreviewView.this.connectSSID = PreviewView.this.mWifiCfg.getSSID();
                        }
                        long curMs = System.currentTimeMillis();
                        if (SysApp.getMe().isVer210() && !PreviewView.this.haveDoneDisConnectWifi && PreviewView.this.lastGetFrameTime != 0 && curMs - PreviewView.this.lastGetFrameTime > 7000) {
                            PreviewView.this.haveDoneDisConnectWifi = true;
                            PreviewView.this.hDisConnectWifi = new Handler();
                            PreviewView.this.hDisConnectWifi.post(PreviewView.this.disConnectWifiThread);
                            break;
                        }
                    case PreviewView.SHOT_FileName /*2001*/:
                        PreviewView.this.mConvertRecThread = new ConvertRecThread(PreviewView.this.mContext, msg.getData().getString("filePath"));
                        PreviewView.this.mConvertRecThread.start();
                        break;
                    case PreviewView.SHOT_Fail /*2002*/:
                        ActivtyUtil.openToast(PreviewView.this.mContext, PreviewView.this.mContext.getString(C0127R.string.str_nofoundSDCard));
                        break;
                    case PreviewView.NOTIFY_REQUEST_I /*2003*/:
                        if (PlayInfo.playType != 3) {
                            SysApp.getMe().requestIFrame();
                            break;
                        }
                        break;
                    case PreviewView.NOTIFY_HIDE_BG /*2004*/:
                        PreviewView.this.hideBackGround(true);
                        PreviewView.this.lastGetFrameTime = System.currentTimeMillis();
                        PreviewView.this.haveDoneDisConnectWifi = false;
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public static String getSettingPath() {
        return REC_PATH;
    }

    public static void saveImageToGallery(Context context, String fileName, Bitmap bmp) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        SysApp.getMe();
        File appDir = new File(externalStorageDirectory, SysApp.SAVE_DATA_PATH);
        if (appDir.exists()) {
            File file = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e3) {
                e3.printStackTrace();
            }
            context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + file.getAbsoluteFile())));
            return;
        }
        log.m1e("appDir is not exists!!");
    }

    public static void cb_data(int type, byte[] data, int len) {
    }

    public static String getSDCardPath() {
        String sdcard_path = null;
        String sd_default = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("text", sd_default);
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("mount").getInputStream()));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                } else if (!(line.contains("secure") || line.contains("asec"))) {
                    String[] columns;
                    if (line.contains("fat") && line.contains("/mnt/")) {
                        columns = line.split(" ");
                        if (!(columns == null || columns.length <= 1 || sd_default.trim().equals(columns[1].trim()))) {
                            sdcard_path = columns[1];
                        }
                    } else if (line.contains("fuse") && line.contains("/mnt/")) {
                        columns = line.split(" ");
                        if (!(columns == null || columns.length <= 1 || sd_default.trim().equals(columns[1].trim()))) {
                            sdcard_path = columns[1];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdcard_path;
    }

    public boolean Connect(String SSID, int Type) {
        if (SSID == null || SSID.length() == 0) {
            return false;
        }
        boolean bFoundId = false;
        String ssid = null;
        try {
            WLANCfg mWifiCfg = new WLANCfg(this.mContext);
            mWifiCfg.startScan();
            List<ScanResult> list = mWifiCfg.getWifiList();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (SSID.equals(((ScanResult) list.get(i)).SSID)) {
                        bFoundId = true;
                        break;
                    }
                }
            }
            Message msg;
            if (bFoundId) {
                if (mWifiCfg.checkState() == 1) {
                    mWifiCfg.openWifi();
                    while (mWifiCfg.checkState() == 2) {
                        try {
                            Thread.currentThread();
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                if (mWifiCfg.checkState() == 3) {
                    ssid = mWifiCfg.getSSID();
                    if (ssid != null && ssid.equals(SSID) && mWifiCfg.getNetWordId() > 0) {
                        return false;
                    }
                    ssid = SSID;
                    msg = this.handler.obtainMessage();
                    msg.what = NOTIFY_SHOW_STRING;
                    msg.obj = "WIFI已断开, 尝试重新连接";
                    this.handler.sendMessage(msg);
                }
                mWifiCfg.disConnectionWifi(mWifiCfg.getNetWordId());
                mWifiCfg.openWifi();
                WifiConfiguration wifiConfig = mWifiCfg.CreateWifiInfo(ssid, null, Type);
                if (wifiConfig == null) {
                    return false;
                }
                WifiConfiguration tempConfig = WLANCfg.IsExsits(ssid);
                if (tempConfig != null) {
                    mWifiCfg.removeNetWork(tempConfig.networkId);
                }
                boolean netID = mWifiCfg.addNetWork(wifiConfig);
                ssid = mWifiCfg.getSSID();
                mWifiCfg.reconnect();
                return netID;
            }
            msg = this.handler.obtainMessage();
            msg.what = NOTIFY_SHOW_STRING;
            msg.obj = "WIFI已丢失";
            this.handler.sendMessage(msg);
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public void SetActiveCheckSSIDThread(boolean val) {
        this.wifiActiveStatus = val;
    }

    public static void startTalkThread() {
        mTalk = new TalkThread();
        mTalk.startRecording();
    }

    public static void stopTalkThread() {
        if (mTalk != null) {
            mTalk.stopRecording();
            mTalk = null;
        }
        FHSDK.stopTalk();
    }

    public void hideExInfo() {
        this.view.setVisibility(8);
    }

    public void hideBackGround(boolean isHide) {
        if (!isHide) {
            imageBg.setVisibility(0);
        } else if (0.0f != imageBg.getAlpha()) {
            float alpha = imageBg.getAlpha() - 0.03f;
            if (alpha < 0.0f) {
                alpha = 0.0f;
            }
            imageBg.setAlpha(alpha);
            SysApp.getMe().bStreamGet = true;
        }
        if (this.bar != null) {
            this.bar.setVisibility(4);
        }
    }

    private void resetMsgSendTime() {
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    private void sendMsgWaitRev() {
        this.mHandler.sendEmptyMessageDelayed(2, 5000);
    }

    private void sendMsgToFlyWait() {
        this.mHandler.sendEmptyMessageDelayed(3, 3000);
    }

    private void sendMsgToLandWait() {
        this.mHandler.sendEmptyMessageDelayed(4, 3000);
    }

    private void sendMsg360RollClean() {
        this.mHandler.sendEmptyMessageDelayed(5, 0);
    }

    private void sendMsgGRightWait() {
        this.mHandler.sendEmptyMessageDelayed(6, 1000);
    }

    private void RecordClick(int val) {
        if (7 == PlayInfo.udpDevType) {
            if (RTSPPlayerView.status == RTSPPlayerStatus.DECODING) {
                if (Utilities.getRandomVideoFilePath() != null && this.rtspSurface.startRecordingVideo(Utilities.getRandomVideoFilePath()) >= 0) {
                    this.mButtonPhoto.setEnabled(false);
                    this.mButtonPhoto.setStatus(false);
                    this.mButtonAlbum.setEnabled(false);
                    this.mButtonAlbum.setStatus(false);
                    this.mButtonRev.setEnabled(false);
                    this.mButtonRev.setStatus(false);
                    this.mButtonReco.setBackgroundResource(C0127R.anim.anim_reco);
                    ((AnimationDrawable) this.mButtonReco.getBackground()).start();
                    if (8 == this.view.getVisibility()) {
                        this.mButtonReco2.setVisibility(0);
                        this.mButtonReco2.setBackgroundResource(C0127R.anim.anim_reco);
                        ((AnimationDrawable) this.mButtonReco2.getBackground()).start();
                    }
                    if (1 == SysApp.getMe().getTransMode()) {
                        SysApp.getMe().udpStartRecord();
                    } else {
                        SysApp.getMe().tcpStartRecord();
                    }
                }
            } else if (RTSPPlayerView.status == RTSPPlayerStatus.RECORDING) {
                if (this.rtspSurface.stopRecodingVideo() >= 0) {
                    this.mButtonPhoto.setEnabled(true);
                    this.mButtonPhoto.setStatus(true);
                    this.mButtonAlbum.setEnabled(true);
                    this.mButtonAlbum.setStatus(true);
                    this.mButtonRev.setEnabled(true);
                    this.mButtonRev.setStatus(true);
                    this.mButtonReco.setBackgroundResource(C0127R.drawable.ic_reco_off);
                    if (1 == SysApp.getMe().getTransMode()) {
                        SysApp.getMe().udpStopRecord();
                    } else {
                        SysApp.getMe().tcpStopRecord();
                    }
                }
                if (8 != this.mButtonReco2.getVisibility()) {
                    this.mButtonReco2.setVisibility(8);
                }
            }
        } else if (val == 1) {
            this.mButtonReco.setTag(Integer.valueOf(0));
            this.mButtonPhoto.setEnabled(true);
            this.mButtonPhoto.setStatus(true);
            this.mButtonAlbum.setEnabled(true);
            this.mButtonAlbum.setStatus(true);
            this.tvRecTime.setVisibility(8);
            this.hRecTime.removeCallbacks(this.recTimeThread);
            this.secondCount = 0;
            if (this.mButtonRev != null) {
                this.mButtonRev.setEnabled(true);
                this.mButtonRev.setStatus(true);
            }
            this.mButtonReco.setBackgroundResource(C0127R.drawable.ic_reco_off);
            if (PlayInfo.udpDevType == 1 || PlayInfo.udpDevType == 2 || PlayInfo.udpDevType == 6) {
                if (1 == SysApp.getMe().getTransMode()) {
                    SysApp.getMe().udpStopRecord();
                } else {
                    SysApp.getMe().tcpStopRecord();
                }
            } else if (PlayInfo.udpDevType == 4 || PlayInfo.udpDevType == 5) {
                SysApp.getMe().writeTCPCmd("end\r\n");
            }
            FHSDK.stopLocalRecordMP4Ex();
            if (8 != this.mButtonReco2.getVisibility()) {
                this.mButtonReco2.setVisibility(8);
            }
            Bundle bundle = new Bundle();
            bundle.putString("filePath", this.recFilePath);
            Message msg = this.handler.obtainMessage();
            msg.what = SHOT_FileName;
            msg.setData(bundle);
            this.handler.sendMessage(msg);
        } else {
            int videoWidth;
            int videoHeight;
            this.mButtonReco.setTag(Integer.valueOf(1));
            String name = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String path = SysApp.SAVE_PATH + name + ".mp4";
            this.recFilePath = path;
            if (PlayInfo.udpDevType == 1) {
                videoWidth = 720;
                videoHeight = 576;
            } else {
                videoWidth = 1280;
                videoHeight = 720;
            }
            Log.i(TAG, "===Record Path:" + path);
            if (FHSDK.startLocalRecordMP4Ex(PlayInfo.userID, videoWidth, videoHeight, 25, path)) {
                this.tvRecTime.setVisibility(0);
                this.hRecTime.post(this.recTimeThread);
                this.mButtonPhoto.setEnabled(false);
                this.mButtonPhoto.setStatus(false);
                this.mButtonAlbum.setEnabled(false);
                this.mButtonAlbum.setStatus(false);
                if (this.mButtonRev != null) {
                    this.mButtonRev.setEnabled(false);
                    this.mButtonRev.setStatus(false);
                }
                this.mButtonReco.setBackgroundResource(C0127R.anim.anim_reco);
                ((AnimationDrawable) this.mButtonReco.getBackground()).start();
                if (8 == this.view.getVisibility()) {
                    this.mButtonReco2.setVisibility(0);
                    this.mButtonReco2.setBackgroundResource(C0127R.anim.anim_reco);
                    ((AnimationDrawable) this.mButtonReco2.getBackground()).start();
                }
            }
            if (PlayInfo.udpDevType == 1 || PlayInfo.udpDevType == 2 || PlayInfo.udpDevType == 6) {
                if (1 == SysApp.getMe().getTransMode()) {
                    SysApp.getMe().udpStartRecord();
                } else {
                    SysApp.getMe().tcpStartRecord();
                }
            } else if (PlayInfo.udpDevType == 4 || PlayInfo.udpDevType == 5) {
                SysApp.getMe().writeTCPCmd("begin " + name + "\r\n");
            }
        }
    }

    private void PhotoClick() {
        if (PlayInfo.udpDevType != 7) {
            long time = System.currentTimeMillis();
            if (time - this.mLastTime > 100) {
                this.mLastTime = time;
                this.mSoundPool.play(this.mMusic, 1.0f, 1.0f, 0, 0, 1.0f);
                FHSDK.setShotOn();
                if (PlayInfo.udpDevType == 1 || PlayInfo.udpDevType == 2 || PlayInfo.udpDevType == 6) {
                    if (1 == SysApp.getMe().getTransMode()) {
                        SysApp.getMe().udpStartPhoto();
                    } else {
                        SysApp.getMe().tcpStartPhoto();
                    }
                } else if (PlayInfo.udpDevType == 4 || PlayInfo.udpDevType == 5) {
                    SysApp.getMe().writeTCPCmd("photo  " + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + "\r\n");
                }
            }
        } else if (RTSPPlayerView.status == RTSPPlayerStatus.DECODING) {
            String photoFilePath = Utilities.getRandomPhotoFilePath();
            String toastText = this.mContext.getResources().getString(C0127R.string.id_fail);
            if (photoFilePath != null && this.rtspSurface.saveScreenshot(photoFilePath) >= 0) {
                if (new File(photoFilePath).exists()) {
                    toastText = photoFilePath;
                }
                this.mSoundPool.play(1, 1.0f, 1.0f, 0, 0, 1.0f);
            }
        }
    }

    public void leftRockerRefresh() {
        this.mLeftRocker.setLock(false);
        this.mLeftRocker.setYV(0);
        this.mLeftRocker.Refresh();
        HideClick(0);
        this.mButtonHide.performClick();
        SysApp.getMe().StartCheckWork(true);
    }

    private void HideClick(int val) {
        int ishide;
        if (val == 1) {
            ishide = 0;
        } else {
            ishide = 1;
        }
        this.mButtonHide.setTag(Integer.valueOf(ishide));
        this.mButtonHide.setBackgroundResource(this.showctr_bg[ishide]);
        if (ishide == 1) {
            this.mLeftRocker.setVisibility(0);
            this.mButton360Roll.setVisibility(0);
            this.mRightRocker.setVisibility(0);
            if (this.isGuijiMode) {
                this.mRightRocker.setVisibility(4);
                this.mTrimRight.setVisibility(4);
            }
            this.mTrimLeft.setVisibility(0);
            this.mTrimRight.setVisibility(0);
            this.mButtonAirn.setEnabled(true);
            this.mButtonAirn.setStatus(true);
            if (1 == ((Integer) this.mButtonAirn.getTag()).intValue()) {
                this.mLeftRocker.setLock(true);
                this.mLeftRocker.setXV(this.mLeftRocker.getMaxX() / 2);
                this.mLeftRocker.setYV(this.mLeftRocker.getMaxY() / 2);
                this.mLeftRocker.Refresh();
            }
            if (this.mAirnValue == 1) {
                this.view.findViewById(C0127R.id.layout_player_midbnt).setVisibility(0);
                this.view.findViewById(C0127R.id.layout_flyland).setVisibility(0);
                this.mButtonStop.setVisibility(0);
                this.mButtonStop.setClickable(true);
            }
            this.view.findViewById(C0127R.id.layout_player_right).setVisibility(0);
            if (this.mSendThread == null) {
                this.mSendThread = new SendThread();
                this.mSendThread.start();
                return;
            }
            return;
        }
        this.mButtonStop.setVisibility(4);
        this.mButtonStop.setClickable(false);
        this.mLeftRocker.setVisibility(8);
        this.mRightRocker.setVisibility(8);
        this.mButton360Roll.setVisibility(8);
        this.mTrimLeft.setVisibility(8);
        this.mTrimRight.setVisibility(8);
        this.mButtonAirn.setEnabled(false);
        this.mButtonAirn.setStatus(false);
        this.view.findViewById(C0127R.id.layout_player_midbnt).setVisibility(8);
        this.view.findViewById(C0127R.id.layout_player_right).setVisibility(8);
        this.view.findViewById(C0127R.id.layout_flyland).setVisibility(8);
        if (this.mSendThread != null) {
            this.mSendThread.cancel();
            this.mSendThread = null;
        }
    }

    private void HideMoreClick(int val) {
        int ishide;
        if (val == 1) {
            ishide = 0;
        } else {
            ishide = 1;
        }
        this.mButtonMore.setTag(Integer.valueOf(ishide));
        if (ishide == 1) {
            this.view.findViewById(C0127R.id.layout_more).setVisibility(8);
        } else {
            this.view.findViewById(C0127R.id.layout_more).setVisibility(0);
        }
    }
}
