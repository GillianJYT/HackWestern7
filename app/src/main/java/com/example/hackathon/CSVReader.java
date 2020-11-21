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

                String[] data = line.split(cvsSplitBy);
                LatLng dataLatLng = new LatLng(Double.parseDouble(data[20]), Double.parseDouble(data[19]));

                latLng.add(dataLatLng);
            }

            return latLng;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}