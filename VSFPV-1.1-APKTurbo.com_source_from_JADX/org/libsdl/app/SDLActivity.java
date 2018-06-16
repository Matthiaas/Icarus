package org.libsdl.app;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SDLActivity extends Activity {
    static final int COMMAND_CHANGE_TITLE = 1;
    static final int COMMAND_SET_KEEP_SCREEN_ON = 5;
    static final int COMMAND_TEXTEDIT_HIDE = 3;
    static final int COMMAND_UNUSED = 2;
    protected static final int COMMAND_USER = 32768;
    private static final String TAG = "SDL";
    protected static AudioRecord mAudioRecord;
    protected static AudioTrack mAudioTrack;
    public static boolean mBrokenLibraries;
    public static boolean mExitCalledFromJava;
    public static boolean mHasFocus;
    public static boolean mIsPaused;
    public static boolean mIsSurfaceReady;
    protected static SDLJoystickHandler mJoystickHandler;
    protected static ViewGroup mLayout;
    protected static Thread mSDLThread;
    public static boolean mSeparateMouseAndTouch;
    protected static SDLActivity mSingleton;
    protected static SDLSurface mSurface;
    protected static View mTextEdit;
    Handler commandHandler = new SDLCommandHandler();
    protected int dialogs = 0;
    private Object expansionFile;
    private Method expansionFileMethod;
    protected final int[] messageboxSelection = new int[1];

    class C01481 implements OnClickListener {
        C01481() {
        }

        public void onClick(DialogInterface dialog, int id) {
            SDLActivity.mSingleton.finish();
        }
    }

    class C01514 implements OnDismissListener {
        C01514() {
        }

        public void onDismiss(DialogInterface unused) {
            synchronized (SDLActivity.this.messageboxSelection) {
                SDLActivity.this.messageboxSelection.notify();
            }
        }
    }

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
                        SDLActivity.mTextEdit.setLayoutParams(new LayoutParams(0, 0));
                        ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(SDLActivity.mTextEdit.getWindowToken(), 0);
                        return;
                    }
                    return;
                case 5:
                    Window window = ((Activity) context).getWindow();
                    if (window == null) {
                        return;
                    }
                    if (!(msg.obj instanceof Integer) || ((Integer) msg.obj).intValue() == 0) {
                        window.clearFlags(128);
                        return;
                    } else {
                        window.addFlags(128);
                        return;
                    }
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
        public int f18h;
        public int f19w;
        public int f20x;
        public int f21y;

        public ShowTextInputTask(int x, int y, int w, int h) {
            this.f20x = x;
            this.f21y = y;
            this.f19w = w;
            this.f18h = h;
        }

        public void run() {
            LayoutParams params = new LayoutParams(this.f19w, this.f18h + 15);
            params.leftMargin = this.f20x;
            params.topMargin = this.f21y;
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

    public static native String nativeGetHint(String str);

    public static native int nativeInit(Object obj);

    public static native void nativeLowMemory();

    public static native void nativePause();

    public static native void nativeQuit();

    public static native int nativeRemoveJoystick(int i);

    public static native void nativeResume();

    public static native void nativeSeek(int i);

    public static native void nativeTogglePlayPause();

    public static native void nativeToggleScale();

    public static native void onNativeAccel(float f, float f2, float f3);

    public static native void onNativeDropFile(String str);

    public static native void onNativeHat(int i, int i2, int i3, int i4);

    public static native void onNativeJoy(int i, int i2, float f);

    public static native void onNativeKeyDown(int i);

    public static native void onNativeKeyUp(int i);

    public static native void onNativeKeyboardFocusLost();

    public static native void onNativeMouse(int i, int i2, float f, float f2);

    public static native int onNativePadDown(int i, int i2);

    public static native int onNativePadUp(int i, int i2);

    public static native void onNativeResize(int i, int i2, int i3, float f);

    public static native void onNativeSurfaceChanged();

    public static native void onNativeSurfaceDestroyed();

    public static native void onNativeTouch(int i, int i2, int i3, float f, float f2, float f3);

    public static synchronized boolean isRunning() {
        boolean z;
        synchronized (SDLActivity.class) {
            z = mSingleton != null;
        }
        return z;
    }

    protected String[] getLibraries() {
        return new String[]{"SDL2", "avcodec-56", "avformat-56", "avfilter-5", "avutil-54", "swresample-1", "swscale-3", "SDLmain"};
    }

    public void loadLibraries() {
        for (String lib : getLibraries()) {
            System.loadLibrary(lib);
        }
    }

    protected String[] getArguments() {
        return new String[0];
    }

    public static synchronized void initialize() {
        synchronized (SDLActivity.class) {
            mSingleton = null;
            mSurface = null;
            mTextEdit = null;
            mLayout = null;
            mJoystickHandler = null;
            mSDLThread = null;
            mAudioTrack = null;
            mAudioRecord = null;
            mExitCalledFromJava = false;
            mBrokenLibraries = false;
            mIsPaused = false;
            mIsSurfaceReady = false;
            mHasFocus = true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Device: " + Build.DEVICE);
        Log.v(TAG, "Model: " + Build.MODEL);
        Log.v(TAG, "onCreate(): " + mSingleton);
        super.onCreate(savedInstanceState);
        initialize();
        mSingleton = this;
        String errorMsgBrokenLib = "";
        try {
            loadLibraries();
        } catch (UnsatisfiedLinkError e) {
            System.err.println(e.getMessage());
            mBrokenLibraries = true;
            errorMsgBrokenLib = e.getMessage();
        } catch (Exception e2) {
            System.err.println(e2.getMessage());
            mBrokenLibraries = true;
            errorMsgBrokenLib = e2.getMessage();
        }
        if (mBrokenLibraries) {
            Builder dlgAlert = new Builder(this);
            dlgAlert.setMessage("An error occurred while trying to start the application. Please try again and/or reinstall." + System.getProperty("line.separator") + System.getProperty("line.separator") + "Error: " + errorMsgBrokenLib);
            dlgAlert.setTitle("SDL Error");
            dlgAlert.setPositiveButton("Exit", new C01481());
            dlgAlert.setCancelable(false);
            dlgAlert.create().show();
            return;
        }
        mSurface = new SDLSurface(getApplication());
        if (VERSION.SDK_INT >= 12) {
            mJoystickHandler = new SDLJoystickHandler_API12();
        } else {
            mJoystickHandler = new SDLJoystickHandler();
        }
        mLayout = new RelativeLayout(this);
        mLayout.addView(mSurface);
        setContentView(mLayout);
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String filename = intent.getData().getPath();
            if (filename != null) {
                Log.v(TAG, "Got filename: " + filename);
                onNativeDropFile(filename);
            }
        }
    }

    protected void onPause() {
        Log.v(TAG, "onPause()");
        super.onPause();
        if (!mBrokenLibraries) {
            handlePause();
        }
    }

    protected void onResume() {
        Log.v(TAG, "onResume()");
        super.onResume();
        if (!mBrokenLibraries) {
            handleResume();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.v(TAG, "onWindowFocusChanged(): " + hasFocus);
        if (!mBrokenLibraries) {
            mHasFocus = hasFocus;
            if (hasFocus) {
                handleResume();
            }
        }
    }

    public void onLowMemory() {
        Log.v(TAG, "onLowMemory()");
        super.onLowMemory();
        if (!mBrokenLibraries) {
            nativeLowMemory();
        }
    }

    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        if (mBrokenLibraries) {
            super.onDestroy();
            initialize();
            return;
        }
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
        if (mBrokenLibraries) {
            return false;
        }
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
            mSurface.handlePause();
        }
    }

    public static void handleResume() {
        if (mIsPaused && mIsSurfaceReady && mHasFocus) {
            mIsPaused = false;
            nativeResume();
            mSurface.handleResume();
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

    public static int audioOpen(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
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
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
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
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
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

    public static int captureOpen(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        int channelConfig;
        int i;
        String str;
        int audioFormat = 3;
        if (isStereo) {
            channelConfig = 3;
        } else {
            channelConfig = 2;
        }
        if (is16Bit) {
            audioFormat = 2;
        }
        if (isStereo) {
            i = 2;
        } else {
            i = 1;
        }
        int frameSize = i * (is16Bit ? 2 : 1);
        String str2 = TAG;
        StringBuilder append = new StringBuilder("SDL capture: wanted ").append(isStereo ? "stereo" : "mono").append(" ");
        if (is16Bit) {
            str = "16-bit";
        } else {
            str = "8-bit";
        }
        Log.v(str2, append.append(str).append(" ").append(((float) sampleRate) / 1000.0f).append("kHz, ").append(desiredFrames).append(" frames buffer").toString());
        desiredFrames = Math.max(desiredFrames, ((AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) + frameSize) - 1) / frameSize);
        if (mAudioRecord == null) {
            mAudioRecord = new AudioRecord(0, sampleRate, channelConfig, audioFormat, desiredFrames * frameSize);
            if (mAudioRecord.getState() != 1) {
                Log.e(TAG, "Failed during initialization of AudioRecord");
                mAudioRecord.release();
                mAudioRecord = null;
                return -1;
            }
            mAudioRecord.startRecording();
        }
        Log.v(TAG, "SDL capture: got " + (mAudioRecord.getChannelCount() >= 2 ? "stereo" : "mono") + " " + (mAudioRecord.getAudioFormat() == 2 ? "16-bit" : "8-bit") + " " + (((float) mAudioRecord.getSampleRate()) / 1000.0f) + "kHz, " + desiredFrames + " frames buffer");
        return 0;
    }

    public static int captureReadShortBuffer(short[] buffer, boolean blocking) {
        return mAudioRecord.read(buffer, 0, buffer.length);
    }

    public static int captureReadByteBuffer(byte[] buffer, boolean blocking) {
        return mAudioRecord.read(buffer, 0, buffer.length);
    }

    public static void audioClose() {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public static void captureClose() {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

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

    public static boolean isDeviceSDLJoystick(int deviceId) {
        InputDevice device = InputDevice.getDevice(deviceId);
        if (device == null || deviceId < 0) {
            return false;
        }
        int sources = device.getSources();
        if ((sources & 16) == 16 || (sources & 513) == 513 || (sources & 1025) == 1025) {
            return true;
        }
        return false;
    }

    public InputStream openAPKExpansionInputStream(String fileName) throws IOException {
        InputStream inputStream = null;
        if (this.expansionFile == null) {
            String mainHint = nativeGetHint("SDL_ANDROID_APK_EXPANSION_MAIN_FILE_VERSION");
            if (mainHint != null) {
                String patchHint = nativeGetHint("SDL_ANDROID_APK_EXPANSION_PATCH_FILE_VERSION");
                if (patchHint != null) {
                    try {
                        Integer mainVersion = Integer.valueOf(mainHint);
                        Integer patchVersion = Integer.valueOf(patchHint);
                        try {
                            this.expansionFile = Class.forName("com.android.vending.expansion.zipfile.APKExpansionSupport").getMethod("getAPKExpansionZipFile", new Class[]{Context.class, Integer.TYPE, Integer.TYPE}).invoke(null, new Object[]{this, mainVersion, patchVersion});
                            this.expansionFileMethod = this.expansionFile.getClass().getMethod("getInputStream", new Class[]{String.class});
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            this.expansionFile = null;
                            this.expansionFileMethod = null;
                            throw new IOException("Could not access APK expansion support library", ex);
                        }
                    } catch (NumberFormatException ex2) {
                        ex2.printStackTrace();
                        throw new IOException("No valid file versions set for APK expansion files", ex2);
                    }
                }
            }
            return inputStream;
        }
        try {
            inputStream = (InputStream) this.expansionFileMethod.invoke(this.expansionFile, new Object[]{fileName});
            if (inputStream == null) {
                throw new IOException("Could not find path in APK expansion file");
            }
            return inputStream;
        } catch (Exception ex3) {
            ex3.printStackTrace();
            throw new IOException("Could not open stream from APK expansion file", ex3);
        }
    }

    public int messageboxShowMessageBox(int flags, String title, String message, int[] buttonFlags, int[] buttonIds, String[] buttonTexts, int[] colors) {
        this.messageboxSelection[0] = -1;
        if (buttonFlags.length != buttonIds.length && buttonIds.length != buttonTexts.length) {
            return -1;
        }
        final Bundle args = new Bundle();
        args.putInt("flags", flags);
        args.putString("title", title);
        args.putString("message", message);
        args.putIntArray("buttonFlags", buttonFlags);
        args.putIntArray("buttonIds", buttonIds);
        args.putStringArray("buttonTexts", buttonTexts);
        args.putIntArray("colors", colors);
        runOnUiThread(new Runnable() {
            public void run() {
                SDLActivity sDLActivity = SDLActivity.this;
                SDLActivity sDLActivity2 = SDLActivity.this;
                int i = sDLActivity2.dialogs;
                sDLActivity2.dialogs = i + 1;
                sDLActivity.showDialog(i, args);
            }
        });
        synchronized (this.messageboxSelection) {
            try {
                this.messageboxSelection.wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                return -1;
            }
        }
        return this.messageboxSelection[0];
    }

    protected Dialog onCreateDialog(int ignore, Bundle args) {
        int backgroundColor;
        int textColor;
        int buttonBackgroundColor;
        int[] colors = args.getIntArray("colors");
        if (colors != null) {
            int i = -1 + 1;
            backgroundColor = colors[i];
            i++;
            textColor = colors[i];
            i++;
            int buttonBorderColor = colors[i];
            i++;
            buttonBackgroundColor = colors[i];
            int buttonSelectedColor = colors[i + 1];
        } else {
            backgroundColor = 0;
            textColor = 0;
            buttonBackgroundColor = 0;
        }
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(args.getString("title"));
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new C01514());
        View textView = new TextView(this);
        textView.setGravity(17);
        textView.setText(args.getString("message"));
        if (textColor != 0) {
            textView.setTextColor(textColor);
        }
        int[] buttonFlags = args.getIntArray("buttonFlags");
        int[] buttonIds = args.getIntArray("buttonIds");
        String[] buttonTexts = args.getStringArray("buttonTexts");
        SparseArray<Button> mapping = new SparseArray();
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(0);
        buttons.setGravity(17);
        for (i = 0; i < buttonTexts.length; i++) {
            Button button = new Button(this);
            final int i2 = buttonIds[i];
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SDLActivity.this.messageboxSelection[0] = i2;
                    dialog.dismiss();
                }
            });
            if (buttonFlags[i] != 0) {
                if ((buttonFlags[i] & 1) != 0) {
                    mapping.put(66, button);
                }
                if ((buttonFlags[i] & 2) != 0) {
                    mapping.put(111, button);
                }
            }
            button.setText(buttonTexts[i]);
            if (textColor != 0) {
                button.setTextColor(textColor);
            }
            if (buttonBackgroundColor != 0) {
                Drawable drawable = button.getBackground();
                if (drawable == null) {
                    button.setBackgroundColor(buttonBackgroundColor);
                } else {
                    drawable.setColorFilter(buttonBackgroundColor, Mode.MULTIPLY);
                }
            }
            buttons.addView(button);
        }
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(1);
        content.addView(textView);
        content.addView(buttons);
        if (backgroundColor != 0) {
            content.setBackgroundColor(backgroundColor);
        }
        dialog.setContentView(content);
        final SparseArray<Button> sparseArray = mapping;
        dialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface d, int keyCode, KeyEvent event) {
                Button button = (Button) sparseArray.get(keyCode);
                if (button == null) {
                    return false;
                }
                if (event.getAction() != 1) {
                    return true;
                }
                button.performClick();
                return true;
            }
        });
        return dialog;
    }
}
