package net.kpearce.AndroSilencer;

import android.R;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: kurtis
 * Date: 1/31/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetworkConfigurationActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        setListAdapter(new ArrayAdapter<WifiConfiguration>(this, R.layout.simple_list_item_1,wifiManager.getConfiguredNetworks()));
    }
}