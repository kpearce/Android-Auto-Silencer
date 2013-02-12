package net.kpearce.AndroSilencer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.AndroSilencer.R;

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
        setContentView(R.layout.locationtypeselection);
    }

    public void onChooseWifi(View view) {
        Intent intent = new Intent(this,SaveOrScanActivity.class);
        startActivity(intent);
    }

    public void onChooseGps(View view){
        Toast.makeText(view.getContext(), "Cheers GPS", Toast.LENGTH_LONG).show();
    }
}