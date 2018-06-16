package com.fh.lib;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;

/* compiled from: SDLActivity */
class SDLGenericMotionListener_API12 implements OnGenericMotionListener {
    SDLGenericMotionListener_API12() {
    }

    public boolean onGenericMotion(View v, MotionEvent event) {
        return SDLActivity.handleJoystickMotionEvent(event);
    }
}
