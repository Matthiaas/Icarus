package et.song.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class ETButton extends ImageButton {
    public static final float[] ETBUTTON_NOT_SELECTED = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    public static final float[] ETBUTTON_SELECTED = new float[]{0.6f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.6f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.6f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.6f, 0.0f};

    class C00951 implements OnTouchListener {
        C00951() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_SELECTED));
                v.setBackground(v.getBackground());
            } else if (event.getAction() == 1) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(ETButton.ETBUTTON_NOT_SELECTED));
                v.setBackground(v.getBackground());
            }
            return false;
        }
    }

    public ETButton(Context context) {
        super(context);
    }

    @SuppressLint({"NewApi"})
    public ETButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new C00951());
    }

    public ETButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setStatus(boolean val) {
        if (val) {
            getBackground().setColorFilter(new ColorMatrixColorFilter(ETBUTTON_NOT_SELECTED));
        } else {
            getBackground().setColorFilter(new ColorMatrixColorFilter(ETBUTTON_SELECTED));
        }
    }
}
