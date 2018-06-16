import java.io.BufferedReader;

public class AirocrackSuite {

    public static boolean deauth(String essid, int nr_packets, String use_interface){

        String cmd = String.format("sudo aireplay-ng -0 %d -e %s %s", nr_packets, essid ,use_interface);
        System.out.println(cmd);
        BufferedReader reader = Terminal.exec(cmd);
    return false;
    }


}
