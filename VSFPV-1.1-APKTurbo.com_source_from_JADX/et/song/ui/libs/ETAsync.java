package et.song.ui.libs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

public class ETAsync {
    private static ImageCallback mCallBack;
    static final Handler mHandler = new C00891();
    private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap();
    private InputStream mInputStream;
    private String mTag;

    class C00891 extends Handler {
        C00891() {
        }

        public void handleMessage(Message message) {
            ETAsync.mCallBack.imageLoaded((byte[]) message.obj);
        }
    }

    class C00902 extends Thread {
        C00902() {
        }

        public void run() {
            try {
                byte[] data = ETAsync.readInputStream(ETAsync.this.mInputStream);
                ETAsync.this.imageCache.put(ETAsync.this.mTag, new SoftReference(BitmapFactory.decodeByteArray(data, 0, data.length)));
                ETAsync.mHandler.sendMessage(ETAsync.mHandler.obtainMessage(0, data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface ImageCallback {
        void imageLoaded(byte[] bArr);
    }

    public ETAsync() {
        mCallBack = null;
    }

    public Bitmap loadDrawable(InputStream input, String tag, ImageCallback imageCallback) {
        mCallBack = imageCallback;
        this.mInputStream = input;
        this.mTag = tag;
        if (this.imageCache.containsKey(this.mTag)) {
            Bitmap bitmap = (Bitmap) ((SoftReference) this.imageCache.get(this.mTag)).get();
            if (bitmap != null) {
                return bitmap;
            }
        }
        new C00902().start();
        return null;
    }

    public static byte[] readInputStream(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int length = is.read(buffer);
            if (length == -1) {
                baos.flush();
                byte[] data = baos.toByteArray();
                is.close();
                baos.close();
                return data;
            }
            baos.write(buffer, 0, length);
        }
    }
}
