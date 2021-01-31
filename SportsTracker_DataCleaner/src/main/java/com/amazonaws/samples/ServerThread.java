package com.amazonaws.samples;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.amazonaws.samples.CleanSportsTrackerData.bucketName;

public class ServerThread implements Runnable {


    private Socket clientSocket;
    private static int trackerID = 0;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }


    public void run() {


        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String tracker = getUniqueTrackerId();
            out.print(tracker);


            String locations = in.readLine();

            JSONObject json = new JSONObject(locations);
            File file = createFile(json, tracker);

            uploadFile(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUniqueTrackerId() {
        trackerID += 1;
        return String.valueOf(trackerID);
    }

    static final int kcal = 100;


    private static File createFile(JSONObject result, String trackerID) throws IOException {

        ArrayList<String> timeKeys = new ArrayList<>();
        Iterator<?> keys = result.keys();
        while (keys.hasNext()) {
            timeKeys.add((String) keys.next());
        }

        Collections.sort(timeKeys);

        JSONArray longAndLatitude = new JSONArray();

        for (String s : timeKeys) {
            longAndLatitude.put(result.get(s));
        }

        JSONObject resultFin = new JSONObject();
        try {
            Field changeMap = resultFin.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);
            changeMap.set(resultFin, new LinkedHashMap<>());
            changeMap.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {

        }


        for (int i = 0; i < longAndLatitude.length(); i++) {
            for (int j = 1; j < longAndLatitude.length(); j++) {
                if (longAndLatitude.get(i).toString().equals(longAndLatitude.get(j).toString()) && i != j) {
                    longAndLatitude.remove(j);
                    timeKeys.remove(j);
                    j--;
                }
            }
        }


        resultFin.put("locations", longAndLatitude);


        long runningTimeStart = Long.parseLong(timeKeys.get(0));
        LocalDateTime test = LocalDateTime.ofInstant(Instant.ofEpochMilli(runningTimeStart), TimeZone.getDefault().toZoneId());

        long runningTimeEnd = Long.parseLong(timeKeys.get(timeKeys.size() - 1));
        LocalDateTime test2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(runningTimeEnd), TimeZone.getDefault().toZoneId());

        long timeRun = ChronoUnit.SECONDS.between(test, test2);
        Duration duration = Duration.ofSeconds(timeRun);
        resultFin.put("runningTime", duration);


        String firstLong;
        String firstLat;
        String lastLong;
        String lastLat;
        double distance = 0d;

        for (int i = 0; i < longAndLatitude.length() - 1; i++) {
            firstLong = longAndLatitude.get(i).toString().substring(longAndLatitude.get(i).toString().indexOf(":") + 1, longAndLatitude.get(i).toString().indexOf("}"));
            firstLat = longAndLatitude.get(i).toString().substring(longAndLatitude.get(i).toString().lastIndexOf(":") + 1, longAndLatitude.get(i).toString().lastIndexOf("}"));
            lastLong = longAndLatitude.get(i + 1).toString().substring(longAndLatitude.get(i + 1).toString().indexOf(":") + 1, longAndLatitude.get(i + 1).toString().indexOf("}"));
            lastLat = longAndLatitude.get(i + 1).toString().substring(longAndLatitude.get(i + 1).toString().lastIndexOf(":") + 1, longAndLatitude.get(i + 1).toString().lastIndexOf("}"));
            distance += distanceInKmBetweenEarthCoordinates(Double.parseDouble(firstLat), Double.parseDouble(firstLong), Double.parseDouble(lastLat), Double.parseDouble(lastLong));
        }

        resultFin.put("distance", distance);

        double burnedKcal = distance * kcal;
        resultFin.put("kcal", burnedKcal);



        File resultFin1 = new File(trackerID + ".txt");
        FileWriter myWriter = new FileWriter(trackerID + ".txt");
        myWriter.write(resultFin.toString());
        myWriter.close();
        return resultFin1;
    }


    private static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    private static double distanceInKmBetweenEarthCoordinates(double lat1, double lon1, double lat2, double lon2) {
        int earthRadiusKm = 6371;

        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lon2 - lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }

    private void uploadFile(File file) {
        /***************** Upload cleaned data to bucket ****************/
        // https://docs.aws.amazon.com/AmazonS3/latest/dev/llJavaUploadFile.html

        String key = file.getName();
        CleanSportsTrackerData.s3.putObject(bucketName, key, file);
    }

}
