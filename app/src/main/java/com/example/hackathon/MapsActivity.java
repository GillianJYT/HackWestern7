package com.example.hackathon;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


//public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
//}

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MapsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // if the menu buttons are clicked, check which one was clicked
        if (id == R.id.action_settings) {
            settings_onClick();
            return true;
        } else if (id == R.id.about) {
            about_onClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // action for settings button
    private void settings_onClick() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    // action for about button
    private void about_onClick() {
        Intent intent = new Intent(getApplicationContext(), About.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng curPos = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(curPos);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        ArrayList<LatLng> benchList = null;
        CSVReader csvReader = new CSVReader();
        InputStream benchIS = getResources().openRawResource(R.raw.benchdata);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(benchIS, Charset.forName("UTF-8"))
        );
        String line = "";
        String cvsSplitBy = ",";
        int latIndex = 0;
        int lngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            line = reader.readLine();
            String[] data = line.split(cvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    latIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    lngIndex = i;
                }
            }
            while ((line = reader.readLine()) != null) {
                try {
                    data = line.split(cvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[latIndex]), Double.parseDouble(data[lngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            benchList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestBench = csvReader.findNearestItem(curPos, benchList);

        mMap.addMarker(new MarkerOptions().position(nearestBench).title("Nearest Bench"));
        MarkerOptions destinationMarker = new MarkerOptions();
        destinationMarker.position(nearestBench);
        destinationMarker.title("Nearest Bench");
        destinationMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

//        CSVReader reader = new CSVReader();
//        String benchFile = "Street_furniture_Bench_data.csv";
//        File file = this.getFileStreamPath(benchFile);
//        if(file == null || !file.exists()) {
//            System.out.println("FILE DONT EXIST");
//        }
//        System.out.println("FILE EXIST");
//
//        Context context = getApplicationContext();
//        CharSequence text = Double.toString(reader.latLngExtract(benchFile).get(0).latitude);
//        int duration = Toast.LENGTH_SHORT;
//
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();

        String url = DirectionGetter.makeURL(curPos, nearestBench);

        final String[] display = {"nothing"};

        RequestQueue requestQueue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);
        // Start the queue
        requestQueue.start();

        // Instantiate the RequestQueue.
        boolean[] pathFound = {false};
        int iterCount = 0;
        //while(!pathFound[0] && iterCount++ <1){
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String temp = "Response: " + response.toString();
//                        Context context = getApplicationContext();
//                        CharSequence text = temp;
//                        int duration = Toast.LENGTH_LONG;
//
//                        Toast toast = Toast.makeText(context, text, duration);
//                        toast.show();

                            String prePoly = "nothing";
                            String pKey = "\"overview_polyline\":{\"points\":\"";
                            try{
                                //prePoly = response.getJSONArray("routes").getJSONArray(2).getJSONArray(6).getJSONObject(4).getString("points");
                                int pPlace = response.toString().lastIndexOf(pKey);
                                prePoly = response.toString().substring(pPlace + pKey.length());
                                prePoly = prePoly.substring(0, prePoly.indexOf("\""));


                                System.out.println();
                                System.out.println();
                                System.out.println(prePoly);
                                System.out.println();
                                System.out.println();

                            }
                            catch   (Exception e){
                                System.out.println("JSON ERROR");
                            }
                            if (prePoly!=null){
//                            Context context2 = getApplicationContext();
//                            CharSequence text2 = prePoly;
//                            int duration2 = Toast.LENGTH_LONG;
//
//                            Toast toast2 = Toast.makeText(context2, text2, duration2);
//                            toast2.show();
                                //List<LatLng> path = decodePoly(prePoly);
                                List<LatLng> path = decodePath(response.toString());
                                path.add(0, curPos);
                                PolylineOptions pOpt = new PolylineOptions().clickable(true);

                                for (int l = 0; l < path.size(); l++){
                                    pOpt.add(path.get(l));
                                }
                                Polyline pLine1 = mMap.addPolyline(pOpt);
                                pLine1.setTag("Get Benched");
//                                if (new CSVReader().calculateDistanceByLatLng(nearestItem, path.get(path.size()-1))>350){
//                                    pathFound[0] = false;
//                                }
//                                else{
//                                    pathFound[0] = true;
//                                }
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String temp = "ERROR";
                            Context context = getApplicationContext();
                            CharSequence text = temp;
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        //}


//        Context context = getApplicationContext();
//        CharSequence text = display[0];
//        int duration = Toast.LENGTH_LONG;
//
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();


//        Context context = getApplicationContext();
//        CharSequence text = "tst";
//        int duration = Toast.LENGTH_LONG;
//
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curPos));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private List<LatLng> decodePath(String path){
        String steps = "\"steps\":";
        String endLoc = "end_location\":";
        String stepsOn = path.substring(path.indexOf(steps)+steps.length());
        List <LatLng> result = new ArrayList<LatLng>();
        while (stepsOn.indexOf(endLoc)>0){
            stepsOn = stepsOn.substring(stepsOn.indexOf(endLoc) + endLoc.length());
            double lat = Double.parseDouble(stepsOn.substring(stepsOn.indexOf(":")+1, stepsOn.indexOf(",")));
            stepsOn = stepsOn.substring(stepsOn.indexOf(",")+1);
            double lng = Double.parseDouble(stepsOn.substring(stepsOn.indexOf(":")+1, stepsOn.indexOf("}")));
            System.out.println("NEW POINT " + lat + " " + lng);
            result.add(new LatLng(lat,lng));
        }
        return result;
    }

    private List<LatLng> decodePoly(String encoded) {
        //pad with 0
        ArrayList<Character> paddedList = new ArrayList<Character>();
        for (int c = 0; c < encoded.length(); c++){
            paddedList.add(encoded.toCharArray()[c]);
        }
        paddedList.add(new Character((char)0));
        paddedList.add(new Character((char)0));
        paddedList.add(new Character((char)0));


        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = (char)paddedList.get(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = (char)paddedList.get(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}


