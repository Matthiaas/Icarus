package com.fh.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioTrack;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import java.util.Arrays;

public class SDLActivity extends Activity {
    static final int COMMAND_CHANGE_TITLE = 1;
    static final int COMMAND_TEXTEDIT_HIDE = 3;
    static final int COMMAND_UNUSED = 2;
    protected static final int COMMAND_USER = 32768;
    private static final String TAG = "SDL";
    protected static AudioTrack mAudioTrack;
    public static boolean mExitCalledFromJava;
    public static boolean mHasFocus;
    public static boolean mIsPaused;
    public static boolean mIsSurfaceReady;
    protected static SDLJoystickHandler mJoystickHandler;
    protected static ViewGroup mLayout;
    protected static Thread mSDLThread;
    protected static SDLActivity mSingleton;
    protected static SDLSurface mSurface;
    protected static View mTextEdit;
    Handler commandHandler = new SDLCommandHandler();

    protected static class SDLCommandHandler extends Handler {
        protected SDLCommandHandler() {
        }

        public void handleMessage(Message msg) {
            Context context = SDLActivity.getContext();
            if (context == null) {
                Log.e(SDLActivity.TAG, "error handling message, getContext() returned null");
                return;
            }
            switch (msg.arg1) {
                case 1:
                    if (context instanceof Activity) {
                        ((Activity) context).setTitle((String) msg.obj);
                        return;
                    } else {
                        Log.e(SDLActivity.TAG, "error handling message, getContext() returned no Activity");
                        return;
                    }
                case 3:
                    if (SDLActivity.mTextEdit != null) {
                        SDLActivity.mTextEdit.setVisibility(8);
                        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(SDLActivity.mTextEdit.getWindowToken(), 0);
                        return;
                    }
                    return;
                default:
                    if ((context instanceof SDLActivity) && !((SDLActivity) context).onUnhandledMessage(msg.arg1, msg.obj)) {
                        Log.e(SDLActivity.TAG, "error handling message, command is " + msg.arg1);
                        return;
                    }
                    return;
            }
        }
    }

    static class ShowTextInputTask implements Runnable {
        static final int HEIGHT_PADDING = 15;
        public int f1h;
        public int f2w;
        public int f3x;
        public int f4y;

        public ShowTextInputTask(int x, int y, int w, int h) {
            this.f3x = x;
            this.f4y = y;
            this.f2w = w;
            this.f1h = h;
        }

        public void run() {
            LayoutParams params = new LayoutParams(this.f2w, this.f1h + 15, this.f3x, this.f4y);
            if (SDLActivity.mTextEdit == null) {
                SDLActivity.mTextEdit = new DummyEdit(SDLActivity.getContext());
                SDLActivity.mLayout.addView(SDLActivity.mTextEdit, params);
            } else {
                SDLActivity.mTextEdit.setLayoutParams(params);
            }
            SDLActivity.mTextEdit.setVisibility(0);
            SDLActivity.mTextEdit.requestFocus();
            ((InputMethodManager) SDLActivity.getContext().getSystemService("input_method")).showSoftInput(SDLActivity.mTextEdit, 0);
        }
    }

    public static native int nativeAddJoystick(int i, String str, int i2, int i3, int i4, int i5, int i6);

    public static native void nativeFlipBuffers();

    public static native void nativeInit();

    public static native void nativeLowMemory();

    public static native void nativePause();

    public static native void nativeQuit();

    public static native int nativeRemoveJoystick(int i);

    public static native void nativeResume();

    public static native void onNativeAccel(float f, float f2, float f3);

    public static native void onNativeHat(int i, int i2, int i3, int i4);

    public static native void onNativeJoy(int i, int i2, float f);

    public static native void onNativeKeyDown(int i);

    public static native void onNativeKeyUp(int i);

    public static native void onNativeKeyboardFocusLost();

    public static native int onNativePadDown(int i, int i2);

    public static native int onNativePadUp(int i, int i2);

    public static native void onNativeResize(int i, int i2, int i3);

    public static native void onNativeSurfaceChanged();

    public static native void onNativeSurfaceDestroyed();

    public static native void onNativeTouch(int i, int i2, int i3, float f, float f2, float f3);

    static {
        System.loadLibrary(TAG);
        System.loadLibrary("main");
    }

