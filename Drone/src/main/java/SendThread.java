public class SendThread extends Thread {



    private boolean isRun = true;

    public void run() {
        while (this.isRun) {
            byte[] data = new byte[11];
            data[0] = (byte) -1;
            data[1] = (byte) 8;
            int ck = 0 + data[1];

            data[2] = (byte) ((int) (255 / 2 * 2.0f));
            ck += data[2];

            data[3] = (byte) ((int) (255 / 2));
            ck += data[3];

            data[4] = (byte) ((int) (255/ 2));
            ck += data[4];

            data[5] = (byte) ((int) (255/ 2));
            // if ((data[4] >= (byte) 96 || data[4] <= (byte) 32 || data[5] >= (byte) 96 || data[5] <= (byte) 32) && 1 == ((Integer) PreviewView.this.mButton360Roll.getTag()).intValue()) {
            //    PreviewView.m360RollValue = 1;
            //}
            ck += data[5];
            data[6] = (byte) 255/2;
            if (PreviewView.mAirnValue == 1) {
                data[6] = (byte) (data[6] | 128);
            } else {
                //data[6] = (byte) (data[6] & TransportMediator.KEYCODE_MEDIA_PAUSE);
            }
            data[6] = (byte) (data[6] | (PreviewView.mGRightValue << 6));
            ck += data[6];
            data[7] = (byte) ((int) (255 / 2));
            ck += data[7];
            data[8] = (byte) ((int) (255 / 2));
            ck += data[8];
            data[9] = (byte) PreviewView.mSpeedValue;
            data[9] = (byte) (data[9] | (PreviewView.m360RollValue << 2));
            data[9] = (byte) (data[9] | (PreviewView.mHeadLessValue << 4));
            data[9] = (byte) (data[9] | (PreviewView.mStopValue << 5));
            data[9] = (byte) (data[9] | (PreviewView.mToflyValue << 6));
            data[9] = (byte) (data[9] | (PreviewView.mToLandValue << 7));
            data[10] = (byte) (255 - ((byte) (ck + data[9])));

            //TODO: SEND DATA
            PreviewView.s.writeUDPCmd(data, PreviewView.devAdress);
            try {
                Thread.sleep(25);
            } catch (Exception e) {
            }

        }
    }

    public void cancel() {
        this.isRun = false;
        interrupt();
    }

    public boolean getRun() {
        return this.isRun;
    }
}