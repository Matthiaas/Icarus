import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class WIFIMonitor {

    static Thread t = null;
    static boolean running = false;
    static Set<WIFIHandler> handlers = new HashSet<>();


    public static void register(WIFIHandler w){
        handlers.add(w);
    }

    static final String[] KEYWORDS = {"drone", "9300"};


    public static void start(String ifname) {
        running = true;
        t = new Thread(() -> {
            while (running) {
                Terminal.exec(String.format("sudo iwlist %s scan", ifname));
                try {
                    Set<String> info = getWifiAP();
                    for (WIFIHandler h : handlers) h.handle(info);
                    Thread.sleep(500);
                } catch (IOException | InterruptedException e) {
                }
            }
        });
        t.start();
    }

    public static void stop() {
        running = false;
        try {
            t.join();
        } catch (InterruptedException e) {
        }
    }


    public static Set<String> getWifiAP() throws IOException {

        Set<String> result = new HashSet<String>(20);
        BufferedReader stdInput = Terminal.exec("nmcli dev wifi");

        stdInput.readLine();

        String s = null;
        while ((s = stdInput.readLine()) != null) {
            String[] arr = s.replace("*", "").trim().replaceAll(" +", " ").split(" ");

            //This means no SEC
            if (arr.length == 8 && Stream.of(KEYWORDS).map(st -> arr[0].contains(st)).parallel().anyMatch(b -> b)) {
                result.add(arr[0]);
            }

        }
        return result;

    }


}
