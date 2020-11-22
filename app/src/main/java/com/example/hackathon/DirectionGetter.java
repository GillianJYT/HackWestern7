package com.example.hackathon;
import android.content.Context;
import android.telecom.Call;

import com.google.android.gms.maps.model.LatLng;


import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DirectionGetter {
    public static String makeURL(LatLng src, LatLng dest) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");
        urlString.append(Double.toString(src.latitude));
        urlString.append(",");
        urlString
                .append(Double.toString(src.longitude));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(dest.latitude));
        urlString.append(",");
        urlString.append(Double.toString(dest.longitude));
        urlString.append("&sensor=false&mode=walking&alternatives=false&key=AIzaSyAqWAa0kaqyrvMiJ2TAsPmM9xzn61n5G1I");
        return urlString.toString();
    }


    public static String httpRequest(LatLng src, LatLng dest){
        String url_str = makeURL(src, dest);
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(url_str);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                res.append(line);
            }
            rd.close();
            System.out.println(res.toString());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return res.toString();
    }

}