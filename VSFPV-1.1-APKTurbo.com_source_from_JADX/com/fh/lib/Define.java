package com.fh.lib;

public class Define {

    public class BCSS {
        public int brightness;
        public int contrast;
        public int saturation;
        public int sharpness;
    }

    public interface CbDataInterface {
        void cb_data(int i, byte[] bArr, int i2);
    }

    public class DevSearch {
        public String devIP;
        public String devName;
        public int isAlive;
        public String port;
    }

    public class DeviceTime {
        public int day;
        public int hour;
        public int minute;
        public int month;
        public int msecond;
        public int second;
        public int wday;
        public int year;
    }

    public class FrameHead {
        public int frameType;
        public int height;
        public long timeStamp;
        public int videoFormat;
        public int width;
    }

    public class IpConfig {
        public int isAutoIP;
        public String sGateway;
        public String sIP;
        public String sMark;
        public String sPort;
    }

    public class PBRecTime {
        public long pbStartTime;
        public long pbStopTime;
    }

    public class PicSearch {
        public int chanSeldID;
        public int lockFSeldID;
        public int startDay;
        public int startMonth;
        public int startYear;
        public int stopDay;
        public int stopMonth;
        public int stopYear;
        public int typeSeldID;
    }

    public class Picture {
        public int chanID;
        public long dataSize;
        public long frameCount;
        public int lockFlag;
        public int picType;
        public long startTime;
        public long stopTime;
    }

    public class Preview {
        public int blocked;
        public int chan;
        public int encId;
        public int transMode;
    }

    public class RecSearch {
        public int chanSeldID;
        public int lockFSeldID;
        public int startDay;
        public int startMonth;
        public int startYear;
        public int stopDay;
        public int stopMonth;
        public int stopYear;
        public int typeSeldID;
    }

    public class Record {
        public int chanID;
        public long dataSize;
        public int lockFlag;
        public int recType;
        public long startTime;
        public long stopTime;
    }

    public enum Res_e {
        FHNPEN_ER_QCIF("QCIF", 0),
        FHNPEN_ER_CIF("CIF", 1),
        FHNPEN_ER_4CIF("4CIF", 2),
        FHNPEN_ER_D1("D1", 3),
        FHNPEN_ER_640x480("VGA", 4),
        FHNPEN_ER_QVGA("QVGA", 5),
        FHNPEN_ER_720P("720P", 6),
        FHNPEN_ER_960P("960P", 7),
        FHNPEN_ER_1080P("1080P", 8),
        FHNPEN_ER_960H("960H", 9);
        
        private int index;
        private String name;

        private Res_e(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }

        public String getName() {
            return this.name;
        }

        public static String getNameByIndex(int index) {
            for (Res_e res : values()) {
                if (index == res.index) {
                    return res.name;
                }
            }
            return null;
        }
    }

    public class SDCardFormat {
        public int formatProgress;
        public int formatState;
    }

    public class SDCardInfo {
        public byte state;
        public long totalSize;
        public long usedSize;
    }

    public interface SerialDataCallBackInterface {
        int SerialDataCallBack(int i, byte[] bArr, int i2);
    }

    public interface StreamDataCallBackInterface {
        void StreamDataCallBack(int i, int i2, FrameHead frameHead, byte[] bArr, int i3);
    }

    public class VideoEncode {
        public int ctrlType;
        public int deinter;
        public int denoise;
        public int iFrameInterval;
        public int maxBitRate;
        public int maxFRate;
        public int quality;
        public int res;
    }

    public class WifiConfig {
        public String sChan;
        public String sPSK;
        public String sSSID;
        public int status;
        public int wifiMode;
        public int wifiType;
    }

    public interface YUVDataCallBackInterface {
        void update(int i, int i2);

        void update(byte[] bArr);

        void update(byte[] bArr, byte[] bArr2, byte[] bArr3);
    }
}
