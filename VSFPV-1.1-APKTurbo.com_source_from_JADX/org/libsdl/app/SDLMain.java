package org.libsdl.app;

/* compiled from: SDLActivity */
class SDLMain implements Runnable {
    SDLMain() {
    }

    public void run() {
        if (SDLActivity.mSingleton != null) {
            SDLActivity.nativeInit(SDLActivity.mSingleton.getArguments()[0]);
        }
    }
}
