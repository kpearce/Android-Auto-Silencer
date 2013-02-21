package net.kpearce.AndroSilencer.activities.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import com.example.AndroSilencer.R;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 2/11/13
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveOrScanActivity extends Activity {
    private WifiManager wifiManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        setContentView(R.layout.savedorscanswitch);
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


    class WifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent resultIntent = new Intent(intent);
            resultIntent.setClass(context, WifiScanResultsActivity.class);
            startActivity(resultIntent);
            getApplicationContext().unregisterReceiver(this);
        }
    }
}