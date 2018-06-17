import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Terminal {

    public static BufferedReader exec(String command){
        try{
            Process p = Runtime.getRuntime().exec(command);
            return new BufferedReader(new InputStreamReader(p.getInputStream()));
        }catch(IOException iox){
            System.err.println(iox.getStackTrace());
            return null;
        }

    }

}
