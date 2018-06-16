package com.android.opengles;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import com.fh.lib.FHSDK;

public class GLFrameSurface extends GLSurfaceView implements OnTouchListener, OnGestureListener, OnDoubleTapListener {
    private static final float NS2S = 1.0E-9f;
    private static final float SCALE_STEP = 0.1f;
    private static final String TAG = "XX";
    private Sensor aSensor;
    float[] accelerometerValues;
    private float[] angle;
    private float baseValue;
    private int curIndex;
    private GestureDetector detector;
    private long flushCount;
    private float hDegrees;
    private float[] hEyeDegrees;
    private float hOffset;
    private boolean isScaleMode;
    private int lastRot;
    private Context mContext;
    private GLFrameRenderer mFrameRender;
    private Sensor mSensor;
    float[] magneticFieldValues;
    Sensor myGyroscope;
    final SensorEventListener myListener;
    private SensorEventListener mySensorListener;
    SensorManager mySensorManager;
    private float startHDegrees;
    private float startVDegrees;
    private float timestamp;
    private float vDegrees;

    class C00671 implements SensorEventListener {
        C00671() {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == 2) {
                GLFrameSurface.this.magneticFieldValues = sensorEvent.values;
            }
            if (sensorEvent.sensor.getType() == 1) {
                GLFrameSurface.this.accelerometerValues = sensorEvent.values;
            }
            GLFrameSurface.this.calculateOrientation();
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    class C00682 implements SensorEventListener {
        C00682() {
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            int uiRot = ((WindowManager) GLFrameSurface.this.getContext().getSystemService("window")).getDefaultDisplay().getRotation();
            if (GLFrameSurface.this.lastRot != uiRot) {
                GLFrameSurface.this.timestamp = 0.0f;
                GLFrameSurface.this.angle[0] = 0.0f;
                GLFrameSurface.this.angle[1] = 0.0f;
                GLFrameSurface.this.angle[2] = 0.0f;
                GLFrameSurface.this.lastRot = uiRot;
                GLFrameSurface.this.startVDegrees = 0.0f;
                GLFrameSurface.this.startHDegrees = 0.0f;
            }
            float anglex = 0.0f;
            float angley = 0.0f;
            float[] values = event.values;
            if (GLFrameSurface.this.timestamp != 0.0f) {
                float dT = (((float) event.timestamp) - GLFrameSurface.this.timestamp) * GLFrameSurface.NS2S;
                float[] access$3 = GLFrameSurface.this.angle;
                access$3[0] = access$3[0] + (event.values[0] * dT);
                access$3 = GLFrameSurface.this.angle;
                access$3[1] = access$3[1] + (event.values[1] * dT);
                access$3 = GLFrameSurface.this.angle;
                access$3[2] = access$3[2] + (event.values[2] * dT);
                anglex = (float) Math.toDegrees((double) GLFrameSurface.this.angle[0]);
                angley = (float) Math.toDegrees((double) GLFrameSurface.this.angle[1]);
                float anglez = (float) Math.toDegrees((double) GLFrameSurface.this.angle[2]);
            }
            GLFrameSurface.this.timestamp = (float) event.timestamp;
            float tmp;
            if (1 == uiRot) {
                tmp = anglex;
                anglex = -angley;
                angley = tmp;
            } else if (2 == uiRot) {
                anglex = -anglex;
                angley = -angley;
            } else if (3 == uiRot) {
                tmp = anglex;
                anglex = angley;
                angley = -tmp;
            }
            GLFrameSurface.this.update(GLFrameSurface.this.startVDegrees, GLFrameSurface.this.startHDegrees, anglex, angley);
        }
    }

    public GLFrameSurface(Context context) {
        super(context);
        this.hOffset = -1.0f;
        this.hDegrees = -1.0f;
        this.vDegrees = -1.0f;
        this.baseValue = -1.0f;
        this.hEyeDegrees = new float[4];
        this.curIndex = 0;
        this.isScaleMode = false;
        this.angle = new float[3];
        this.lastRot = -1;
        this.accelerometerValues = new float[3];
        this.magneticFieldValues = new float[3];
        this.startVDegrees = 0.0f;
        this.startHDegrees = 0.0f;
        this.flushCount = 0;
        this.myListener = new C00671();
        this.mySensorListener = new C00682();
        setEGLContextClientVersion(2);
        this.mContext = context;
        this.mFrameRender = GLFrameRenderer.getInstance();
        this.detector = new GestureDetector(this);
        this.detector.setIsLongpressEnabled(true);
        this.detector.setOnDoubleTapListener(this);
        setOnTouchListener(this);
        this.mySensorManager = (SensorManager) context.getSystemService("sensor");
        this.myGyroscope = this.mySensorManager.getDefaultSensor(4);
        this.aSensor = this.mySensorManager.getDefaultSensor(1);
        this.mSensor = this.mySensorManager.getDefaultSensor(2);
    }

