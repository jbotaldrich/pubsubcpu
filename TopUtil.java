import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.text.SimpleDateFormat;

public class TopUtil {

   public static final String statPath = "/proc/stat";
   public static final String meminfoPath = "/proc/meminfo";

/*   public static void main(String[] args) {
       //System.out.println("Hello, World");
       //CPUInfo();
       //MemoryInfo();
        try{
             System.out.println(MemoryPercentUsed() + "%");
            System.out.println(GetHostName());
            System.out.println(CPUPercentUsed() + "%");
            System.out.println(ProcessCount());
            System.out.println(GetCurrentTime());
       }catch(Exception e)
        {
            System.out.println(e);
        }
    }
*/
    public static String GetCurrentTime() {
        long epoch = System.currentTimeMillis();
        Date time = new Date((long)epoch);
        SimpleDateFormat dateform = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        dateform.setTimeZone(TimeZone.getDefault());
        String formattedDate = dateform.format(time);
        return formattedDate;
        
    }
    //Makes a call to ls to get the directory structure for subdirictories of /proc
    //uses a regex to count the running processes by looking for pid numbers
    public static int ProcessCount() {
        String[] getpidstr =  new String[] {"bash", "-c", "ls /proc/*/ -d"};
        int pidcount = 0;
        Pattern pattern = Pattern.compile("/proc/(\\d+)/");
        try{
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec(getpidstr);
            BufferedReader in = new BufferedReader( new InputStreamReader( proc.getInputStream() ));
            while(pattern.matcher(in.readLine()).find()) {
                pidcount++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return pidcount;

    }

    //Collectes cpu info accorss all cpus using /proc/stat returns as a percentage of used capactiy
    public static float CPUPercentUsed() {
        BufferedReader br = null;
        int busy = 0;
        int cpusum = 0;
        try{
            br = getStream(statPath);
            String line = "";
            //only use those lines starting with cpu can quit when they run out
            while((line = br.readLine()).startsWith("cpu")) {
                String[] parts = line.split("\\s+");
                //Get all the times for sum save the active usage separately
                for(int i = 1; i <= 4; i++) {
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

    //Collects information on memory usage from /proc/mieminfo and returns as percentage
    //used.
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
        

    //Gets the hostname and returns it as a string
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

    //Gets a stream of information from the pseudofile system /proc
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
    
    //Specific to get memory info from /proc/meminfo
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


