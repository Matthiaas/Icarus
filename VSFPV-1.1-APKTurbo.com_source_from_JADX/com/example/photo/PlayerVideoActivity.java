package com.example.photo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import et.song.vspfv.C0127R;
import et.song.vspfv.SysApp;
import java.io.File;
import java.util.LinkedList;

public class PlayerVideoActivity extends Activity {
    private MediaController mController;
    LinkedList<File> mDirList = new LinkedList();
    private String mPath;
    private int mPlayIndex;
    private FSVideoView vv_video;

    class C00851 implements OnClickListener {
        C00851() {
        }

        public void onClick(View v) {
            PlayerVideoActivity.this.PlayVideo(1);
        }
    }

    class C00862 implements OnClickListener {
        C00862() {
        }

        public void onClick(View v) {
            PlayerVideoActivity.this.PlayVideo(-1);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        setContentView(C0127R.layout.activity_show_video);
        this.mPath = getIntent().getStringExtra("path");
        ScanVideoFiles();
        CheckCurIndex(this.mPath);
        this.vv_video = (FSVideoView) findViewById(C0127R.id.fSVideoView1);
        this.mController = new MediaController(this);
        File file = new File(this.mPath);
        if (file.exists()) {
            this.vv_video.setVideoPath(file.getAbsolutePath());
            this.vv_video.setMediaController(this.mController);
            this.mController.setMediaPlayer(this.vv_video);
            this.mController.setPrevNextListeners(new C00851(), new C00862());
            this.vv_video.start();
        }
    }

    private void PlayVideo(int set) {
        if (this.mDirList.size() > 1) {
            this.mPlayIndex += set;
            if (this.mPlayIndex >= this.mDirList.size()) {
                this.mPlayIndex = 0;
            }
            if (this.mPlayIndex < 0) {
                this.mPlayIndex = this.mDirList.size() - 1;
            }
            this.vv_video.setVideoPath(((File) this.mDirList.get(this.mPlayIndex)).getAbsolutePath());
            this.vv_video.start();
        }
    }

    private void ScanVideoFiles() {
        File dir = new File(SysApp.SAVE_PATH);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int ii = 0; ii < files.length; ii++) {
                if (files[ii].getAbsolutePath().endsWith(".avi")) {
                    this.mDirList.add(files[ii]);
                }
            }
        }
    }

    private void CheckCurIndex(String path) {
        for (int ii = 0; ii < this.mDirList.size(); ii++) {
            if (((File) this.mDirList.get(ii)).getAbsolutePath().equals(path)) {
                this.mPlayIndex = ii;
            }
        }
    }
}
