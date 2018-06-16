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

public class ETTBTrim extends RelativeLayout {
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
    Runnable runnableLongClick = new C01041();

    class C01041 implements Runnable {
        C01041() {
        }

        public void run() {
            ETTBTrim eTTBTrim;
            if (ETTBTrim.this.mIsLeftLongClick) {
                if (ETTBTrim.this.mPos > ETTBTrim.this.mMin) {
                    eTTBTrim = ETTBTrim.this;
                    eTTBTrim.mPos = eTTBTrim.mPos - 1;
                    ETTBTrim.this.mBall.setY(ETTBTrim.this.mBall.getY() - ((float) ETTBTrim.this.mStep));
                }
            } else if (ETTBTrim.this.mIsRightLongClick && ETTBTrim.this.mPos < ETTBTrim.this.mMax) {
                eTTBTrim = ETTBTrim.this;
                eTTBTrim.mPos = eTTBTrim.mPos + 1;
                ETTBTrim.this.mBall.setY(ETTBTrim.this.mBall.getY() + ((float) ETTBTrim.this.mStep));
            }
            ETTBTrim.this.handlerLongClick.postDelayed(this, 100);
        }
    }

    class C01052 implements android.view.View.OnClickListener {
        C01052() {
        }

        public void onClick(View v) {
            if (ETTBTrim.this.mPos > ETTBTrim.this.mMin) {
                ETTBTrim eTTBTrim = ETTBTrim.this;
                eTTBTrim.mPos = eTTBTrim.mPos - 1;
                ETTBTrim.this.mBall.setY(ETTBTrim.this.mBall.getY() - ((float) ETTBTrim.this.mStep));
            }
            if (ETTBTrim.this.listener != null) {
                ETTBTrim.this.listener.leftClick(ETTBTrim.this.mPos);
            }
        }
    }

    class C01063 implements OnLongClickListener {
        C01063() {
        }

        public boolean onLongClick(View v) {
            ETTBTrim.this.mIsLeftLongClick = true;
            ETTBTrim.this.handlerLongClick.post(ETTBTrim.this.runnableLongClick);
            return false;
        }
    }

    class C01074 implements OnTouchListener {
        C01074() {
        }

        @SuppressLint({"NewApi", "ClickableViewAccessibility"})
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_SELECTED));
                v.setBackground(v.getBackground());
            } else if (event.getAction() == 1) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_NOT_SELECTED));
                v.setBackground(v.getBackground());
                ETTBTrim.this.mIsLeftLongClick = false;
                ETTBTrim.this.handlerLongClick.removeCallbacks(ETTBTrim.this.runnableLongClick);
            }
            return false;
        }
    }

    class C01085 implements android.view.View.OnClickListener {
        C01085() {
        }

        public void onClick(View v) {
            if (ETTBTrim.this.mPos < ETTBTrim.this.mMax) {
                ETTBTrim eTTBTrim = ETTBTrim.this;
                eTTBTrim.mPos = eTTBTrim.mPos + 1;
                ETTBTrim.this.mBall.setY(ETTBTrim.this.mBall.getY() + ((float) ETTBTrim.this.mStep));
            }
            if (ETTBTrim.this.listener != null) {
                ETTBTrim.this.listener.rightClick(ETTBTrim.this.mPos);
            }
        }
    }

    class C01096 implements OnLongClickListener {
        C01096() {
        }

        public boolean onLongClick(View v) {
            ETTBTrim.this.mIsRightLongClick = true;
            ETTBTrim.this.handlerLongClick.post(ETTBTrim.this.runnableLongClick);
            return false;
        }
    }

    class C01107 implements OnTouchListener {
        C01107() {
        }

        @SuppressLint({"NewApi", "ClickableViewAccessibility"})
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_SELECTED));
                v.setBackground(v.getBackground());
            } else if (event.getAction() == 1) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_NOT_SELECTED));
                v.setBackground(v.getBackground());
                ETTBTrim.this.mIsRightLongClick = false;
                ETTBTrim.this.handlerLongClick.removeCallbacks(ETTBTrim.this.runnableLongClick);
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

    public ETTBTrim(Context context) {
        super(context);
    }

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    public ETTBTrim(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0127R.layout.view_tbtrim, this, true);
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
        this.mLeftButton.setOnClickListener(new C01052());
        this.mLeftButton.setOnLongClickListener(new C01063());
        this.mLeftButton.setOnTouchListener(new C01074());
        this.mRightButton.setOnClickListener(new C01085());
        this.mRightButton.setOnLongClickListener(new C01096());
        this.mRightButton.setOnTouchListener(new C01107());
    }

    public ETTBTrim(Context context, AttributeSet attrs, int defStyle) {
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
        this.mBgWidth = ETTool.ETHeight(this.mBg);
        this.mStep = this.mBgWidth / (this.mMax - this.mMin);
        this.mPos = (this.mMax + this.mMin) / 2;
    }

    public int getPos() {
        return this.mPos;
    }

    public int getMax() {
        return this.mMax;
    }
}
