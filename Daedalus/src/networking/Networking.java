package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


public class Networking {

    public static void deauth(String ifName_monitor, String essid) throws InterruptedException {
        //System.out.println("spoofing...");
        System.out.println("Starting DEAUTH ...");
        boolean success = AirocrackSuite.deauth(essid, 10, ifName_monitor);
        Thread.sleep(3000);
        //System.out.println("Deauthentication " + (success?"successful!":"failed!"));
    }

    public static String connect(String ifName_managed, String ifName_monitor, String essid) {

        System.err.println("Connecting to [" + essid + "]");
        if (!Networking.connectWIFI(essid, ifName_managed)) {
            System.err.println("Failed to connect!\nAborting...");
            System.exit(-1);
        }
        System.out.println("Connected!");
        String ap = Networking.getGatewayIp();
        System.out.println("Found access point [" + ap + "]");
        String connectedNetwork = Networking.findConnectedNetwork(ifName_managed);
        //System.err.println("Connection name: " + Networking.findConnectedNetwork(ifName_managed));
        //System.err.println("spoofing...");
        //String ap_mac = Networking.getMAC(ap);
        //if (ap_mac.equals("")) {
        //    System.out.println("Failed to find MAC!\nAborting");
        //    System.exit(-1);
        //}
        //System.err.println("Found MAC [" + ap_mac + "]");

        return ap;
    }

    public static boolean connectWIFI(String essid, String ifName) {

        if (essid.equals(Networking.findConnectedNetwork(ifName))) return true;

        BufferedReader consoleOutput = Terminal.exec(String.format("nmcli dev wifi connect %s ifname %s", essid, ifName));
        try {
            String response = consoleOutput.readLine();
            if (response == null || response.startsWith("Error")) {
                System.out.println(response);
                return false;
            }
            consoleOutput.close();
        } catch (IOException e) {
            System.err.println("err __ " + e.getStackTrace());
            return false;
        }
        return true;
    }

    public static String getMAC(String ip) {
        try {
            BufferedReader out = Terminal.exec("sudo arp-scan " + ip);
            String line = out.readLine();
            while (line != null) {
                String[] words = line.split("\t");
                line = out.readLine();
                if (!words[0].equals(ip)) continue;
                int i = 0;
                for (String w : words) {
                    if (!w.equals(" ") && !w.equals("")) i++;
                    if (i == 2) return w;
                }
            }
        } catch (IOException e) {
            System.err.println("err __ " + e.getStackTrace());
        }
        return "";
    }

    public static String findConnectedNetwork(String ifName) {
        BufferedReader connections = Terminal.exec(String.format("nmcli d"));
        String connectionName = "";
        try {
            String line = connections.readLine();
            while (line != null) {
                String[] words = line.split(" ");
                line = connections.readLine();
                if (!words[0].equals(ifName)) continue;
                int i = 0;
                for (String s : words) {
                    if (s.equals("")) continue;
                    if (i++ == 3) {
                        connectionName += s;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("err __ " + e.getStackTrace());
        }
        if (connectionName.equals("")) {
            return "";
        }
        return connectionName;
    }

    public static String getGatewayIp() {
        BufferedReader output = Terminal.exec("ip r");
        try {
            String line = output.readLine();
            String[] words = line.split(" ");
            return words[2];
        } catch (Exception e) {
            System.err.println("err __ " + e.getStackTrace());
            return "";
        }

    }


    private static final String[] keywords ={"drone", "9300"};

    public static Set<String> getWifiAP() throws IOException {

        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("nmcli dev wifi");

        Set<String> result = new HashSet<String>(20);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
        stdInput.readLine();


        String s = null;
        while ((s = stdInput.readLine()) != null) {
            String[] arr = s.replace("*" , "").trim().replaceAll(" +", " ").split(" ");

            //This means no SEC
            if(arr.length == 8 && Stream.of(keywords).map(st -> arr[0].contains(st)).parallel().anyMatch(b-> b)){
                result.add(arr[0]);
            }

        }


        return result;
    }

    public static Thread startWIFIMonitor(String ifname){
        Thread x = new Thread(
                () -> {
                    while(true)
                        Terminal.exec(String.format("sudo iwlist %s scan", ifname));
                }
        );
        x.start();
        return x;
    }
    public static boolean stopWIFIMonitor(Thread x, String ifname){
        if(!x.getName().equals(ifname)) return false;
        x.interrupt();
        return true;
    }

}