    public void rigisterListener() {
        try {
            this.mySensorManager.registerListener(this.mySensorListener, this.myGyroscope, 1);
            this.mySensorManager.registerListener(this.myListener, this.aSensor, 1);
            this.mySensorManager.registerListener(this.myListener, this.mSensor, 1);
        } catch (Exception e) {
        }
    }

    public void unRigisterListener() {
        try {
            this.mySensorManager.unregisterListener(this.mySensorListener, this.myGyroscope);
            this.mySensorManager.unregisterListener(this.myListener, this.aSensor);
            this.mySensorManager.unregisterListener(this.myListener, this.mSensor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GLFrameSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.hOffset = -1.0f;
        this.hDegrees = -1.0f;
        this.vDegrees = -1.0f;
        this.baseValue = -1.0f;
        this.hEyeDegrees = new float[4];
        this.curIndex = 0;
        this.isScaleMode = false;
        this.angle = new float[3];
        this.lastRot = -1;
        this.accelerometerValues = new float[3];
        this.magneticFieldValues = new float[3];
        this.startVDegrees = 0.0f;
        this.startHDegrees = 0.0f;
        this.flushCount = 0;
        this.myListener = new C00671();
        this.mySensorListener = new C00682();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 0) {
            this.mFrameRender.setvelocityX(0.0f);
            this.mFrameRender.setvelocityY(0.0f);
            this.baseValue = 0.0f;
        }
        if (event.getAction() == 1 && this.isScaleMode) {
            this.isScaleMode = false;
            return false;
        }
        if (event.getAction() == 2) {
            if (event.getPointerCount() == 1 && this.isScaleMode) {
                return false;
            }
            if (event.getPointerCount() == 2) {
                this.isScaleMode = true;
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                float value = (float) Math.sqrt((double) ((x * x) + (y * y)));
                if (this.baseValue == 0.0f) {
                    this.baseValue = value;
                    return false;
                }
                float step = 0.0f;
                float scale = value / this.baseValue;
                if (scale > 1.0f) {
                    step = SCALE_STEP;
                } else if (scale < 1.0f) {
                    step = -0.1f;
                }
                GLFrameRenderer.depth += step;
                if (GLFrameRenderer.depth < FHSDK.getMaxZDepth(GLFrameRenderer.hWin)) {
                    GLFrameRenderer.depth = FHSDK.getMaxZDepth(GLFrameRenderer.hWin);
                    return false;
                } else if (GLFrameRenderer.depth <= 0.0f) {
                    return false;
                } else {
                    GLFrameRenderer.depth = 0.0f;
                    return false;
                }
            }
        }
        return this.detector.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent e) {
        if (3 == GLFrameRenderer.eyeMode) {
            return false;
        }
        this.hOffset = GLFrameRenderer.hOffset;
        this.vDegrees = GLFrameRenderer.vDegrees;
        this.hDegrees = GLFrameRenderer.hDegrees;
        for (int i = 0; i < 4; i++) {
            this.hEyeDegrees[i] = GLFrameRenderer.hEyeDegrees[i];
        }
        if (e.getX() <= ((float) (GLFrameRenderer.mScreenWidth / 2)) && e.getY() <= ((float) (GLFrameRenderer.mScreenHeight / 2))) {
            this.curIndex = 2;
        } else if (e.getX() <= ((float) GLFrameRenderer.mScreenWidth) && e.getX() > ((float) (GLFrameRenderer.mScreenWidth / 2)) && e.getY() <= ((float) (GLFrameRenderer.mScreenHeight / 2))) {
            this.curIndex = 3;
        } else if (e.getX() <= ((float) (GLFrameRenderer.mScreenWidth / 2)) && e.getY() <= ((float) GLFrameRenderer.mScreenHeight) && e.getY() > ((float) (GLFrameRenderer.mScreenHeight / 2))) {
            this.curIndex = 0;
        } else if (e.getX() <= ((float) GLFrameRenderer.mScreenWidth) && e.getX() > ((float) (GLFrameRenderer.mScreenWidth / 2)) && e.getY() <= ((float) GLFrameRenderer.mScreenHeight) && e.getY() > ((float) (GLFrameRenderer.mScreenHeight / 2))) {
            this.curIndex = 1;
        }
        GLFrameRenderer.curIndex = this.curIndex;
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > 2000.0f) {
            this.mFrameRender.setvelocityX(velocityX);
        }
        if (Math.abs(velocityY) > 2000.0f) {
            this.mFrameRender.setvelocityY(velocityY);
        }
        return false;
    }

