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

    private WifiManager wifiManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationtypeselection);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    }

    public void onChooseWifi(View view) {
        WifiTypeSelection typeSelection = WifiTypeSelection.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("Network chooser");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
       typeSelection.show(ft,"Network chooser");
    }

    public void onChooseWifiScan(View view){
        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.getApplicationContext().registerReceiver(new WifiScanReceiver(), intentFilter);
        wifiManager.startScan();
    }

    public void onChooseSavedWifi(View view){
        Intent intent = new Intent(this,NetworkConfigurationActivity.class);
        startActivity(intent);
    }

    public void onChooseGps(View view){
        Toast.makeText(view.getContext(), "Cheers GPS", Toast.LENGTH_LONG).show();
    }

    class WifiScanReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent resultIntent = new Intent(intent);
            resultIntent.setClass(context, WifiScanResultsActivity.class);
            startActivity(resultIntent);
            getApplicationContext().unregisterReceiver(this);
        }
    }
}