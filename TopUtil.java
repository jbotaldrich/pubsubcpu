import java.util.*;
import java.io.*;
import java.util.regex.*;

public class TopUtil {

   public static final String statPath = "/proc/stat";
   public static final String meminfoPath = "/proc/meminfo";

   public static void main(String[] args) {
       //System.out.println("Hello, World");
       //CPUInfo();
       //MemoryInfo();
        try{
            System.out.println(MemoryPercentUsed() + "%");
            System.out.println(GetHostName());
            System.out.println(CPUPercentUsed() + "%");
        }catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public static void CPUInfo() {
   //  use /proc/stat to get the necessary info with scanner
        BufferedReader br = null;
        try{
            br = getStream(statPath);
            String line = "";
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }catch(IOException ex) {
            System.out.println("Something went wrong");
        }



    }

    public static float CPUPercentUsed() {
        BufferedReader br = null;
        int busy = 0;
        int cpusum = 0;
        try{
            br = getStream(statPath);
            String line = "";
            while((line = br.readLine()).startsWith("cpu")) {
                String[] parts = line.split("\\s+");
                //Get all the times for sum save idle separately
                for(int i = 1; i <= 4; i++) {
                //    System.out.println(parts[i]);
                    int temp = Integer.parseInt(parts[i]);
                    if(i < 4){
                        busy += temp;
                    }

                    cpusum += temp;
                }
            }
        return 100 *(float)busy / (float)cpusum;
        }catch(IOException ex) {
            ex.printStackTrace();
        }
        return 0.0f;
    }

    public static float MemoryPercentUsed() {
        BufferedReader br = null;
        try{
            br = getStream(meminfoPath);
            String line = "";
            int possible = 0;
            int current = 0;
            line = br.readLine();
            possible = getMemNumber(line);
            line = br.readLine();
            current = getMemNumber(line);
            return 100 * ((float)possible - (float)current)/ (float)possible;
        }catch(IOException ex){
            System.out.println("Read it wrong?");
        }
        return 0.0f;
    }
        

    public static String GetHostName() {
        String line = "";
        try{
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("hostname");
            BufferedReader in = new BufferedReader( new InputStreamReader( proc.getInputStream() ));
            line = in.readLine();
        }catch (IOException e){
            e.printStackTrace();
        }
        return line;
       // }catch(IOException ioe){
       //     ioe.printStackTrace();
       // }
       // return "no worky";
    }

    public static void MemoryInfo() {
    // Zuse /proc/meminfo to get the memory usage using scnaner
        BufferedReader br = null;
        try{
            br = getStream(meminfoPath);
            String line = "";
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }catch(IOException ex) {
            System.out.println("Something went wrong");
        }
    }
    private static BufferedReader getStream(String inpath)
    {
        BufferedReader br = null;
        File file = new File(inpath);
        FileReader fileReader = null;
        try{
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);
        }catch(FileNotFoundException ex) {
            System.out.println("File " + inpath +" not found.");
        }
        return br;
    }

    public static int getMemNumber(String str) {
        Pattern pattern = Pattern.compile("^\\w+.:\\s+(\\d+)\\s+\\w+$");
        Matcher matcher = pattern.matcher(str);
        int matchedNumber = 0;
        while(matcher.find()) {
            matchedNumber = Integer.parseInt(matcher.group(1));
        }
        return matchedNumber;
    }
        
}


