package com.fh.lib;

/* compiled from: SDLActivity */
class SDLMain implements Runnable {
    SDLMain() {
    }

    public void run() {
        synchronized (SDLActivity.class) {
            SDLActivity.nativeInit();
        }
    }
}