    public static void initialize() {
        mSingleton = null;
        mSurface = null;
        mTextEdit = null;
        mLayout = null;
        mJoystickHandler = null;
        mSDLThread = null;
        mAudioTrack = null;
        mExitCalledFromJava = false;
        mIsPaused = false;
        mIsSurfaceReady = false;
        mHasFocus = true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate():" + mSingleton);
        super.onCreate(savedInstanceState);
        initialize();
        mSingleton = this;
        mSurface = new SDLSurface(getApplication());
        if (VERSION.SDK_INT >= 12) {
            mJoystickHandler = new SDLJoystickHandler_API12();
        } else {
            mJoystickHandler = new SDLJoystickHandler();
        }
        mLayout = new AbsoluteLayout(this);
        mLayout.addView(mSurface);
        setContentView(mLayout);
    }

    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();
        handlePause();
    }

    protected void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();
        handleResume();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.v(TAG, "onWindowFocusChanged(): " + hasFocus);
        mHasFocus = hasFocus;
        if (hasFocus) {
            handleResume();
        }
    }

    public void onLowMemory() {
        Log.v(TAG, "onLowMemory()");
        super.onLowMemory();
        nativeLowMemory();
    }

    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        mExitCalledFromJava = true;
        nativeQuit();
        if (mSDLThread != null) {
            try {
                mSDLThread.join();
            } catch (Exception e) {
                Log.v(TAG, "Problem stopping thread: " + e);
            }
            mSDLThread = null;
        }
        super.onDestroy();
        initialize();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == 25 || keyCode == 24 || keyCode == 27 || keyCode == 168 || keyCode == 169) {
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public static void handlePause() {
        if (!mIsPaused && mIsSurfaceReady) {
            mIsPaused = true;
            nativePause();
            mSurface.enableSensor(1, false);
        }
    }

    public static void handleResume() {
        if (mIsPaused && mIsSurfaceReady && mHasFocus) {
            mIsPaused = false;
            nativeResume();
            mSurface.enableSensor(1, true);
        }
    }

    public static void handleNativeExit() {
        mSDLThread = null;
        if (mSingleton != null) {
            mSingleton.finish();
        }
    }

    protected boolean onUnhandledMessage(int command, Object param) {
        return false;
    }

    boolean sendCommand(int command, Object data) {
        Message msg = this.commandHandler.obtainMessage();
        msg.arg1 = command;
        msg.obj = data;
        return this.commandHandler.sendMessage(msg);
    }

    public static void flipBuffers() {
        nativeFlipBuffers();
    }

    public static boolean setActivityTitle(String title) {
        return mSingleton.sendCommand(1, title);
    }

    public static boolean sendMessage(int command, int param) {
        return mSingleton.sendCommand(command, Integer.valueOf(param));
    }

    public static Context getContext() {
        return mSingleton;
    }

    public Object getSystemServiceFromUiThread(final String name) {
        final Object lock = new Object();
        final Object[] results = new Object[2];
        synchronized (lock) {
            runOnUiThread(new Runnable() {
                public void run() {
                    synchronized (lock) {
                        results[0] = SDLActivity.this.getSystemService(name);
                        results[1] = Boolean.TRUE;
                        lock.notify();
                    }
                }
            });
            if (results[1] == null) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return results[0];
    }

    public static boolean showTextInput(int x, int y, int w, int h) {
        return mSingleton.commandHandler.post(new ShowTextInputTask(x, y, w, h));
    }

    public static Surface getNativeSurface() {
        return mSurface.getNativeSurface();
    }

    public static int audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        int channelConfig;
        int audioFormat;
        int i;
        String str;
        if (isStereo) {
            channelConfig = 3;
        } else {
            channelConfig = 2;
        }
        if (is16Bit) {
            audioFormat = 2;
        } else {
            audioFormat = 3;
        }
        if (isStereo) {
            i = 2;
        } else {
            i = 1;
        }
        int frameSize = i * (is16Bit ? 2 : 1);
        String str2 = TAG;
        StringBuilder append = new StringBuilder("SDL audio: wanted ").append(isStereo ? "stereo" : "mono").append(" ");
        if (is16Bit) {
            str = "16-bit";
        } else {
            str = "8-bit";
        }
        Log.v(str2, append.append(str).append(" ").append(((float) sampleRate) / 1000.0f).append("kHz, ").append(desiredFrames).append(" frames buffer").toString());
        desiredFrames = Math.max(desiredFrames, ((AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat) + frameSize) - 1) / frameSize);
        if (mAudioTrack == null) {
            mAudioTrack = new AudioTrack(3, sampleRate, channelConfig, audioFormat, desiredFrames * frameSize, 1);
            if (mAudioTrack.getState() != 1) {
                Log.e(TAG, "Failed during initialization of Audio Track");
                mAudioTrack = null;
                return -1;
            }
            mAudioTrack.play();
        }
        Log.v(TAG, "SDL audio: got " + (mAudioTrack.getChannelCount() >= 2 ? "stereo" : "mono") + " " + (mAudioTrack.getAudioFormat() == 2 ? "16-bit" : "8-bit") + " " + (((float) mAudioTrack.getSampleRate()) / 1000.0f) + "kHz, " + desiredFrames + " frames buffer");
        return 0;
    }

    public static void audioWriteShortBuffer(short[] buffer) {
        int i = 0;
        while (i < buffer.length) {
            int result = 0;
            if (mAudioTrack != null) {
                result = mAudioTrack.write(buffer, i, buffer.length - i);
            }
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    public static void audioWriteByteBuffer(byte[] buffer) {
        int i = 0;
        while (i < buffer.length) {
            int result = 0;
            if (mAudioTrack != null) {
                result = mAudioTrack.write(buffer, i, buffer.length - i);
            }
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    public static void audioQuit() {
        Log.v(TAG, "audioQuit()");
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack = null;
        }
    }

    @SuppressLint({"NewApi"})
    public static int[] inputGetInputDeviceIds(int sources) {
        int[] ids = InputDevice.getDeviceIds();
        int[] filtered = new int[ids.length];
        int used = 0;
        for (int device : ids) {
            InputDevice device2 = InputDevice.getDevice(device);
            if (!(device2 == null || (device2.getSources() & sources) == 0)) {
                int used2 = used + 1;
                filtered[used] = device2.getId();
                used = used2;
            }
        }
        return Arrays.copyOf(filtered, used);
    }

    public static boolean handleJoystickMotionEvent(MotionEvent event) {
        return mJoystickHandler.handleMotionEvent(event);
    }

    public static void pollInputDevices() {
        if (mSDLThread != null) {
            mJoystickHandler.pollInputDevices();
        }
    }
}
