package net.kpearce.AndroSilencer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.AndroSilencer.R;
import net.kpearce.AndroSilencer.activities.gps.MapBaseLocationActivity;
import net.kpearce.AndroSilencer.activities.wifi.SaveOrScanActivity;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 1/30/13
 * Time: 11:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocationTypeActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_type_selection);
    }

    public void onChooseWifi(View view) {
        Intent intent = new Intent(this,SaveOrScanActivity.class);
        startActivity(intent);
    }

    public void onChooseGps(View view){
        Intent intent = new Intent(this, MapBaseLocationActivity.class);
        startActivity(intent);
    }
}