package org.libsdl.app;

import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;

/* compiled from: SDLActivity */
class SDLGenericMotionListener_API12 implements OnGenericMotionListener {
    SDLGenericMotionListener_API12() {
    }

    public boolean onGenericMotion(View v, MotionEvent event) {
        switch (event.getSource()) {
            case 513:
            case 1025:
            case 16777232:
                return SDLActivity.handleJoystickMotionEvent(event);
            case FragmentTransaction.TRANSIT_FRAGMENT_CLOSE /*8194*/:
                int action = event.getActionMasked();
                switch (action) {
                    case 7:
                        SDLActivity.onNativeMouse(0, action, event.getX(0), event.getY(0));
                        return true;
                    case 8:
                        SDLActivity.onNativeMouse(0, action, event.getAxisValue(10, 0), event.getAxisValue(9, 0));
                        return true;
                    default:
                        break;
                }
        }
        return false;
    }
}
