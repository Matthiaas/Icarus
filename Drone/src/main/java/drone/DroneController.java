package drone;

import java.net.InetAddress;

public class DroneController extends Thread {

    private static final int MAX_VALUE = 255;

    public int mSpeedValue = 0;
    public int mStopValue = 0;
    public int mToLandValue = 0;
    public int mToflyValue = 1;
    public int m360RollValue = 0;
    public int mHeadLessValue = 0;
    //public int mGRightValue = 0;
    // probably one
    //public int mAirnValue = 1;

    public int YVL = MAX_VALUE;
    public int XVL = MAX_VALUE / 4;
    public int YVR = MAX_VALUE / 4;
    public int XVR = MAX_VALUE / 4;
    public int leftTrim = 0;
    public int rightTrim = 0;
    public int sideTrim = 0;


    private InetAddress devAddress;
    private UDPServer udpServer;

    public DroneController(InetAddress devAddress) {
        this.devAddress = devAddress;
        udpServer = new UDPServer();
        udpServer.Start();
    }

    public void fly() {
        System.out.println("Stage [0]: taking control");
    }

    public void land() {
        System.out.println("Stage [1]: landing");
        mToflyValue = 0;
        mSpeedValue = 2;
        YVL = MAX_VALUE / 4;
        mToLandValue = 1;
    }

    public void kill() {
        System.out.println("Stage [0]: killing motors");
        mToflyValue = 0;
        mStopValue = 1;
    }

    public void run() {
        long time = System.currentTimeMillis();
        int state = 0;
        while (true) {
            long d = System.currentTimeMillis() - time;

            if (state == 0) {
                state++;
                time = System.currentTimeMillis();
                fly();
            } else if (state == 1 && d > 5000) {
                state++;
                time = System.currentTimeMillis();
                land();
            } else if (state == 2 && d > 15000) {
                state++;
                time = System.currentTimeMillis();
                kill();
            } else if (state == 3 && d > 5000) {
                System.exit(-1);
            }

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

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
            }
        }
    }

}