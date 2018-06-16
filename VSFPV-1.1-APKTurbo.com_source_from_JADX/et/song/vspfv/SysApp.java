package et.song.vspfv;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import com.android.opengles.GLFrameRenderer;
import com.app.util.WLANCfg;
import com.app.util.log;
import com.fh.lib.FHSDK;
import com.fh.lib.PlayInfo;
import et.song.ui.libs.ETTool;
import et.song.ui.libs.ETValue;
import et.song.ui.libs.TCPSocket;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class SysApp extends Application {
    public static final int DEV_TCP_PORT = 8080;
    public static final int DEV_TCP_PORT2 = 8888;
    private static final int DEV_UDP_PORT = 8080;
    public static final String RECORD_END = "et.song.vspfv.REMOTE_RECORD_END";
    public static final String RECORD_START = "et.song.vspfv.REMOTE_RECORD_START";
    public static final String REMOTE_PHOTO = "et.song.vspfv.REMOTE_PHOTO";
    public static final String REMOTE_RECORD = "et.song.vspfv.REMOTE_RECORD";
    public static String SAVE_DATA_PATH = "/AirSnapshot/";
    public static String SAVE_PATH = "";
    protected static final String TAG = "SysApp";
    private static final char[] asciiTable = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final char[] buf = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V'};
    private static SysApp mMe;
    private byte[] SnapshotData;
    private final int TRANS_MODE_TCP = 0;
    private final int TRANS_MODE_UDP = 1;
    private boolean bFirstRecvPhot = true;
    public boolean bMirror = false;
    private boolean bOffLine = false;
    public boolean bStreamGet = false;
    public boolean bVer210 = false;
    Runnable checkAP = new C01383();
    Runnable checkDevType = new C01394();
    Runnable checkSSID = new C01405();
    private boolean go2CheckSSID = true;
    private int[] lastIp = new int[2];
    private AppConfig mConfig;
    private Context mContext = null;
    private String mCurSSID = null;
    private InetAddress mDevAddr;
    private int mDevType = 0;
    private GLFrameRenderer mFrameRender;
    private Handler mHandler = new C01361();
    private int[] mIPinfo = new int[3];
    private boolean mIsPiratical = false;
    private boolean mIsRecvForceI = false;
    private int mRecSnapshotLen = 0;
    public boolean mRemoteRecord = false;
    private int mSnapshotLen = 0;
    private TCPSocket mTcpSocket;
    private UDPServer mUdpSer = new UDPServer();
    private WLANCfg mWifiCfg;
    private int mWifiRSSI = -200;
    private Runnable sendActiveCmd = new C01427();
    private Runnable sendCheckWorkCmd = new C01449();
    Runnable sendDevTime = new C01416();
    private Runnable sendEndCmd = new C01438();
    private Runnable sendRequestCmd = new Runnable() {
        public void run() {
            SysApp.this.writeUDPCmd(new byte[]{(byte) 39});
        }
    };
    private String strShotName = "XXXX";
    private int tcpSendCount = 0;
    private int transMode = -1;
    private BroadcastReceiver updataReceiver = new C01372();

    class C01361 extends Handler {
        C01361() {
        }

        public void handleMessage(Message msg) {
            String tmpStr = "";
            switch (msg.what) {
                case 10001:
                    byte[] udpRec = msg.obj;
                    tmpStr = ETTool.BytesToHexString(udpRec, 20);
                    if (tmpStr.startsWith("4d4a564741")) {
                        log.m1e("MJVGA");
                        PlayInfo.udpDevType = 7;
                        PlayInfo.playType = 5;
                        if (SysApp.this.mTcpSocket != null) {
                            SysApp.this.mTcpSocket.Stop();
                            SysApp.this.mTcpSocket = null;
                        }
                        SysApp.this.mTcpSocket = new TCPSocket(SysApp.this.getDevAddrStr(), SysApp.DEV_TCP_PORT2, SysApp.this.mHandler);
                        SysApp.this.mTcpSocket.Start();
                        return;
                    } else if (tmpStr.startsWith("564741")) {
                        log.m1e("VGA");
                        PlayInfo.udpDevType = 1;
                        if (SysApp.this.mTcpSocket != null) {
                            SysApp.this.mTcpSocket.Stop();
                            SysApp.this.mTcpSocket = null;
                        }
                        SysApp.this.mTcpSocket = new TCPSocket(SysApp.this.getDevAddrStr(), SysApp.DEV_TCP_PORT2, SysApp.this.mHandler);
                        SysApp.this.mTcpSocket.Start();
                        return;
                    } else if (tmpStr.startsWith("554450373230505955")) {
                        PlayInfo.udpDevType = 6;
                        SysApp.this.mHandler.post(SysApp.this.sendDevTime);
                        if (SysApp.this.mTcpSocket != null) {
                            SysApp.this.mTcpSocket.Stop();
                            SysApp.this.mTcpSocket = null;
                        }
                        SysApp.this.mTcpSocket = new TCPSocket(SysApp.this.getDevAddrStr(), SysApp.DEV_TCP_PORT2, SysApp.this.mHandler);
                        SysApp.this.mTcpSocket.Start();
                        return;
                    } else if (tmpStr.startsWith("55445037323050") || tmpStr.startsWith("3130383050")) {
                        log.m1e("UDP720P");
                        PlayInfo.udpDevType = 2;
                        SysApp.this.mHandler.post(SysApp.this.sendDevTime);
                        if (SysApp.this.mTcpSocket != null) {
                            SysApp.this.mTcpSocket.Stop();
                            SysApp.this.mTcpSocket = null;
                        }
                        SysApp.this.mTcpSocket = new TCPSocket(SysApp.this.getDevAddrStr(), SysApp.DEV_TCP_PORT2, SysApp.this.mHandler);
                        SysApp.this.mTcpSocket.Start();
                        SysApp.this.getVersion();
                        return;
                    } else if (tmpStr.startsWith("37323050")) {
                        log.m1e("RTSP 720P");
                        PlayInfo.udpDevType = 4;
                        PlayInfo.RTSPUrl = ETValue.NETWORK_REQUEST;
                        PlayInfo.playType = 4;
                        if (SysApp.this.mTcpSocket != null) {
                            SysApp.this.mTcpSocket.Stop();
                            SysApp.this.mTcpSocket = null;
                        }
                        SysApp.this.mTcpSocket = new TCPSocket(SysApp.this.getDevAddrStr(), 8080, SysApp.this.mHandler);
                        SysApp.this.mTcpSocket.Start();
                        return;
                    } else if (tmpStr.startsWith("3130383050")) {
                        PlayInfo.udpDevType = 5;
                        PlayInfo.RTSPUrl = ETValue.NETWORK_REQUEST;
                        PlayInfo.playType = 4;
                        if (SysApp.this.mTcpSocket != null) {
                            SysApp.this.mTcpSocket.Stop();
                            SysApp.this.mTcpSocket = null;
                        }
                        SysApp.this.mTcpSocket = new TCPSocket(SysApp.this.getDevAddrStr(), 8080, SysApp.this.mHandler);
                        SysApp.this.mTcpSocket.Start();
                        SysApp.this.rtspSendTime();
                        return;
                    } else if (tmpStr.startsWith("666c6173")) {
                        String[] tmpArr = tmpStr.substring(8).split("26");
                        if (tmpArr.length != 2 || !tmpArr[1].startsWith(tmpArr[0])) {
                            SysApp.this.mIsPiratical = true;
                            return;
                        }
                        return;
                    } else if (tmpStr.startsWith("6f6b697") && udpRec.length > 20) {
                        return;
                    } else {
                        if (tmpStr.startsWith("70686f746f")) {
                            SysApp.this.sendBroadcast(new Intent(SysApp.REMOTE_PHOTO));
                            return;
                        } else if (tmpStr.startsWith("7265636f7264")) {
                            SysApp.this.sendBroadcast(new Intent(SysApp.REMOTE_RECORD));
                            return;
                        } else if (tmpStr.startsWith("6e6f6163740d0a")) {
                            SysApp.this.transMode = 1;
                            return;
                        } else if ((tmpStr.startsWith("000001a5") && udpRec.length > 20) || SysApp.this.mSnapshotLen > 0 || tmpStr.startsWith("forceI")) {
                            return;
                        } else {
                            if (tmpStr.startsWith("56322e")) {
                                try {
                                    byte[] btVer = ETTool.HexStringToBytes(tmpStr);
                                    log.m1e("tmpStr = " + tmpStr);
                                    if (((long) ((((btVer[1] * 255) * 255) + (btVer[3] * 255)) + btVer[5])) >= 3263802 && PlayInfo.udpDevType == 2) {
                                        PlayInfo.transMode = 1;
                                        SysApp.this.transMode = 0;
                                    }
                                    log.m1e("PlayInfo.transMode = " + PlayInfo.transMode);
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return;
                                }
                            } else if (tmpStr.startsWith("56322e312e38")) {
                                PlayInfo.transMode = 1;
                                return;
                            } else if (tmpStr.startsWith("56322e312e39")) {
                                PlayInfo.transMode = 1;
                                return;
                            } else if (tmpStr.startsWith("6d6972726f723d30")) {
                                SysApp.this.bMirror = false;
                                SysApp.this.mFrameRender = GLFrameRenderer.getInstance();
                                GLFrameRenderer.circleX = 506.0f;
                                GLFrameRenderer.circleY = 498.0f;
                                GLFrameRenderer.circleR = 444.0f;
                                return;
                            } else if (tmpStr.startsWith("6d6972726f723d31")) {
                                SysApp.this.bMirror = true;
                                SysApp.this.mFrameRender = GLFrameRenderer.getInstance();
                                GLFrameRenderer.circleX = 454.0f;
                                GLFrameRenderer.circleY = 462.0f;
                                GLFrameRenderer.circleR = 444.0f;
                                return;
                            } else {
                                return;
                            }
                        }
                    }
                case 10002:
                    SysApp.this.mHandler.post(SysApp.this.checkDevType);
                    return;
                case AppConfig.TCP_CONNECT_SUCCEED /*11001*/:
                    Log.i(SysApp.TAG, "======TCP TCP_CONNECT_SUCCEED");
                    return;
                case AppConfig.TCP_RECEIVE_DATA /*11002*/:
                    tmpStr = ETTool.BytesToHexString(msg.obj, 20);
                    if (!tmpStr.startsWith("4f4b") && SysApp.this.mSnapshotLen <= 0) {
                        if (tmpStr.startsWith("70686f746f")) {
                            SysApp.this.transMode = 0;
                            SysApp.this.sendBroadcast(new Intent(SysApp.REMOTE_PHOTO));
                            return;
                        } else if (tmpStr.startsWith("7265636f7264")) {
                            SysApp.this.transMode = 0;
                            SysApp.this.bFirstRecvPhot = false;
                            SysApp.this.sendBroadcast(new Intent(SysApp.REMOTE_RECORD));
                            return;
                        } else if (tmpStr.startsWith("6e6f6163740d0a")) {
                            SysApp.this.bFirstRecvPhot = false;
                            SysApp.this.transMode = 0;
                            return;
                        } else if (tmpStr.startsWith("7265636f7274")) {
                            SysApp.this.transMode = 0;
                            if (SysApp.this.bStreamGet) {
                                SysApp.this.bFirstRecvPhot = false;
                                if (!SysApp.this.mRemoteRecord) {
                                    SysApp.this.sendBroadcast(new Intent(SysApp.RECORD_START));
                                    SysApp.this.mRemoteRecord = true;
                                    return;
                                }
                                return;
                            }
                            return;
                        } else if (tmpStr.startsWith("73746f70")) {
                            SysApp.this.transMode = 0;
                            SysApp.this.bFirstRecvPhot = false;
                            if (SysApp.this.mRemoteRecord) {
                                SysApp.this.sendBroadcast(new Intent(SysApp.RECORD_END));
                                SysApp.this.mRemoteRecord = false;
                                return;
                            }
                            return;
                        } else if (tmpStr.startsWith("70686f7431")) {
                            SysApp.this.transMode = 0;
                            s = "phot1";
                            if (!(s.equals(SysApp.this.strShotName) || SysApp.this.bFirstRecvPhot)) {
                                SysApp.this.sendBroadcast(new Intent(SysApp.REMOTE_PHOTO));
                            }
                            SysApp.this.strShotName = s;
                            SysApp.this.bFirstRecvPhot = false;
                            return;
                        } else if (tmpStr.startsWith("70686f7432")) {
                            SysApp.this.transMode = 0;
                            s = "phot2";
                            if (!(s.equals(SysApp.this.strShotName) || SysApp.this.bFirstRecvPhot)) {
                                SysApp.this.sendBroadcast(new Intent(SysApp.REMOTE_PHOTO));
                            }
                            SysApp.this.strShotName = s;
                            SysApp.this.bFirstRecvPhot = false;
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                case AppConfig.TCP_CONNECT_ERROR /*11003*/:
                    Log.i(SysApp.TAG, "======TCP TCP_CONNECT_ERROR");
                    return;
                default:
                    return;
            }
        }
    }

    class C01372 extends BroadcastReceiver {
        private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        C01372() {
        }

        public void onReceive(Context context, Intent intent) {
            Log.i(SysApp.TAG, "updataReceiver:" + intent.getAction());
            if (intent.equals(CONNECTIVITY_CHANGE_ACTION)) {
                if (SysApp.this.checkNetworkConnection()) {
                    SysApp.this.mIPinfo = SysApp.this.getIpInfo();
                    return;
                }
                SysApp.this.mIPinfo = new int[3];
                if (SysApp.this.mUdpSer.isRun()) {
                    SysApp.this.mUdpSer.Stop();
                }
            } else if (intent.getAction().equals(CONNECTIVITY_CHANGE_ACTION)) {
                SysApp.this.restartTCP();
            } else if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                Log.i(LOG_TAG, "reason: " + reason);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    Log.i(LOG_TAG, SYSTEM_DIALOG_REASON_HOME_KEY);
                }
            } else {
                intent.equals("android.net.wifi.RSSI_CHANGED");
            }
        }
    }

    class C01383 implements Runnable {
        C01383() {
        }

        public void run() {
            SysApp.this.mIPinfo = SysApp.this.getIpInfo();
            if (!(SysApp.this.mIPinfo[0] == SysApp.this.lastIp[0] && SysApp.this.mIPinfo[1] == SysApp.this.lastIp[1])) {
                SysApp.this.lastIp[0] = SysApp.this.mIPinfo[0];
                SysApp.this.lastIp[1] = SysApp.this.mIPinfo[1];
                SysApp.this.mUdpSer.Stop();
                if (!(SysApp.this.mIPinfo[0] == 0 || SysApp.this.mIPinfo[1] == 0)) {
                    SysApp.this.mUdpSer.setAccesser(SysApp.this.mHandler);
                    SysApp.this.mUdpSer.Start();
                    try {
                        SysApp.this.mDevAddr = InetAddress.getByAddress(SysApp.long2byte(SysApp.this.mIPinfo[1]));
                    } catch (UnknownHostException e) {
                        System.out.print(e.toString());
                    }
                    PlayInfo.udpIpAddr = SysApp.this.getLocalAddrStr();
                    PlayInfo.RTSPUrl = SysApp.this.getLocalAddrStr();
                    PlayInfo.udpPort = SysApp.DEV_TCP_PORT2;
                    PlayInfo.playType = 1;
                }
            }
            if (SysApp.this.go2CheckSSID) {
                SysApp.this.mHandler.postDelayed(SysApp.this.checkAP, 1000);
            }
        }
    }

    class C01394 implements Runnable {
        C01394() {
        }

        public void run() {
            if (PlayInfo.udpDevType == 2) {
                if (PlayInfo.transMode != -1) {
                    SysApp.this.mWifiCfg = new WLANCfg(SysApp.this.mContext);
                    SysApp.this.mCurSSID = SysApp.this.mWifiCfg.getSSID();
                    SysApp.this.mHandler.postDelayed(SysApp.this.checkSSID, 1000);
                    SysApp.this.go2CheckSSID = false;
                    FHSDK.setPlayInfo(new PlayInfo());
                    return;
                }
                PlayInfo.transMode = -1;
                SysApp.this.writeUDPCmd(new byte[]{(byte) 15});
                SysApp.this.mHandler.postDelayed(SysApp.this.checkDevType, 200);
            } else if (PlayInfo.udpDevType != 0) {
                SysApp.this.mWifiCfg = new WLANCfg(SysApp.this.mContext);
                SysApp.this.mCurSSID = SysApp.this.mWifiCfg.getSSID();
                SysApp.this.mHandler.postDelayed(SysApp.this.checkSSID, 1000);
                SysApp.this.go2CheckSSID = false;
                FHSDK.setPlayInfo(new PlayInfo());
            } else {
                SysApp.this.writeUDPCmd(new byte[]{(byte) 15});
                SysApp.this.mHandler.postDelayed(SysApp.this.checkDevType, 200);
            }
        }
    }

    class C01405 implements Runnable {
        int checkCount = 0;

        C01405() {
        }

        public void run() {
        }
    }

    class C01416 implements Runnable {
        C01416() {
        }

        public void run() {
            if (PlayInfo.udpDevType == 2) {
                SysApp.this.udpSendTime();
            }
            SysApp.this.mHandler.postDelayed(SysApp.this.sendDevTime, 1000);
        }
    }

    class C01427 implements Runnable {
        C01427() {
        }

        public void run() {
            if (PlayInfo.transMode != 1) {
                try {
                    InetAddress mDevAddr = InetAddress.getByAddress(SysApp.long2byte(SysApp.this.mIPinfo[1]));
                    ByteBuffer buf = ByteBuffer.allocate(5);
                    buf.put((byte) 8);
                    buf.put(SysApp.long2byte(SysApp.this.mIPinfo[0]));
                    SysApp.this.mUdpSer.addCmdP(buf.array(), mDevAddr, 8080);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SysApp.this.mHandler.postDelayed(SysApp.this.sendActiveCmd, 1000);
            }
        }
    }

    class C01438 implements Runnable {
        C01438() {
        }

        public void run() {
            try {
                InetAddress mDevAddr = InetAddress.getByAddress(SysApp.long2byte(SysApp.this.mIPinfo[1]));
                ByteBuffer buf = ByteBuffer.allocate(5);
                buf.put((byte) 13);
                buf.put(SysApp.long2byte(SysApp.this.mIPinfo[0]));
                SysApp.this.mUdpSer.addCmdP(buf.array(), mDevAddr, 8080);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class C01449 implements Runnable {
        C01449() {
        }

        public void run() {
            try {
                InetAddress mDevAddr = InetAddress.getByAddress(SysApp.long2byte(SysApp.this.mIPinfo[1]));
                SysApp sysApp;
                byte[] bArr;
                if (-1 == SysApp.this.getTransMode() && SysApp.this.tcpSendCount < 10) {
                    if (PlayInfo.udpDevType == 4) {
                        SysApp.this.writeTCPCmd("remote\r\n");
                    } else {
                        sysApp = SysApp.this;
                        bArr = new byte[12];
                        bArr[1] = (byte) 1;
                        bArr[2] = (byte) 2;
                        bArr[3] = (byte) 3;
                        bArr[4] = (byte) 4;
                        bArr[5] = (byte) 5;
                        bArr[6] = (byte) 6;
                        bArr[7] = (byte) 7;
                        bArr[8] = (byte) 8;
                        bArr[9] = (byte) 9;
                        bArr[10] = (byte) 37;
                        bArr[11] = (byte) 37;
                        sysApp.writeTCPCmd(bArr);
                    }
                    sysApp = SysApp.this;
                    sysApp.tcpSendCount = sysApp.tcpSendCount + 1;
                    if (PlayInfo.transMode == 1) {
                        SysApp.this.mHandler.postDelayed(SysApp.this.sendCheckWorkCmd, 100);
                    }
                } else if (-1 == SysApp.this.getTransMode() || 1 == SysApp.this.getTransMode()) {
                    SysApp.this.mUdpSer.addCmdP(new byte[]{(byte) 37}, mDevAddr, 8080);
                    if (PlayInfo.transMode == 1) {
                        SysApp.this.mHandler.postDelayed(SysApp.this.sendCheckWorkCmd, 100);
                    }
                } else {
                    if (SysApp.this.getTransMode() == 0) {
                        if (PlayInfo.udpDevType == 4) {
                            SysApp.this.writeTCPCmd("remote\r\n");
                        } else {
                            sysApp = SysApp.this;
                            bArr = new byte[12];
                            bArr[1] = (byte) 1;
                            bArr[2] = (byte) 2;
                            bArr[3] = (byte) 3;
                            bArr[4] = (byte) 4;
                            bArr[5] = (byte) 5;
                            bArr[6] = (byte) 6;
                            bArr[7] = (byte) 7;
                            bArr[8] = (byte) 8;
                            bArr[9] = (byte) 9;
                            bArr[10] = (byte) 37;
                            bArr[11] = (byte) 37;
                            sysApp.writeTCPCmd(bArr);
                        }
                    }
                    if (PlayInfo.transMode == 1) {
                        SysApp.this.mHandler.postDelayed(SysApp.this.sendCheckWorkCmd, 100);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isVer210() {
        return this.bVer210;
    }

    public boolean isMirror() {
        return this.bMirror;
    }

    public void setMirror(boolean isMirror) {
        this.bMirror = isMirror;
    }

    public int getTransMode() {
        return this.transMode;
    }

    public boolean isPiratical() {
        return this.mIsPiratical;
    }

    public int getWifiRSSI() {
        return this.mWifiRSSI;
    }

    public boolean isOffLine() {
        return this.bOffLine;
    }

    public void setOffLine(boolean isOffLine) {
        this.bOffLine = isOffLine;
    }

    public void sendUDPEndCmd() {
        this.mHandler.post(this.sendEndCmd);
    }

    public void onCreate() {
        Log.e(TAG, "onCreate()");
        mMe = this;
        this.mConfig = new AppConfig(this);
        this.mHandler.post(this.checkAP);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        mFilter.addAction("android.net.wifi.RSSI_CHANGED");
        registerReceiver(this.updataReceiver, mFilter);
        this.mContext = this;
        createFilePath();
        super.onCreate();
    }

    public void onTerminate() {
        if (this.mUdpSer.isRun()) {
            this.mUdpSer.Stop();
        }
        if (this.mTcpSocket != null) {
            this.mTcpSocket.Stop();
        }
        unregisterReceiver(this.updataReceiver);
        System.exit(0);
        super.onTerminate();
    }

    public static Context getAppContext() {
        return mMe;
    }

    private void createFilePath() {
        String exSdPath = Environment.getExternalStorageDirectory().getPath();
        SAVE_DATA_PATH = "/" + this.mContext.getText(C0127R.string.app_name).toString() + "/";
        File vfile = new File(new StringBuilder(String.valueOf(exSdPath)).append(SAVE_DATA_PATH).toString());
        if (!vfile.exists()) {
            vfile.mkdir();
        }
        SAVE_PATH = vfile.getAbsolutePath() + "/";
        FHSDK.setShotPath(vfile.getAbsolutePath());
        Log.i(TAG, "SAVE_PATH:" + SAVE_PATH);
    }

    public static SysApp getMe() {
        return mMe;
    }

    public int[] getIPinfo() {
        return this.mIPinfo;
    }

    public UDPServer getUDPSer() {
        return this.mUdpSer;
    }

    public InetAddress getDevAddr() {
        return this.mDevAddr;
    }

    public String getDevAddrStr() {
        return long2ip(this.mIPinfo[1]);
    }

    public String getLocalAddrStr() {
        return long2ip(this.mIPinfo[0]);
    }

    public void writeUDPCmd(byte[] data) {
        this.mUdpSer.addCmdP(data, this.mDevAddr, 8080);
    }

    public int getDevType() {
        return this.mDevType;
    }

    public void writeTCPCmd(byte[] data) {
        if (this.mTcpSocket != null) {
            this.mTcpSocket.SendData(data);
        }
    }

    public void writeTCPCmd(String data) {
        if (this.mTcpSocket != null) {
            this.mTcpSocket.SendData(data);
        }
    }

    public void restartTCP() {
        if (this.mTcpSocket != null) {
            this.mTcpSocket.Stop();
            this.mTcpSocket = null;
        }
        log.m1e("getDevAddrStr() = " + getDevAddrStr());
        if (PlayInfo.udpDevType == 1 || PlayInfo.udpDevType == 2) {
            this.mTcpSocket = new TCPSocket(getDevAddrStr(), DEV_TCP_PORT2, this.mHandler);
            this.mTcpSocket.Start();
        } else if (PlayInfo.udpDevType == 4 || PlayInfo.udpDevType == 5) {
            this.mTcpSocket = new TCPSocket(getDevAddrStr(), 8080, this.mHandler);
            this.mTcpSocket.Start();
        }
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService("connectivity");
        NetworkInfo wifi = connMgr.getNetworkInfo(1);
        NetworkInfo mobile = connMgr.getNetworkInfo(0);
        if (wifi != null && wifi.isAvailable()) {
            return true;
        }
        if (mobile == null || !mobile.isAvailable()) {
            return false;
        }
        return true;
    }

    public int[] getIpInfo() {
        int[] reData = new int[3];
        WifiManager wm = (WifiManager) getSystemService("wifi");
        reData[0] = wm.getConnectionInfo().getIpAddress();
        DhcpInfo di = wm.getDhcpInfo();
        reData[1] = di.gateway;
        reData[2] = di.netmask;
        Log.i("Airactivity", "IP val:" + reData[0] + " IP:" + long2ip(reData[0]) + "  Way val:" + reData[1] + "  Way:" + long2ip(reData[1]));
        return reData;
    }

    public static String long2ip(int ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(ip & 255));
        sb.append('.');
        sb.append(String.valueOf((ip >> 8) & 255));
        sb.append('.');
        sb.append(String.valueOf((ip >> 16) & 255));
        sb.append('.');
        sb.append(String.valueOf((ip >> 24) & 255));
        return sb.toString();
    }

    public static byte[] long2byte(int ip) {
        return new byte[]{(byte) (ip & 255), (byte) ((ip >> 8) & 255), (byte) ((ip >> 16) & 255), (byte) ((ip >> 24) & 255)};
    }

    public void StartActive(boolean val) {
        if (val) {
            this.mHandler.removeCallbacks(this.sendActiveCmd);
            this.mHandler.postDelayed(this.sendActiveCmd, 10);
            return;
        }
        this.mHandler.removeCallbacks(this.sendActiveCmd);
    }

    public void StartCheckWork(boolean val) {
        this.tcpSendCount = 0;
        if (val) {
            this.mHandler.removeCallbacks(this.sendCheckWorkCmd);
            this.mHandler.postDelayed(this.sendCheckWorkCmd, 10);
            return;
        }
        this.mHandler.removeCallbacks(this.sendCheckWorkCmd);
    }

    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "System is running low on memory");
    }

    public static Resources getAppResources() {
        if (mMe == null) {
            return null;
        }
        return mMe.getResources();
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
        Log.d("text", sdcard_path);
        return sdcard_path;
    }

    public boolean isSupportMediaCodecHardDecoder() {
        boolean isHardcode = false;
        InputStream inFile = null;
        try {
            inFile = new FileInputStream(new File("/system/etc/media_codecs.xml"));
        } catch (Exception e) {
        }
        if (inFile != null) {
            try {
                XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
                xmlPullParser.setInput(inFile, "UTF-8");
                for (int eventType = xmlPullParser.getEventType(); eventType != 1; eventType = xmlPullParser.next()) {
                    String tagName = xmlPullParser.getName();
                    switch (eventType) {
                        case 2:
                            if (!"MediaCodec".equals(tagName)) {
                                break;
                            }
                            String componentName = xmlPullParser.getAttributeValue(0);
                            if (componentName.startsWith("OMX.") && !componentName.startsWith("OMX.google.")) {
                                isHardcode = true;
                                break;
                            }
                        default:
                            break;
                    }
                }
            } catch (Exception e2) {
            }
        }
        return isHardcode;
    }

    public AppConfig getConfig() {
        return this.mConfig;
    }

    private boolean checkExpire(String dateStr) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
            Date curDate = new Date(System.currentTimeMillis());
            if (curDate.getYear() > date.getYear() && curDate.getMonth() > date.getMonth() && curDate.getDate() > date.getDate()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void udpStartPhoto() {
        Date d = new Date();
        int time1 = ((d.getHours() * 2048) + (d.getMinutes() * 32)) + (d.getSeconds() / 2);
        int data1 = (((year - 1980) * 512) + (month * 32)) + day;
        writeUDPCmd(new byte[]{(byte) 17, (byte) buf[year / 10], (byte) buf[(d.getYear() % 100) % 10], (byte) buf[d.getMonth()], (byte) buf[d.getDay()], (byte) buf[d.getHours()], (byte) buf[minite / 10], (byte) buf[d.getMinutes() % 10], (byte) asciiTable[d.getSeconds() / 2], (byte) 74, (byte) 80, (byte) 71, (byte) ((data1 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (data1 & 255), (byte) ((time1 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (time1 & 255)});
    }

    public void tcpStartPhoto() {
        Date d = new Date();
        int time1 = ((d.getHours() * 2048) + (d.getMinutes() * 32)) + (d.getSeconds() / 2);
        int data1 = (((year - 1980) * 512) + (month * 32)) + day;
        writeTCPCmd(new byte[]{(byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 17, (byte) 17, (byte) buf[year / 10], (byte) buf[(d.getYear() % 100) % 10], (byte) buf[d.getMonth()], (byte) buf[d.getDay()], (byte) buf[d.getHours()], (byte) buf[minite / 10], (byte) buf[d.getMinutes() % 10], (byte) asciiTable[d.getSeconds() / 2], (byte) 74, (byte) 80, (byte) 71, (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & data1) >> 8), (byte) (data1 & 255), (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & time1) >> 8), (byte) (time1 & 255)});
    }

    public void udpStartRecord() {
        Date d = new Date();
        int time1 = ((d.getHours() * 2048) + (d.getMinutes() * 32)) + (d.getSeconds() / 2);
        int data1 = (((year - 1980) * 512) + (month * 32)) + day;
        writeUDPCmd(new byte[]{(byte) 18, (byte) buf[year / 10], (byte) buf[(d.getYear() % 100) % 10], (byte) buf[d.getMonth()], (byte) buf[d.getDay()], (byte) buf[d.getHours()], (byte) buf[minite / 10], (byte) buf[d.getMinutes() % 10], (byte) asciiTable[d.getSeconds() / 2], (byte) 65, (byte) 86, (byte) 73});
    }

    public void tcpStartRecord() {
        Date d = new Date();
        int time1 = ((d.getHours() * 2048) + (d.getMinutes() * 32)) + (d.getSeconds() / 2);
        int data1 = (((year - 1980) * 512) + (month * 32)) + day;
        writeTCPCmd(new byte[]{(byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 18, (byte) 18, (byte) buf[year / 10], (byte) buf[(d.getYear() % 100) % 10], (byte) buf[d.getMonth()], (byte) buf[d.getDay()], (byte) buf[d.getHours()], (byte) buf[minite / 10], (byte) buf[d.getMinutes() % 10], (byte) asciiTable[d.getSeconds() / 2], (byte) 65, (byte) 86, (byte) 73});
    }

    public void udpStopRecord() {
        Date d = new Date();
        int year = d.getYear() % 100;
        int month = d.getMonth();
        int day = d.getDay();
        int time1 = ((d.getHours() * 2048) + (d.getMinutes() * 32)) + (d.getSeconds() / 2);
        int data1 = (((year - 1980) * 512) + (month * 32)) + day;
        writeUDPCmd(new byte[]{(byte) 19, (byte) ((data1 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (data1 & 255), (byte) ((time1 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) (time1 & 255)});
    }

    public void tcpStopRecord() {
        Date d = new Date();
        int year = d.getYear() % 100;
        int month = d.getMonth();
        int day = d.getDay();
        int time1 = ((d.getHours() * 2048) + (d.getMinutes() * 32)) + (d.getSeconds() / 2);
        int data1 = (((year - 1980) * 512) + (month * 32)) + day;
        writeTCPCmd(new byte[]{(byte) 0, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8, (byte) 9, (byte) 19, (byte) 19, (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & data1) >> 8), (byte) (data1 & 255), (byte) ((MotionEventCompat.ACTION_POINTER_INDEX_MASK & time1) >> 8), (byte) (time1 & 255)});
    }

    public void udpSendTime() {
        Date d = new Date();
        int year = d.getYear() + 1900;
        int month = d.getMonth() + 1;
        int date = d.getDate();
        int day = d.getDay();
        int hour = d.getHours();
        int minite = d.getMinutes();
        int second = d.getSeconds();
        writeUDPCmd(new byte[]{(byte) 38, (byte) (year & 255), (byte) ((year & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((year & 16711680) >> 16), (byte) ((year & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) (month & 255), (byte) ((month & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((month & 16711680) >> 16), (byte) ((month & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) (date & 255), (byte) ((date & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((date & 16711680) >> 16), (byte) ((date & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) (day & 255), (byte) ((day & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((day & 16711680) >> 16), (byte) ((day & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) (hour & 255), (byte) ((hour & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((hour & 16711680) >> 16), (byte) ((hour & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) (minite & 255), (byte) ((minite & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((minite & 16711680) >> 16), (byte) ((minite & ViewCompat.MEASURED_STATE_MASK) >> 24), (byte) (second & 255), (byte) ((second & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8), (byte) ((second & 16711680) >> 16), (byte) ((second & ViewCompat.MEASURED_STATE_MASK) >> 24)});
    }

    public void rtspSendTime() {
        Date d = new Date();
        String setDate = "date -s \"" + (d.getYear() + 1900) + "-" + (d.getMonth() + 1) + "-" + d.getDate() + " " + d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds() + "\"";
        ByteBuffer buf = ByteBuffer.allocate(29);
        buf.put((byte) 38);
        buf.put(setDate.getBytes());
        writeUDPCmd(buf.array());
    }

    public void getVersion() {
        writeUDPCmd(new byte[]{(byte) 40});
    }

    public void requestIFrame() {
        this.mHandler.post(this.sendRequestCmd);
    }
}
