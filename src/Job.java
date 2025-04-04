import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;

public class Job {

    private LocalTime startTime;
    private String description;
    private String pID;

    public static void main(String[] args) {
        String csvFile = "/Users/macbookpro/Downloads/LogMonitoringApp/data/logs.log";
        // HashMap to store Job objects
        HashMap<String, Job> jobMap = new HashMap<>();
        // Using try-with-resources to automatically close the BufferedReader at the end of block
        try(BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            System.out.println("----- Log report -----");
            // Split the line and add each part to an array
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(",");

                LocalTime time = LocalTime.parse(splitLine[0]);
                String description = splitLine[1];
                String startOrEnd = splitLine[2].trim();
                String pID = splitLine[3];
                String message = "OK";
                // if it's the first time encountering the job, add it to the map
                if (splitLine[2].trim().equals("START")) {
                    Job job = new Job();
                    job.startTime = time;
                    job.description = description;
                    job.pID = pID;
                    jobMap.put(job.pID, job);
                    // if this is true, the job is already in the map, and we can compare the start and end time
                } else if (startOrEnd.equals("END")) {

                    Job finalJob = jobMap.get(pID);
                    Duration duration = Duration.between(finalJob.startTime, time);

                    if(duration.isNegative()){
                        message = "Duration is negative - something is wrong";
                    } else if(duration.toMinutes() >= 5 && duration.toMinutes() < 10) {
                        message = "WARNING";
                    } else if (duration.toMinutes() >= 10) {
                        message = "ERROR";
                    }
                    System.out.println("ID: " + finalJob.pID + " -> " + message);
                    System.out.println("Start: " + finalJob.startTime + " | End: " + time);
                    System.out.println("Description: " + finalJob.description);
                    System.out.println(
                            "Duration: HH:" + duration.toHoursPart() +
                            " MM:" + duration.toMinutesPart() +
                            " SS:" + duration.toSecondsPart()
                    );
                    System.out.println("--------------------------------");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}