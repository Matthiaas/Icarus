package com.android.opengles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import com.app.Interface.MyMediaCodec;
import com.app.util.log;
import com.fh.lib.Define.YUVDataCallBackInterface;
import com.fh.lib.FHSDK;
import com.fh.lib.PlayInfo;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLFrameRenderer implements Renderer {
    private static final int DISPLAY_TYPE_RGB = 1;
    private static final int DISPLAY_TYPE_YUV = 0;
    private static final float H_OFFSET_BASE = 1000.0f;
    private static final float STEP_BASE_FAST = 200.0f;
    private static final float STEP_BASE_SLOW = 100.0f;
    private static final float STEP_OFFSET = 5.0f;
    public static boolean bMixMode = false;
    public static boolean bSnapshot = false;
    public static float circleR;
    public static float circleX;
    public static float circleY;
    public static int ctrlIndex = 0;
    public static int curIndex = 0;
    public static float depth = 0.0f;
    public static int displayMode = 0;
    public static int eyeMode = 0;
    public static int hBuffer = 0;
    public static float hDegrees = 0.0f;
    public static float[] hEyeDegrees = new float[]{0.0f, 90.0f, 180.0f, 270.0f};
    public static float hOffset = 0.0f;
    public static int hWin = 0;
    public static int[] hWinMixMode = new int[4];
    private static GLFrameRenderer instance;
    public static final boolean isDebugMode = false;
    public static boolean isDoubleClick = false;
    public static boolean isZoomIn = false;
    public static int mDrawHeight;
    public static int mDrawWidth;
    public static RGBRes[] mRgbRes = new RGBRes[24];
    public static int mScreenHeight;
    public static int mScreenWidth;
    public static int modeOffset = 0;
    public static boolean resChanged = false;
    public static int rgbResIndex = 0;
    public static float scrollStep = 0.0f;
    public static float vDegrees = 0.0f;
    private static float velocityX = 0.0f;
    private static float velocityY = 0.0f;
    private boolean bIsTime2Draw = false;
    private boolean bSurfaceChanged = false;
    private boolean bSurfaceCreate = false;
    private boolean bUpdated = false;
    public YUVDataCallBackInterface dataFun = new C01723();
    private int drawCount = 0;
    private byte[] frameBuf = null;
    private float lastDepth = -1.0f;
    private float lastHDegrees = -1.0f;
    private float lastHOffset = -1.0f;
    private int lastShowMode = -1;
    private float lastVDegrees = -1.0f;
    private Context mContext;
    private Handler mHandler = null;
    private SnapshotThread mSnapshotThread;
    private GLSurfaceView mTargetSurface;
    private int mVideoHeight;
    private int mVideoWidth;
    Runnable requestRender = new C00662();
    Runnable scaleView = new C00651();
    private int view_h;
    private int view_w;
    private int view_x;
    private int view_y;
    private byte[] yuv;

    class C00651 implements Runnable {
        C00651() {
        }

        public void run() {
            float vDegreesStep = Math.abs(FHSDK.getMaxVDegress(GLFrameRenderer.hWin) / 50.0f);
            float depthStep = Math.abs(FHSDK.getMaxZDepth(GLFrameRenderer.hWin) / 50.0f);
            if (GLFrameRenderer.isZoomIn) {
                GLFrameRenderer.vDegrees -= vDegreesStep;
                GLFrameRenderer.hDegrees += 1.8f;
                GLFrameRenderer.depth += depthStep;
            } else {
                GLFrameRenderer.vDegrees += vDegreesStep * 4.0f;
                GLFrameRenderer.hDegrees -= 1.8f * 4.0f;
                GLFrameRenderer.depth -= depthStep * 4.0f;
            }
            if (GLFrameRenderer.vDegrees < FHSDK.getMaxVDegress(GLFrameRenderer.hWin)) {
                GLFrameRenderer.vDegrees = FHSDK.getMaxVDegress(GLFrameRenderer.hWin);
            } else if (GLFrameRenderer.vDegrees > 0.0f) {
                GLFrameRenderer.vDegrees = 0.0f;
            }
            if (GLFrameRenderer.depth < FHSDK.getMaxZDepth(GLFrameRenderer.hWin)) {
                GLFrameRenderer.depth = FHSDK.getMaxZDepth(GLFrameRenderer.hWin);
            } else if (GLFrameRenderer.depth > 0.0f) {
                GLFrameRenderer.depth = 0.0f;
            }
            if ((GLFrameRenderer.isZoomIn && GLFrameRenderer.depth != 0.0f) || (!GLFrameRenderer.isZoomIn && GLFrameRenderer.depth != FHSDK.getMaxZDepth(GLFrameRenderer.hWin))) {
                GLFrameRenderer.this.mHandler.postDelayed(GLFrameRenderer.this.scaleView, 40);
            }
        }
    }

    class C00662 implements Runnable {
        C00662() {
        }

        public void run() {
            if (!(GLFrameRenderer.displayMode == 0 || 6 == GLFrameRenderer.displayMode)) {
                GLFrameRenderer.eyeMode = 0;
            }
            if (GLFrameRenderer.isDoubleClick && ((GLFrameRenderer.displayMode == 0 && GLFrameRenderer.eyeMode == 0) || 6 == GLFrameRenderer.displayMode)) {
                GLFrameRenderer.isDoubleClick = false;
                if (GLFrameRenderer.depth != FHSDK.getMaxZDepth(GLFrameRenderer.hWin)) {
                    GLFrameRenderer.isZoomIn = false;
                } else {
                    GLFrameRenderer.isZoomIn = true;
                }
                GLFrameRenderer.this.mHandler.post(GLFrameRenderer.this.scaleView);
            }
            float[] fArr;
            int i;
            if (GLFrameRenderer.velocityX > 0.0f) {
                GLFrameRenderer.velocityX = GLFrameRenderer.velocityX - GLFrameRenderer.scrollStep;
                if (GLFrameRenderer.velocityX < 0.0f || GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE < 0.005f) {
                    GLFrameRenderer.velocityX = 0.0f;
                }
                if (GLFrameRenderer.displayMode != 0 && 6 != GLFrameRenderer.displayMode) {
                    GLFrameRenderer.hOffset -= GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE;
                } else if (GLFrameRenderer.eyeMode == 0 || 1 == GLFrameRenderer.eyeMode) {
                    GLFrameRenderer.hDegrees -= (GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE) * 50.0f;
                } else if (2 == GLFrameRenderer.eyeMode) {
                    fArr = GLFrameRenderer.hEyeDegrees;
                    i = GLFrameRenderer.curIndex;
                    fArr[i] = fArr[i] - ((GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE) * 50.0f);
                }
            } else if (GLFrameRenderer.velocityX < 0.0f) {
                GLFrameRenderer.velocityX = GLFrameRenderer.velocityX + GLFrameRenderer.scrollStep;
                if (GLFrameRenderer.velocityX > 0.0f || GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE < 0.005f) {
                    GLFrameRenderer.velocityX = 0.0f;
                }
                if (GLFrameRenderer.displayMode != 0 && 6 != GLFrameRenderer.displayMode) {
                    GLFrameRenderer.hOffset += GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE;
                } else if (GLFrameRenderer.eyeMode == 0 || 1 == GLFrameRenderer.eyeMode) {
                    GLFrameRenderer.hDegrees += (GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE) * 50.0f;
                } else if (2 == GLFrameRenderer.eyeMode) {
                    fArr = GLFrameRenderer.hEyeDegrees;
                    i = GLFrameRenderer.curIndex;
                    fArr[i] = fArr[i] + ((GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE) * 50.0f);
                }
            }
            if (GLFrameRenderer.velocityY > 0.0f) {
                GLFrameRenderer.velocityY = GLFrameRenderer.velocityY - GLFrameRenderer.scrollStep;
                if (GLFrameRenderer.velocityY < 0.0f || GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE < 0.005f) {
                    GLFrameRenderer.velocityY = 0.0f;
                }
                if (6 == GLFrameRenderer.displayMode) {
                    GLFrameRenderer.vDegrees -= (GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE) * 50.0f;
                } else {
                    GLFrameRenderer.hOffset -= GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE;
                }
            } else if (GLFrameRenderer.velocityY < 0.0f) {
                GLFrameRenderer.velocityY = GLFrameRenderer.velocityY + GLFrameRenderer.scrollStep;
                if (GLFrameRenderer.velocityY > 0.0f || GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE < 0.005f) {
                    GLFrameRenderer.velocityY = 0.0f;
                }
                if (6 == GLFrameRenderer.displayMode) {
                    GLFrameRenderer.vDegrees += (GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE) * 50.0f;
                } else {
                    GLFrameRenderer.hOffset += GLFrameRenderer.scrollStep / GLFrameRenderer.H_OFFSET_BASE;
                }
            }
            if (GLFrameRenderer.scrollStep > 0.0f) {
                GLFrameRenderer.scrollStep -= GLFrameRenderer.STEP_OFFSET;
            }
            GLFrameRenderer.this.mHandler.postDelayed(GLFrameRenderer.this.requestRender, 40);
        }
    }

    public class RGBRes {
        public int height;
        public byte[] rgb;
        public int width;
    }

    class SnapshotThread implements Runnable {
        private boolean isShoting = false;

        SnapshotThread() {
        }

        public void start() {
            new Thread(this).start();
        }

        public boolean isShoting() {
            return this.isShoting;
        }

        public void run() {
            synchronized (this) {
                byte[] outBuffer = new byte[GLFrameRenderer.this.frameBuf.length];
                for (int i = 0; i < GLFrameRenderer.this.view_h; i++) {
                    int offset1 = (GLFrameRenderer.this.view_w * i) * 4;
                    int offset2 = (((GLFrameRenderer.this.view_h - i) - 1) * GLFrameRenderer.this.view_w) * 4;
                    for (int j = 0; j < GLFrameRenderer.this.view_w * 4; j++) {
                        outBuffer[offset2 + j] = GLFrameRenderer.this.frameBuf[offset1 + j];
                    }
                }
                ByteBuffer btBuf = ByteBuffer.wrap(outBuffer);
                btBuf.order(ByteOrder.LITTLE_ENDIAN);
                btBuf.rewind();
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append("/").append(System.currentTimeMillis()).append(".jpg").toString()));
                    try {
                        Bitmap bmp = Bitmap.createBitmap(GLFrameRenderer.this.view_w, GLFrameRenderer.this.view_h, Config.ARGB_8888);
                        bmp.copyPixelsFromBuffer(btBuf);
                        bmp.compress(CompressFormat.JPEG, 90, bos);
                        bmp.recycle();
                        if (bos != null) {
                            try {
                                bos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        if (bos != null) {
                            try {
                                bos.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                    } catch (Throwable th) {
                        if (bos != null) {
                            try {
                                bos.close();
                            } catch (IOException e32) {
                                e32.printStackTrace();
                            }
                        }
                    }
                } catch (FileNotFoundException e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

    class C01723 implements YUVDataCallBackInterface {
        C01723() {
        }

        public void update(byte[] ydata, byte[] udata, byte[] vdata) {
            synchronized (this) {
            }
        }

        public void update(int w, int h) {
            if (w > 0 && h > 0) {
                if (GLFrameRenderer.mScreenWidth > 0) {
                }
                if (w != GLFrameRenderer.this.mVideoWidth && h != GLFrameRenderer.this.mVideoHeight) {
                    GLFrameRenderer.this.mVideoWidth = w;
                    GLFrameRenderer.this.mVideoHeight = h;
                    int uvarraySize = (w * h) / 4;
                    synchronized (this) {
                        GLFrameRenderer.this.yuv = new byte[(((w * h) * 3) / 2)];
                    }
                }
            }
        }

        public void update(byte[] yuvdata) {
            if (GLFrameRenderer.this.yuv != null) {
                System.arraycopy(yuvdata, 0, GLFrameRenderer.this.yuv, 0, yuvdata.length);
                GLFrameRenderer.this.bUpdated = true;
            }
        }
    }

    public GLFrameRenderer(Context context, GLSurfaceView surface, DisplayMetrics dm) {
        this.mContext = context;
        this.mTargetSurface = surface;
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        FHSDK.registerUpdateCallBack(this.dataFun);
        this.mHandler = new Handler();
        this.mHandler.post(this.requestRender);
        this.mSnapshotThread = new SnapshotThread();
    }

    public static GLFrameRenderer getInstance() {
        if (instance == null) {
            instance = new GLFrameRenderer();
        }
        return instance;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        log.m0d("GLFrameRenderer :: onSurfaceCreated");
        log.m0d("GLFrameRenderer :: buildProgram done");
        MyMediaCodec.getInstance().startPlay(null);
        this.bSurfaceCreate = true;
        displayMode = 0;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        log.m0d("GLFrameRenderer :: onSurfaceChanged()(" + width + "," + height + ")");
        this.bIsTime2Draw = false;
        mScreenWidth = width;
        mScreenHeight = height;
        if (mScreenWidth < mScreenHeight) {
            int i = mScreenWidth;
            mDrawHeight = i;
            mDrawWidth = i;
        } else {
            mDrawWidth = mScreenWidth;
            mDrawHeight = mScreenHeight;
        }
        this.bSurfaceChanged = true;
        this.view_x = (mScreenWidth - mDrawWidth) / 2;
        this.view_y = (mScreenHeight - mDrawHeight) / 2;
        this.view_w = mDrawWidth;
        this.view_h = mDrawHeight;
        if (PlayInfo.udpDevType == 6) {
            FHSDK.init(mDrawWidth, mDrawHeight);
            if (hBuffer == 0) {
                hBuffer = FHSDK.createBuffer(0);
            }
            if (hWin == 0) {
                hWin = FHSDK.createWindow(displayMode);
            }
            FHSDK.unbind(hWin);
            FHSDK.bind(hWin, hBuffer);
            depth = FHSDK.getMaxZDepth(hWin);
            return;
        }
        if (hWin != 0) {
            FHSDK.FHunInit(hWin);
            hWin = 0;
        }
        hWin = FHSDK.FHinit(mDrawWidth, mDrawHeight);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 r15) {
        /*
        r14 = this;
        r13 = 3;
        r12 = 4;
        r11 = 2;
        r10 = 1;
        monitor-enter(r14);
        r6 = com.fh.lib.PlayInfo.udpDevType;	 Catch:{ all -> 0x00c0 }
        r7 = 6;
        if (r6 != r7) goto L_0x0264;
    L_0x000a:
        r6 = displayMode;	 Catch:{ all -> 0x00c0 }
        r7 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = com.fh.lib.FHSDK.getDisplayMode(r7);	 Catch:{ all -> 0x00c0 }
        if (r6 == r7) goto L_0x002d;
    L_0x0014:
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.unbind(r6);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.destroyWindow(r6);	 Catch:{ all -> 0x00c0 }
        r6 = displayMode;	 Catch:{ all -> 0x00c0 }
        r6 = com.fh.lib.FHSDK.createWindow(r6);	 Catch:{ all -> 0x00c0 }
        hWin = r6;	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = hBuffer;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.bind(r6, r7);	 Catch:{ all -> 0x00c0 }
    L_0x002d:
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = circleX;	 Catch:{ all -> 0x00c0 }
        r8 = circleY;	 Catch:{ all -> 0x00c0 }
        r9 = circleR;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.setStandardCircle(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = r14.yuv;	 Catch:{ all -> 0x00c0 }
        if (r6 == 0) goto L_0x0051;
    L_0x003c:
        r6 = r14.bUpdated;	 Catch:{ all -> 0x00c0 }
        if (r6 == 0) goto L_0x0051;
    L_0x0040:
        r6 = hBuffer;	 Catch:{ all -> 0x00c0 }
        r7 = r14.yuv;	 Catch:{ all -> 0x00c0 }
        r8 = r14.mVideoWidth;	 Catch:{ all -> 0x00c0 }
        r9 = r14.mVideoHeight;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.update(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r14.bUpdated = r6;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r14.bIsTime2Draw = r6;	 Catch:{ all -> 0x00c0 }
    L_0x0051:
        r6 = r14.bIsTime2Draw;	 Catch:{ all -> 0x00c0 }
        if (r6 != 0) goto L_0x0057;
    L_0x0055:
        monitor-exit(r14);	 Catch:{ all -> 0x00c0 }
    L_0x0056:
        return;
    L_0x0057:
        com.fh.lib.FHSDK.clear();	 Catch:{ all -> 0x00c0 }
        r6 = bMixMode;	 Catch:{ all -> 0x00c0 }
        if (r6 == 0) goto L_0x00f3;
    L_0x005e:
        r6 = 4;
        r4 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 3;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 4;
        r5 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 3;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r2 = 0;
        r1 = 0;
    L_0x00a2:
        if (r1 < r12) goto L_0x00c3;
    L_0x00a4:
        r6 = bSnapshot;	 Catch:{ all -> 0x00c0 }
        if (r6 == 0) goto L_0x00be;
    L_0x00a8:
        r6 = 0;
        bSnapshot = r6;	 Catch:{ all -> 0x00c0 }
        r6 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r9 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r6 = com.fh.lib.FHSDK.snapshot(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r14.frameBuf = r6;	 Catch:{ all -> 0x00c0 }
        r6 = r14.mSnapshotThread;	 Catch:{ all -> 0x00c0 }
        r6.start();	 Catch:{ all -> 0x00c0 }
    L_0x00be:
        monitor-exit(r14);	 Catch:{ all -> 0x00c0 }
        goto L_0x0056;
    L_0x00c0:
        r6 = move-exception;
        monitor-exit(r14);	 Catch:{ all -> 0x00c0 }
        throw r6;
    L_0x00c3:
        r6 = hWinMixMode;	 Catch:{ all -> 0x00c0 }
        r6 = r6[r1];	 Catch:{ all -> 0x00c0 }
        r7 = circleX;	 Catch:{ all -> 0x00c0 }
        r8 = circleY;	 Catch:{ all -> 0x00c0 }
        r9 = circleR;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.setStandardCircle(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = r4[r1];	 Catch:{ all -> 0x00c0 }
        r7 = r5[r1];	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r9 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r9 = r9 / 2;
        com.fh.lib.FHSDK.viewport(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        if (r10 != r1) goto L_0x00e9;
    L_0x00e1:
        r6 = hWinMixMode;	 Catch:{ all -> 0x00c0 }
        r6 = r6[r1];	 Catch:{ all -> 0x00c0 }
        r7 = 1;
        com.fh.lib.FHSDK.setImagingType(r6, r7);	 Catch:{ all -> 0x00c0 }
    L_0x00e9:
        r6 = hWinMixMode;	 Catch:{ all -> 0x00c0 }
        r6 = r6[r1];	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.draw(r6);	 Catch:{ all -> 0x00c0 }
        r1 = r1 + 1;
        goto L_0x00a2;
    L_0x00f3:
        r6 = displayMode;	 Catch:{ all -> 0x00c0 }
        if (r6 == 0) goto L_0x00fc;
    L_0x00f7:
        r6 = 6;
        r7 = displayMode;	 Catch:{ all -> 0x00c0 }
        if (r6 != r7) goto L_0x024b;
    L_0x00fc:
        r6 = eyeMode;	 Catch:{ all -> 0x00c0 }
        if (r6 != 0) goto L_0x011c;
    L_0x0100:
        r6 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r9 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.viewport(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = vDegrees;	 Catch:{ all -> 0x00c0 }
        r8 = hDegrees;	 Catch:{ all -> 0x00c0 }
        r9 = depth;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.eyeLookAt(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.draw(r6);	 Catch:{ all -> 0x00c0 }
        goto L_0x00a4;
    L_0x011c:
        r6 = eyeMode;	 Catch:{ all -> 0x00c0 }
        if (r10 != r6) goto L_0x018e;
    L_0x0120:
        r6 = 4;
        r4 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 3;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 4;
        r5 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 3;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r1 = 0;
    L_0x0163:
        if (r1 >= r12) goto L_0x00a4;
    L_0x0165:
        r6 = r4[r1];	 Catch:{ all -> 0x00c0 }
        r7 = r5[r1];	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r9 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r9 = r9 / 2;
        com.fh.lib.FHSDK.viewport(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = com.fh.lib.FHSDK.getMaxVDegress(r7);	 Catch:{ all -> 0x00c0 }
        r8 = hDegrees;	 Catch:{ all -> 0x00c0 }
        r9 = r1 * 90;
        r9 = (float) r9;	 Catch:{ all -> 0x00c0 }
        r8 = r8 + r9;
        r9 = 0;
        com.fh.lib.FHSDK.eyeLookAt(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.draw(r6);	 Catch:{ all -> 0x00c0 }
        r1 = r1 + 1;
        goto L_0x0163;
    L_0x018e:
        r6 = eyeMode;	 Catch:{ all -> 0x00c0 }
        if (r11 != r6) goto L_0x01fe;
    L_0x0192:
        r6 = 4;
        r4 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 3;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 4;
        r5 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 3;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r1 = 0;
    L_0x01d5:
        if (r1 >= r12) goto L_0x00a4;
    L_0x01d7:
        r6 = r4[r1];	 Catch:{ all -> 0x00c0 }
        r7 = r5[r1];	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r9 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r9 = r9 / 2;
        com.fh.lib.FHSDK.viewport(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = com.fh.lib.FHSDK.getMaxVDegress(r7);	 Catch:{ all -> 0x00c0 }
        r8 = hEyeDegrees;	 Catch:{ all -> 0x00c0 }
        r8 = r8[r1];	 Catch:{ all -> 0x00c0 }
        r9 = 0;
        com.fh.lib.FHSDK.eyeLookAt(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.draw(r6);	 Catch:{ all -> 0x00c0 }
        r1 = r1 + 1;
        goto L_0x01d5;
    L_0x01fe:
        r6 = eyeMode;	 Catch:{ all -> 0x00c0 }
        if (r13 != r6) goto L_0x00a4;
    L_0x0202:
        r6 = 2;
        r4 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r5 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r1 = 0;
    L_0x0222:
        if (r1 >= r11) goto L_0x00a4;
    L_0x0224:
        r6 = r4[r1];	 Catch:{ all -> 0x00c0 }
        r7 = r5[r1];	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r9 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.viewport(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = vDegrees;	 Catch:{ all -> 0x00c0 }
        r8 = hDegrees;	 Catch:{ all -> 0x00c0 }
        r9 = 0;
        com.fh.lib.FHSDK.eyeLookAt(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = vDegrees;	 Catch:{ all -> 0x00c0 }
        r14.lastVDegrees = r6;	 Catch:{ all -> 0x00c0 }
        r6 = hDegrees;	 Catch:{ all -> 0x00c0 }
        r14.lastHDegrees = r6;	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.draw(r6);	 Catch:{ all -> 0x00c0 }
        r1 = r1 + 1;
        goto L_0x0222;
    L_0x024b:
        r6 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r9 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.viewport(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = hOffset;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.expandLookAt(r6, r7);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.draw(r6);	 Catch:{ all -> 0x00c0 }
        goto L_0x00a4;
    L_0x0264:
        r6 = r14.yuv;	 Catch:{ all -> 0x00c0 }
        if (r6 == 0) goto L_0x027d;
    L_0x0268:
        r6 = r14.bUpdated;	 Catch:{ all -> 0x00c0 }
        if (r6 == 0) goto L_0x027d;
    L_0x026c:
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        r7 = r14.yuv;	 Catch:{ all -> 0x00c0 }
        r8 = r14.mVideoWidth;	 Catch:{ all -> 0x00c0 }
        r9 = r14.mVideoHeight;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.FHupdate(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r14.bUpdated = r6;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r14.bIsTime2Draw = r6;	 Catch:{ all -> 0x00c0 }
    L_0x027d:
        r6 = r14.bIsTime2Draw;	 Catch:{ all -> 0x00c0 }
        if (r6 != 0) goto L_0x0284;
    L_0x0281:
        monitor-exit(r14);	 Catch:{ all -> 0x00c0 }
        goto L_0x0056;
    L_0x0284:
        com.fh.lib.FHSDK.FHclear();	 Catch:{ all -> 0x00c0 }
        r6 = com.app.Interface.MyMediaCodec.SHOW_MODE_3D;	 Catch:{ all -> 0x00c0 }
        r7 = com.app.Interface.MyMediaCodec.getInstance();	 Catch:{ all -> 0x00c0 }
        r7 = r7.getShowMode();	 Catch:{ all -> 0x00c0 }
        if (r6 != r7) goto L_0x0300;
    L_0x0293:
        r6 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r3 = r6 / 2;
        r6 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r6 = (float) r6;	 Catch:{ all -> 0x00c0 }
        r7 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = r6 / r7;
        r7 = r14.mVideoHeight;	 Catch:{ all -> 0x00c0 }
        r7 = (float) r7;	 Catch:{ all -> 0x00c0 }
        r8 = r14.mVideoWidth;	 Catch:{ all -> 0x00c0 }
        r8 = (float) r8;	 Catch:{ all -> 0x00c0 }
        r7 = r7 / r8;
        r6 = r6 * r7;
        r0 = (int) r6;	 Catch:{ all -> 0x00c0 }
        r6 = 4;
        r4 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 3;
        r7 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r4[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 4;
        r5 = new int[r6];	 Catch:{ all -> 0x00c0 }
        r6 = 0;
        r7 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r7 = r7 - r0;
        r7 = r7 / 2;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 1;
        r7 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r7 = r7 - r0;
        r7 = r7 / 2;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 2;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r6 = 3;
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        r8 = r8 / 2;
        r7 = r7 + r8;
        r5[r6] = r7;	 Catch:{ all -> 0x00c0 }
        r1 = 0;
    L_0x02ef:
        if (r1 >= r11) goto L_0x00be;
    L_0x02f1:
        r6 = r4[r1];	 Catch:{ all -> 0x00c0 }
        r7 = r5[r1];	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.FHviewport(r6, r7, r3, r0);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.FHdraw(r6);	 Catch:{ all -> 0x00c0 }
        r1 = r1 + 1;
        goto L_0x02ef;
    L_0x0300:
        r6 = r14.view_x;	 Catch:{ all -> 0x00c0 }
        r7 = r14.view_y;	 Catch:{ all -> 0x00c0 }
        r8 = r14.view_w;	 Catch:{ all -> 0x00c0 }
        r9 = r14.view_h;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.FHviewport(r6, r7, r8, r9);	 Catch:{ all -> 0x00c0 }
        r6 = hWin;	 Catch:{ all -> 0x00c0 }
        com.fh.lib.FHSDK.FHdraw(r6);	 Catch:{ all -> 0x00c0 }
        goto L_0x00be;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.opengles.GLFrameRenderer.onDrawFrame(javax.microedition.khronos.opengles.GL10):void");
    }

    public void setvelocityX(float velocityX) {
        velocityX = velocityX;
        if (Math.abs(velocityX) > 3000.0f) {
            scrollStep = STEP_BASE_FAST;
        } else {
            scrollStep = STEP_BASE_SLOW;
        }
    }

    public void setvelocityY(float velocityY) {
        velocityY = velocityY;
        if (Math.abs(velocityY) > 3000.0f) {
            scrollStep = STEP_BASE_FAST;
        } else {
            scrollStep = STEP_BASE_SLOW;
        }
    }
}
