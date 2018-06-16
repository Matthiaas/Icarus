package com.bwin.airtoplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RTSPPlayerView extends SurfaceView implements android.os.Handler.Callback {
    private static final String TAG = "RTSPPlayerView";
    public static RTSPPlayerStatus status = RTSPPlayerStatus.STOPPED;
    private static Matrix videoScaleMatrix;
    private boolean b3DView = false;
    private boolean bFlipCamera = false;
    private Callback mCallback;
    private Thread mDecoderInitThread;
    private Thread mDecoderRunningThread;
    private SurfaceHolder mHolder;
    private Handler mPlayerHandler;

    class C00791 implements Runnable {
        C00791() {
        }

        public void run() {
            int retVal = RTSPPlayerView.this.recordFrame();
            Message message = new Message();
            message.what = 4;
            RTSPPlayerView.this.mPlayerHandler.sendMessage(message);
        }
    }

    public interface Callback {
        void rtspPlayerStatusChanged(int i);
    }

    private class DecoderInitThread extends Thread {
        int ret;

        private DecoderInitThread() {
            this.ret = -1;
        }

        public void run() {
            RTSPPlayerView.status = RTSPPlayerStatus.INITIALIZING;
            while (!isInterrupted()) {
                int access$0 = RTSPPlayerView.this.initDecoder(Constants.RTSP_ADDRESS);
                this.ret = access$0;
                if (access$0 >= 0) {
                    break;
                }
                Log.e(RTSPPlayerView.TAG, "Failed to initDecoder");
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    currentThread().interrupt();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (this.ret >= 0) {
                Message message = new Message();
                message.what = 1;
                RTSPPlayerView.this.mPlayerHandler.sendMessage(message);
                RTSPPlayerView.status = RTSPPlayerStatus.INITIALIZED;
            }
        }
    }

    private class DecoderRunningThread extends Thread {
        Bitmap bitmap;

        private DecoderRunningThread() {
        }

        public void run() {
            RTSPPlayerView.status = RTSPPlayerStatus.DECODING;
            while (!isInterrupted()) {
                this.bitmap = RTSPPlayerView.this.readDecodedImage();
                if (this.bitmap == null) {
                    Message message = new Message();
                    message.what = 3;
                    RTSPPlayerView.this.mPlayerHandler.sendMessage(message);
                    break;
                }
                message = new Message();
                message.what = 2;
                message.obj = this.bitmap;
                RTSPPlayerView.this.mPlayerHandler.sendMessage(message);
            }
            RTSPPlayerView.status = RTSPPlayerStatus.STOPPED;
            RTSPPlayerView.this.deinitDecoder();
        }
    }

    public enum RTSPPlayerStatus {
        INITIALIZING,
        INITIALIZED,
        DECODING,
        RECORDING,
        STOPPED
    }

    private native void deinitDecoder();

    private native int initDecoder(String str);

    private native Bitmap readDecodedImage();

    private native int recordFrame();

    private native void sendRTCPRawData(int[] iArr, int i);

    private native int startRecording(String str);

    private native int stopRecoding();

    private native int takeScreenshot(String str);

    static {
        System.loadLibrary("RTSPPlayerCodec");
        System.loadLibrary("RTSPPlayer");
    }

    public boolean isFlipCamera() {
        return this.bFlipCamera;
    }

    public void setFlipCamera(boolean bFlipCamera) {
        this.bFlipCamera = bFlipCamera;
    }

    public boolean is3DView() {
        return this.b3DView;
    }

    public void set3DView(boolean b3DView) {
        this.b3DView = b3DView;
    }

    public RTSPPlayerView(Context context) {
        super(context);
        init();
    }

    public RTSPPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RTSPPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Log.i(TAG, "---- init ----");
        this.mHolder = getHolder();
        this.mHolder.setFormat(1);
        this.mPlayerHandler = new Handler(this);
    }

    public void initPlaying() {
        Log.d(TAG, "---- initPlaying() ----");
        this.mDecoderInitThread = new DecoderInitThread();
        this.mDecoderInitThread.start();
    }

    public void startPlaying() {
        Log.d(TAG, "---- start startPlaying() ----");
        this.mDecoderRunningThread = new DecoderRunningThread();
        this.mDecoderRunningThread.start();
    }

    public void stopPlaying() {
        Log.d(TAG, "---- start stopPlaying() ----");
        if (status == RTSPPlayerStatus.INITIALIZING) {
            Log.d(TAG, "Stop INITIALIZING");
            if (this.mDecoderInitThread != null) {
                this.mDecoderInitThread.interrupt();
            }
        } else if (status == RTSPPlayerStatus.INITIALIZED) {
            Log.d(TAG, "Release INITIALIZED");
            deinitDecoder();
        } else if (status == RTSPPlayerStatus.DECODING || status == RTSPPlayerStatus.RECORDING) {
            Log.d(TAG, "Stop DECODING or RECORDING");
            if (status == RTSPPlayerStatus.RECORDING) {
                stopRecodingVideo();
            }
            if (this.mDecoderRunningThread != null) {
                this.mDecoderRunningThread.interrupt();
            }
        }
    }

    public int saveScreenshot(String filePath) {
        return takeScreenshot(filePath);
    }

    public int startRecordingVideo(String filePath) {
        int retVal = startRecording(filePath);
        if (retVal >= 0) {
            status = RTSPPlayerStatus.RECORDING;
            new Thread(new C00791()).start();
        }
        return retVal;
    }

    public int stopRecodingVideo() {
        int retVal = stopRecoding();
        status = RTSPPlayerStatus.DECODING;
        return retVal;
    }

    public synchronized boolean handleMessage(Message message) {
        int what = message.what;
        switch (what) {
            case 1:
            case 3:
            case 4:
                break;
            case 2:
                Bitmap bitmap = message.obj;
                int bitmapWidth = bitmap.getWidth();
                int bitmapHeight = bitmap.getHeight();
                int canvasWidth = getWidth();
                int canvasHeight = getHeight();
                Canvas canvas = null;
                try {
                    canvas = this.mHolder.lockCanvas();
                    if (this.bFlipCamera) {
                        canvas.rotate(180.0f, (float) (canvasWidth / 2), (float) (canvasHeight / 2));
                    }
                    if (this.b3DView) {
                        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmapWidth, bitmapHeight), new Rect(0, 0, canvasWidth / 2, canvasHeight), null);
                        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmapWidth, bitmapHeight), new Rect(canvasWidth / 2, 0, canvasWidth, canvasHeight), null);
                    } else {
                        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmapWidth, bitmapHeight), new Rect(0, 0, canvasWidth, canvasHeight), null);
                    }
                    if (canvas != null) {
                        this.mHolder.unlockCanvasAndPost(canvas);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (canvas != null) {
                        this.mHolder.unlockCanvasAndPost(canvas);
                        break;
                    }
                } catch (Throwable th) {
                    if (canvas != null) {
                        this.mHolder.unlockCanvasAndPost(canvas);
                    }
                }
                break;
            default:
                Log.i(TAG, "handleMessage: Why run into here?");
                break;
        }
        if (this.mCallback != null) {
            this.mCallback.rtspPlayerStatusChanged(what);
        }
        return true;
    }

    public void addCallback(Callback callback) {
        this.mCallback = callback;
    }

    public void sendRTCPCommand(int[] data) {
        sendRTCPRawData(data, data.length);
    }
}
