package et.song.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import et.song.ui.libs.ETTool;
import et.song.vspfv.C0127R;

public class ETTrim extends RelativeLayout {
    private Handler handlerLongClick = new Handler();
    private OnClickListener listener;
    private Drawable mBackground;
    private ImageView mBall;
    private Drawable mBallBackground;
    private RelativeLayout mBg;
    private int mBgWidth = 0;
    private boolean mIsLeftLongClick = false;
    private boolean mIsRightLongClick = false;
    private Drawable mLeftBackground;
    private ETButton mLeftButton;
    private int mMax;
    private int mMin;
    private int mPos = 0;
    private Drawable mRightBackground;
    private ETButton mRightButton;
    private int mStep = 0;
    Runnable runnableLongClick = new C01111();

    class C01111 implements Runnable {
        C01111() {
        }

        public void run() {
            ETTrim eTTrim;
            if (ETTrim.this.mIsLeftLongClick) {
                if (ETTrim.this.mPos > ETTrim.this.mMin) {
                    eTTrim = ETTrim.this;
                    eTTrim.mPos = eTTrim.mPos - 1;
                    ETTrim.this.mBall.setX(ETTrim.this.mBall.getX() - ((float) ETTrim.this.mStep));
                }
            } else if (ETTrim.this.mIsRightLongClick && ETTrim.this.mPos < ETTrim.this.mMax) {
                eTTrim = ETTrim.this;
                eTTrim.mPos = eTTrim.mPos + 1;
                ETTrim.this.mBall.setX(ETTrim.this.mBall.getX() + ((float) ETTrim.this.mStep));
            }
            ETTrim.this.handlerLongClick.postDelayed(this, 100);
        }
    }

    class C01122 implements android.view.View.OnClickListener {
        C01122() {
        }

        public void onClick(View v) {
            if (ETTrim.this.mPos > ETTrim.this.mMin) {
                ETTrim eTTrim = ETTrim.this;
                eTTrim.mPos = eTTrim.mPos - 1;
                ETTrim.this.mBall.setX(ETTrim.this.mBall.getX() - ((float) ETTrim.this.mStep));
            }
            if (ETTrim.this.listener != null) {
                ETTrim.this.listener.leftClick(ETTrim.this.mPos);
            }
        }
    }

    class C01133 implements OnLongClickListener {
        C01133() {
        }

        public boolean onLongClick(View v) {
            ETTrim.this.mIsLeftLongClick = true;
            ETTrim.this.handlerLongClick.post(ETTrim.this.runnableLongClick);
            return false;
        }
    }

    class C01144 implements OnTouchListener {
        C01144() {
        }

        @SuppressLint({"NewApi", "ClickableViewAccessibility"})
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_SELECTED));
                v.setBackground(v.getBackground());
            } else if (event.getAction() == 1) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_NOT_SELECTED));
                v.setBackground(v.getBackground());
                ETTrim.this.mIsLeftLongClick = false;
                ETTrim.this.handlerLongClick.removeCallbacks(ETTrim.this.runnableLongClick);
            }
            return false;
        }
    }

    class C01155 implements android.view.View.OnClickListener {
        C01155() {
        }

        public void onClick(View v) {
            if (ETTrim.this.mPos < ETTrim.this.mMax) {
                ETTrim eTTrim = ETTrim.this;
                eTTrim.mPos = eTTrim.mPos + 1;
                ETTrim.this.mBall.setX(ETTrim.this.mBall.getX() + ((float) ETTrim.this.mStep));
            }
            if (ETTrim.this.listener != null) {
                ETTrim.this.listener.rightClick(ETTrim.this.mPos);
            }
        }
    }

    class C01166 implements OnLongClickListener {
        C01166() {
        }

        public boolean onLongClick(View v) {
            ETTrim.this.mIsRightLongClick = true;
            ETTrim.this.handlerLongClick.post(ETTrim.this.runnableLongClick);
            return false;
        }
    }

    class C01177 implements OnTouchListener {
        C01177() {
        }

        @SuppressLint({"NewApi", "ClickableViewAccessibility"})
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_SELECTED));
                v.setBackground(v.getBackground());
            } else if (event.getAction() == 1) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_NOT_SELECTED));
                v.setBackground(v.getBackground());
                ETTrim.this.mIsRightLongClick = false;
                ETTrim.this.handlerLongClick.removeCallbacks(ETTrim.this.runnableLongClick);
            }
            return false;
        }
    }

    public interface OnClickListener {
        void leftClick(int i);

        void rightClick(int i);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public ETTrim(Context context) {
        super(context);
    }

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    public ETTrim(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0127R.layout.view_trim, this, true);
        TypedArray ta = context.obtainStyledAttributes(attrs, C0127R.styleable.ETTrim);
        this.mMin = ta.getInt(2, 0);
        this.mMax = ta.getInt(3, 0);
        this.mBallBackground = ta.getDrawable(0);
        this.mLeftBackground = ta.getDrawable(4);
        this.mRightBackground = ta.getDrawable(5);
        this.mBackground = ta.getDrawable(1);
        ta.recycle();
        this.mLeftButton = (ETButton) findViewById(C0127R.id.left);
        this.mRightButton = (ETButton) findViewById(C0127R.id.right);
        this.mBg = (RelativeLayout) findViewById(C0127R.id.bg);
        this.mBall = (ImageView) findViewById(C0127R.id.ball);
        this.mLeftButton.setBackground(this.mLeftBackground);
        this.mRightButton.setBackground(this.mRightBackground);
        this.mBg.setBackground(this.mBackground);
        this.mBall.setBackground(this.mBallBackground);
        this.mLeftButton.setOnClickListener(new C01122());
        this.mLeftButton.setOnLongClickListener(new C01133());
        this.mLeftButton.setOnTouchListener(new C01144());
        this.mRightButton.setOnClickListener(new C01155());
        this.mRightButton.setOnLongClickListener(new C01166());
        this.mRightButton.setOnTouchListener(new C01177());
    }

    public ETTrim(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        return super.onKeyDown(keyCode, keyEvent);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mBgWidth = ETTool.ETWidth(this.mBg);
        this.mStep = this.mBgWidth / (this.mMax - this.mMin);
        this.mPos = (this.mMax + this.mMin) / 2;
    }

    public int getPos() {
        return this.mPos;
    }
}
