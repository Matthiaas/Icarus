package et.song.ui.libs;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import et.song.vspfv.AppConfig;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPSocket {
    public static final String TAG = "TCPSocket";
    public boolean isWork;
    private Handler mAccesser;
    private List<byte[]> mCmdPool = new ArrayList();
    public Handler mHandler = new Handler();
    private String mHost;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;
    private int mPort;
    private Thread mReceiveThread;
    private Socket mSocket = null;
    private Thread mWriteThread;
    Runnable runTCPReceiveThread = new C00921();
    Runnable runTCPWriteThread = new C00932();

    class C00921 implements Runnable {
        C00921() {
        }

        public void run() {
            TCPSocket.this.mReceiveThread = new Thread(new ReceiveThread());
            TCPSocket.this.mReceiveThread.start();
        }
    }

    class C00932 implements Runnable {
        C00932() {
        }

        public void run() {
            TCPSocket.this.mWriteThread = new Thread(new WriteThread());
            TCPSocket.this.mWriteThread.start();
        }
    }

    class C00943 extends Thread {
        C00943() {
        }

        public void run() {
            try {
                TCPSocket.this.mSocket = new Socket(TCPSocket.this.mHost, TCPSocket.this.mPort);
                TCPSocket.this.mInStream = TCPSocket.this.mSocket.getInputStream();
                TCPSocket.this.mOutStream = TCPSocket.this.mSocket.getOutputStream();
                TCPSocket.this.mReceiveThread = new Thread(new ReceiveThread());
                TCPSocket.this.mReceiveThread.start();
                TCPSocket.this.mWriteThread = new Thread(new WriteThread());
                TCPSocket.this.mWriteThread.start();
                if (TCPSocket.this.mAccesser != null) {
                    TCPSocket.this.mAccesser.sendEmptyMessage(AppConfig.TCP_CONNECT_SUCCEED);
                }
            } catch (Exception e) {
                if (TCPSocket.this.mAccesser != null) {
                    TCPSocket.this.mAccesser.sendEmptyMessage(AppConfig.TCP_CONNECT_ERROR);
                }
                TCPSocket.this.isWork = false;
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread implements Runnable {
        ReceiveThread() {
        }

        public void run() {
            try {
                byte[] byteBuffer = new byte[AccessibilityEventCompat.TYPE_GESTURE_DETECTION_START];
                while (TCPSocket.this.mInStream != null) {
                    int temp = TCPSocket.this.mInStream.read(byteBuffer);
                    if (temp != -1) {
                        if (TCPSocket.this.mAccesser != null && temp > 0) {
                            byte[] redata = new byte[temp];
                            System.arraycopy(byteBuffer, 0, redata, 0, temp);
                            Message msg = new Message();
                            msg.obj = redata;
                            msg.what = AppConfig.TCP_RECEIVE_DATA;
                            TCPSocket.this.mAccesser.sendMessage(msg);
                        }
                    } else {
                        return;
                    }
                }
            } catch (Exception e) {
                TCPSocket.this.mHandler.postDelayed(TCPSocket.this.runTCPReceiveThread, 1000);
                System.out.print(e.toString());
            }
        }
    }

    class WriteThread implements Runnable {
        WriteThread() {
        }

        public void run() {
            while (TCPSocket.this.isWork) {
                try {
                    if (TCPSocket.this.mCmdPool.size() > 0 && TCPSocket.this.mOutStream != null) {
                        TCPSocket.this.mOutStream.write((byte[]) TCPSocket.this.mCmdPool.get(0));
                        TCPSocket.this.mOutStream.flush();
                        TCPSocket.this.mCmdPool.remove(0);
                    }
                    Thread.sleep(10);
                } catch (Exception e) {
                    TCPSocket.this.mHandler.postDelayed(TCPSocket.this.runTCPWriteThread, 1000);
                    System.out.print(e.toString());
                    return;
                }
            }
        }
    }

    public TCPSocket(String host, int port, Handler handler) {
        this.mHost = host;
        this.mPort = port;
        this.mAccesser = handler;
    }

    public void Start() {
        this.isWork = true;
        new C00943().start();
    }

    public void Stop() {
        try {
            this.isWork = false;
            this.mSocket.close();
            this.mSocket = null;
            this.mReceiveThread.join();
            this.mReceiveThread = null;
            this.mWriteThread.join();
            this.mWriteThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SendData(byte[] data) {
        this.mCmdPool.add(data);
    }

    public void SendData(String data) {
        this.mCmdPool.add(data.getBytes());
    }

    public String getHost() {
        return this.mHost;
    }

    public void setHost(String mHost) {
        this.mHost = mHost;
    }

    public int getPort() {
        return this.mPort;
    }

    public void setPort(int mPort) {
        this.mPort = mPort;
    }

    public Handler getAccesser() {
        return this.mAccesser;
    }

    public void setAccesser(Handler mAccesser) {
        this.mAccesser = mAccesser;
    }
}
