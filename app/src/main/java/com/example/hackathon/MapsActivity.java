package com.example.hackathon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

//import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


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
        LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public static GoogleMap mMap;
    public static LatLng curPos = null;
    public static ArrayList<MarkerOptions> nearestMarkers = new ArrayList<MarkerOptions>();
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
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
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
        curPos = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(curPos);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.setOnMarkerClickListener(this);
        // bench
        ArrayList<LatLng> benchList = null;
        CSVReader reader = new CSVReader();
        InputStream benchIS = getResources().openRawResource(R.raw.benchdata);
        BufferedReader benchReader = new BufferedReader(
                new InputStreamReader(benchIS, Charset.forName("UTF-8"))
        );
        String benchLine = "";
        String benchCvsSplitBy = ",";
        int benchLatIndex = 0;
        int benchLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            benchLine = benchReader.readLine();
            String[] data = benchLine.split(benchCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    benchLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    benchLngIndex = i;
                }
            }
            while ((benchLine = benchReader.readLine()) != null) {
                try {
                    data = benchLine.split(benchCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[benchLatIndex]), Double.parseDouble(data[benchLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            benchList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestBench = reader.findNearestItem(curPos, benchList);

        mMap.addMarker(new MarkerOptions().position(nearestBench).title("Nearest Bench"));
        MarkerOptions benchMarker = new MarkerOptions();
        benchMarker.position(nearestBench);
        benchMarker.title("Nearest Bench");
        benchMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        // bike parking
        ArrayList<LatLng> bikeList = null;
        InputStream bikeIS = getResources().openRawResource(R.raw.bicycleparkingdata);
        BufferedReader bikeReader = new BufferedReader(
                new InputStreamReader(bikeIS, Charset.forName("UTF-8"))
        );
        String bikeLine = "";
        String bikeCvsSplitBy = ",";
        int bikeLatIndex = 0;
        int bikeLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            bikeLine = bikeReader.readLine();
            String[] data = bikeLine.split(bikeCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    bikeLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    bikeLngIndex = i;
                }
            }
            while ((bikeLine = bikeReader.readLine()) != null) {
                try {
                    data = bikeLine.split(bikeCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[bikeLatIndex]), Double.parseDouble(data[bikeLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            bikeList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestBike = reader.findNearestItem(curPos, bikeList);

        mMap.addMarker(new MarkerOptions().position(nearestBike).title("Nearest Bike Parking"));
        MarkerOptions bikeMarker = new MarkerOptions();
        bikeMarker.position(nearestBike);
        bikeMarker.title("Nearest Bike Parking");
        bikeMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // info
        ArrayList<LatLng> infoList = null;
        InputStream infoIS = getResources().openRawResource(R.raw.informationpillardata);
        BufferedReader infoReader = new BufferedReader(
                new InputStreamReader(infoIS, Charset.forName("UTF-8"))
        );
        String infoLine = "";
        String infoCvsSplitBy = ",";
        int infoLatIndex = 0;
        int infoLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            infoLine = infoReader.readLine();
            String[] data = infoLine.split(infoCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    infoLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    infoLngIndex = i;
                }
            }
            while ((infoLine = infoReader.readLine()) != null) {
                try {
                    data = infoLine.split(infoCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[infoLatIndex]), Double.parseDouble(data[infoLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            infoList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestInfo = reader.findNearestItem(curPos, infoList);

        mMap.addMarker(new MarkerOptions().position(nearestInfo).title("Nearest Information Board"));
        MarkerOptions infoMarker = new MarkerOptions();
        infoMarker.position(nearestInfo);
        infoMarker.title("Nearest Information Board");
        infoMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        // litter
        ArrayList<LatLng> litterList = null;
        InputStream litterIS = getResources().openRawResource(R.raw.litterreceptacledata);
        BufferedReader litterReader = new BufferedReader(
                new InputStreamReader(litterIS, Charset.forName("UTF-8"))
        );
        String litterLine = "";
        String litterCvsSplitBy = ",";
        int litterLatIndex = 0;
        int litterLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            litterLine = litterReader.readLine();
            String[] data = litterLine.split(litterCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    litterLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    litterLngIndex = i;
                }
            }
            while ((litterLine = litterReader.readLine()) != null) {
                try {
                    data = litterLine.split(litterCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[litterLatIndex]), Double.parseDouble(data[litterLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            litterList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestLitter = reader.findNearestItem(curPos, litterList);

        mMap.addMarker(new MarkerOptions().position(nearestLitter).title("Nearest Garbage Bin"));
        MarkerOptions litterMarker = new MarkerOptions();
        litterMarker.position(nearestLitter);
        litterMarker.title("Nearest Garbage Bin");
        litterMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

        // notice board
        ArrayList<LatLng> noticeList = null;
        InputStream noticeIS = getResources().openRawResource(R.raw.posterstructuredata);
        BufferedReader noticeReader = new BufferedReader(
                new InputStreamReader(noticeIS, Charset.forName("UTF-8"))
        );
        String noticeLine = "";
        String noticeCvsSplitBy = ",";
        int noticeLatIndex = 0;
        int noticeLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            noticeLine = noticeReader.readLine();
            String[] data = noticeLine.split(noticeCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    noticeLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    noticeLngIndex = i;
                }
            }
            while ((noticeLine = noticeReader.readLine()) != null) {
                try {
                    data = noticeLine.split(noticeCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[noticeLatIndex]), Double.parseDouble(data[noticeLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            noticeList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestNotice = reader.findNearestItem(curPos, noticeList);

        mMap.addMarker(new MarkerOptions().position(nearestNotice).title("Nearest Notice Board"));
        MarkerOptions noticeMarker = new MarkerOptions();
        noticeMarker.position(nearestNotice);
        noticeMarker.title("Nearest Notice Board");
        noticeMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        // poster board
        ArrayList<LatLng> posterList = null;
        InputStream posterIS = getResources().openRawResource(R.raw.posterboarddata);
        BufferedReader posterReader = new BufferedReader(
                new InputStreamReader(posterIS, Charset.forName("UTF-8"))
        );
        String posterLine = "";
        String posterCvsSplitBy = ",";
        int posterLatIndex = 0;
        int posterLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            posterLine = posterReader.readLine();
            String[] data = posterLine.split(posterCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    posterLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    posterLngIndex = i;
                }
            }
            while ((posterLine = posterReader.readLine()) != null) {
                try {
                    data = posterLine.split(posterCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[posterLatIndex]), Double.parseDouble(data[posterLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            posterList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestPoster = reader.findNearestItem(curPos, posterList);

        mMap.addMarker(new MarkerOptions().position(nearestPoster).title("Nearest Poster Board"));
        MarkerOptions posterMarker = new MarkerOptions();
        posterMarker.position(nearestPoster);
        posterMarker.title("Nearest Poster Board");
        posterMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        // newsstand
        ArrayList<LatLng> newsList = null;
        InputStream newsIS = getResources().openRawResource(R.raw.publicationstructuredata);
        BufferedReader newsReader = new BufferedReader(
                new InputStreamReader(newsIS, Charset.forName("UTF-8"))
        );
        String newsLine = "";
        String newsCvsSplitBy = ",";
        int newsLatIndex = 0;
        int newsLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            newsLine = newsReader.readLine();
            String[] data = newsLine.split(newsCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    newsLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    newsLngIndex = i;
                }
            }
            while ((newsLine = newsReader.readLine()) != null) {
                try {
                    data = newsLine.split(newsCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[newsLatIndex]), Double.parseDouble(data[newsLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            newsList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestNews = reader.findNearestItem(curPos, newsList);

        mMap.addMarker(new MarkerOptions().position(nearestNews).title("Nearest Newsstand"));
        MarkerOptions newsMarker = new MarkerOptions();
        newsMarker.position(nearestNews);
        newsMarker.title("Nearest Newsstand");
        newsMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

        // washroom
        ArrayList<LatLng> washroomList = null;
        InputStream washroomIS = getResources().openRawResource(R.raw.publicwashroomdata);
        BufferedReader washroomReader = new BufferedReader(
                new InputStreamReader(washroomIS, Charset.forName("UTF-8"))
        );
        String washroomLine = "";
        String washroomCvsSplitBy = ",";
        int washroomLatIndex = 0;
        int washroomLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            washroomLine = washroomReader.readLine();
            String[] data = washroomLine.split(washroomCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    washroomLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    washroomLngIndex = i;
                }
            }
            while ((washroomLine = washroomReader.readLine()) != null) {
                try {
                    data = washroomLine.split(washroomCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[washroomLatIndex]), Double.parseDouble(data[washroomLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            washroomList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestWashroom = reader.findNearestItem(curPos, washroomList);

        mMap.addMarker(new MarkerOptions().position(nearestWashroom).title("Nearest Public Washroom"));
        MarkerOptions washroomMarker = new MarkerOptions();
        washroomMarker.position(nearestWashroom);
        washroomMarker.title("Nearest Public Washroom");
        washroomMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        // transit shelter
        ArrayList<LatLng> transitList = null;
        InputStream transitIS = getResources().openRawResource(R.raw.transitshelterdata);
        BufferedReader transitReader = new BufferedReader(
                new InputStreamReader(transitIS, Charset.forName("UTF-8"))
        );
        String transitLine = "";
        String transitCvsSplitBy = ",";
        int transitLatIndex = 0;
        int transitLngIndex = 0;
        try {
            ArrayList<LatLng> latLng = new ArrayList();
            transitLine = transitReader.readLine();
            String[] data = transitLine.split(transitCvsSplitBy);
            for (int i = 0; i < data.length; i ++) {
                if (data[i].equals("LATITUDE")) {
                    transitLatIndex = i;
                }
                if (data[i].equals("LONGITUDE")) {
                    transitLngIndex = i;
                }
            }
            while ((transitLine = transitReader.readLine()) != null) {
                try {
                    data = transitLine.split(transitCvsSplitBy);
                    LatLng dataLatLng = new LatLng(Double.parseDouble(data[transitLatIndex]), Double.parseDouble(data[transitLngIndex]));

                    latLng.add(dataLatLng);
                }
                catch (NumberFormatException e) {
                }
            }
            transitList = latLng;
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng nearestTransit = reader.findNearestItem(curPos, transitList);

        mMap.addMarker(new MarkerOptions().position(nearestTransit).title("Nearest Transit Shelter"));
        MarkerOptions transitMarker = new MarkerOptions();
        transitMarker.position(nearestTransit);
        transitMarker.title("Nearest Transit Shelter");
        transitMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        MarkerOptions[] nearestMarkersArr = {benchMarker,bikeMarker, infoMarker,litterMarker,noticeMarker,posterMarker,newsMarker,washroomMarker,transitMarker};

        //String[] nearestURLS = new String[nearestItems.length];
        for (int s = 0; s < nearestMarkersArr.length; s++){
            nearestMarkers.add(nearestMarkersArr[s]);
            nearestMarkers.get(s).visible(false);
        }
//
//
//        final String[] display = {"nothing"};
//
//        RequestQueue requestQueue;
//        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
//        Network network = new BasicNetwork(new HurlStack());
//        requestQueue = new RequestQueue(cache, network);
//        requestQueue.start();
//
//        // Instantiate the RequestQueue.
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                    (Request.Method.GET, benchUrl, null, new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            String temp = "Response: " + response.toString();
////                        Context context = getApplicationContext();
////                        CharSequence text = temp;
////                        int duration = Toast.LENGTH_LONG;
////
////                        Toast toast = Toast.makeText(context, text, duration);
////                        toast.show();
//
//                            String prePoly = "nothing";
//                            String pKey = "\"overview_polyline\":{\"points\":\"";
//                            try{
//                                //prePoly = response.getJSONArray("routes").getJSONArray(2).getJSONArray(6).getJSONObject(4).getString("points");
//                                int pPlace = response.toString().lastIndexOf(pKey);
//                                prePoly = response.toString().substring(pPlace + pKey.length());
//                                prePoly = prePoly.substring(0, prePoly.indexOf("\""));
//
//
//                                System.out.println();
//                                System.out.println();
//                                System.out.println(prePoly);
//                                System.out.println();
//                                System.out.println();
//
//                            }
//                            catch   (Exception e){
//                                System.out.println("JSON ERROR");
//                            }
//                            if (prePoly!=null){
////                            Context context2 = getApplicationContext();
////                            CharSequence text2 = prePoly;
////                            int duration2 = Toast.LENGTH_LONG;
////
////                            Toast toast2 = Toast.makeText(context2, text2, duration2);
////                            toast2.show();
//                                //List<LatLng> path = decodePoly(prePoly);
//                                List<LatLng> path = decodePath(response.toString());
//                                path.add(0, curPos);
//                                PolylineOptions pOpt = new PolylineOptions().clickable(true);
//
//                                for (int l = 0; l < path.size(); l++){
//                                    pOpt.add(path.get(l));
//                                }
//                                Polyline pLine1 = mMap.addPolyline(pOpt);
//                                pLine1.setTag("Get Benched");
////                                if (new CSVReader().calculateDistanceByLatLng(nearestItem, path.get(path.size()-1))>350){
////                                    pathFound[0] = false;
////                                }
////                                else{
////                                    pathFound[0] = true;
////                                }
//                            }
//
//
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            String temp = "ERROR";
//                            Context context = getApplicationContext();
//                            CharSequence text = temp;
//                            int duration = Toast.LENGTH_LONG;
//
//                            Toast toast = Toast.makeText(context, text, duration);
//                            toast.show();
//                        }
//                    });
//            requestQueue.add(jsonObjectRequest);
//


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

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.clear();
        for (int m = 0; m < nearestMarkers.size(); m++){
            if(nearestMarkers.get(m).isVisible()){
                mMap.addMarker(nearestMarkers.get(m));
            }

        }
        marker.getTitle();


        final String[] display = {"nothing"};

        String curUrl = DirectionGetter.makeURL(curPos,marker.getPosition());

        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        // Instantiate the RequestQueue.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, curUrl, null, new Response.Listener<JSONObject>() {
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
        return true;
    }
}


