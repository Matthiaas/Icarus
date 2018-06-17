import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer implements Runnable {
    private boolean running = true;
    private List<DatagramPacket> mCmdPool = new ArrayList();
    private Thread mReceiveThread;
    private DatagramSocket mUDPSocket;
    private Thread mWriteThread;

    public void run() {
        while (running) {
            try {
                if (UDPServer.this.mCmdPool.size() > 0) {
                    UDPServer.this.mUDPSocket.send(UDPServer.this.mCmdPool.get(0));
                    UDPServer.this.mCmdPool.remove(0);
                }
                Thread.sleep(1);
            } catch (Exception e) {

                System.out.print(e.toString());
                return;
            }
        }
    }


    public void start() {
        running = true;
        try {
            mUDPSocket = new DatagramSocket();
        } catch (IOException ex) {
            running = false;
            System.out.print(ex.toString());
            mWriteThread = new Thread(this);
            mWriteThread.start();
        }
    }

    public void stop() {
        running = false;
        try {
            mWriteThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void addCmdP(byte[] Msg, InetAddress addr, int port) {
        if (mUDPSocket != null && addr != null && Msg != null && Msg.length > 0) {
            mCmdPool.add(new DatagramPacket(Msg, Msg.length, addr, port));
        }
    }

    public void writeUDPCmd(byte[] data, InetAddress mDevAddr) {
        addCmdP(data, mDevAddr, 8080);
    }
}
