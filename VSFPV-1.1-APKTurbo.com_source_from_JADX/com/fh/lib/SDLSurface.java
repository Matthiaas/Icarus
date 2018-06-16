package com.fh.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build.VERSION;
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

/* compiled from: SDLActivity */
class SDLSurface extends SurfaceView implements Callback, OnKeyListener, OnTouchListener, SensorEventListener {
    protected static Display mDisplay;
    protected static float mHeight;
    protected static SensorManager mSensorManager;
    protected static float mWidth;

    /* compiled from: SDLActivity */
    class C00881 implements Runnable {
        C00881() {
        }

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
            r0 = com.fh.lib.SDLActivity.mSDLThread;	 Catch:{ Exception -> 0x000d, all -> 0x0016 }
            r0.join();	 Catch:{ Exception -> 0x000d, all -> 0x0016 }
            r0 = com.fh.lib.SDLActivity.mExitCalledFromJava;
            if (r0 != 0) goto L_0x000c;
        L_0x0009:
            com.fh.lib.SDLActivity.handleNativeExit();
        L_0x000c:
            return;
        L_0x000d:
            r0 = move-exception;
            r0 = com.fh.lib.SDLActivity.mExitCalledFromJava;
            if (r0 != 0) goto L_0x000c;
        L_0x0012:
            com.fh.lib.SDLActivity.handleNativeExit();
            goto L_0x000c;
        L_0x0016:
            r0 = move-exception;
            r1 = com.fh.lib.SDLActivity.mExitCalledFromJava;
            if (r1 != 0) goto L_0x001e;
        L_0x001b:
            com.fh.lib.SDLActivity.handleNativeExit();
        L_0x001e:
            throw r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.fh.lib.SDLSurface.1.run():void");
        }
    }

    public SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        mSensorManager = (SensorManager) context.getSystemService("sensor");
        if (VERSION.SDK_INT >= 12) {
            setOnGenericMotionListener(new SDLGenericMotionListener_API12());
        }
        mWidth = 1.0f;
        mHeight = 1.0f;
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
        SDLActivity.onNativeResize(width, height, sdlFormat);
        Log.v("SDL", "Window size:" + width + "x" + height);
        SDLActivity.mIsSurfaceReady = true;
        SDLActivity.onNativeSurfaceChanged();
        if (SDLActivity.mSDLThread == null) {
            SDLActivity.mSDLThread = new Thread(new SDLMain(), "SDLThread");
            enableSensor(1, true);
            SDLActivity.mSDLThread.start();
            new Thread(new C00881()).start();
        }
    }

    public void onDraw(Canvas canvas) {
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (!((event.getSource() & 1025) == 0 && (event.getSource() & 513) == 0)) {
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
                SDLActivity.onNativeKeyUp(keyCode);
                return true;
            }
        }
        return false;
    }

    public boolean onTouch(View v, MotionEvent event) {
        int touchDevId = event.getDeviceId();
        int pointerCount = event.getPointerCount();
        int action = event.getActionMasked();
        int i = -1;
        switch (action) {
            case 0:
            case 1:
                i = 0;
                break;
            case 2:
                for (i = 0; i < pointerCount; i++) {
                    SDLActivity.onNativeTouch(touchDevId, event.getPointerId(i), action, event.getX(i) / mWidth, event.getY(i) / mHeight, event.getPressure(i));
                }
                break;
            case 5:
            case 6:
                break;
        }
        if (i == -1) {
            i = event.getActionIndex();
        }
        SDLActivity.onNativeTouch(touchDevId, event.getPointerId(i), action, event.getX(i) / mWidth, event.getY(i) / mHeight, event.getPressure(i));
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
            SDLActivity.onNativeAccel((-x) / 9.80665f, y / 9.80665f, (event.values[2] / 9.80665f) - 1.0f);
        }
    }
}
