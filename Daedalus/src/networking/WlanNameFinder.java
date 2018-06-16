package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class WlanNameFinder {


    private static final String[] keywords ={"drone", "9300"};

    public static Set<String> getNames() throws IOException {

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
            if(arr.length == 8 && Stream.of(keywords).map( st -> arr[0].contains(st)).parallel().anyMatch(b-> b)){
                result.add(arr[0]);
            }

        }


        return result;
    }
}
