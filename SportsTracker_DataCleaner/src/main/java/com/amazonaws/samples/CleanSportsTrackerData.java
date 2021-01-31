package com.amazonaws.samples;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.*;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.KeyPair;

public class CleanSportsTrackerData {

	static KeyPair keyPair; 
	static AmazonEC2 ec2;
	static final String IMAGE_ID = "ami-0947d2ba12ee1ff75"; //replace 
	private static File uploadFile;
	
	public CleanSportsTrackerData() throws IOException {
		this.uploadFile = createSampleFile();
	}
	


	private static double degreesToRadians(double degrees) {
		return degrees * Math.PI / 180;
	}

	private static double distanceInKmBetweenEarthCoordinates(double lat1,double lon1,double lat2,double lon2) {
		int earthRadiusKm = 6371;

		double dLat = degreesToRadians(lat2-lat1);
		double dLon = degreesToRadians(lon2-lon1);

		lat1 = degreesToRadians(lat1);
		lat2 = degreesToRadians(lat2);

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return earthRadiusKm * c;
	}
	
	//TODO: replace this method with yours
	private static File createSampleFile() throws IOException {
		JSONObject result = new JSONObject();

		for (int i = 0; i < 20; i++) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			JSONArray time = new JSONArray();
			JSONObject longi = new JSONObject();
			JSONObject lati = new JSONObject();
			if(i%2 == 0){
				longi.put("longitude", 1);
				lati.put("latitude", 1);
			} else {
				longi.put("longitude", i);
				lati.put("latitude", i);
			}

			time.put(longi);
			time.put(lati);
			result.put(String.valueOf(timestamp.getTime()), time);
			System.out.println(result.toString());
		}
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

		JSONObject resultFin =  new JSONObject();
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

		for (int i = 0; i < longAndLatitude.length(); i++) {
			resultFin.put(timeKeys.get(i), longAndLatitude.get(i));
		}

		double runningTimeStart = Double.parseDouble(timeKeys.get(0));
		double runningTimeEnd = Double.parseDouble(timeKeys.get(timeKeys.size()-1));

		System.out.println(runningTimeEnd - runningTimeStart);

		String firstLong = longAndLatitude.get(0).toString().substring(longAndLatitude.get(0).toString().indexOf(":")+1, longAndLatitude.get(0).toString().indexOf("}"));
		String firstLat = longAndLatitude.get(0).toString().substring(longAndLatitude.get(0).toString().lastIndexOf(":")+1, longAndLatitude.get(0).toString().lastIndexOf("}"));
		String lastLong = longAndLatitude.get(longAndLatitude.length()-1).toString().substring(longAndLatitude.get(longAndLatitude.length()-1).toString().indexOf(":")+1, longAndLatitude.get(longAndLatitude.length()-1).toString().indexOf("}"));
		String lastLat = longAndLatitude.get(longAndLatitude.length()-1).toString().substring(longAndLatitude.get(longAndLatitude.length()-1).toString().lastIndexOf(":")+1, longAndLatitude.get(longAndLatitude.length()-1).toString().lastIndexOf("}"));

		Double distance = distanceInKmBetweenEarthCoordinates(Double.parseDouble(firstLat),Double.parseDouble(firstLong), Double.parseDouble(lastLat), Double.parseDouble(lastLong));
		resultFin.put("distance", distance);

		File resultFin1 = new File("TrackerGPS.txt");
		FileWriter myWriter = new FileWriter("TrackerGPS.txt");
		myWriter.write(resultFin.toString());
		myWriter.close();
		return resultFin1;
    }



	public static File getUploadFile() {
		return uploadFile;
	}

	public static AmazonEC2 getEC2Instance() {
		return ec2;
	}
	
	
}
