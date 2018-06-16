package com.fh.lib;

import android.graphics.Rect;
import com.fh.lib.Define.BCSS;
import com.fh.lib.Define.CbDataInterface;
import com.fh.lib.Define.DevSearch;
import com.fh.lib.Define.DeviceTime;
import com.fh.lib.Define.IpConfig;
import com.fh.lib.Define.PBRecTime;
import com.fh.lib.Define.PicSearch;
import com.fh.lib.Define.Picture;
import com.fh.lib.Define.RecSearch;
import com.fh.lib.Define.Record;
import com.fh.lib.Define.SDCardFormat;
import com.fh.lib.Define.SDCardInfo;
import com.fh.lib.Define.SerialDataCallBackInterface;
import com.fh.lib.Define.StreamDataCallBackInterface;
import com.fh.lib.Define.VideoEncode;
import com.fh.lib.Define.WifiConfig;
import com.fh.lib.Define.YUVDataCallBackInterface;

public class FHSDK {
    public static final int DECODE_TYPE_FFMPEG2OPENGL = 0;
    public static final int DECODE_TYPE_FFMPEG2SDL = 2;
    public static final int DECODE_TYPE_MEDIACODEC2GLVIEW = 4;
    public static final int DECODE_TYPE_MEDIACODEC2OPENGL = 1;
    public static final int DECODE_TYPE_MEDIACODEC2SDL = 3;
    public static final int DEVICE_TYPE_3518E = 4;
    public static final int DEVICE_TYPE_EYE = 6;
    public static final int DEVICE_TYPE_FH8610 = 1;
    public static final int DEVICE_TYPE_FH8620 = 2;
    public static final int DEVICE_TYPE_FH8810 = 3;
    public static final int DEVICE_TYPE_GM8136 = 5;
    public static final int DEVICE_TYPE_MJVGA = 7;
    public static final int PLAY_TYPE_LOCATE_PLAYBACK = 3;
    public static final int PLAY_TYPE_MJVGA = 5;
    public static final int PLAY_TYPE_PREVIEW = 0;
    public static final int PLAY_TYPE_REMOTE_PLAYBACK = 2;
    public static final int PLAY_TYPE_RTSP = 4;
    public static final int PLAY_TYPE_UDP = 1;

    public static native boolean FHclear();

    public static native boolean FHdraw(int i);

    public static native int FHinit(int i, int i2);

    public static native byte[] FHsnapshot(int i, int i2, int i3, int i4);

    public static native boolean FHunInit(int i);

    public static native boolean FHupdate(int i, byte[] bArr, int i2, int i3);

    public static native boolean FHupdate2(int i, byte[] bArr, byte[] bArr2, byte[] bArr3, int i2, int i3);

    public static native boolean FHviewport(int i, int i2, int i3, int i4);

    public static native boolean apiCleanup();

    public static native boolean apiInit();

    public static native boolean audioInit();

    public static native boolean bind(int i, int i2);

    public static native boolean clear();

    public static native boolean closeAudio(int i);

    public static native boolean closeSearchPicture(int i);

    public static native boolean closeSearchRecord(int i);

    public static native boolean continuePBPlay();

    public static native int createBuffer(int i);

    public static native int createPicDownload(int i, Picture picture);

    public static native int createWindow(int i);

    public static native boolean destoryPicDownload(int i);

    public static native int destroyBuffer(int i);

    public static native boolean destroyWindow(int i);

    public static native boolean draw(int i);

    public static native boolean expandLookAt(int i, float f);

    public static native boolean eyeLookAt(int i, float f, float f2, float f3);

    public static native boolean frameParse(int i, byte[] bArr, int i2);

    public static native int getConvertProgress(int i);

    public static native long getCurrentPts();

    public static native int getDevStatus();

    public static native int getDeviceFlag(int i);

    public static native boolean getDeviceTime(int i, DeviceTime deviceTime);

    public static native int getDisplayMode(int i);

    public static native boolean getEncodeVideoConfig(int i, int i2, VideoEncode videoEncode);

    public static native int getFieldOfView(int i);

    public static native boolean getIPConfig(int i, IpConfig ipConfig);

    public static native int getImagingType(int i);

    public static native boolean getInterruptFlag();

    public static native int getMDAlarm();

    public static native float getMaxHDegress(int i);

    public static native float getMaxVDegress(int i);

    public static native float getMaxZDepth(int i);

    public static native float getMinHDegress(int i);

    public static native float getMinVDegress(int i);

    public static native boolean getRealAudioState();

    public static native int getRecPlayProgress();

    public static native boolean getRecPlayTimeInfo(PBRecTime pBRecTime);

    public static native boolean getRemoteRecordState(int i);

    public static native boolean getSDCardFormatState(SDCardFormat sDCardFormat);

    public static native boolean getSDCardInfo(int i, SDCardInfo sDCardInfo);

    public static native int getTalkUnitSize(int i);

    public static native float getVerticalCutRatio(int i);

    public static native boolean getVideoBCSS(int i, BCSS bcss);

    public static native float getViewAngle(int i);

    public static native boolean getWifiConfig(int i, WifiConfig wifiConfig);

    public static native boolean init(int i, int i2);

    public static native boolean is3DMode();

    public static native boolean isBind(int i);

    public static native boolean jumpPlayBack(long j);

    public static native boolean loadSDCard(int i);

