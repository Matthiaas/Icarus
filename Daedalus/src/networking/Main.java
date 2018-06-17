package networking;

import GUI.GUIMain;
import networking.Procedures;

public class Main {
    static String ifName_managed = "wlp1s0";
    static String ifName_monitor = "mon0";

    public static void main(String[] args) {
        if (args.length == 2) {
            ifName_managed = args[0];
            ifName_monitor = args[1];
        }

        System.out.println("Using [" + ifName_managed + "] as managed-interface");
        System.out.println("Using [" + ifName_monitor + "] as monitor-interface");

        WIFIMonitor.start(ifName_managed);

        GUIMain dialog = new GUIMain();
        dialog.pack();
        dialog.setVisible(true);
        //System.exit(0);

        // start gui, register for wifi monitor

        //networking.Procedures.land("9300-0006C7");
        //System.out.println(Procedures.info("9300-0006C7"));
    }
}