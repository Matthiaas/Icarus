import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {


    public static void main(String[] aghs) throws InterruptedException, UnknownHostException {

        Thread t = new SendThread(InetAddress.getByName("172.16.10.1"));
        t.start();
        Thread.sleep(10000);

    }
}
