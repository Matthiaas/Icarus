package org.libsdl.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build.VERSION;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import et.song.vspfv.C0127R;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/* compiled from: SDLActivity */
class SDLSurface extends SurfaceView implements Callback, OnKeyListener, OnTouchListener, SensorEventListener {
    static final int COMMAND_ON_SURFACE_CLICK = 32771;
    private static final int MAX_CLICK_DURATION = 200;
    protected static Display mDisplay;
    protected static float mHeight;
    protected static SensorManager mSensorManager;
    protected static float mWidth;
    private boolean pressedBack = false;
    private long startClickTime;

    /* compiled from: SDLActivity */
    class C01552 extends TimerTask {
        C01552() {
        }

        public void run() {
            SDLSurface.this.pressedBack = false;
        }
    }

    public SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        mDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        mSensorManager = (SensorManager) context.getSystemService("sensor");
        if (VERSION.SDK_INT >= 12) {
            setOnGenericMotionListener(new SDLGenericMotionListener_API12());
        }
        mWidth = 1.0f;
        mHeight = 1.0f;
    }

    public void handlePause() {
        enableSensor(1, false);
    }

    public void handleResume() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(this);
        setOnTouchListener(this);
        enableSensor(1, true);
    }

    public Surface getNativeSurface() {
        return getHolder().getSurface();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("SDL", "surfaceCreated()");
        holder.setType(2);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("SDL", "surfaceDestroyed()");
        SDLActivity.handlePause();
        SDLActivity.mIsSurfaceReady = false;
        SDLActivity.onNativeSurfaceDestroyed();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v("SDL", "surfaceChanged()");
        int sdlFormat = 353701890;
        switch (format) {
            case 1:
                Log.v("SDL", "pixel format RGBA_8888");
                sdlFormat = 373694468;
                break;
            case 2:
                Log.v("SDL", "pixel format RGBX_8888");
                sdlFormat = 371595268;
                break;
            case 3:
                Log.v("SDL", "pixel format RGB_888");
                sdlFormat = 370546692;
                break;
            case 4:
                Log.v("SDL", "pixel format RGB_565");
                sdlFormat = 353701890;
                break;
            case 6:
                Log.v("SDL", "pixel format RGBA_5551");
                sdlFormat = 356782082;
                break;
            case 7:
                Log.v("SDL", "pixel format RGBA_4444");
                sdlFormat = 356651010;
                break;
            case 8:
                Log.v("SDL", "pixel format A_8");
                break;
            case 9:
                Log.v("SDL", "pixel format L_8");
                break;
            case 10:
                Log.v("SDL", "pixel format LA_88");
                break;
            case C0127R.styleable.PercentLayout_Layout_layout_maxHeightPercent /*11*/:
                Log.v("SDL", "pixel format RGB_332");
                sdlFormat = 336660481;
                break;
            default:
                Log.v("SDL", "pixel format unknown " + format);
                break;
        }
        mWidth = (float) width;
        mHeight = (float) height;
        SDLActivity.onNativeResize(width, height, sdlFormat, mDisplay.getRefreshRate());
        Log.v("SDL", "Window size: " + width + "x" + height);
        boolean skip = false;
        int requestedOrientation = -1;
        if (SDLActivity.mSingleton != null) {
            requestedOrientation = SDLActivity.mSingleton.getRequestedOrientation();
        }
        if (requestedOrientation != -1) {
            if (requestedOrientation == 1) {
                if (mWidth > mHeight) {
                    skip = true;
                }
            } else if (requestedOrientation == 0 && mWidth < mHeight) {
                skip = true;
            }
        }
        if (skip) {
            if (((double) Math.max(mWidth, mHeight)) / ((double) Math.min(mWidth, mHeight)) < 1.2d) {
                Log.v("SDL", "Don't skip on such aspect-ratio. Could be a square resolution.");
                skip = false;
            }
        }
        if (skip) {
            Log.v("SDL", "Skip .. Surface is not ready.");
            return;
        }
        SDLActivity.mIsSurfaceReady = true;
        SDLActivity.onNativeSurfaceChanged();
        if (SDLActivity.mSDLThread == null) {
            final Thread sdlThread = new Thread(new SDLMain(), "SDLThread");
            enableSensor(1, true);
            sdlThread.start();
            SDLActivity.mSDLThread = new Thread(new Runnable() {
                public void run() {
                    /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1431)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1453)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
                    /*
                    r2 = this;
                    r0 = r8;	 Catch:{ Exception -> 0x000d, all -> 0x0016 }
                    r0.join();	 Catch:{ Exception -> 0x000d, all -> 0x0016 }
                    r0 = org.libsdl.app.SDLActivity.mExitCalledFromJava;
                    if (r0 != 0) goto L_0x000c;
                L_0x0009:
                    org.libsdl.app.SDLActivity.handleNativeExit();
                L_0x000c:
                    return;
                L_0x000d:
                    r0 = move-exception;
                    r0 = org.libsdl.app.SDLActivity.mExitCalledFromJava;
                    if (r0 != 0) goto L_0x000c;
                L_0x0012:
                    org.libsdl.app.SDLActivity.handleNativeExit();
                    goto L_0x000c;
                L_0x0016:
                    r0 = move-exception;
                    r1 = org.libsdl.app.SDLActivity.mExitCalledFromJava;
                    if (r1 != 0) goto L_0x001e;
                L_0x001b:
                    org.libsdl.app.SDLActivity.handleNativeExit();
                L_0x001e:
                    throw r0;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLSurface.1.run():void");
                }
            }, "SDLThreadListener");
            SDLActivity.mSDLThread.start();
        }
        if (SDLActivity.mHasFocus) {
            SDLActivity.handleResume();
        }
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (SDLActivity.isDeviceSDLJoystick(event.getDeviceId())) {
            if (event.getAction() == 0) {
                if (SDLActivity.onNativePadDown(event.getDeviceId(), keyCode) == 0) {
                    return true;
                }
            } else if (event.getAction() == 1 && SDLActivity.onNativePadUp(event.getDeviceId(), keyCode) == 0) {
                return true;
            }
        }
        if ((event.getSource() & 257) != 0) {
            if (event.getAction() == 0) {
                SDLActivity.onNativeKeyDown(keyCode);
                return true;
            } else if (event.getAction() == 1) {
                if (keyCode != 4) {
                    SDLActivity.onNativeKeyUp(keyCode);
                    return true;
                } else if (this.pressedBack) {
                    this.pressedBack = false;
                    SDLActivity.nativeQuit();
                    return true;
                } else {
                    this.pressedBack = true;
                    new Timer().schedule(new C01552(), 2000);
                    return true;
                }
            }
        }
        if ((event.getSource() & FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) != 0 && (keyCode == 4 || keyCode == 125)) {
            switch (event.getAction()) {
                case 0:
                case 1:
                    return true;
            }
        }
        return false;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            case 1:
                if (Calendar.getInstance().getTimeInMillis() - this.startClickTime < 200 && SDLActivity.mSingleton != null) {
                    SDLActivity.sendMessage(COMMAND_ON_SURFACE_CLICK, 0);
                    break;
                }
        }
        return true;
    }

    public void enableSensor(int sensortype, boolean enabled) {
        if (enabled) {
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(sensortype), 1, null);
        } else {
            mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(sensortype));
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == 1) {
            float x;
            float y;
            switch (mDisplay.getRotation()) {
                case 1:
                    x = -event.values[1];
                    y = event.values[0];
                    break;
                case 2:
                    x = -event.values[1];
                    y = -event.values[0];
                    break;
                case 3:
                    x = event.values[1];
                    y = -event.values[0];
                    break;
                default:
                    x = event.values[0];
                    y = event.values[1];
                    break;
            }
            SDLActivity.onNativeAccel((-x) / 9.80665f, y / 9.80665f, event.values[2] / 9.80665f);
        }
    }
}
