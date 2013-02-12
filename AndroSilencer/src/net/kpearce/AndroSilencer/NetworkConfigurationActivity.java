package net.kpearce.AndroSilencer;

import android.R;
import android.app.ListActivity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

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
        setListAdapter(new WifiConfigurationListAdapter(this, R.layout.simple_list_item_1,wifiManager.getConfiguredNetworks()));
    }

    class WifiConfigurationListAdapter extends ArrayAdapter<WifiConfiguration>{

        public WifiConfigurationListAdapter(NetworkConfigurationActivity networkConfigurationActivity, int simple_list_item_1, List<WifiConfiguration> configuredNetworks) {
            super(networkConfigurationActivity, simple_list_item_1, configuredNetworks);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           WifiConfiguration wifiConfiguration =  getItem(position);

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TextView viewById = (TextView) inflater.inflate(R.layout.simple_list_item_1,parent,false);
            viewById.setText(wifiConfiguration.SSID.substring(1,wifiConfiguration.SSID.length()-1));
            return viewById;
        }
    }
}