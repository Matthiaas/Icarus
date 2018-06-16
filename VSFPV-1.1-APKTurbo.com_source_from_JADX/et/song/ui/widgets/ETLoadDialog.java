package et.song.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import et.song.vspfv.C0127R;

public class ETLoadDialog extends Dialog {
    Context mContext;

    public ETLoadDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        init();
    }

    public ETLoadDialog(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        LinearLayout contentView = new LinearLayout(this.mContext);
        contentView.setMinimumHeight(48);
        contentView.setGravity(17);
        contentView.setOrientation(0);
        ImageView image = new ImageView(this.mContext);
        image.setImageResource(17301629);
        Animation anim = AnimationUtils.loadAnimation(this.mContext, C0127R.anim.rotate_repeat);
        anim.setInterpolator(new LinearInterpolator());
        image.setAnimation(anim);
        contentView.addView(image);
        setContentView(contentView);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == 4 ? true : true;
    }
}
