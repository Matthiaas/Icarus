import java.net.InetAddress;
import java.net.UnknownHostException;

public class PreviewView {




    public static int mSpeedValue = 0;
    public static int mStopValue = 0;
    public static int mToLandValue = 0;
    public static int mToflyValue = 1;
    public static int m360RollValue = 0;
    public static int mHeadLessValue = 0;
    public static int mGRightValue = 0;


    public static UDPServer s;


    public static InetAddress devAdress;

    static {
        try {
            devAdress = InetAddress.getByName("172.16.10.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    //Hope thats 1!
    public static int mAirnValue = 1;
}
