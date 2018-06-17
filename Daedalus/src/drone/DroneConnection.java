package drone;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DroneConnection implements Runnable {

    private static final int MAX_VALUE = 255;

    private int mSpeedValue = 0;
    private int mStopValue = 0;
    private int mToLandValue = 0;
    private int mToflyValue = 1;
    private int m360RollValue = 0;
    private int mHeadLessValue = 0;


    private int YVL = MAX_VALUE;
    private int XVL = MAX_VALUE / 4;
    private int YVR = MAX_VALUE / 4;
    private int XVR = MAX_VALUE / 4;


    private InetAddress devAddress;
    private UDPServer udpServer;
    private boolean running;

    private Thread sender;

    public DroneConnection(String ip) {
        try {
            devAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
        }
        udpServer = new UDPServer();
    }

    public void start() {
        running = true;
        sender = new Thread(this);
        udpServer.start();
        sender.start();
    }

    public void stop() {
        udpServer.stop();
        running = false;
        try {
            sender.join();
        } catch (InterruptedException e) {
        }
    }

    public void setFlyParameters() {
        mSpeedValue = 0;
        mStopValue = 0;
        mToLandValue = 0;
        mToflyValue = 1;
        YVL = MAX_VALUE;
    }

    public void setLandingParams() {
        mSpeedValue = 2;
        mStopValue = 0;
        mToLandValue = 1;
        mToflyValue = 0;
        YVL = MAX_VALUE / 4;
    }

    public void setKillParameters() {
        mSpeedValue = 2;
        mStopValue = 1;
        mToLandValue = 1;
        mToflyValue = 0;
        YVL = 0;
    }

    public void run() {
        while (running) {
            sendPacket();
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
            }
        }
    }

    private void sendPacket() {
        byte[] data = new byte[11];
        data[0] = (byte) -1;
        data[1] = (byte) 8;
        int ck = 0 + data[1];

        //mLeftRocker.getYV()
        data[2] = (byte) ((int) (YVL));
        ck += data[2];

        //mLeftRocker.getXV()
        data[3] = (byte) ((int) (XVL));
        ck += data[3];

        //PreviewView.this.mRightRocker.getMaxY()) - PreviewView.this.mRightRocker.getYV()
        data[4] = (byte) ((int) (MAX_VALUE - YVR));
        ck += data[4];

        //PreviewView.this.mRightRocker.getXV()
        data[5] = (byte) ((int) (XVR));
        ck += data[5];

        //loopings!?!?

        //mTrimLeft.getPos()
        //data[6] = (byte) leftTrim;
        //if (mAirnValue == 1) {
        //    data[6] = (byte) (data[6] | 128);
        //} else {
        //    //data[6] = (byte) (data[6] & TransportMediator.KEYCODE_MEDIA_PAUSE);
        //}
        //data[6] = (byte) (data[6] | (mGRightValue << 6));
        data[6] = (byte) 0x90;

        ck += data[6];

        //PreviewView.this.mTrimSide.getMax() - PreviewView.this.mTrimSide.getPos()
        //data[7] = (byte) ((int) (MAX_VALUE - sideTrim));
        data[7] = (byte) 0x10;
        ck += data[7];

        //PreviewView.this.mTrimRight.getPos()
        //data[8] = (byte) ((int) (rightTrim));
        data[7] = (byte) 0x10;
        ck += data[8];

        // settings (0 or 1)
        data[9] = (byte) mSpeedValue;
        data[9] = (byte) (data[9] | (m360RollValue << 2));
        data[9] = (byte) (data[9] | (mHeadLessValue << 4));
        data[9] = (byte) (data[9] | (mStopValue << 5));
        data[9] = (byte) (data[9] | (mToflyValue << 6));
        data[9] = (byte) (data[9] | (mToLandValue << 7));

        //checksum
        data[10] = (byte) (255 - ((byte) (ck + data[9])));

        //sending data
        udpServer.writeUDPCmd(data, devAddress);

    }
}