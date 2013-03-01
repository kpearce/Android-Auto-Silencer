package net.kpearce.AndroSilencer.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.LocationManager;
import android.os.*;
import android.view.View;
import android.widget.TextView;
import com.example.AndroSilencer.R;
import net.kpearce.AndroSilencer.activities.gps.MapBaseLocationActivity;
import net.kpearce.AndroSilencer.activities.wifi.ManageLocationsActivity;
import net.kpearce.AndroSilencer.activities.wifi.WifiOptionsActivity;
import net.kpearce.AndroSilencer.fragments.gps.GPSOptionDialog;
import net.kpearce.AndroSilencer.fragments.gps.NameGpsDialog;
import net.kpearce.AndroSilencer.services.WifiLocationSilenceService;
import net.kpearce.AndroSilencer.setttings.SettingsActivity;

public class MainActivity extends Activity {
    private static final String STATUS_STRING = "statusString";
    private TextView statusBox;
    private WifiLocationSilenceService.WifiServiceCallback callback;
    private LocationManager locationManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main);
        if (!WifiLocationSilenceService.isStarted()) {
            Intent intent = new Intent(this, WifiLocationSilenceService.class);
            startService(intent);
        }

        statusBox = (TextView) findViewById(R.id.status_message);
        String silencedLocation = WifiLocationSilenceService.getSilencedLocation();
        statusBox.setText(silencedLocation==null?"":"Near "+silencedLocation);
        callback = getWifiServiceCallback();
        WifiLocationSilenceService.registerCallback(callback);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    private WifiLocationSilenceService.WifiServiceCallback getWifiServiceCallback() {
        return new WifiLocationSilenceService.WifiServiceCallback() {
            @Override
            public void onSilenceChanged(String msg) {
                if (msg != null) {
                    statusBox.setText("Near " + msg);
                } else {
                    statusBox.setText("");
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATUS_STRING,statusBox.getText().toString());
    }



    @Override
    protected void onPause() {
        WifiLocationSilenceService.unRegisterCallback(callback);
        callback = null;
        super.onPause();

    }

    @Override
    protected void onResume() {
        if(callback != null){
            WifiLocationSilenceService.registerCallback(callback);
        }
        else {
            callback = getWifiServiceCallback();
            WifiLocationSilenceService.registerCallback(callback);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(callback != null){
            WifiLocationSilenceService.unRegisterCallback(callback);
            callback = null;
        }
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void addWifiLocationClick(View view){
        Intent intent = new Intent(this,WifiOptionsActivity.class);
        startActivity(intent);
    }

    public void addGpsLocationClick(View view){
        GPSOptionDialog dialog = new GPSOptionDialog();
        dialog.show(getFragmentManager(), "gps_type");

    }

    public void manageLocationsClick(View view){
        Intent manageLocationIntent = new Intent(view.getContext(),ManageLocationsActivity.class);
        startActivity(manageLocationIntent);
    }

    public void openSettings(View view){
        Intent settingsActivity = new Intent(view.getContext(),SettingsActivity.class);
        startActivity(settingsActivity);
    }

    public void chooseMap() {
        Intent intent = new Intent(this, MapBaseLocationActivity.class);
        startActivity(intent);
    }

    public void chooseMyLocation() {
        NameGpsDialog nameGpsDialog = new NameGpsDialog();
        nameGpsDialog.show(getFragmentManager(),"new_gps_location");
    }
}