    public void onLongPress(MotionEvent e) {
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (GLFrameRenderer.displayMode == 0 || 6 == GLFrameRenderer.displayMode) {
            float offsetX = e2.getX() - e1.getX();
            float offsetY = e2.getY() - e1.getY();
            if (Math.abs(offsetX) >= 2.0f || Math.abs(offsetY) >= 2.0f) {
                if (GLFrameRenderer.eyeMode == 0 || 1 == GLFrameRenderer.eyeMode) {
                    GLFrameRenderer.vDegrees = this.vDegrees - (offsetY / 10.0f);
                    GLFrameRenderer.hDegrees = this.hDegrees - (offsetX / 10.0f);
                    if (6 == GLFrameRenderer.displayMode) {
                        if (GLFrameRenderer.hDegrees >= FHSDK.getMaxHDegress(GLFrameRenderer.hWin)) {
                            GLFrameRenderer.hDegrees = FHSDK.getMaxHDegress(GLFrameRenderer.hWin);
                        }
                        if (GLFrameRenderer.hDegrees <= FHSDK.getMinHDegress(GLFrameRenderer.hWin)) {
                            GLFrameRenderer.hDegrees = FHSDK.getMinHDegress(GLFrameRenderer.hWin);
                        }
                    }
                    if (GLFrameRenderer.vDegrees < FHSDK.getMaxVDegress(GLFrameRenderer.hWin)) {
                        GLFrameRenderer.vDegrees = FHSDK.getMaxVDegress(GLFrameRenderer.hWin);
                    } else if (GLFrameRenderer.vDegrees > FHSDK.getMinVDegress(GLFrameRenderer.hWin)) {
                        GLFrameRenderer.vDegrees = FHSDK.getMinVDegress(GLFrameRenderer.hWin);
                    }
                } else if (2 == GLFrameRenderer.eyeMode) {
                    GLFrameRenderer.hEyeDegrees[this.curIndex] = this.hEyeDegrees[this.curIndex] - (offsetX / 10.0f);
                }
            }
        } else {
            GLFrameRenderer.hOffset = this.hOffset - ((e2.getX() - e1.getX()) / 500.0f);
        }
        return false;
    }

    public void onShowPress(MotionEvent e) {
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public boolean onDoubleTap(MotionEvent e) {
        GLFrameRenderer.isDoubleClick = true;
        return false;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, this.accelerometerValues, this.magneticFieldValues);
        SensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees((double) values[0]);
        values[1] = (float) Math.toDegrees((double) values[1]);
        values[2] = (float) Math.toDegrees((double) values[2]);
        int uiRot = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getRotation();
        float anglex = 0.0f;
        float angley = 0.0f;
        if (0.0f == this.startVDegrees) {
            if (uiRot == 0) {
                this.startVDegrees = values[1];
            } else if (1 == uiRot) {
                this.startVDegrees = values[2];
            } else if (2 == uiRot) {
                this.startVDegrees = -values[1];
            } else if (3 == uiRot) {
                this.startVDegrees = -values[2];
            }
        }
        if (0.0f == this.startHDegrees) {
            if (uiRot == 0) {
                this.startHDegrees = -values[2];
            } else if (1 == uiRot) {
                this.startHDegrees = values[0];
            } else if (2 == uiRot) {
                this.startHDegrees = values[2];
            } else if (3 == uiRot) {
                this.startHDegrees = values[0] - 180.0f;
            }
        }
        if (this.myGyroscope == null) {
            if (uiRot == 0) {
                anglex = -values[1];
                angley = values[2];
            } else if (1 == uiRot) {
                anglex = -values[2];
                angley = -values[0];
            } else if (2 != uiRot && 3 == uiRot) {
                anglex = values[2];
                angley = -values[0];
            }
            long j = this.flushCount + 1;
            this.flushCount = j;
            if (j % 10 == 0) {
                update(0.0f, 0.0f, anglex, angley);
            }
        }
    }

    public void update(float startVDegrees, float startHDegrees, float anglex, float angley) {
        float offsetX = anglex;
        float offsetY = angley;
        if (3 != GLFrameRenderer.eyeMode) {
            return;
        }
        if (GLFrameRenderer.displayMode == 0) {
            this.vDegrees = startVDegrees - offsetX;
            this.hDegrees = startHDegrees - offsetY;
            if (this.vDegrees < FHSDK.getMaxVDegress(GLFrameRenderer.hWin)) {
                this.vDegrees = FHSDK.getMaxVDegress(GLFrameRenderer.hWin);
            } else if (this.vDegrees > 0.0f) {
                this.vDegrees = 0.0f;
            }
            GLFrameRenderer.vDegrees = this.vDegrees;
            GLFrameRenderer.hDegrees = this.hDegrees;
        } else if (6 == GLFrameRenderer.displayMode) {
            this.vDegrees = (startVDegrees - offsetX) + 90.0f;
            this.hDegrees = startHDegrees - offsetY;
            if (this.vDegrees < FHSDK.getMaxVDegress(GLFrameRenderer.hWin)) {
                this.vDegrees = FHSDK.getMaxVDegress(GLFrameRenderer.hWin);
            } else if (this.vDegrees > FHSDK.getMinVDegress(GLFrameRenderer.hWin)) {
                this.vDegrees = FHSDK.getMinVDegress(GLFrameRenderer.hWin);
            }
            GLFrameRenderer.vDegrees = this.vDegrees;
            GLFrameRenderer.hDegrees = this.hDegrees;
        }
    }
}
