package com.example.hackathon;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;


import java.net.HttpURLConnection;

public class DirectionGetter {
    public static String makeURL (LatLng src, LatLng dest){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");
        urlString.append(Double.toString(src.latitude));
        urlString.append(",");
        urlString
                .append(Double.toString(src.longitude));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( dest.latitude));
        urlString.append(",");
        urlString.append(Double.toString( dest.longitude));
        urlString.append("&sensor=false&mode=walking&alternatives=false&key=AIzaSyAqWAa0kaqyrvMiJ2TAsPmM9xzn61n5G1I");
        return urlString.toString();
    }
//
//    public static JSONObject httpRequest(LatLng src, LatLng dest){
//        String url = makeURL(src, dest);
//        HttpClient  client = (HttpURLConnection) url.openConnection();
//    }
}
