package com.example.hackathon;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }

        @Override
        public boolean onPreferenceTreeClick (Preference preference) {
            String key = (String)preference.getKey();
            MapsActivity.mMap.clear();


            for(int i = 0; i<MapsActivity.nearestMarkers.size(); i++){
                if (MapsActivity.nearestMarkers.get(i).getTitle().contains(key)){
                    MapsActivity.nearestMarkers.get(i).visible(!MapsActivity.nearestMarkers.get(i).isVisible());
                }
            }

            for(int i = 0; i<MapsActivity.nearestMarkers.size(); i++){
                if(MapsActivity.nearestMarkers.get(i).isVisible()){
                    MapsActivity.mMap.addMarker(MapsActivity.nearestMarkers.get(i));
                }
            }


            return false;
        }
    }

}