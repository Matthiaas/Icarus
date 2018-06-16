public class Main {



    public static void main(String[] aghs) throws InterruptedException {


        PreviewView.s = new UDPServer();
        PreviewView.s.Start();


        Thread t = new SendThread();
        t.start();



        Thread.sleep(10000);




    }
}
