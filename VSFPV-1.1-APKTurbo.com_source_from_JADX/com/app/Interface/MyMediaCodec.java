package com.app.Interface;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.view.Surface;
import com.android.opengles.GLFrameRenderer;
import com.app.util.log;
import com.fh.lib.Define.FrameHead;
import com.fh.lib.Define.StreamDataCallBackInterface;
import com.fh.lib.FHSDK;
import com.fh.lib.PlayInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyMediaCodec {
    public static int SHOW_MODE_3D = 2;
    public static int SHOW_MODE_FULLSCREEN = 1;
    public static int SHOW_MODE_NORMAL = 0;
    private static String TAG = "MediaCodecInterface";
    private static MyMediaCodec instance;
    public int curVideoHeight;
    public int curVideoWidth;
    private boolean forceIframe = false;
    public StreamDataCallBackInterface fun = new C01731();
    private ByteBuffer[] inputBuffers;
    public boolean isUpdateShowRect = true;
    private byte[] lastPPSData;
    private byte[] lastSPSData;
    protected Thread mDecodeThread;
    private int mFrameNo = 0;
    public GLFrameRenderer mFrameRender;
    public int mLastVideoHeight;
    public int mLastVideoWidth;
    private Surface mSurface = null;
    public int mVideoHeight;
    public int mVideoWidth;
    public YUVData mYUVData = null;
    private MediaCodec mediaCodec;
    private boolean mediaCodecStartF = false;
    private MediaFormat mediaFormat;
    private ByteBuffer[] outputBuffers;
    private int playHandle;
    private byte[] ppsData;
    final BlockingQueue<DecodeDataQueue> queue = new LinkedBlockingQueue(100);
    private int showMode = 0;
    private byte[] spsData;
    protected boolean threadStartF = false;

    public class DecodeDataQueue {
        public int mBufLen;
        public byte[] mDataBuf;
        public int mFrameNo;
        public int mFrameType;
        public int mStreamType;
        public int mVideoHeight;
        public int mVideoWidth;
    }

    class PlayThread implements Runnable {
        PlayThread() {
        }

        public void run() {
            synchronized (this) {
                FHSDK.audioInit();
                FHSDK.startPlay();
            }
        }
    }

    public class YUVData {
        public byte[] dataU;
        public byte[] dataV;
        public byte[] dataY;
        public byte[] dataYUV;
        public byte[] dataYV12;
    }

    class decodeThread implements Runnable {
        boolean firstTest = true;

        decodeThread() {
        }

        public void run() {
            while (MyMediaCodec.this.threadStartF) {
                if (MyMediaCodec.this.queue.size() < PlayInfo.frameCacheNum) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    DecodeDataQueue q = (DecodeDataQueue) MyMediaCodec.this.queue.poll();
                    if (q != null) {
                        int index = 0;
                        while (index < q.mBufLen - 4) {
                            if ((q.mDataBuf[index + 0] == (byte) 0 && q.mDataBuf[index + 1] == (byte) 0 && q.mDataBuf[index + 2] == (byte) 1) || (q.mDataBuf[index + 0] == (byte) 0 && q.mDataBuf[index + 1] == (byte) 0 && q.mDataBuf[index + 2] == (byte) 0 && q.mDataBuf[index + 3] == (byte) 1)) {
                                if (q.mFrameType == 0 && this.firstTest) {
                                    MyMediaCodec.this.getSPSAndPPS(q.mDataBuf, q.mBufLen);
                                }
                                MyMediaCodec.this.inputFrame(q.mDataBuf, index, q.mBufLen - index, q.mFrameType);
                            } else {
                                index++;
                            }
                        }
                    }
                    synchronized (this) {
                        if (MyMediaCodec.this.outputFrame() < 0) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
            }
            MyMediaCodec.this.mYUVData.dataY = null;
            MyMediaCodec.this.mYUVData.dataU = null;
            MyMediaCodec.this.mYUVData.dataV = null;
        }
    }

    class C01731 implements StreamDataCallBackInterface {
        C01731() {
        }

        public void StreamDataCallBack(int playHandle, int streamType, FrameHead frameHead, byte[] buf, int dataLen) {
            MyMediaCodec.this.mVideoWidth = frameHead.width;
            MyMediaCodec.this.mVideoHeight = frameHead.height;
            DecodeDataQueue q = new DecodeDataQueue();
            q.mDataBuf = buf;
            MyMediaCodec myMediaCodec = MyMediaCodec.this;
            int access$0 = myMediaCodec.mFrameNo;
            myMediaCodec.mFrameNo = access$0 + 1;
            q.mFrameNo = access$0;
            q.mBufLen = dataLen;
            q.mFrameType = frameHead.frameType;
            q.mVideoWidth = frameHead.width;
            q.mVideoHeight = frameHead.height;
            try {
                MyMediaCodec.this.queue.put(q);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static MyMediaCodec getInstance() {
        if (instance == null) {
            instance = new MyMediaCodec();
        }
        return instance;
    }

    public void setShowMode(int mode) {
        this.showMode = mode;
    }

    public int getShowMode() {
        return this.showMode;
    }

    public int getVideoWidth() {
        return this.mVideoWidth;
    }

    public int getVideoHeight() {
        return this.mVideoHeight;
    }

    public void init(GLFrameRenderer mFrameRender) {
        this.mFrameRender = mFrameRender;
        setShowMode(SHOW_MODE_FULLSCREEN);
        if (PlayInfo.decodeType != 0) {
            FHSDK.registerStreamDataCallBack(this.fun);
            this.mYUVData = new YUVData();
            try {
                this.mediaCodec = MediaCodec.createDecoderByType("video/avc");
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mediaFormat = MediaFormat.createVideoFormat("video/avc", 1920, 1080);
            this.mediaFormat.setInteger("color-format", 21);
            this.mDecodeThread = new Thread(new decodeThread(), "decodeThread");
            this.threadStartF = true;
            this.mDecodeThread.start();
        }
    }

    public void unInit() {
        stopPlay();
        this.threadStartF = false;
        if (this.mDecodeThread != null) {
            this.mDecodeThread = null;
        }
        closeMediaCodec();
    }

    private int getSPSAndPPS(byte[] buf, int bufLen) {
        int index;
        int[] pos = new int[]{-1, -1, -1};
        int spsLen = 0;
        int ppsLen = 0;
        int i = 0;
        int index2 = 0;
        while (i < bufLen) {
            if (buf[i] == (byte) 0 && buf[i + 1] == (byte) 0 && buf[i + 2] == (byte) 0 && (byte) 1 == buf[i + 3]) {
                pos[index2] = i;
                index = index2 + 1;
                if (2 == index2) {
                    break;
                }
            } else if (bufLen - 1 == i + 3) {
                index = index2;
                break;
            } else {
                index = index2;
            }
            i++;
            index2 = index;
        }
        index = index2;
        for (i = 0; i < 3; i++) {
            if (pos[i] < 0) {
                return -1;
            }
        }
        int spsOffset = pos[0];
        int ppsOffset = pos[1];
        if (7 == (buf[spsOffset + 4] & 31) && 8 == (buf[ppsOffset + 4] & 31)) {
            spsLen = pos[1] - pos[0];
            ppsLen = pos[2] - pos[1];
        }
        if (this.lastSPSData == null) {
            this.lastSPSData = new byte[spsLen];
        }
        if (this.lastPPSData == null) {
            this.lastPPSData = new byte[ppsLen];
        }
        boolean sameSPS = true;
        if (spsLen == this.lastSPSData.length) {
            for (i = 0; i < spsLen; i++) {
                if (buf[spsOffset + i] != this.lastSPSData[i]) {
                    sameSPS = false;
                    break;
                }
            }
        } else {
            sameSPS = false;
        }
        boolean samePPS = true;
        if (ppsLen == this.lastPPSData.length) {
            for (i = 0; i < ppsLen; i++) {
                if (buf[ppsOffset + i] != this.lastPPSData[i]) {
                    samePPS = false;
                    break;
                }
            }
        } else {
            samePPS = false;
        }
        if (sameSPS && samePPS) {
            return 0;
        }
        this.spsData = new byte[spsLen];
        this.ppsData = new byte[ppsLen];
        for (i = 0; i < spsLen; i++) {
            System.arraycopy(buf, spsOffset, this.spsData, 0, spsLen);
        }
        for (i = 0; i < ppsLen; i++) {
            System.arraycopy(buf, ppsOffset, this.ppsData, 0, ppsLen);
        }
        this.lastSPSData = new byte[spsLen];
        this.lastPPSData = new byte[ppsLen];
        System.arraycopy(this.spsData, 0, this.lastSPSData, 0, spsLen);
        System.arraycopy(this.ppsData, 0, this.lastPPSData, 0, ppsLen);
        openMediaCodec();
        return 0;
    }

    public void openMediaCodec() {
        if (!this.mediaCodecStartF || this.mediaFormat == null) {
            this.mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(this.spsData));
            this.mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(this.ppsData));
            if (PlayInfo.decodeType == 4) {
                this.mediaCodec.configure(this.mediaFormat, this.mSurface, null, 0);
            } else {
                this.mediaCodec.configure(this.mediaFormat, null, null, 0);
            }
            this.mediaCodec.start();
            this.inputBuffers = this.mediaCodec.getInputBuffers();
            this.outputBuffers = this.mediaCodec.getOutputBuffers();
            this.mediaCodecStartF = true;
            return;
        }
        this.mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(this.spsData));
        this.mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(this.ppsData));
    }

    public void closeMediaCodec() {
        try {
            this.mediaCodecStartF = false;
            this.mSurface = null;
            if (this.mediaCodec != null) {
                this.mediaCodec.stop();
                this.mediaCodec.release();
                this.mediaCodec = null;
            }
            this.lastSPSData = null;
            this.lastPPSData = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void inputFrame(byte[] buf, int offset, int dataLen, int frameType) {
        if (this.mediaCodecStartF) {
            try {
                int inputBufferIndex = this.mediaCodec.dequeueInputBuffer(0);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = this.inputBuffers[inputBufferIndex];
                    inputBuffer.clear();
                    inputBuffer.put(buf, offset, dataLen);
                    this.mediaCodec.queueInputBuffer(inputBufferIndex, 0, dataLen, System.currentTimeMillis(), 0);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public int outputFrame() {
        int ret = 0;
        if (this.mediaCodec == null) {
            return -1;
        }
        try {
            BufferInfo bufferInfo = new BufferInfo();
            int outputBufferIndex = this.mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            switch (outputBufferIndex) {
                case -3:
                    this.outputBuffers = this.mediaCodec.getOutputBuffers();
                    break;
                case -2:
                    this.outputBuffers = this.mediaCodec.getOutputBuffers();
                    break;
                case -1:
                    ret = -1;
                    break;
                default:
                    if (PlayInfo.decodeType != 4) {
                        int w = this.mVideoWidth;
                        int h = this.mVideoHeight;
                        if (this.mYUVData.dataY == null && this.mYUVData.dataU == null && this.mYUVData.dataV == null) {
                            this.mYUVData.dataY = new byte[(w * h)];
                            this.mYUVData.dataU = new byte[(((w * h) * 1) / 4)];
                            this.mYUVData.dataV = new byte[(((w * h) * 1) / 4)];
                            this.mYUVData.dataYV12 = new byte[(((w * h) * 3) / 2)];
                            this.mYUVData.dataYUV = new byte[(((w * h) * 3) / 2)];
                        }
                        ByteBuffer buffer = this.outputBuffers[outputBufferIndex];
                        buffer.position(bufferInfo.offset);
                        buffer.limit(bufferInfo.offset + bufferInfo.size);
                        buffer.get(this.mYUVData.dataYV12, 0, this.mYUVData.dataYV12.length);
                        int color = this.mediaCodec.getOutputFormat().getInteger("color-format");
                        if (color == 21) {
                            FHSDK.yuv420sp2yuv(this.mYUVData.dataYV12, w, h, this.mYUVData.dataY, this.mYUVData.dataU, this.mYUVData.dataV);
                            if (this.mFrameRender != null) {
                                this.mFrameRender.dataFun.update(w, h);
                                this.mFrameRender.dataFun.update(this.mYUVData.dataY, this.mYUVData.dataU, this.mYUVData.dataV);
                            } else {
                                FHSDK.send2Sdl(this.mYUVData.dataY, this.mYUVData.dataU, this.mYUVData.dataV, w, h);
                            }
                        } else {
                            log.m1e("unSupport color Format :" + color);
                        }
                    }
                    this.mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                    break;
            }
            return ret;
        } catch (IllegalStateException e) {
            return -1;
        }
    }

    public void stopPlay() {
        FHSDK.stopPlay();
    }

    public void startPlay(Surface mSurface) {
        if (this.mSurface == null) {
            this.mSurface = mSurface;
        }
        new Thread(new PlayThread(), "PlayThread").start();
    }

    public void checkFrame(int frameWidth, int frameHeight) {
        if (this.curVideoWidth == frameWidth && this.curVideoHeight == frameHeight) {
            this.isUpdateShowRect = false;
            return;
        }
        this.curVideoWidth = frameWidth;
        this.curVideoHeight = frameHeight;
        this.isUpdateShowRect = true;
    }

    private static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (codecInfo.isEncoder()) {
                String[] types = codecInfo.getSupportedTypes();
                for (String equalsIgnoreCase : types) {
                    if (equalsIgnoreCase.equalsIgnoreCase(mimeType)) {
                        return codecInfo;
                    }
                }
                continue;
            }
        }
        return null;
    }

    public void cleanQueue() {
        this.queue.clear();
    }
}
