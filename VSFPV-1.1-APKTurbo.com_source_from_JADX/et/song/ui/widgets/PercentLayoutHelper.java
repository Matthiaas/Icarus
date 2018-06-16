package et.song.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.TextView;
import et.song.vspfv.C0127R;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PercentLayoutHelper {
    private static /* synthetic */ int[] f17x70450a90 = null;
    private static final String REGEX_PERCENT = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)%([s]?[wh]?)$";
    private static final String TAG = "PercentLayout";
    private static int mHeightScreen;
    private static int mWidthScreen;
    private final ViewGroup mHost;

    public static class PercentLayoutInfo {
        public PercentVal bottomMarginPercent;
        public PercentVal endMarginPercent;
        public PercentVal heightPercent;
        public PercentVal leftMarginPercent;
        final MarginLayoutParams mPreservedParams = new MarginLayoutParams(0, 0);
        public PercentVal maxHeightPercent;
        public PercentVal maxWidthPercent;
        public PercentVal minHeightPercent;
        public PercentVal minWidthPercent;
        public PercentVal paddingBottomPercent;
        public PercentVal paddingLeftPercent;
        public PercentVal paddingRightPercent;
        public PercentVal paddingTopPercent;
        public PercentVal rightMarginPercent;
        public PercentVal startMarginPercent;
        public PercentVal textSizePercent;
        public PercentVal topMarginPercent;
        public PercentVal widthPercent;

        private enum BASEMODE {
            BASE_WIDTH,
            BASE_HEIGHT,
            BASE_SCREEN_WIDTH,
            BASE_SCREEN_HEIGHT;
            
            public static final String f15H = "h";
            public static final String PERCENT = "%";
            public static final String SH = "sh";
            public static final String SW = "sw";
            public static final String f16W = "w";
        }

        public static class PercentVal {
            public BASEMODE basemode;
            public float percent = -1.0f;

            public PercentVal(float percent, BASEMODE baseMode) {
                this.percent = percent;
                this.basemode = baseMode;
            }

            public String toString() {
                return "PercentVal{percent=" + this.percent + ", basemode=" + this.basemode.name() + '}';
            }
        }

        public void fillLayoutParams(LayoutParams params, int widthHint, int heightHint) {
            this.mPreservedParams.width = params.width;
            this.mPreservedParams.height = params.height;
            if (this.widthPercent != null) {
                params.width = (int) (((float) PercentLayoutHelper.getBaseByModeAndVal(widthHint, heightHint, this.widthPercent.basemode)) * this.widthPercent.percent);
            }
            if (this.heightPercent != null) {
                params.height = (int) (((float) PercentLayoutHelper.getBaseByModeAndVal(widthHint, heightHint, this.heightPercent.basemode)) * this.heightPercent.percent);
            }
            if (Log.isLoggable(PercentLayoutHelper.TAG, 3)) {
                Log.d(PercentLayoutHelper.TAG, "after fillLayoutParams: (" + params.width + ", " + params.height + ")");
            }
        }

        public void fillMarginLayoutParams(MarginLayoutParams params, int widthHint, int heightHint) {
            fillLayoutParams(params, widthHint, heightHint);
            this.mPreservedParams.leftMargin = params.leftMargin;
            this.mPreservedParams.topMargin = params.topMargin;
            this.mPreservedParams.rightMargin = params.rightMargin;
            this.mPreservedParams.bottomMargin = params.bottomMargin;
            MarginLayoutParamsCompat.setMarginStart(this.mPreservedParams, MarginLayoutParamsCompat.getMarginStart(params));
            MarginLayoutParamsCompat.setMarginEnd(this.mPreservedParams, MarginLayoutParamsCompat.getMarginEnd(params));
            if (this.leftMarginPercent != null) {
                params.leftMargin = (int) (((float) PercentLayoutHelper.getBaseByModeAndVal(widthHint, heightHint, this.leftMarginPercent.basemode)) * this.leftMarginPercent.percent);
            }
            if (this.topMarginPercent != null) {
                params.topMargin = (int) (((float) PercentLayoutHelper.getBaseByModeAndVal(widthHint, heightHint, this.topMarginPercent.basemode)) * this.topMarginPercent.percent);
            }
            if (this.rightMarginPercent != null) {
                params.rightMargin = (int) (((float) PercentLayoutHelper.getBaseByModeAndVal(widthHint, heightHint, this.rightMarginPercent.basemode)) * this.rightMarginPercent.percent);
            }
            if (this.bottomMarginPercent != null) {
                params.bottomMargin = (int) (((float) PercentLayoutHelper.getBaseByModeAndVal(widthHint, heightHint, this.bottomMarginPercent.basemode)) * this.bottomMarginPercent.percent);
            }
            if (this.startMarginPercent != null) {
                MarginLayoutParamsCompat.setMarginStart(params, (int) (((float) PercentLayoutHelper.getBaseByModeAndVal(widthHint, heightHint, this.startMarginPercent.basemode)) * this.startMarginPercent.percent));
            }
            if (this.endMarginPercent != null) {
                MarginLayoutParamsCompat.setMarginEnd(params, (int) (((float) PercentLayoutHelper.getBaseByModeAndVal(widthHint, heightHint, this.endMarginPercent.basemode)) * this.endMarginPercent.percent));
            }
            if (Log.isLoggable(PercentLayoutHelper.TAG, 3)) {
                Log.d(PercentLayoutHelper.TAG, "after fillMarginLayoutParams: (" + params.width + ", " + params.height + ")");
            }
        }

        public String toString() {
            return "PercentLayoutInfo{widthPercent=" + this.widthPercent + ", heightPercent=" + this.heightPercent + ", leftMarginPercent=" + this.leftMarginPercent + ", topMarginPercent=" + this.topMarginPercent + ", rightMarginPercent=" + this.rightMarginPercent + ", bottomMarginPercent=" + this.bottomMarginPercent + ", startMarginPercent=" + this.startMarginPercent + ", endMarginPercent=" + this.endMarginPercent + ", textSizePercent=" + this.textSizePercent + ", maxWidthPercent=" + this.maxWidthPercent + ", maxHeightPercent=" + this.maxHeightPercent + ", minWidthPercent=" + this.minWidthPercent + ", minHeightPercent=" + this.minHeightPercent + ", paddingLeftPercent=" + this.paddingLeftPercent + ", paddingRightPercent=" + this.paddingRightPercent + ", paddingTopPercent=" + this.paddingTopPercent + ", paddingBottomPercent=" + this.paddingBottomPercent + ", mPreservedParams=" + this.mPreservedParams + '}';
        }

        public void restoreMarginLayoutParams(MarginLayoutParams params) {
            restoreLayoutParams(params);
            params.leftMargin = this.mPreservedParams.leftMargin;
            params.topMargin = this.mPreservedParams.topMargin;
            params.rightMargin = this.mPreservedParams.rightMargin;
            params.bottomMargin = this.mPreservedParams.bottomMargin;
            MarginLayoutParamsCompat.setMarginStart(params, MarginLayoutParamsCompat.getMarginStart(this.mPreservedParams));
            MarginLayoutParamsCompat.setMarginEnd(params, MarginLayoutParamsCompat.getMarginEnd(this.mPreservedParams));
        }

        public void restoreLayoutParams(LayoutParams params) {
            params.width = this.mPreservedParams.width;
            params.height = this.mPreservedParams.height;
        }
    }

    public interface PercentLayoutParams {
        PercentLayoutInfo getPercentLayoutInfo();
    }

    static /* synthetic */ int[] m6x70450a90() {
        int[] iArr = f17x70450a90;
        if (iArr == null) {
            iArr = new int[BASEMODE.values().length];
            try {
                iArr[BASEMODE.BASE_HEIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[BASEMODE.BASE_SCREEN_HEIGHT.ordinal()] = 4;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[BASEMODE.BASE_SCREEN_WIDTH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[BASEMODE.BASE_WIDTH.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            f17x70450a90 = iArr;
        }
        return iArr;
    }

    public PercentLayoutHelper(ViewGroup host) {
        this.mHost = host;
        getScreenSize();
    }

    private void getScreenSize() {
        WindowManager wm = (WindowManager) this.mHost.getContext().getSystemService("window");
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidthScreen = outMetrics.widthPixels;
        mHeightScreen = outMetrics.heightPixels;
    }

    public static void fetchWidthAndHeight(LayoutParams params, TypedArray array, int widthAttr, int heightAttr) {
        params.width = array.getLayoutDimension(widthAttr, 0);
        params.height = array.getLayoutDimension(heightAttr, 0);
    }

    public void adjustChildren(int widthMeasureSpec, int heightMeasureSpec) {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "adjustChildren: " + this.mHost + " widthMeasureSpec: " + MeasureSpec.toString(widthMeasureSpec) + " heightMeasureSpec: " + MeasureSpec.toString(heightMeasureSpec));
        }
        int widthHint = MeasureSpec.getSize(widthMeasureSpec);
        int heightHint = MeasureSpec.getSize(heightMeasureSpec);
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "widthHint = " + widthHint + " , heightHint = " + heightHint);
        }
        int N = this.mHost.getChildCount();
        for (int i = 0; i < N; i++) {
            View view = this.mHost.getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "should adjust " + view + " " + params);
            }
            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "using " + info);
                }
                if (info != null) {
                    supportTextSize(widthHint, heightHint, view, info);
                    supportPadding(widthHint, heightHint, view, info);
                    supportMinOrMaxDimesion(widthHint, heightHint, view, info);
                    if (params instanceof MarginLayoutParams) {
                        info.fillMarginLayoutParams((MarginLayoutParams) params, widthHint, heightHint);
                    } else {
                        info.fillLayoutParams(params, widthHint, heightHint);
                    }
                }
            }
        }
    }

    private void supportPadding(int widthHint, int heightHint, View view, PercentLayoutInfo info) {
        int left = view.getPaddingLeft();
        int right = view.getPaddingRight();
        int top = view.getPaddingTop();
        int bottom = view.getPaddingBottom();
        PercentVal percentVal = info.paddingLeftPercent;
        if (percentVal != null) {
            left = (int) (((float) getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode)) * percentVal.percent);
        }
        percentVal = info.paddingRightPercent;
        if (percentVal != null) {
            right = (int) (((float) getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode)) * percentVal.percent);
        }
        percentVal = info.paddingTopPercent;
        if (percentVal != null) {
            top = (int) (((float) getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode)) * percentVal.percent);
        }
        percentVal = info.paddingBottomPercent;
        if (percentVal != null) {
            bottom = (int) (((float) getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode)) * percentVal.percent);
        }
        view.setPadding(left, top, right, bottom);
    }

    private void supportMinOrMaxDimesion(int widthHint, int heightHint, View view, PercentLayoutInfo info) {
        try {
            Class clazz = view.getClass();
            invokeMethod("setMaxWidth", widthHint, heightHint, view, clazz, info.maxWidthPercent);
            invokeMethod("setMaxHeight", widthHint, heightHint, view, clazz, info.maxHeightPercent);
            invokeMethod("setMinWidth", widthHint, heightHint, view, clazz, info.minWidthPercent);
            invokeMethod("setMinHeight", widthHint, heightHint, view, clazz, info.minHeightPercent);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
    }

    private void invokeMethod(String methodName, int widthHint, int heightHint, View view, Class clazz, PercentVal percentVal) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, new StringBuilder(String.valueOf(methodName)).append(" ==> ").append(percentVal).toString());
        }
        if (percentVal != null) {
            Method setMaxWidthMethod = clazz.getMethod(methodName, new Class[]{Integer.TYPE});
            setMaxWidthMethod.setAccessible(true);
            int base = getBaseByModeAndVal(widthHint, heightHint, percentVal.basemode);
            setMaxWidthMethod.invoke(view, new Object[]{Integer.valueOf((int) (((float) base) * percentVal.percent))});
        }
    }

    private void supportTextSize(int widthHint, int heightHint, View view, PercentLayoutInfo info) {
        PercentVal textSizePercent = info.textSizePercent;
        if (textSizePercent != null) {
            float textSize = (float) ((int) (((float) getBaseByModeAndVal(widthHint, heightHint, textSizePercent.basemode)) * textSizePercent.percent));
            if (view instanceof TextView) {
                ((TextView) view).setTextSize(0, textSize);
            }
        }
    }

    private static int getBaseByModeAndVal(int widthHint, int heightHint, BASEMODE basemode) {
        switch (m6x70450a90()[basemode.ordinal()]) {
            case 1:
                return widthHint;
            case 2:
                return heightHint;
            case 3:
                return mWidthScreen;
            case 4:
                return mHeightScreen;
            default:
                return 0;
        }
    }

    public static PercentLayoutInfo getPercentLayoutInfo(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, C0127R.styleable.PercentLayout_Layout);
        PercentLayoutInfo info = setPaddingRelatedVal(array, setMinMaxWidthHeightRelatedVal(array, setTextSizeSupportVal(array, setMarginRelatedVal(array, setWidthAndHeightVal(array, null)))));
        array.recycle();
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "constructed: " + info);
        }
        return info;
    }

    private static PercentLayoutInfo setWidthAndHeightVal(TypedArray array, PercentLayoutInfo info) {
        PercentVal percentVal = getPercentVal(array, 0, true);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "percent width: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.widthPercent = percentVal;
        }
        percentVal = getPercentVal(array, 1, false);
        if (percentVal == null) {
            return info;
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "percent height: " + percentVal.percent);
        }
        info = checkForInfoExists(info);
        info.heightPercent = percentVal;
        return info;
    }

    private static PercentLayoutInfo setTextSizeSupportVal(TypedArray array, PercentLayoutInfo info) {
        PercentVal percentVal = getPercentVal(array, 9, false);
        if (percentVal == null) {
            return info;
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "percent text size: " + percentVal.percent);
        }
        info = checkForInfoExists(info);
        info.textSizePercent = percentVal;
        return info;
    }

    private static PercentLayoutInfo setMinMaxWidthHeightRelatedVal(TypedArray array, PercentLayoutInfo info) {
        PercentVal percentVal = getPercentVal(array, 10, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.maxWidthPercent = percentVal;
        }
        percentVal = getPercentVal(array, 11, false);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.maxHeightPercent = percentVal;
        }
        percentVal = getPercentVal(array, 12, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.minWidthPercent = percentVal;
        }
        percentVal = getPercentVal(array, 13, false);
        if (percentVal == null) {
            return info;
        }
        info = checkForInfoExists(info);
        info.minHeightPercent = percentVal;
        return info;
    }

    private static PercentLayoutInfo setMarginRelatedVal(TypedArray array, PercentLayoutInfo info) {
        PercentVal percentVal = getPercentVal(array, 2, true);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "percent margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.leftMarginPercent = percentVal;
            info.topMarginPercent = percentVal;
            info.rightMarginPercent = percentVal;
            info.bottomMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, 3, true);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "percent left margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.leftMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, 4, false);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "percent top margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.topMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, 5, true);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "percent right margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.rightMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, 6, false);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "percent bottom margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.bottomMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, 7, true);
        if (percentVal != null) {
            if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "percent start margin: " + percentVal.percent);
            }
            info = checkForInfoExists(info);
            info.startMarginPercent = percentVal;
        }
        percentVal = getPercentVal(array, 8, true);
        if (percentVal == null) {
            return info;
        }
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "percent end margin: " + percentVal.percent);
        }
        info = checkForInfoExists(info);
        info.endMarginPercent = percentVal;
        return info;
    }

    private static PercentLayoutInfo setPaddingRelatedVal(TypedArray array, PercentLayoutInfo info) {
        PercentVal percentVal = getPercentVal(array, 14, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingLeftPercent = percentVal;
            info.paddingRightPercent = percentVal;
            info.paddingBottomPercent = percentVal;
            info.paddingTopPercent = percentVal;
        }
        percentVal = getPercentVal(array, 17, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingLeftPercent = percentVal;
        }
        percentVal = getPercentVal(array, 18, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingRightPercent = percentVal;
        }
        percentVal = getPercentVal(array, 15, true);
        if (percentVal != null) {
            info = checkForInfoExists(info);
            info.paddingTopPercent = percentVal;
        }
        percentVal = getPercentVal(array, 16, true);
        if (percentVal == null) {
            return info;
        }
        info = checkForInfoExists(info);
        info.paddingBottomPercent = percentVal;
        return info;
    }

    private static PercentVal getPercentVal(TypedArray array, int index, boolean baseWidth) {
        return getPercentVal(array.getString(index), baseWidth);
    }

    private static PercentLayoutInfo checkForInfoExists(PercentLayoutInfo info) {
        if (info != null) {
            return info;
        }
        return new PercentLayoutInfo();
    }

    private static PercentVal getPercentVal(String percentStr, boolean isOnWidth) {
        if (percentStr == null) {
            return null;
        }
        Matcher matcher = Pattern.compile(REGEX_PERCENT).matcher(percentStr);
        if (matcher.matches()) {
            int len = percentStr.length();
            String floatVal = matcher.group(1);
            String lastAlpha = percentStr.substring(len - 1);
            float percent = Float.parseFloat(floatVal) / 100.0f;
            PercentVal percentVal = new PercentVal();
            percentVal.percent = percent;
            if (percentStr.endsWith(BASEMODE.SW)) {
                percentVal.basemode = BASEMODE.BASE_SCREEN_WIDTH;
                return percentVal;
            } else if (percentStr.endsWith(BASEMODE.SH)) {
                percentVal.basemode = BASEMODE.BASE_SCREEN_HEIGHT;
                return percentVal;
            } else if (percentStr.endsWith(BASEMODE.PERCENT)) {
                if (isOnWidth) {
                    percentVal.basemode = BASEMODE.BASE_WIDTH;
                    return percentVal;
                }
                percentVal.basemode = BASEMODE.BASE_HEIGHT;
                return percentVal;
            } else if (percentStr.endsWith(BASEMODE.f16W)) {
                percentVal.basemode = BASEMODE.BASE_WIDTH;
                return percentVal;
            } else if (percentStr.endsWith(BASEMODE.f15H)) {
                percentVal.basemode = BASEMODE.BASE_HEIGHT;
                return percentVal;
            } else {
                throw new IllegalArgumentException("the " + percentStr + " must be endWith [%|w|h|sw|sh]");
            }
        }
        throw new RuntimeException("the value of layout_xxxPercent invalid! ==>" + percentStr);
    }

    public void restoreOriginalParams() {
        int N = this.mHost.getChildCount();
        for (int i = 0; i < N; i++) {
            View view = this.mHost.getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "should restore " + view + " " + params);
            }
            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "using " + info);
                }
                if (info != null) {
                    if (params instanceof MarginLayoutParams) {
                        info.restoreMarginLayoutParams((MarginLayoutParams) params);
                    } else {
                        info.restoreLayoutParams(params);
                    }
                }
            }
        }
    }

    public boolean handleMeasuredStateTooSmall() {
        boolean needsSecondMeasure = false;
        int N = this.mHost.getChildCount();
        for (int i = 0; i < N; i++) {
            View view = this.mHost.getChildAt(i);
            LayoutParams params = view.getLayoutParams();
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "should handle measured state too small " + view + " " + params);
            }
            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    if (shouldHandleMeasuredWidthTooSmall(view, info)) {
                        needsSecondMeasure = true;
                        params.width = -2;
                    }
                    if (shouldHandleMeasuredHeightTooSmall(view, info)) {
                        needsSecondMeasure = true;
                        params.height = -2;
                    }
                }
            }
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "should trigger second measure pass: " + needsSecondMeasure);
        }
        return needsSecondMeasure;
    }

    private static boolean shouldHandleMeasuredWidthTooSmall(View view, PercentLayoutInfo info) {
        int state = ViewCompat.getMeasuredWidthAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.widthPercent == null || state != ViewCompat.MEASURED_STATE_TOO_SMALL || info.widthPercent.percent < 0.0f || info.mPreservedParams.width != -2) {
            return false;
        }
        return true;
    }

    private static boolean shouldHandleMeasuredHeightTooSmall(View view, PercentLayoutInfo info) {
        int state = ViewCompat.getMeasuredHeightAndState(view) & ViewCompat.MEASURED_STATE_MASK;
        if (info == null || info.heightPercent == null || state != ViewCompat.MEASURED_STATE_TOO_SMALL || info.heightPercent.percent < 0.0f || info.mPreservedParams.height != -2) {
            return false;
        }
        return true;
    }
}
