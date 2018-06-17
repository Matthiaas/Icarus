import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public class Main {
    static String ifName_managed = "wlp1s0";
    static String ifName_monitor = "mon0";

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 2) {
            ifName_managed = args[0];
            ifName_monitor = args[1];
        }

        ApiContextInitializer.init();
        TeleBot bot = new TeleBot();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }



        System.out.println("Using [" + ifName_managed + "] as managed-interface");
        System.out.println("Using [" + ifName_monitor + "] as monitor-interface");

        WIFIMonitor.start(ifName_managed);



        Scanner s = new Scanner(System.in);


        while(true){
            Set<String> strings;
            do{
                 strings = WIFIMonitor.getWifiAP();
                 Thread.sleep(1000);
            }while(strings.isEmpty());


            o("Drones found:");
            for(String d : strings){
                o("\t" + d);
            }

            bot.sendAll("Some Drones where found:");
            for(String d : strings){
                bot.sendAll( d);
            }

            o("");
            System.out.print("$");

            String in[] = s.nextLine().toLowerCase().split("");
            while(true)
                if( in.length < 2 || in[0].equals("help") ){
                    o("3 optinos:");
                    o("\t kill name: for killing the drone!");
                    o("\t land name: for landing the drone!");
                    o("\t balloon name: let it go!");
                    o("");

                    in = s.nextLine().toLowerCase().split("");
                }else{
                    break;
                }


            switch(in[0]){
                case "kill":
                    Procedures.kill(in[1]);
                    break;
                case "balloon":
                    Procedures.balloon(in[1]);
                    break;

                case "land":
                    Procedures.land(in[1]);
                    break;
            }






        }





        //GUIMain dialog = new GUIMain();
        //dialog.pack();
        //dialog.setVisible(true);
        //System.exit(0);

        // start gui, register for wifi monitor

        //Procedures.land("9300-0006C7");
        //System.out.println(Procedures.info("9300-0006C7"));
    }


    private static void o(String s){
        System.out.println(s);
    }
}