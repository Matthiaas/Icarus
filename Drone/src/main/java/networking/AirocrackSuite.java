package networking;

public class AirocrackSuite {

    public static void deauth(String essid, int nr_packets, String use_interface) {
        String cmd = String.format("sudo aireplay-ng -0 %d -e %s %s", nr_packets, essid, use_interface);
        Terminal.exec(cmd);
    }


}
