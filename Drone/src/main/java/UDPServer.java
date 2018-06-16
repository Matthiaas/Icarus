import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    public static final String TAG = "UDPServer";
    public static final int UDP_RECEIVE_DATA = 10001;
    public static final int UDP_SERVER_INIT = 10002;
    private boolean isWork = true;
    private List<DatagramPacket> mCmdPool = new ArrayList();
    private Thread mReceiveThread;
    private DatagramSocket mUDPSocket;
    private Thread mWriteThread;
    //Runnable runUDPReceiveThread = new C01451();
    Runnable runUDPWriteThread = new C01462();

    /*
    class C01451 implements Runnable {
        C01451() {
        }

        public void run() {
            UDPServer.this.mReceiveThread = new Thread(new UDPReceiveThread());
            UDPServer.this.mReceiveThread.start();
        }
    }
    */

    class C01462 implements Runnable {
        C01462() {
        }

        public void run() {
            UDPServer.this.mWriteThread = new Thread(new UDPWriteThread());
            UDPServer.this.mWriteThread.start();
        }
    }
/*
    class UDPReceiveThread implements Runnable {
        UDPReceiveThread() {
        }

        public void run() {
            byte[] message = new byte[AccessibilityEventCompat.TYPE_GESTURE_DETECTION_START];
            DatagramPacket dataPacket = new DatagramPacket(message, message.length);
            if (UDPServer.this.mAccesser != null) {
                Message msg = new Message();
                msg.what = 10002;
                UDPServer.this.mAccesser.sendMessage(msg);
            }
            while (UDPServer.this.isWork) {
                try {
                    UDPServer.this.mUDPSocket.receive(dataPacket);
                    byte[] recData = new byte[dataPacket.getLength()];
                    System.arraycopy(dataPacket.getData(), 0, recData, 0, recData.length);
                    if (UDPServer.this.mAccesser != null) {
                        msg = new Message();
                        msg.obj = recData;
                        msg.what = 10001;
                        UDPServer.this.mAccesser.sendMessage(msg);
                    }
                } catch (Exception e) {
                    UDPServer.this.mHandler.postDelayed(UDPServer.this.runUDPReceiveThread, 1000);
                    System.out.print(e.toString());
                    return;
                }
            }
        }
    }
*/
    class UDPWriteThread implements Runnable {
        UDPWriteThread() {
        }

        public void run() {
            while (UDPServer.this.isWork) {
                try {
                    if (UDPServer.this.mCmdPool.size() > 0) {
                        UDPServer.this.mUDPSocket.send((DatagramPacket) UDPServer.this.mCmdPool.get(0));
                        //System.out.println("dd");
                        UDPServer.this.mCmdPool.remove(0);
                    }
                    Thread.sleep(1);
                } catch (Exception e) {

                    System.out.print(e.toString());
                    return;
                }
            }
        }
    }

    public void Start() {
        this.isWork = true;
        StartServer();
        //this.mReceiveThread = new Thread(new UDPReceiveThread());
        //this.mReceiveThread.start();
        this.mWriteThread = new Thread(new UDPWriteThread());
        this.mWriteThread.start();
    }

    private void StartServer() {
        try {
            this.mUDPSocket = new DatagramSocket();
        } catch (IOException ex) {
            this.isWork = false;
            System.out.print(ex.toString());
        }
    }

    public boolean isRun() {
        return (this.mUDPSocket == null || this.mUDPSocket.isClosed()) ? false : true;
    }

    public void Stop() {
        this.isWork = false;
        if (this.mReceiveThread != null) {
            this.mReceiveThread.interrupt();
            this.mReceiveThread = null;
        }
        if (this.mWriteThread != null) {
            this.mWriteThread.interrupt();
            this.mWriteThread = null;
        }
        if (this.mUDPSocket != null) {
            this.mUDPSocket.close();
            this.mUDPSocket = null;
        }
    }



    public void addCmdP(byte[] Msg, InetAddress addr, int port) {
        if (this.mUDPSocket != null && addr != null && Msg != null && Msg.length > 0) {
            this.mCmdPool.add(new DatagramPacket(Msg, Msg.length, addr, port));
        }
    }

    public void writeUDPCmd(byte[] data , InetAddress mDevAddr) {
        addCmdP(data, mDevAddr, 8080);
    }
}
