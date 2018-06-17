package networking;

import java.io.BufferedReader;
import java.io.IOException;



public class Networking {

    public static boolean connectWIFI(String essid, String ifName){

        if(essid.equals(Networking.findConnectedNetwork(ifName))) return true;

        BufferedReader consoleOutput = networking.Terminal.exec(String.format("nmcli dev wifi connect %s ifname %s", essid, ifName));
        try {
            consoleOutput.readLine();
            System.err.println(consoleOutput.readLine());
            consoleOutput.close();
        } catch (IOException e) {
            System.err.println("err __ " + e.getStackTrace());
            return false;
        }
        return true;
    }

    public static String getMAC(String ip) {
        try {
            BufferedReader out = networking.Terminal.exec("sudo arp-scan " + ip);
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
        }catch (IOException e){
            System.err.println("err __ " + e.getStackTrace());
        }
        return "";
    }

    public static String findConnectedNetwork(String ifName){
        BufferedReader connections = networking.Terminal.exec(String.format("nmcli d"));
        String connectionName = "";
        try {
            String line = connections.readLine();
            while(line != null){
                String[] words = line.split(" ");
                line = connections.readLine();
                if(!words[0].equals(ifName)) continue;
                int i = 0;
                for(String s : words){
                    if(s.equals("")) continue;
                    if(i++ == 3){
                        connectionName += s;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("err __ " + e.getStackTrace());
        }
        if(connectionName.equals("")){
            System.err.println("no connection found");
            return "";
        }
        return connectionName;
    }

    public static String getGatewayIp() {
        BufferedReader output = networking.Terminal.exec("ip r");
        try {
            String line = output.readLine();
            String[] words = line.split(" ");
            return words[2];
        } catch (Exception e) {
            System.err.println("err __ " + e.getStackTrace());
            return "";
        }

    }


}
