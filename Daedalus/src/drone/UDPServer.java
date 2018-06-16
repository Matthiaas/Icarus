package drone;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private boolean isWork = true;
    private List<DatagramPacket> mCmdPool = new ArrayList();
    private Thread mReceiveThread;
    private DatagramSocket mUDPSocket;
    private Thread mWriteThread;

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

    public void addCmdP(byte[] Msg, InetAddress addr, int port) {
        if (this.mUDPSocket != null && addr != null && Msg != null && Msg.length > 0) {
            this.mCmdPool.add(new DatagramPacket(Msg, Msg.length, addr, port));
        }
    }

    public void writeUDPCmd(byte[] data, InetAddress mDevAddr) {
        addCmdP(data, mDevAddr, 8080);
    }
}
