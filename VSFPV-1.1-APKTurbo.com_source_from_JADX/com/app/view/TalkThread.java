package com.app.view;

import android.media.AudioRecord;
import android.util.Log;
import com.fh.lib.FHSDK;
import com.fh.lib.PlayInfo;

/* compiled from: PreviewView */
class TalkThread implements Runnable {
    private static final int BUFFER_FRAME_SIZE = 2048;
    private static final int audioFormat = 2;
    private static final int audioSource = 1;
    private static final int channelConfig = 16;
    private static int sampleRate;
    private static int sendFormat = 0;
    String LOG = "Recorder ";
    private int audioBufSize = 0;
    private AudioRecord audioRecord;
    private int bufferRead = 0;
    private int bufferSize = 0;
    private boolean isRecording = false;
    private byte[] samples;

    TalkThread() {
    }

    public void startRecording() {
        if (PreviewView.talkSample == 0) {
            sampleRate = 8000;
        } else {
            sampleRate = 16000;
        }
        this.audioBufSize = AudioRecord.getMinBufferSize(sampleRate, 16, 2);
        if (this.audioBufSize == -2) {
            Log.e(this.LOG, "audioBufSize error");
            return;
        }
        Log.e(this.LOG, "audioBufSize = " + this.audioBufSize);
        if (this.audioRecord == null) {
            this.audioRecord = new AudioRecord(1, sampleRate, 16, 2, this.audioBufSize);
        }
        new Thread(this).start();
    }

    public void stopRecording() {
        this.isRecording = false;
    }

    public boolean isRecording() {
        return this.isRecording;
    }

    public void run() {
        Log.e(this.LOG, "audioRecord startRecording()");
        this.bufferSize = FHSDK.getTalkUnitSize(PlayInfo.userID);
        if (this.bufferSize <= 0) {
            this.bufferSize = 2048;
        }
        if (this.bufferSize > this.audioBufSize) {
            this.bufferSize = this.audioBufSize;
        }
        this.samples = new byte[this.audioBufSize];
        this.audioRecord.startRecording();
        this.isRecording = true;
        FHSDK.startTalk(PlayInfo.userID);
        sendFormat = PreviewView.talkFormat;
        while (this.isRecording) {
            this.bufferRead = this.audioRecord.read(this.samples, 0, this.bufferSize);
            if (this.bufferRead > 0) {
                byte[] tempData = new byte[this.bufferRead];
                System.arraycopy(this.samples, 0, tempData, 0, this.bufferRead);
                FHSDK.sendTalkData(tempData, this.bufferRead, sampleRate, sendFormat);
            }
        }
        System.out.println(this.LOG + "stop");
        this.audioRecord.stop();
    }
}
