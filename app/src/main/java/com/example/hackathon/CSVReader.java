package com.example.hackathon;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVReader {

    public ArrayList<LatLng> latLngExtract(String filePath) {

        String csvFile = filePath;
        String line = "";
        String cvsSplitBy = ",";
        int latIndex = 0;
        int lngIndex = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            ArrayList<LatLng> latLng = new ArrayList();

            line = br.readLine();
            String[] data = line.split(cvsSplitBy);

            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    latIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    lngIndex = i;
                }
            }

            while ((line = br.readLine()) != null) {

                try {
                    data = line.split(cvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[latIndex]), Double.parseDouble(data[lngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {

                }
            }

            return latLng;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public double calculateDistanceByLatLng(LatLng location1, LatLng location2) {
        double RADIUS_EARTH = 6371000.0;
        double latRadA = Math.toRadians(location1.latitude);
        double latRadB = Math.toRadians(location2.latitude);
        double deltaLatRad = Math.toRadians(location2.latitude - location1.latitude);
        double deltaLngRad = Math.toRadians(location2.longitude - location1.longitude);

        double a = Math.pow(Math.sin(deltaLatRad/2), 2) + Math.cos(latRadA) * Math.cos(latRadB) * Math.pow(Math.sin(deltaLngRad/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = RADIUS_EARTH * c;

        return d;
    }

    public LatLng findNearestItem(LatLng currentLocation, ArrayList<LatLng> listOfLocations) {
        LatLng nearestLocation = new LatLng(listOfLocations.get(0).latitude, listOfLocations.get(0).longitude);
        double nearestDistance = calculateDistanceByLatLng(currentLocation, nearestLocation);

        for (int i = 1; i < listOfLocations.size(); i++) {
            if (calculateDistanceByLatLng(currentLocation, listOfLocations.get(i)) < nearestDistance) {
                nearestLocation = listOfLocations.get(i);
                nearestDistance = calculateDistanceByLatLng(currentLocation, listOfLocations.get(i));
            }
        }

        return nearestLocation;
    }
}