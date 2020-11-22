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

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            ArrayList<LatLng> latLng = new ArrayList();

            while ((line = br.readLine()) != null) {

                try {
                    String[] data = line.split(cvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[19]), Double.parseDouble(data[18]));

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

}