package et.song.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.app.util.log;
import et.song.vspfv.C0127R;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ETRocker extends View {
    private static final int BALL_DEFAULT_COLOR = -1892456705;
    private static final int BG_DEFAULT_COLOR = -1880298260;
    private static final String TAG = null;
    private static Bitmap mBitmapBall = null;
    private static Bitmap mBitmapBg = null;
    private int[] ballCoord;
    private int canvasHeight;
    private int canvasWidth;
    private int[] centerCoord;
    private DisplayMetrics dm;
    private boolean isPress;
    private boolean ismLock;
    private boolean ismManual;
    private int mActiveSize;
    private int mBallSize;
    private Context mContext;
    private Handler mHandler;
    private Bitmap mLineBitmap;
    private int mMarginSize;
    private int mMaxX;
    private int mMaxY;
    private Paint mPaint;
    private Canvas mPlaneBitmapCanvas;
    private Paint mPlanePaint;
    private int mRadius;
    private int mode;
    final BlockingQueue<trackPoint> queue;
    private float startX;
    private float startY;
    Runnable updatePlanePos;

    class C01021 extends Handler {
        C01021() {
        }

        public void handleMessage(Message msg) {
            trackPoint q;
            switch (msg.what) {
                case 1:
                    q = msg.obj;
                    log.m1e("q.x = " + q.f13x + ", q.y = " + q.f14y);
                    ETRocker.this.ballCoord[0] = (int) q.f13x;
                    ETRocker.this.ballCoord[1] = (int) q.f14y;
                    ETRocker.this.invalidate();
                    return;
                case 2:
                    q = (trackPoint) msg.obj;
                    float stopX = q.f13x;
                    float stopY = q.f14y;
                    return;
                case 3:
                    ETRocker.this.ballCoord[0] = 0;
                    if (ETRocker.this.ismLock) {
                        ETRocker.this.ballCoord[1] = 0;
                    }
                    ETRocker.this.invalidate();
                    return;
                default:
                    return;
            }
        }
    }

    class C01032 implements Runnable {
        C01032() {
        }

        public void run() {
            if (ETRocker.this.queue.isEmpty()) {
                log.m1e("empty");
                Message msg = new Message();
                msg.what = 3;
                ETRocker.this.mHandler.sendMessage(msg);
                return;
            }
            trackPoint q = (trackPoint) ETRocker.this.queue.poll();
            msg = new Message();
            msg.obj = q;
            msg.what = 1;
            ETRocker.this.mHandler.sendMessage(msg);
            ETRocker.this.mHandler.postDelayed(ETRocker.this.updatePlanePos, 100);
        }
    }

    private class trackPoint {
        float f13x;
        float f14y;

        private trackPoint() {
        }
    }

    public ETRocker(Context context) {
        super(context);
        this.centerCoord = new int[2];
        this.ballCoord = new int[2];
        this.mMaxX = 255;
        this.mMaxY = 255;
        this.ismLock = true;
        this.ismManual = true;
        this.isPress = false;
        this.mode = 0;
        this.mPlaneBitmapCanvas = null;
        this.mLineBitmap = null;
        this.mPlanePaint = null;
        this.queue = new LinkedBlockingQueue(5000);
        this.mHandler = new C01021();
        this.updatePlanePos = new C01032();
        this.mContext = context;
        initView();
    }

    public ETRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.centerCoord = new int[2];
        this.ballCoord = new int[2];
        this.mMaxX = 255;
        this.mMaxY = 255;
        this.ismLock = true;
        this.ismManual = true;
        this.isPress = false;
        this.mode = 0;
        this.mPlaneBitmapCanvas = null;
        this.mLineBitmap = null;
        this.mPlanePaint = null;
        this.queue = new LinkedBlockingQueue(5000);
        this.mHandler = new C01021();
        this.updatePlanePos = new C01032();
        this.mContext = context;
        initView();
        TypedArray tArray = context.obtainStyledAttributes(attrs, C0127R.styleable.ETRockerView);
        if (mBitmapBg == null) {
            mBitmapBg = drawable2Bitmap(tArray.getDrawable(2));
        }
        if (mBitmapBall == null) {
            mBitmapBall = drawable2Bitmap(tArray.getDrawable(3));
        }
        this.mBallSize = (int) tArray.getDimension(1, (float) dip2px(40.0f));
        this.mMarginSize = (int) tArray.getDimension(0, (float) dip2px(10.0f));
        this.mPaint = new Paint(1);
    }

    private void initView() {
        this.dm = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getMetrics(this.dm);
    }

    public float getXV() {
        if (this.mActiveSize <= 0 || (this.mode == 1 && this.isPress)) {
            return 0.0f;
        }
        return (float) ((((this.ballCoord[0] * this.mMaxX) / 2) / this.mRadius) + (this.mMaxX / 2));
    }

    public void setXV(int mXV) {
        this.ballCoord[0] = ((mXV - (this.mMaxX / 2)) * this.mRadius) / (this.mMaxX / 2);
    }

    public float getYV() {
        if (this.mActiveSize <= 0 || (this.mode == 1 && this.isPress)) {
            return 0.0f;
        }
        return (float) ((((this.ballCoord[1] * this.mMaxY) / 2) / this.mRadius) + (this.mMaxY / 2));
    }

    public void setYV(int mYV) {
        this.ballCoord[1] = ((mYV - (this.mMaxY / 2)) * this.mRadius) / (this.mMaxY / 2);
    }

    public void setBitmapBg(Bitmap bitmapBg) {
        mBitmapBg = bitmapBg;
    }

    public void setBitmapBall(Bitmap bitmapBall) {
        mBitmapBall = bitmapBall;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.canvasHeight = getLayoutParams().height;
        this.canvasWidth = getLayoutParams().width;
        this.mActiveSize = (this.canvasWidth > this.canvasHeight ? this.canvasHeight : this.canvasWidth) - this.mMarginSize;
        this.mRadius = (this.mActiveSize / 2) - (this.mBallSize / 2);
        if (mBitmapBg == null) {
            this.mPaint.setColor(BG_DEFAULT_COLOR);
            mBitmapBg = CreateCircle(this.mActiveSize, this.mPaint);
        } else {
            mBitmapBg = resizeImage(mBitmapBg, this.canvasWidth - this.mMarginSize, this.canvasHeight - this.mMarginSize);
        }
        if (mBitmapBall == null) {
            this.mPaint.setColor(BALL_DEFAULT_COLOR);
            mBitmapBall = CreateCircle(this.mBallSize, this.mPaint);
        } else {
            mBitmapBall = resizeImage(mBitmapBall, this.mBallSize, this.mBallSize);
        }
        this.centerCoord[0] = this.canvasWidth / 2;
        this.centerCoord[1] = this.canvasHeight / 2;
    }

    protected void onDraw(Canvas canvas) {
        int ballX = (this.centerCoord[0] - (this.mBallSize / 2)) + this.ballCoord[0];
        int ballY = (this.centerCoord[1] - (this.mBallSize / 2)) - this.ballCoord[1];
        canvas.drawBitmap(mBitmapBg, (float) (this.centerCoord[0] - (this.mActiveSize / 2)), (float) (this.centerCoord[1] - (this.mActiveSize / 2)), null);
        if (!(this.mode != 1 || this.mLineBitmap == null || this.mPlanePaint == null)) {
            canvas.drawBitmap(this.mLineBitmap, 0.0f, 0.0f, this.mPlanePaint);
        }
        canvas.drawBitmap(mBitmapBall, (float) ballX, (float) ballY, null);
        if (this.queue.isEmpty() && this.mPlaneBitmapCanvas != null) {
            this.mPlaneBitmapCanvas.drawColor(0, Mode.CLEAR);
        }
        super.onDraw(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.ismManual) {
            return false;
        }
        int currentX = (int) event.getX();
        int currentY = (int) event.getY();
        if (event.getAction() == 0) {
            if (Math.abs(currentX - (this.centerCoord[0] + this.ballCoord[0])) < this.mActiveSize / 2 && Math.abs(currentY - (this.centerCoord[1] - this.ballCoord[1])) < this.mActiveSize / 2) {
                this.isPress = true;
            }
            this.startX = event.getX();
            this.startY = event.getY();
        } else if (event.getAction() == 1) {
            this.ballCoord[0] = 0;
            if (this.ismLock) {
                this.ballCoord[1] = 0;
            }
            this.isPress = false;
            if (this.mode == 1) {
                this.mHandler.post(this.updatePlanePos);
            }
        } else if (event.getAction() == 2) {
            ETRocker eTRocker;
            trackPoint p;
            if (this.mode == 1) {
                float stopX = event.getX();
                float stopY = event.getY();
                if (this.mPlaneBitmapCanvas != null) {
                    this.mPlaneBitmapCanvas.drawLine(this.startX, this.startY, stopX, stopY, this.mPlanePaint);
                }
                eTRocker = this;
                p = new trackPoint();
                p.f13x = stopX;
                p.f14y = stopY;
                Message msg = new Message();
                msg.what = 2;
                msg.obj = p;
                this.mHandler.sendMessage(msg);
                this.startX = event.getX();
                this.startY = event.getY();
            }
            if (this.isPress) {
                this.ballCoord[0] = currentX - this.centerCoord[0];
                this.ballCoord[1] = this.centerCoord[1] - currentY;
                if (Math.pow((double) this.ballCoord[0], 2.0d) + Math.pow((double) this.ballCoord[1], 2.0d) > Math.pow((double) this.mRadius, 2.0d)) {
                    float xie = (float) Math.sqrt(Math.pow((double) this.ballCoord[0], 2.0d) + Math.pow((double) this.ballCoord[1], 2.0d));
                    this.ballCoord[0] = (int) (((float) this.mRadius) * (((float) this.ballCoord[0]) / xie));
                    this.ballCoord[1] = (int) (((float) this.mRadius) * (((float) this.ballCoord[1]) / xie));
                } else {
                    if (Math.abs(this.ballCoord[0]) > this.mRadius) {
                        this.ballCoord[0] = this.ballCoord[0] > 0 ? this.mRadius : -this.mRadius;
                    }
                    if (Math.abs(this.ballCoord[1]) > this.mRadius) {
                        this.ballCoord[1] = this.ballCoord[1] > 0 ? this.mRadius : -this.mRadius;
                    }
                }
                if (this.mode == 1 && this.mLineBitmap == null) {
                    this.mLineBitmap = Bitmap.createBitmap(this.canvasWidth, this.canvasHeight, Config.ARGB_8888);
                    this.mPlaneBitmapCanvas = new Canvas(this.mLineBitmap);
                    this.mPlanePaint = new Paint();
                    this.mPlanePaint.setColor(SupportMenu.CATEGORY_MASK);
                    this.mPlanePaint.setStrokeWidth(20.0f);
                }
                eTRocker = this;
                p = new trackPoint();
                p.f13x = (float) this.ballCoord[0];
                p.f14y = (float) this.ballCoord[1];
                try {
                    if (this.queue.remainingCapacity() != 0) {
                        this.queue.put(p);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        invalidate();
        return true;
    }

    public void Refresh() {
        if (Math.pow((double) this.ballCoord[0], 2.0d) + Math.pow((double) this.ballCoord[1], 2.0d) > Math.pow((double) this.mRadius, 2.0d)) {
            float xie = (float) Math.sqrt(Math.pow((double) this.ballCoord[0], 2.0d) + Math.pow((double) this.ballCoord[1], 2.0d));
            this.ballCoord[0] = (int) (((float) this.mRadius) * (((float) this.ballCoord[0]) / xie));
            this.ballCoord[1] = (int) (((float) this.mRadius) * (((float) this.ballCoord[1]) / xie));
        }
        invalidate();
    }

    public boolean isLock() {
        return this.ismLock;
    }

    public void setLock(boolean lock) {
        this.ismLock = lock;
        if (this.ismLock) {
            this.ballCoord[0] = 0;
            this.ballCoord[1] = 0;
            Refresh();
        }
    }

    public boolean isManual() {
        return this.ismManual;
    }

    public void setManual(boolean manual) {
        this.ismManual = manual;
    }

    public int getMaxY() {
        return this.mMaxY;
    }

    public void setMaxY(int mMaxY) {
        this.mMaxY = mMaxY;
    }

    public int getMaxX() {
        return this.mMaxX;
    }

    public void setMaxX(int mMaxX) {
        this.mMaxX = mMaxX;
    }

    private static Bitmap CreateCircle(int width, Paint paint) {
        Bitmap output = Bitmap.createBitmap(width, width, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Rect rect = new Rect(0, 0, width, width);
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        int radius = (int) (Math.sqrt(((double) (width * width)) * 2.0d) / 2.0d);
        canvas.drawRoundRect(rectF, (float) radius, (float) radius, paint);
        canvas.drawBitmap(output, rect, rect, paint);
        return output;
    }

    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        if (!(drawable instanceof NinePatchDrawable)) {
            return null;
        }
        Config config;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (drawable.getOpacity() != -1) {
            config = Config.ARGB_8888;
        } else {
            config = Config.RGB_565;
        }
        Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        float scaleWidth = ((float) w) / ((float) width);
        float scaleHeight = ((float) h) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
    }

    private int dip2px(float dpValue) {
        return (int) ((this.dm.xdpi / 160.0f) * dpValue);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return this.mode;
    }
}
