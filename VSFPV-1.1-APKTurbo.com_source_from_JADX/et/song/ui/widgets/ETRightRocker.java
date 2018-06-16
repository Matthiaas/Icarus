package et.song.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.app.util.log;
import et.song.vspfv.C0127R;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ETRightRocker extends View {
    private static final int BALL_DEFAULT_COLOR = -1892456705;
    private static final int BG_DEFAULT_COLOR = -1880298260;
    private static final String TAG = null;
    private static Bitmap mBitmapBall = null;
    private static Bitmap mBitmapBg = null;
    private static Bitmap mBitmapPlane = null;
    final int GUIJI_STATUS_DRAW_LINE;
    final int GUIJI_STATUS_DRAW_OVER;
    final int GUIJI_STATUS_DRAW_PLANE;
    private trackPoint PointNoFive;
    private trackPoint PointNoOne;
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
    private int[] planeCoord;
    private int planeStatus;
    private int pointIdx;
    private int pointLength;
    final BlockingQueue<trackPoint> queue;
    private float startX;
    private float startY;
    Runnable updatePlanePos;

    class C01001 extends Handler {
        C01001() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    trackPoint q = msg.obj;
                    ETRightRocker.this.planeCoord[0] = (int) (q.f11x - ((float) ETRightRocker.this.centerCoord[0]));
                    ETRightRocker.this.planeCoord[1] = (int) (((float) ETRightRocker.this.centerCoord[1]) - q.f12y);
                    ETRightRocker.this.planeStatus = 2;
                    ETRightRocker.this.invalidate();
                    return;
                case 3:
                    ETRightRocker.this.planeCoord[0] = 0;
                    ETRightRocker.this.planeCoord[1] = 0;
                    ETRightRocker.this.ballCoord[0] = 0;
                    if (ETRightRocker.this.ismLock) {
                        ETRightRocker.this.ballCoord[1] = 0;
                    }
                    ETRightRocker.this.invalidate();
                    return;
                default:
                    return;
            }
        }
    }

    class C01012 implements Runnable {
        C01012() {
        }

        public void run() {
            if (ETRightRocker.this.mode != 1) {
                ETRightRocker.this.queue.clear();
            }
            if (ETRightRocker.this.queue.isEmpty()) {
                log.m1e("empty");
                ETRightRocker.this.planeStatus = 0;
                ETRightRocker.this.pointIdx = 0;
                Message msg = new Message();
                msg.what = 3;
                ETRightRocker.this.mHandler.sendMessage(msg);
                return;
            }
            trackPoint q = (trackPoint) ETRightRocker.this.queue.poll();
            if (ETRightRocker.this.pointIdx == 0) {
                ETRightRocker.this.PointNoOne = q;
            } else if (ETRightRocker.this.pointIdx == 1) {
                ETRightRocker.this.PointNoFive = q;
                float xie = (float) Math.sqrt(Math.pow((double) (ETRightRocker.this.PointNoFive.f11x - ETRightRocker.this.PointNoOne.f11x), 2.0d) + Math.pow((double) (ETRightRocker.this.PointNoFive.f12y - ETRightRocker.this.PointNoOne.f12y), 2.0d));
                float sinAngle = (ETRightRocker.this.PointNoOne.f12y - ETRightRocker.this.PointNoFive.f12y) / xie;
                ETRightRocker.this.ballCoord[0] = (int) (((ETRightRocker.this.PointNoFive.f11x - ETRightRocker.this.PointNoOne.f11x) / xie) * 44.0f);
                ETRightRocker.this.ballCoord[1] = (int) (sinAngle * 44.0f);
            }
            ETRightRocker eTRightRocker = ETRightRocker.this;
            int access$8 = eTRightRocker.pointIdx + 1;
            eTRightRocker.pointIdx = access$8;
            if (access$8 > 1) {
                ETRightRocker.this.pointIdx = 0;
            }
            msg = new Message();
            msg.obj = q;
            msg.what = 1;
            ETRightRocker.this.mHandler.sendMessage(msg);
            ETRightRocker.this.mHandler.postDelayed(ETRightRocker.this.updatePlanePos, 40);
        }
    }

    private class trackPoint {
        float f11x;
        float f12y;

        private trackPoint() {
        }
    }

    public ETRightRocker(Context context) {
        super(context);
        this.centerCoord = new int[2];
        this.ballCoord = new int[2];
        this.planeCoord = new int[2];
        this.PointNoOne = new trackPoint();
        this.PointNoFive = new trackPoint();
        this.mMaxX = 255;
        this.mMaxY = 255;
        this.ismLock = true;
        this.ismManual = true;
        this.isPress = false;
        this.mode = 0;
        this.pointIdx = 0;
        this.pointLength = 0;
        this.GUIJI_STATUS_DRAW_OVER = 0;
        this.GUIJI_STATUS_DRAW_LINE = 1;
        this.GUIJI_STATUS_DRAW_PLANE = 2;
        this.planeStatus = 0;
        this.mPlaneBitmapCanvas = null;
        this.mLineBitmap = null;
        this.mPlanePaint = null;
        this.queue = new LinkedBlockingQueue(5000);
        this.mHandler = new C01001();
        this.updatePlanePos = new C01012();
        this.mContext = context;
        initView();
    }

    public ETRightRocker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.centerCoord = new int[2];
        this.ballCoord = new int[2];
        this.planeCoord = new int[2];
        this.PointNoOne = new trackPoint();
        this.PointNoFive = new trackPoint();
        this.mMaxX = 255;
        this.mMaxY = 255;
        this.ismLock = true;
        this.ismManual = true;
        this.isPress = false;
        this.mode = 0;
        this.pointIdx = 0;
        this.pointLength = 0;
        this.GUIJI_STATUS_DRAW_OVER = 0;
        this.GUIJI_STATUS_DRAW_LINE = 1;
        this.GUIJI_STATUS_DRAW_PLANE = 2;
        this.planeStatus = 0;
        this.mPlaneBitmapCanvas = null;
        this.mLineBitmap = null;
        this.mPlanePaint = null;
        this.queue = new LinkedBlockingQueue(5000);
        this.mHandler = new C01001();
        this.updatePlanePos = new C01012();
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
        if (mBitmapPlane == null) {
            mBitmapPlane = BitmapFactory.decodeResource(getResources(), C0127R.drawable.plane_track);
        }
        this.mPaint = new Paint(1);
    }

    private void initView() {
        this.dm = new DisplayMetrics();
        ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getMetrics(this.dm);
    }

    public float getXV() {
        if (this.mActiveSize <= 0) {
            return 0.0f;
        }
        if (this.mode == 1) {
            return (float) (this.ballCoord[0] + (this.mMaxX / 2));
        }
        return (float) ((((this.ballCoord[0] * this.mMaxX) / 2) / this.mRadius) + (this.mMaxX / 2));
    }

    public void setXV(int mXV) {
        this.ballCoord[0] = ((mXV - (this.mMaxX / 2)) * this.mRadius) / (this.mMaxX / 2);
    }

    public float getYV() {
        if (this.mActiveSize <= 0) {
            return 0.0f;
        }
        if (this.mode == 1) {
            return (float) (this.ballCoord[1] + (this.mMaxY / 2));
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
        if (mBitmapPlane != null) {
            mBitmapPlane = resizeImage(mBitmapPlane, this.mBallSize, this.mBallSize);
        }
        this.centerCoord[0] = this.canvasWidth / 2;
        this.centerCoord[1] = this.canvasHeight / 2;
    }

    protected void onDraw(Canvas canvas) {
        int ballX;
        int ballY;
        if (this.mode == 1) {
            ballX = (this.centerCoord[0] - (this.mBallSize / 2)) + this.planeCoord[0];
            ballY = (this.centerCoord[1] - (this.mBallSize / 2)) - this.planeCoord[1];
            if (!(this.mLineBitmap == null || this.mPlanePaint == null)) {
                canvas.drawBitmap(this.mLineBitmap, 0.0f, 0.0f, this.mPlanePaint);
            }
            if (this.planeStatus == 2) {
                canvas.drawBitmap(mBitmapPlane, (float) ballX, (float) ballY, null);
            }
            if (this.queue.isEmpty() && this.mPlaneBitmapCanvas != null) {
                this.mPlaneBitmapCanvas.drawColor(0, Mode.CLEAR);
            }
        } else {
            ballX = (this.centerCoord[0] - (this.mBallSize / 2)) + this.ballCoord[0];
            ballY = (this.centerCoord[1] - (this.mBallSize / 2)) - this.ballCoord[1];
            canvas.drawBitmap(mBitmapBg, (float) (this.centerCoord[0] - (this.mActiveSize / 2)), (float) (this.centerCoord[1] - (this.mActiveSize / 2)), null);
            canvas.drawBitmap(mBitmapBall, (float) ballX, (float) ballY, null);
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
            if (this.mode == 1) {
                if (!this.queue.isEmpty()) {
                    this.queue.clear();
                }
                this.planeStatus = 1;
                this.startX = event.getX();
                this.startY = event.getY();
                if (this.mLineBitmap == null) {
                    this.mLineBitmap = Bitmap.createBitmap(this.canvasWidth, this.canvasHeight, Config.ALPHA_8);
                    this.mPlaneBitmapCanvas = new Canvas(this.mLineBitmap);
                    this.mPlanePaint = new Paint();
                    this.mPlanePaint.setColor(-16776961);
                    this.mPlanePaint.setStrokeWidth(10.0f);
                }
            }
        } else if (event.getAction() == 1) {
            if (this.mode == 1) {
                this.mHandler.post(this.updatePlanePos);
            } else {
                this.ballCoord[0] = 0;
                if (this.ismLock) {
                    this.ballCoord[1] = 0;
                }
            }
            this.isPress = false;
        } else if (event.getAction() == 2) {
            float xie;
            if (this.mode == 1) {
                float stopX = event.getX();
                float stopY = event.getY();
                if (this.mPlaneBitmapCanvas != null) {
                    this.mPlaneBitmapCanvas.drawLine(this.startX, this.startY, stopX, stopY, this.mPlanePaint);
                }
                xie = (float) Math.sqrt(Math.pow((double) (stopX - this.startX), 2.0d) + Math.pow((double) (stopY - this.startY), 2.0d));
                this.pointLength = (int) (((float) this.pointLength) + xie);
                if (this.pointLength >= 10) {
                    int chu = this.pointLength / 10;
                    float sinAngle = (stopY - this.startY) / xie;
                    float cosAngle = (stopX - this.startX) / xie;
                    for (int i = 0; i < chu; i++) {
                        ETRightRocker eTRightRocker = this;
                        trackPoint p = new trackPoint();
                        p.f11x = ((float) ((int) this.startX)) + ((10.0f * cosAngle) * ((float) i));
                        p.f12y = ((float) ((int) this.startY)) + ((10.0f * sinAngle) * ((float) i));
                        try {
                            if (this.queue.remainingCapacity() != 0) {
                                this.queue.put(p);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    this.pointLength = 0;
                }
                this.startX = stopX;
                this.startY = stopY;
            } else if (this.isPress) {
                this.ballCoord[0] = currentX - this.centerCoord[0];
                this.ballCoord[1] = this.centerCoord[1] - currentY;
                if (this.mode != 1) {
                    if (Math.pow((double) this.ballCoord[0], 2.0d) + Math.pow((double) this.ballCoord[1], 2.0d) > Math.pow((double) this.mRadius, 2.0d)) {
                        xie = (float) Math.sqrt(Math.pow((double) this.ballCoord[0], 2.0d) + Math.pow((double) this.ballCoord[1], 2.0d));
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
        invalidate();
    }

    public int getMode() {
        return this.mode;
    }
}
