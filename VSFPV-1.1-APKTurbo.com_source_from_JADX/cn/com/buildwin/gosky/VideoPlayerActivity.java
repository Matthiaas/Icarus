package cn.com.buildwin.gosky;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import et.song.vspfv.C0127R;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.libsdl.app.SDLActivity;

public class VideoPlayerActivity extends SDLActivity implements OnSeekBarChangeListener {
    static final int COMMAND_ON_SURFACE_CLICK = 32771;
    static final int COMMAND_SET_DURATION = 32769;
    static final int COMMAND_SET_PROGRESS = 32770;
    private static final String TAG = VideoPlayerActivity.class.getSimpleName();
    private OnClickListener bntClickListener = new C00621();
    ImageButton mBackButton;
    private Timer mControlPanelHiddenTimer;
    RelativeLayout mControllerLayout;
    TextView mCurrentTimeTextView;
    private int mDuration;
    TextView mFilenameTextView;
    ImageButton mPlayButton;
    ImageButton mScaleButton;
    SeekBar mSeekbar;
    TextView mTotalTimeTextView;
    private String mVideoFilePath;
    private boolean paused = false;
    private boolean scaled = false;

    class C00621 implements OnClickListener {
        C00621() {
        }

        public void onClick(View view) {
            boolean z = false;
            VideoPlayerActivity videoPlayerActivity;
            switch (view.getId()) {
                case C0127R.id.video_player_controller_layout:
                    VideoPlayerActivity.this.showOrHideControlPanel();
                    return;
                case C0127R.id.video_player_backButton:
                    SDLActivity.nativeQuit();
                    return;
                case C0127R.id.video_player_scale_button:
                    videoPlayerActivity = VideoPlayerActivity.this;
                    if (!VideoPlayerActivity.this.scaled) {
                        z = true;
                    }
                    videoPlayerActivity.scaled = z;
                    VideoPlayerActivity.this.mScaleButton.setImageResource(VideoPlayerActivity.this.scaled ? C0127R.mipmap.vp_scale_fit : C0127R.mipmap.vp_scale_fill);
                    SDLActivity.nativeToggleScale();
                    return;
                case C0127R.id.video_player_play_button:
                    videoPlayerActivity = VideoPlayerActivity.this;
                    if (!VideoPlayerActivity.this.paused) {
                        z = true;
                    }
                    videoPlayerActivity.paused = z;
                    VideoPlayerActivity.this.mPlayButton.setImageResource(VideoPlayerActivity.this.paused ? C0127R.mipmap.vp_play : C0127R.mipmap.vp_pause);
                    SDLActivity.nativeTogglePlayPause();
                    if (VideoPlayerActivity.this.paused) {
                        VideoPlayerActivity.this.cancelControlPanelHidenTimer();
                        return;
                    } else {
                        VideoPlayerActivity.this.mControlPanelHiddenTimer = VideoPlayerActivity.this.createControlPanelHiddenTimer();
                        return;
                    }
                default:
                    return;
            }
        }
    }

    private class ControlPanelHiddenTimerTask extends TimerTask {

        class C00631 implements Runnable {
            C00631() {
            }

            public void run() {
                VideoPlayerActivity.this.mControllerLayout.setVisibility(4);
            }
        }

        private ControlPanelHiddenTimerTask() {
        }

        public void run() {
            new Handler(VideoPlayerActivity.this.getMainLooper()).post(new C00631());
            VideoPlayerActivity.this.cancelControlPanelHidenTimer();
        }
    }

    protected String[] getLibraries() {
        return super.getLibraries();
    }

    protected String[] getArguments() {
        return new String[]{this.mVideoFilePath};
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        super.onCreate(savedInstanceState);
        this.mVideoFilePath = getIntent().getStringExtra("VideoFilePath");
        View view = View.inflate(SDLActivity.getContext(), C0127R.layout.layout_video_player, null);
        addContentView(view, new LayoutParams(-1, -1));
        this.mPlayButton = (ImageButton) view.findViewById(C0127R.id.video_player_play_button);
        this.mBackButton = (ImageButton) view.findViewById(C0127R.id.video_player_backButton);
        this.mFilenameTextView = (TextView) view.findViewById(C0127R.id.video_player_filename_TextView);
        this.mCurrentTimeTextView = (TextView) view.findViewById(C0127R.id.video_player_current_time_TextView);
        this.mTotalTimeTextView = (TextView) view.findViewById(C0127R.id.video_player_total_time_TextView);
        this.mScaleButton = (ImageButton) view.findViewById(C0127R.id.video_player_scale_button);
        this.mSeekbar = (SeekBar) view.findViewById(C0127R.id.video_player_seekbar);
        this.mControllerLayout = (RelativeLayout) view.findViewById(C0127R.id.video_player_controller_layout);
        this.mPlayButton.setOnClickListener(this.bntClickListener);
        this.mBackButton.setOnClickListener(this.bntClickListener);
        this.mScaleButton.setOnClickListener(this.bntClickListener);
        this.mControllerLayout.setOnClickListener(this.bntClickListener);
        String strPath = this.mVideoFilePath;
        this.mFilenameTextView.setText(strPath.substring(strPath.lastIndexOf("/") + 1));
        this.mSeekbar.setOnSeekBarChangeListener(this);
        this.mControlPanelHiddenTimer = createControlPanelHiddenTimer();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private String convertTimeToString(int time) {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)});
    }

    protected boolean onUnhandledMessage(int command, Object param) {
        switch (command) {
            case COMMAND_SET_DURATION /*32769*/:
                this.mDuration = ((Integer) param).intValue();
                this.mTotalTimeTextView.setText(convertTimeToString(this.mDuration));
                return true;
            case COMMAND_SET_PROGRESS /*32770*/:
                int progress = ((Integer) param).intValue();
                if (this.mDuration != 0) {
                    this.mSeekbar.setProgress((this.mSeekbar.getMax() * progress) / this.mDuration);
                }
                this.mCurrentTimeTextView.setText(convertTimeToString(progress));
                return true;
            case COMMAND_ON_SURFACE_CLICK /*32771*/:
                showOrHideControlPanel();
                return true;
            default:
                return super.onUnhandledMessage(command, param);
        }
    }

    private Timer createControlPanelHiddenTimer() {
        Timer timer = new Timer("ControlPanelHidden");
        timer.schedule(new ControlPanelHiddenTimerTask(), 3000);
        return timer;
    }

    private void cancelControlPanelHidenTimer() {
        if (this.mControlPanelHiddenTimer != null) {
            this.mControlPanelHiddenTimer.cancel();
            this.mControlPanelHiddenTimer = null;
        }
    }

    private void showOrHideControlPanel() {
        if (this.mControllerLayout.getVisibility() == 0) {
            this.mControllerLayout.setVisibility(4);
            cancelControlPanelHidenTimer();
            return;
        }
        this.mControllerLayout.setVisibility(0);
        this.mControlPanelHiddenTimer = createControlPanelHiddenTimer();
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            SDLActivity.nativeSeek(progress);
            this.mCurrentTimeTextView.setText(convertTimeToString((int) ((((double) seekBar.getProgress()) / ((double) seekBar.getMax())) * ((double) this.mDuration))));
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        cancelControlPanelHidenTimer();
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getProgress() == seekBar.getMax()) {
            SDLActivity.nativeQuit();
        }
        SDLActivity.nativeSeek((int) ((((double) seekBar.getProgress()) / ((double) seekBar.getMax())) * 100.0d));
        this.mControlPanelHiddenTimer = createControlPanelHiddenTimer();
    }
}
