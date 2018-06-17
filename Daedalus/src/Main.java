public class Main {
    static String ifName_managed = "wlan0";
    static String ifName_monitor = "mon0";

    public static void main(String[] args) {
        if (args.length == 2) {
            ifName_managed = args[0];
            ifName_monitor = args[1];
        }

        System.out.println("Using [" + ifName_managed + "] as managed-interface");
        System.out.println("Using [" + ifName_monitor + "] as monitor-interface");

        //WIFIMonitor.start(ifName_managed);


        // start gui, register for wifi monitor

        //Procedures.land("9300-0006C7");
        System.out.println(Procedures.info("9300-0006C7"));
    }
}