    public static native boolean locateContinuePBPlay();

    public static native boolean locateJumpPlayBack(int i);

    public static native boolean locatePausePBPlay();

    public static native int login(String str, int i, String str2, String str3);

    public static native boolean logout(int i);

    public static native boolean mirrorCtrl(int i, int i2);

    public static native boolean openAudio(int i);

    public static native boolean pausePBPlay();

    public static native boolean playFrame();

    public static native void printScreen(String str);

    public static native boolean registerDevNotifyFun();

    public static native boolean registerDevStateFun();

    public static native void registerNotifyCallBack(CbDataInterface cbDataInterface);

    public static native void registerStreamDataCallBack(StreamDataCallBackInterface streamDataCallBackInterface);

    public static native void registerUpdateCallBack(YUVDataCallBackInterface yUVDataCallBackInterface);

    public static native boolean resetDev(int i);

    public static native boolean resetEyeView(int i);

    public static native boolean resetStandardCircle(int i);

    public static native boolean restartDev(int i);

    public static native boolean saveBMP(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2);

    public static native boolean saveDevConfig(int i);

    public static native boolean searchCleanup();

    public static native int searchDev();

    public static native boolean searchDevClose(int i);

    public static native boolean searchInit();

    public static native boolean searchNextDev(int i, DevSearch devSearch);

    public static native boolean searchNextPicture(int i, Picture picture);

    public static native boolean searchNextRecord(int i, Record record);

    public static native int searchPicture(int i, PicSearch picSearch);

    public static native int searchRecord(int i, RecSearch recSearch);

    public static native boolean send2Sdl(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2);

    public static native boolean sendSerial(int i, byte[] bArr, int i2);

    public static native boolean sendTalkData(byte[] bArr, int i, int i2, int i3);

    public static native boolean sendToSerialPort(int i, int i2, int i3, byte[] bArr, int i4);

    public static native void set3DMode(boolean z);

    public static native boolean setCryptKey(String str);

    public static native boolean setDebugMode(int i, byte[] bArr, int i2, int i3);

    public static native boolean setDevName(int i, String str);

    public static native boolean setDeviceTime(int i, DeviceTime deviceTime);

    public static native boolean setEncodeVideoConfig(int i, int i2, VideoEncode videoEncode);

    public static native boolean setFieldOfView(int i, int i2);

    public static native boolean setIPConfig(int i, IpConfig ipConfig);

    public static native boolean setImagingType(int i, int i2);

    public static native boolean setLocatePBSpeed(int i);

    public static native boolean setPBSpeed(int i);

    public static native int setPlayInfo(PlayInfo playInfo);

    public static native void setShotOn();

    public static native void setShotPath(String str);

    public static native boolean setShowRect(Rect rect, boolean z);

    public static native boolean setStandardCircle(int i, float f, float f2, float f3);

    public static native boolean setVerticalCutRatio(int i, float f);

    public static native boolean setVideoBCSS(int i, BCSS bcss);

    public static native boolean setWifiConfig(int i, WifiConfig wifiConfig);

    public static native boolean setWifiSpeed(int i);

    public static native boolean shot(int i, String str, boolean z);

    public static native byte[] snapshot(int i, int i2, int i3, int i4);

    public static native int startConvertRecFormat(String str, String str2);

    public static native boolean startLocalRecord(String str);

    public static native boolean startLocalRecordMP4(int i, int i2, String str);

    public static native boolean startLocalRecordMP4Ex(int i, int i2, int i3, int i4, String str);

    public static native boolean startPlay();

    public static native boolean startRemoteRecord(int i);

    public static native boolean startSDCardFormat(int i, int i2);

    public static native int startSerial(int i, int i2, int i3, SerialDataCallBackInterface serialDataCallBackInterface);

    public static native int startSerialEx(int i, int i2, int i3, int i4, boolean z, SerialDataCallBackInterface serialDataCallBackInterface);

    public static native boolean startTalk(int i);

    public static native boolean stopConvertRecFormat(int i);

    public static native boolean stopLocalRecord();

    public static native boolean stopLocalRecordMP4();

    public static native boolean stopLocalRecordMP4Ex();

    public static native boolean stopPlay();

    public static native boolean stopRemoteRecord(int i);

    public static native boolean stopSDCardFormat();

    public static native boolean stopSerial(int i);

    public static native boolean stopTalk();

    public static native boolean testWifiConfig(int i, WifiConfig wifiConfig);

    public static native String timeConvert(int i, long j);

    public static native boolean unInit();

    public static native boolean unLoadSDCard(int i);

    public static native boolean unbind(int i);

    public static native boolean update(int i, byte[] bArr, int i2, int i3);

    public static native void updateFlyinfo(byte[] bArr, int i);

    public static native void updateNewFlyinfo(int i);

    public static native boolean viewport(int i, int i2, int i3, int i4);

    public static native boolean yuv420sp2yuv(byte[] bArr, int i, int i2, byte[] bArr2, byte[] bArr3, byte[] bArr4);

    public static native boolean yuv420sp2yuv420p(byte[] bArr, int i, int i2, byte[] bArr2);

    static {
        System.loadLibrary("FHDEV_Discover");
        System.loadLibrary("FHDEV_Net");
        System.loadLibrary("FHMP4");
        System.loadLibrary("main");
    }
}
