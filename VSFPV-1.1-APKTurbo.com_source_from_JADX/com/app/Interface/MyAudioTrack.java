package com.app.Interface;

import android.media.AudioTrack;
import android.util.Log;

public class MyAudioTrack {
    private static final String TAG = "AudioPlay";
    protected static AudioTrack mAudioTrack;

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
        StringBuilder append = new StringBuilder("audio: wanted ").append(isStereo ? "stereo" : "mono").append(" ");
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
        Log.v(TAG, "audio: got " + (mAudioTrack.getChannelCount() >= 2 ? "stereo" : "mono") + " " + (mAudioTrack.getAudioFormat() == 2 ? "16-bit" : "8-bit") + " " + (((float) mAudioTrack.getSampleRate()) / 1000.0f) + "kHz, " + desiredFrames + " frames buffer");
        return 0;
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
                Log.w(TAG, "audio: error return from write(byte)");
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
}
