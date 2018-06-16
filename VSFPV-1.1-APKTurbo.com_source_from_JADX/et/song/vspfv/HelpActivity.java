package et.song.vspfv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.app.util.MyApplication;

public class HelpActivity extends Activity implements OnClickListener {
    private FrameLayout background;
    int globalHeight;
    int globalNum;
    int globalWidth;
    final Handler handler = new Handler();
    private int[] imgs = new int[]{C0127R.drawable.img_help1, C0127R.drawable.img_help2, C0127R.drawable.img_help3};
    private ImageButton last;
    private Bitmap mBitmap = null;
    private Bitmap mCutBitmap = null;
    private ImageView mImageHelp;
    Runnable mSetBitmap = new C01181();
    private ImageButton mback;
    private ImageButton next;
    private int position = 0;
    Runnable show = new C01192();

    class C01181 implements Runnable {
        C01181() {
        }

        public void run() {
            HelpActivity.this.setImage();
        }
    }

    class C01192 implements Runnable {
        C01192() {
        }

        public void run() {
            int displayWidth = HelpActivity.this.getResources().getDisplayMetrics().widthPixels;
            Options opt = new Options();
            opt.inPreferredConfig = Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            HelpActivity.this.mBitmap = BitmapFactory.decodeStream(HelpActivity.this.getResources().openRawResource(C0127R.drawable.helpbg), null, opt);
            Matrix matrixBall = new Matrix();
            float scaleWidth = ((float) displayWidth) / ((float) HelpActivity.this.mBitmap.getWidth());
            matrixBall.postScale(1.0f * scaleWidth, 1.0f * scaleWidth);
            HelpActivity.this.mBitmap = Bitmap.createBitmap(HelpActivity.this.mBitmap, 0, 0, HelpActivity.this.mBitmap.getWidth(), HelpActivity.this.mBitmap.getHeight(), matrixBall, true);
            HelpActivity.this.globalWidth = HelpActivity.this.mBitmap.getWidth();
            HelpActivity.this.globalHeight = HelpActivity.this.mBitmap.getHeight();
            if (HelpActivity.this.globalHeight > HelpActivity.this.mImageHelp.getHeight()) {
                HelpActivity.this.mCutBitmap = Bitmap.createBitmap(HelpActivity.this.mBitmap, 0, 0, HelpActivity.this.mBitmap.getWidth(), HelpActivity.this.mImageHelp.getHeight(), null, true);
            } else {
                HelpActivity.this.mCutBitmap = Bitmap.createBitmap(HelpActivity.this.mBitmap, 0, 0, HelpActivity.this.mBitmap.getWidth(), HelpActivity.this.globalHeight, null, true);
            }
            HelpActivity.this.handler.post(HelpActivity.this.mSetBitmap);
        }
    }

    public class MulitPointTouchListener implements OnTouchListener {
        PointF pf = new PointF();

        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouch(View v, MotionEvent event) {
            ImageView view = (ImageView) v;
            float curX;
            switch (event.getAction()) {
                case 0:
                    this.pf.set(event.getX(), event.getY());
                    break;
                case 1:
                    curX = event.getX();
                    event.getY();
                    break;
                case 2:
                    curX = event.getX();
                    float curY = event.getY();
                    HelpActivity helpActivity = HelpActivity.this;
                    helpActivity.globalNum += (int) (this.pf.y - curY);
                    Log.i("globalNum", Integer.valueOf(HelpActivity.this.globalNum).toString());
                    if (HelpActivity.this.globalNum > HelpActivity.this.globalHeight - view.getHeight() || HelpActivity.this.globalNum < 0) {
                        if (HelpActivity.this.globalNum < 0) {
                            HelpActivity.this.globalNum = 0;
                        }
                        if (HelpActivity.this.globalNum > HelpActivity.this.globalHeight - view.getHeight()) {
                            HelpActivity.this.globalNum = HelpActivity.this.globalHeight - view.getHeight();
                        }
                    } else {
                        HelpActivity.this.mCutBitmap = Bitmap.createBitmap(HelpActivity.this.mBitmap, 0, HelpActivity.this.globalNum, HelpActivity.this.mBitmap.getWidth(), HelpActivity.this.mImageHelp.getHeight(), null, true);
                        HelpActivity.this.handler.post(HelpActivity.this.mSetBitmap);
                    }
                    this.pf.set(curX, curY);
                    break;
            }
            return true;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(128);
        setContentView(C0127R.layout.activity_show_help);
        this.mback = (ImageButton) findViewById(C0127R.id.btn_back);
        this.mback.setOnClickListener(this);
        this.last = (ImageButton) findViewById(C0127R.id.btn_last);
        this.last.setOnClickListener(this);
        this.next = (ImageButton) findViewById(C0127R.id.btn_next);
        this.next.setOnClickListener(this);
        this.background = (FrameLayout) findViewById(C0127R.id.background);
        this.background.setBackgroundResource(this.imgs[this.position]);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case C0127R.id.btn_back:
                Intent it = new Intent();
                it.setClass(this, WelcomeActivity.class);
                startActivity(it);
                finish();
                return;
            case C0127R.id.btn_last:
                if (this.position != 0) {
                    this.position--;
                    this.background.setBackgroundResource(this.imgs[this.position]);
                    return;
                }
                return;
            case C0127R.id.btn_next:
                if (this.position != this.imgs.length - 1) {
                    this.position++;
                    this.background.setBackgroundResource(this.imgs[this.position]);
                    return;
                }
                return;
            default:
                return;
        }
    }

    void setImage() {
        if (this.mCutBitmap != null) {
            this.mImageHelp.setImageBitmap(this.mCutBitmap);
            this.handler.removeCallbacks(this.mSetBitmap);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (4 == keyCode) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onStop() {
        if (!(this.mBitmap == null || this.mBitmap.isRecycled())) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }
        if (!(this.mCutBitmap == null || this.mCutBitmap.isRecycled())) {
            this.mCutBitmap.recycle();
            this.mCutBitmap = null;
        }
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (!(this.mBitmap == null || this.mBitmap.isRecycled())) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }
        if (this.mCutBitmap != null && !this.mCutBitmap.isRecycled()) {
            this.mCutBitmap.recycle();
            this.mCutBitmap = null;
        }
    }
}
