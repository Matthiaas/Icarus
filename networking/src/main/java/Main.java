import java.io.BufferedReader;
import java.io.IOException;

public class Main {

    static String ifName_managed = "wlan0";
    static String ifName_monitor = "wlan1";

    static String essid = "9300-0006C7";
    /**
     * you need to set your ifName
     *
     * @param args
     */
    public static void main(String[] args){
        System.out.println("please make sure you have installed arp-scan");


        /*connect to wifi*/
 /*       System.err.println("connecting...");
        System.err.print("|-> ");
        if(!Networking.connectWIFI(essid, ifName)){
            System.err.println("failed to connect to " + essid);
            System.err.println("aborting ... ");
            return;
        }
        String ap = Networking.getGatewayIp();
        System.out.println("gatemway ip: " + ap);
        String connectedNetwork = Networking.findConnectedNetwork(ifName);
        System.err.println("connection name: " + Networking.findConnectedNetwork(ifName));
        //System.err.println("spoofing...");
        String ap_mac = Networking.getMAC(ap);
        System.err.println("MAC: " + ap_mac);
        if(ap_mac.equals("")) return;*/
        System.out.println("spoofing...");

        boolean success = AirocrackSuite.deauth(essid, 100, ifName_monitor);
        System.out.println("success: " + success);
    }
}
