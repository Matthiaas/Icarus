import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Test{

    final static String workWith = "";


    public static void main(String []ahgs) throws IOException {

        File file = new File("out.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String s  = new String(Files.readAllBytes(Paths.get("in.txt"))) ;
            boolean f = false;
            for(String ss : s.split(" ")){
                if(s.length() == 2){

                    if(f){
                        writer.write(",");
                    }else{
                        f = true;
                    }
                    writer.write("0x");
                    writer.write(s);
                }


            }
        }


        DatagramSocket u = new DatagramSocket();


        InetAddress address = InetAddress.getByName("127.0.0.1");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 6969);
        //DataGramSocket u = new DataGramSocket();


        // InetAddress address = InetAddress.getByName("127.0.0.1");
        //byte[] buffer;
        // DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 6969);


    }
}