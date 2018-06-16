import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class TCPServer {


    private Socket mSocket = null;
    private String mHost;
    private int mPort;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;
    LinkedList<byte[]> cmd = new LinkedList<byte[]>();


    class ReceiveThread implements Runnable {
        ReceiveThread() {
        }

        public void run() {
            try {
                byte[] byteBuffer = new byte[262144];

                while (TCPServer.this.mInStream != null) {
                    int temp = TCPServer.this.mInStream.read(byteBuffer);
                    if (temp != -1) {
                        byte[] redata = new byte[temp];
                        System.arraycopy(byteBuffer, 0, redata, 0, temp);
                        //Message msg = new Message();
                        //msg.obj = redata;
                        // msg.what = AppConfig.TCP_RECEIVE_DATA;
                        // TCPSocket.this.mAccesser.sendMessage(msg);
                    }

                }
            } catch (Exception e) {
                System.out.print(e.toString());
            }
        }
    }

    class WriteThread implements Runnable {
        WriteThread() {
        }

        public void run() {
            while (TCPServer.this.mOutStream != null) {
                try {
                    TCPServer.this.mOutStream.write((byte[]) TCPServer.this.cmd.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    TCPServer.this.mOutStream.flush();
                    TCPServer.this.cmd.remove(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public TCPServer() throws IOException {

        this.mSocket = new Socket(this.mHost, this.mPort);
        this.mInStream = this.mSocket.getInputStream();
        this.mOutStream = this.mSocket.getOutputStream();
        //this.mReceiveThread = new Thread(new ReceiveThread());
        //this.mReceiveThread.start();
        //this.mWriteThread = new Thread(new WriteThread());
        //this.mWriteThread.start();
    }





    public static void main(String aghs[]){







    }
}
