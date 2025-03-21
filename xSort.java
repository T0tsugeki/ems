/*
 * Name:Simon Peun, ID: 1620939
 * code for heapsort: https://www.geeksforgeeks.org/java-program-for-heap-sort/
 */
import java.io.*;
import java.util.*;

public class xSort {

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Inappropriate num of tapes specified, choose either 1 or 2");
            
        }
        int kway = (args.length == 2) ? Integer.parseInt(args[1]) : 1; //if the second arg is missing then default the value to 1
        if (kway < 1 || kway > 2) {
            System.err.println("Inappropriate num of tapes specified, choose either 1 or 2");
        }
        int runSize = Integer.parseInt(args[0]); //run size arg
        if (runSize < 64 || runSize > 1024) {
            System.out.println("Each run must be between 64 - 1024 bytes. Terminating process.");
            return;
        }
        List<File> sortR = new ArrayList<>(); //list of all the individual runs that have been sorted
        List<String> cloud = new ArrayList<>(runSize); //stores data in the "cloud" until needed

        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); //note to self System.in refers to the pipe "<"
        try {
            int runNumber = 1;
            while ((line = reader.readLine()) != null) { //reads from input stream
                cloud.add(line); //store the line that was just read from input stream into the "cloud"
                
                //When the cloud has hit capacity sorts the line and writes them to a file (and iterates to next run file)
                if (cloud.size() >= runSize) {
                    sortR.add(outputSortR(cloud, runNumber++));
                    
                    cloud.clear();
                }
                }
                //handles any lines that were leftover (eg. lines that were at the end of the file but not a multiple of runsize)
                if (!cloud.isEmpty()) {
                    sortR.add(outputSortR(cloud, runNumber++));
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        if (kway == 1) {//if nothing was put (or 1) was put as second argument just print runs to a folder, idk if I have to do this bum ass friend said you should
            try {
                //same as outputSortR method but doesn't sort and iterates through files instead of strings
                for (File run : sortR) {
                    BufferedReader br = new BufferedReader(new FileReader(run));
                    String something = br.readLine(); //think of something better than something lol
                    while (something != null) {
                        System.out.println(something);
                        something = br.readLine();
                    }
                    br.close();
                }
                return;
            } catch (IOException e) {
                System.out.println(e);
            }
            
        }
        else if (kway == 2) {
            try {
            Queue<File> queue = new LinkedList<>(sortR);
            while (queue.size() > 1) { //runs until one file left which would be the final sorted file
                int batchSize = Math.min(queue.size(), 2); //ensures 2 files are merged at a time
                List<File> subList = new ArrayList<>();

                for (int i = 0; i < batchSize; i++) { //2 files are removed via poll and stored in sublist
                    subList.add(queue.poll());
                }

                File mergedFile = mergeFiles(subList);//read and sorts both the file and writes them to mergedFile
                queue.offer(mergedFile);//put back into queue to continue merging
            }
            File finalSortedFile = queue.poll();//this will be the final merged file
            try {
                String brline;
                BufferedReader br = new BufferedReader(new FileReader(finalSortedFile));
                while ((brline = br.readLine()) != null) {
                    System.out.println(brline);
                }
                br.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        }
        
        

            
    }
    private static File outputSortR(List<String> cloud, int runNumber) throws IOException {
        File runFile = new File("sortedRun" + runNumber + ".txt");


        heapsort(cloud);//sorts the stuff stored in the cloud
        try {
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(runFile)); //writer for the temp file
            for (String line : cloud) {
                writer.write(line); //writes current line to file
                writer.newLine();//puts each line on a new line
            } 
            writer.close();
        }catch (IOException e) {
            System.out.println(e);
        }
        
        
        return runFile;
    }
    /*
     * converts list to max heap by bringing up the largest element using heapify, then swap element with last one then heapify
     */
        private static void heapsort(List<String> arr) {
            int n = arr.size();
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(arr, n, i);
            }
            for (int i = n - 1; i > 0; i--) {
                String temp = arr.get(0);
                arr.set(0, arr.get(i));
                arr.set(i, temp);
                heapify(arr, i, 0);
            }
        }
    
        /*
         * assumes root is largest and sets child indicies, checks if left child exists and is bigger than root,
         * does same for right child, then swaps and continue heapifying if the root is not the largest
         */
        private static void heapify(List<String> arr, int n, int i) {
            int largest = i;
            int l = 2 * i + 1;
            int r = 2 * i + 2;
            if (l < n && arr.get(l).compareTo(arr.get(largest)) > 0)
                largest = l;
            if (r < n && arr.get(r).compareTo(arr.get(largest)) > 0)
                largest = r;

            if (largest != i) {
                String temp = arr.get(i);
                arr.set(i, arr.get(largest));
                arr.set(largest, temp);
                heapify(arr, n, largest);
            }
        }
        private static File mergeFiles(List<File> Files) throws IOException { //use to mergefiles
        
            // Collect all lines from all files into one list
            List<String> allLines = new ArrayList<>();
            for (File file : Files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        allLines.add(line);
                    }
                }
            }
        
            // Sort all the lines
            Collections.sort(allLines);
        
            // Write the sorted lines to a temporary file
            File tempFile = File.createTempFile("mergedRun", ".txt");
            tempFile.deleteOnExit();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                for (String line : allLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        
            return tempFile;
        }
        
}
